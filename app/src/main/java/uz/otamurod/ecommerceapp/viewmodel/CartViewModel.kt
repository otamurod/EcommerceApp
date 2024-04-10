package uz.otamurod.ecommerceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import uz.otamurod.ecommerceapp.data.CartProduct
import uz.otamurod.ecommerceapp.firebase.FirebaseCommon
import uz.otamurod.ecommerceapp.firebase.FirebaseCommon.QuantityChanging
import uz.otamurod.ecommerceapp.firebase.FirebaseCommon.QuantityChanging.DECREASE
import uz.otamurod.ecommerceapp.firebase.FirebaseCommon.QuantityChanging.INCREASE
import uz.otamurod.ecommerceapp.helper.getProductPrice
import uz.otamurod.ecommerceapp.util.Constants.CART_COLLECTION
import uz.otamurod.ecommerceapp.util.Constants.USER_COLLECTION
import uz.otamurod.ecommerceapp.util.Resource
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
) : ViewModel() {
    private val _cartProducts =
        MutableStateFlow<Resource<List<CartProduct>>>(Resource.Unspecified())
    val cartProducts: StateFlow<Resource<List<CartProduct>>> = _cartProducts.asStateFlow()

    private var cartProductDocuments = emptyList<DocumentSnapshot>()

    val productsPrice = cartProducts.map {
        when (it) {
            is Resource.Success -> {
                calculatePrice(it.data!!)
            }
            else -> null
        }
    }

    private val _deleteProductDialog = MutableSharedFlow<CartProduct>()
    val deleteProductDialog = _deleteProductDialog.asSharedFlow()

    init {
        getCartProducts()
    }

    private fun calculatePrice(cartProducts: List<CartProduct>): Float {
        return cartProducts.sumByDouble { cartProduct ->
            (cartProduct.product.offerPercentage.getProductPrice(cartProduct.product.price) * cartProduct.quantity).toDouble()
        }.toFloat()
    }

    private fun getCartProducts() {
        viewModelScope.launch {
            _cartProducts.emit(Resource.Loading())
        }

        firestore.collection(USER_COLLECTION).document(firebaseAuth.uid!!)
            .collection(CART_COLLECTION)
            .addSnapshotListener { value, error ->
                if (error != null && value == null) {
                    viewModelScope.launch {
                        _cartProducts.emit(Resource.Error(error.message.toString()))
                    }
                } else if (value != null) {
                    cartProductDocuments = value.documents
                    val cartProducts = value.toObjects(CartProduct::class.java)
                    viewModelScope.launch {
                        _cartProducts.emit(Resource.Success(cartProducts))
                    }
                }
            }
    }

    fun changeQuantity(cartProduct: CartProduct, quantityChanging: QuantityChanging) {
        val index = cartProducts.value.data?.indexOf(cartProduct)

        /**
         * index could be equal to -1 if the function [getCartProducts] delays which will also
         * delay the result we expect to be inside the [_cartProducts] and to prevent the app from crashing
         * we make a check below
         */

        if (index != null && index != -1) {
            val documentId = cartProductDocuments[index].id
            when (quantityChanging) {
                INCREASE -> {
                    viewModelScope.launch {
                        _cartProducts.emit(Resource.Loading())
                    }

                    increaseQuantity(documentId)
                }
                DECREASE -> {
                    if (cartProduct.quantity == 1) {
                        viewModelScope.launch {
                            _deleteProductDialog.emit(cartProduct)
                        }
                        return
                    }
                    viewModelScope.launch {
                        _cartProducts.emit(Resource.Loading())
                    }

                    decreaseQuantity(documentId)
                }
            }
        }
    }

    private fun decreaseQuantity(documentId: String) {
        firebaseCommon.decreaseQuantity(documentId) { result, e ->
            if (e != null) {
                viewModelScope.launch {
                    _cartProducts.emit(Resource.Error(e.message.toString()))
                }
            }
        }
    }

    private fun increaseQuantity(documentId: String) {
        firebaseCommon.increaseQuantity(documentId) { result, e ->
            if (e != null) {
                viewModelScope.launch {
                    _cartProducts.emit(Resource.Error(e.message.toString()))
                }
            }
        }
    }

    fun deleteCartProduct(cartProduct: CartProduct) {
        val index = cartProducts.value.data?.indexOf(cartProduct)

        /**
         * index could be equal to -1 if the function [getCartProducts] delays which will also
         * delay the result we expect to be inside the [_cartProducts] and to prevent the app from crashing
         * we make a check below
         */

        if (index != null && index != -1) {
            val documentId = cartProductDocuments[index].id

            firestore.collection(USER_COLLECTION).document(firebaseAuth.uid!!)
                .collection(CART_COLLECTION).document(documentId).delete()
        }
    }
}
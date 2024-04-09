package uz.otamurod.ecommerceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.otamurod.ecommerceapp.data.Category
import uz.otamurod.ecommerceapp.data.PagingInfo
import uz.otamurod.ecommerceapp.data.Product
import uz.otamurod.ecommerceapp.util.Resource

class CategoryViewModel(
    private val firestore: FirebaseFirestore,
    private val category: Category
) : ViewModel() {
    private val _offerProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val offerProducts: StateFlow<Resource<List<Product>>> = _offerProducts.asStateFlow()

    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProducts: StateFlow<Resource<List<Product>>> = _bestProducts.asStateFlow()

    private val pagingInfo = PagingInfo()

    init {
        fetchOfferProducts()
        fetchBestProducts()
    }

    fun fetchOfferProducts() {
        if (!pagingInfo.isOfferProductsPagingDone) {
            viewModelScope.launch {
                _offerProducts.emit(Resource.Loading())
            }
            firestore.collection("Products")
                .whereEqualTo("category", category.category)
                .whereNotEqualTo("offerPercentage", null)
                .limit(pagingInfo.offerProductsPage * 5)
                .get()
                .addOnSuccessListener {
                    val products = it.toObjects(Product::class.java)
                    pagingInfo.isOfferProductsPagingDone = products == pagingInfo.oldOfferProducts
                    pagingInfo.oldOfferProducts = products

                    viewModelScope.launch {
                        _offerProducts.emit(Resource.Success(products))
                    }
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _offerProducts.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }

    fun fetchBestProducts() {
        if (!pagingInfo.isBestProductsPagingDone) {
            viewModelScope.launch {
                _bestProducts.emit(Resource.Loading())
            }
            firestore.collection("Products")
                .whereEqualTo("category", category.category)
                .whereEqualTo("offerPercentage", null)
                .limit(pagingInfo.bestProductsPage * 6)
                .get()
                .addOnSuccessListener {
                    val products = it.toObjects(Product::class.java)
                    pagingInfo.isBestProductsPagingDone = products == pagingInfo.oldBestProducts
                    pagingInfo.oldBestProducts = products

                    viewModelScope.launch {
                        _bestProducts.emit(Resource.Success(products))
                    }
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _bestProducts.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }
}
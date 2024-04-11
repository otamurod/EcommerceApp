package uz.otamurod.ecommerceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.otamurod.ecommerceapp.data.order.Order
import uz.otamurod.ecommerceapp.util.Constants.CART_COLLECTION
import uz.otamurod.ecommerceapp.util.Constants.ORDERS_COLLECTION
import uz.otamurod.ecommerceapp.util.Constants.USER_COLLECTION
import uz.otamurod.ecommerceapp.util.Resource
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    private val _order = MutableStateFlow<Resource<Order>>(Resource.Unspecified())
    val order = _order.asStateFlow()

    fun placeOrder(order: Order) {
        viewModelScope.launch {
            _order.emit(Resource.Loading())
        }

        firestore.runBatch { batch ->
            // 1: Add the order into user-orders collection
            // 2: Add the order into orders collection
            // 3: Delete the products from user-cart collection

            firestore.collection(USER_COLLECTION)
                .document(firebaseAuth.uid!!)
                .collection(ORDERS_COLLECTION)
                .document()
                .set(order)

            firestore.collection(ORDERS_COLLECTION)
                .document()
                .set(order)

            firestore.collection(USER_COLLECTION)
                .document(firebaseAuth.uid!!)
                .collection(CART_COLLECTION)
                .get()
                .addOnSuccessListener {
                    it.documents.forEach {
                        it.reference.delete()
                    }
                }
        }.addOnSuccessListener {
            viewModelScope.launch {
                _order.emit(Resource.Success(order))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _order.emit(Resource.Error(it.message.toString()))
            }
        }
    }
}
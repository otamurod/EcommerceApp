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
import uz.otamurod.ecommerceapp.util.Constants.ORDERS_COLLECTION
import uz.otamurod.ecommerceapp.util.Constants.USER_COLLECTION
import uz.otamurod.ecommerceapp.util.Resource
import javax.inject.Inject

@HiltViewModel
class AllOrdersViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    private val _allOrders = MutableStateFlow<Resource<List<Order>>>(Resource.Unspecified())
    val allOrders = _allOrders.asStateFlow()

    init {
        getAllOrders()
        setUpOrderStatusChangeListener()
    }

    private fun setUpOrderStatusChangeListener() {
        firestore.collection(USER_COLLECTION).document(firebaseAuth.uid!!)
            .collection(ORDERS_COLLECTION)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    viewModelScope.launch {
                        _allOrders.emit(Resource.Error(error.message.toString()))
                    }
                    return@addSnapshotListener
                }

                val orders = value?.toObjects(Order::class.java)!!
                viewModelScope.launch {
                    _allOrders.emit(Resource.Success(orders))
                }
            }
    }

    private fun getAllOrders() {
        viewModelScope.launch { _allOrders.emit(Resource.Loading()) }

        firestore.collection(USER_COLLECTION).document(firebaseAuth.uid!!)
            .collection(ORDERS_COLLECTION).get()
            .addOnSuccessListener {
                val orders = it.toObjects(Order::class.java)
                viewModelScope.launch {
                    _allOrders.emit(Resource.Success(orders))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _allOrders.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}
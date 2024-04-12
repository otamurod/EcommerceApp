package uz.otamurod.ecommerceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.otamurod.ecommerceapp.data.Address
import uz.otamurod.ecommerceapp.util.Constants.ADDRESS_COLLECTION
import uz.otamurod.ecommerceapp.util.Constants.USER_COLLECTION
import uz.otamurod.ecommerceapp.util.Resource
import javax.inject.Inject

@HiltViewModel
class BillingViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    private val _addresses = MutableStateFlow<Resource<List<Address>>>(Resource.Unspecified())
    val addresses = _addresses.asStateFlow()

    init {
        getUserAddresses()
    }

    private fun getUserAddresses() {
        viewModelScope.launch { _addresses.emit(Resource.Loading()) }

        firestore.collection(USER_COLLECTION).document(firebaseAuth.uid!!)
            .collection(ADDRESS_COLLECTION)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    viewModelScope.launch { _addresses.emit(Resource.Error(error.message.toString())) }
                    return@addSnapshotListener
                }

                val addresses = value?.toObjects(Address::class.java)
                viewModelScope.launch { _addresses.emit(Resource.Success(addresses!!)) }
            }
    }
}
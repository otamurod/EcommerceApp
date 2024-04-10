package uz.otamurod.ecommerceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.otamurod.ecommerceapp.data.Address
import uz.otamurod.ecommerceapp.util.Constants.ADDRESS_COLLECTION
import uz.otamurod.ecommerceapp.util.Constants.USER_COLLECTION
import uz.otamurod.ecommerceapp.util.Resource
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    private val _addNewAddress = MutableStateFlow<Resource<Address>>(Resource.Unspecified())
    val addNewAddress = _addNewAddress.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    fun addAddress(address: Address) {
        val validateInputs = validateInputs(address)

        if (validateInputs) {
            viewModelScope.launch { _addNewAddress.emit(Resource.Loading()) }
            firestore.collection(USER_COLLECTION).document(firebaseAuth.uid!!)
                .collection(ADDRESS_COLLECTION)
                .document().set(address)
                .addOnSuccessListener {
                    viewModelScope.launch { _addNewAddress.emit(Resource.Success(address)) }
                }.addOnFailureListener {
                    viewModelScope.launch { _addNewAddress.emit(Resource.Error(it.message.toString())) }
                }
        } else {
            viewModelScope.launch {
                _error.emit("All fields are required")
            }
        }
    }

    private fun validateInputs(address: Address): Boolean {
        return address.addressTitle.isNotEmpty() &&
                address.city.isNotEmpty() &&
                address.phoneNumber.isNotEmpty() &&
                address.state.isNotEmpty() &&
                address.fullName.isNotEmpty() &&
                address.street.isNotEmpty()
    }
}
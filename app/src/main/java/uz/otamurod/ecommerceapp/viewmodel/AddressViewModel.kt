package uz.otamurod.ecommerceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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

    private val _userAddresses =
        MutableStateFlow<Resource<List<Address>>>(Resource.Unspecified())
    private val userAddresses: StateFlow<Resource<List<Address>>> = _userAddresses.asStateFlow()

    private var addressCollectionDocuments = emptyList<DocumentSnapshot>()

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

    fun updateAddress(updatedAddress: Address, originalAddress: Address) {
        val validateInputs = validateInputs(updatedAddress)

        if (validateInputs) {
            getUserAddresses(updatedAddress, originalAddress, false)
        } else {
            viewModelScope.launch {
                _error.emit("All fields are required")
            }
        }
    }

    fun removeAddress(originalAddress: Address) {
        getUserAddresses(null, originalAddress, true)
    }

    private fun getUserAddresses(
        updatedAddress: Address?,
        originalAddress: Address,
        shouldBeDeleted: Boolean
    ) {
        viewModelScope.launch {
            _userAddresses.emit(Resource.Loading())
        }

        firestore.collection(USER_COLLECTION).document(firebaseAuth.uid!!)
            .collection(ADDRESS_COLLECTION)
            .get()
            .addOnSuccessListener {
                addressCollectionDocuments = it.documents
                val addressList = it.toObjects(Address::class.java)
                viewModelScope.launch {
                    _userAddresses.emit(Resource.Success(addressList))
                }

                if (shouldBeDeleted) {
                    deleteAddress(originalAddress)
                } else {
                    updateAddressByTransaction(updatedAddress!!, originalAddress)
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _userAddresses.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    private fun updateAddressByTransaction(updatedAddress: Address, originalAddress: Address) {
        val index = userAddresses.value.data?.indexOf(originalAddress)

        /**
         * index could be equal to -1 if the function [getUserAddresses] delays which will also
         * delay the result we expect to be inside the [_userAddresses] and to prevent the app from crashing
         * we make a check below
         */

        if (index != null && index != -1) {
            val documentId = addressCollectionDocuments[index].id

            firestore.runTransaction { transaction ->
                val documentRef =
                    firestore.collection(USER_COLLECTION).document(firebaseAuth.uid!!)
                        .collection(ADDRESS_COLLECTION).document(documentId)
                val currentAddress = transaction.get(documentRef).toObject(Address::class.java)
                if (currentAddress != null) {
                    transaction.set(documentRef, updatedAddress)
                }
            }.addOnSuccessListener {
                viewModelScope.launch { _addNewAddress.emit(Resource.Success(updatedAddress)) }
            }.addOnFailureListener {
                viewModelScope.launch { _addNewAddress.emit(Resource.Error(it.message.toString())) }
            }
        }
    }

    private fun deleteAddress(address: Address) {
        val index = userAddresses.value.data?.indexOf(address)

        /**
         * index could be equal to -1 if the function [getCartProducts] delays which will also
         * delay the result we expect to be inside the [_cartProducts] and to prevent the app from crashing
         * we make a check below
         */

        if (index != null && index != -1) {
            val documentId = addressCollectionDocuments[index].id

            firestore.collection(USER_COLLECTION).document(firebaseAuth.uid!!)
                .collection(ADDRESS_COLLECTION).document(documentId).delete()
                .addOnSuccessListener {
                    viewModelScope.launch { _addNewAddress.emit(Resource.Success(address)) }
                }.addOnFailureListener {
                    viewModelScope.launch { _addNewAddress.emit(Resource.Error(it.message.toString())) }
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

    companion object {
        private const val TAG = "AddressViewModel"
    }
}
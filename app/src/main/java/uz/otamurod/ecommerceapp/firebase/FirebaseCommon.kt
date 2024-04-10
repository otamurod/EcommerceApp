package uz.otamurod.ecommerceapp.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import uz.otamurod.ecommerceapp.data.CartProduct
import uz.otamurod.ecommerceapp.util.Constants.CART_COLLECTION
import uz.otamurod.ecommerceapp.util.Constants.USER_COLLECTION

class FirebaseCommon(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {
    private val cartCollection =
        firestore.collection(USER_COLLECTION).document(firebaseAuth.uid!!).collection(
            CART_COLLECTION
        )

    fun addProductToCart(cartProduct: CartProduct, onResult: (CartProduct?, Exception?) -> Unit) {
        cartCollection.document()
            .set(cartProduct)
            .addOnSuccessListener {
                onResult(cartProduct, null)
            }.addOnFailureListener {
                onResult(null, it)
            }
    }

    fun increaseQuantity(documentId: String, onResult: (String?, Exception?) -> Unit) {
        firestore.runTransaction { transaction ->
            val documentReference = cartCollection.document(documentId)
            val document = transaction.get(documentReference)
            val productObject = document.toObject(CartProduct::class.java)

            productObject?.let { cartProduct ->
                val newQuantity = cartProduct.quantity + 1
                val newProductObject = cartProduct.copy(quantity = newQuantity)
                transaction.set(documentReference, newProductObject)
            }
        }.addOnSuccessListener {
            onResult(documentId, null)
        }.addOnFailureListener {
            onResult(null, it)
        }
    }

    fun decreaseQuantity(documentId: String, onResult: (String?, Exception?) -> Unit) {
        firestore.runTransaction { transaction ->
            val documentReference = cartCollection.document(documentId)
            val document = transaction.get(documentReference)
            val productObject = document.toObject(CartProduct::class.java)

            productObject?.let { cartProduct ->
                val newQuantity = cartProduct.quantity - 1
                val newProductObject = cartProduct.copy(quantity = newQuantity)
                transaction.set(documentReference, newProductObject)
            }
        }.addOnSuccessListener {
            onResult(documentId, null)
        }.addOnFailureListener {
            onResult(null, it)
        }
    }

    enum class QuantityChanging {
        INCREASE, DECREASE
    }
}
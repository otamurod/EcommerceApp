package uz.otamurod.ecommerceapp.data.order

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import uz.otamurod.ecommerceapp.data.Address
import uz.otamurod.ecommerceapp.data.CartProduct

@Parcelize
data class Order(
    val orderStatus: String,
    val totalPrice: Float,
    val cartProducts: List<CartProduct>,
    val address: Address,
    val date: String,
    val orderId: Long
) : Parcelable {
    constructor() : this(
        "", 0f, emptyList(), Address(), "", 0L
    )
}

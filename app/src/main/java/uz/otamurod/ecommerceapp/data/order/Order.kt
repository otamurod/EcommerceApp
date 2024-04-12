package uz.otamurod.ecommerceapp.data.order

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import uz.otamurod.ecommerceapp.data.Address
import uz.otamurod.ecommerceapp.data.CartProduct
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random.Default.nextLong

@Parcelize
data class Order(
    val orderStatus: String = "",
    val totalPrice: Float = 0f,
    val cartProducts: List<CartProduct> = emptyList(),
    val address: Address = Address(),
    val date: String = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date()),
    val orderId: Long = nextLong(0, 100_000_000_000) + totalPrice.toLong()
) : Parcelable

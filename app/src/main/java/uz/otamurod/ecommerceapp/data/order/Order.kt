package uz.otamurod.ecommerceapp.data.order

import uz.otamurod.ecommerceapp.data.Address
import uz.otamurod.ecommerceapp.data.CartProduct

data class Order(
    val orderStatus: String,
    val totalPriceFloat: Float,
    val cartProducts: List<CartProduct>,
    val address: Address
)

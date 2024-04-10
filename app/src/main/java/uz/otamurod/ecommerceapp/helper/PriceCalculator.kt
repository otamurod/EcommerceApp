package uz.otamurod.ecommerceapp.helper

fun Float?.getProductPrice(price: Float): Float {
    if (this == null) {
        return price
    }
    val remainingPricePercentage = 1f - this
    val priceAfterDiscount = remainingPricePercentage * price
    return priceAfterDiscount
}
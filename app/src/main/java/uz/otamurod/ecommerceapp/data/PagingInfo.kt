package uz.otamurod.ecommerceapp.data

internal data class PagingInfo(
    var bestProductsPage: Long = 1,
    var oldBestProducts: List<Product> = emptyList(),
    var isBestProductsPagingDone: Boolean = false,
    var offerProductsPage: Long = 1,
    var oldOfferProducts: List<Product> = emptyList(),
    var isOfferProductsPagingDone: Boolean = false
)
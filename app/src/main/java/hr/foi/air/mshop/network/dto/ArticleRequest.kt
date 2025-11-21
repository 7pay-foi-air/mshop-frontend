package hr.foi.air.mshop.network.dto

data class ArticleRequest(
    val ean: String,
    val name: String,
    val description: String?,
    val price: Double,
    val imageUri: String? = null
)

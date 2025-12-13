package hr.foi.air.ws.models.articleManagement

data class ArticleRequest(
    val ean: String,
    val name: String,
    val description: String?,
    val price: Double,
    val imageUri: String? = null
)

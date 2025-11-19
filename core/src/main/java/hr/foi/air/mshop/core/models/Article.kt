package hr.foi.air.mshop.core.models

data class Article(
    val ean: String,
    val articleName : String,
    val description : String?,
    val price: Double,
    val imageUrl: String?  // URL ili path do slike koju backend vrati
)

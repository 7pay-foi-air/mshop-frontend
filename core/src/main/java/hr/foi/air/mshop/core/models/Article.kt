package hr.foi.air.mshop.core.models

data class Article(
    val id: Int? = null,
    val ean: String,
    val articleName : String,
    val description : String?,
    val price: Double,
    val currency: String = "EUR",
    val stockQuantity: Int = 0,
    val imageUrl: String?  = null, // remote slika s backenda
    val imageUri: String? = null,   // lokalno odabrana slika
)

package hr.foi.air.ws.models.articleManagement

import com.google.gson.annotations.SerializedName

typealias AllArticlesResponse = List<ArticleResponse>

class ArticleResponse (
    @SerializedName("created_at")
    val createdAt: String,
    val currency: String,
    val description: String,
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("is_active")
    val isActive: Boolean,
    val name: String,
    val price: Double,
    val sku: String,
    @SerializedName("stock_quantity")
    val stockQuantity: Int,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("uuid_item")
    val uuidItem: String,
    @SerializedName("uuid_organisation")
    val uuidOrganisation: String
)


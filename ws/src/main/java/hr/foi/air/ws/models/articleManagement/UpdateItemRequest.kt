package hr.foi.air.ws.models.articleManagement

import com.google.gson.annotations.SerializedName

data class UpdateItemRequest(
    val name: String,
    val description: String,
    val price: Double,
    val currency: String,
    val sku: String?,
    @SerializedName("stock_quantity")
    val stockQuantity: Int
)


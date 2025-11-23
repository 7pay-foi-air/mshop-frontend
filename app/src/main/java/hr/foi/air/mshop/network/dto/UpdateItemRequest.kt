package hr.foi.air.mshop.network.dto

import com.google.gson.annotations.SerializedName

data class UpdateItemRequest(
    val name: String,
    val description: String,
    val price: Double,
    val currency: String,
    val sku: String?,   // nullable
    @SerializedName("stock_quantity")
    val stockQuantity: Int
)


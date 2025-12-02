package hr.foi.air.ws.models.articleManagement

import com.google.gson.annotations.SerializedName

data class CreateItemResponse(
    val message: String,
    @SerializedName("uuid_item") val uuidItem: String,
    @SerializedName("image_url") val imageUrl: String?
)

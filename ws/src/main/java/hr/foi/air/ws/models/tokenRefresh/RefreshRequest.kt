package hr.foi.air.ws.models.tokenRefresh

import com.google.gson.annotations.SerializedName

data class RefreshRequest(
    @SerializedName("refresh_token")
    val refreshToken: String?
)

package hr.foi.air.mshop.network.dto.login

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("access_token")
    val accessToken: String?,

    @SerializedName("message")
    val message: String?,

    @SerializedName("refresh_token")
    val refreshToken: String?,

    @SerializedName("role")
    val role: String?,

    @SerializedName("error")
    val error: String?
)
package hr.foi.air.ws.models.userManagement

import com.google.gson.annotations.SerializedName

data class RecoverPasswordRequest(
    @SerializedName("username") val username: String,
    @SerializedName("recovery_token") val recoveryToken: String,
    @SerializedName("new_password") val newPassword: String
)

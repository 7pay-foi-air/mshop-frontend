package hr.foi.air.ws.models.login

import com.google.gson.annotations.SerializedName

data class ChangePasswordRequest(
    @SerializedName("new_password")
    val newPassword: String,

    @SerializedName("recovery_token")
    val recoveryToken: String
)
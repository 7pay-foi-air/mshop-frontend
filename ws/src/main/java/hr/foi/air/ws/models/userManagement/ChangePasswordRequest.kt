package hr.foi.air.ws.models.userManagement

import com.google.gson.annotations.SerializedName

data class ChangePasswordRequest(
    @SerializedName("recovery_token") val recoveryToken: String,
    @SerializedName("new_password") val newPassword: String
)

package hr.foi.air.ws.models.userManagement

import com.google.gson.annotations.SerializedName

data class RecoveryTokenResponse(
    @SerializedName("recovery_code_location") val recoveryCodeLocation: String?,
    @SerializedName("error") val error: String?
)

package hr.foi.air.ws.models.login

import com.google.gson.annotations.SerializedName

data class RecoveryCodeLocationRequest(
    val username: String,
    val answer1: String,
    val answer2: String,
    val answer3: String,
    @SerializedName("recovery_code_location")
    val recoveryCodeLocation: String
)

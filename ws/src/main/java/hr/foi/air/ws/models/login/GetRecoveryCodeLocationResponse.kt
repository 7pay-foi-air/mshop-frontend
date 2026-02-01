package hr.foi.air.ws.models.login

data class GetRecoveryCodeLocationResponse(
    val recoveryCodeLocation: String?,
    val valid: Boolean
)

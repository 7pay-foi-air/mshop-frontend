package hr.foi.air.ws.models.login

data class GetRecoveryCodeLocationRequest(
    val username: String,
    val answer1: String,
    val answer2: String,
    val answer3: String,
)

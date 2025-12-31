package hr.foi.air.ws.models.userManagement

data class UpdateMyProfileRequest(
    val first_name: String,
    val last_name: String,
    val address: String,
    val phone_number: String,
    val date_of_birth: String?
)

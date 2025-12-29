package hr.foi.air.ws.models.userManagement

data class AddUserRequest(
    val address: String,
    val date_of_birth: String, // yyyy-MM-dd
    val email: String,
    val first_name: String,
    val is_admin: Boolean,
    val last_name: String,
    val organisation_uuid: String,
    val phone_number: String,
    val username: String
)
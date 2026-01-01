package hr.foi.air.ws.models.userManagement

data class UpdateUserAsAdminRequest(
    val first_name: String,
    val last_name: String,val username: String,
    val email: String,
    val address: String,
    val phone_number: String,
    val date_of_birth: String?, // String "yyyy-MM-dd"
    val is_admin: Boolean,
    val role: String
)

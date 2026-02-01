package hr.foi.air.mshop.core.models

data class User(
    val uuidUser: String? = null,
    val firstName: String,
    val lastName: String,
    val username: String,
    val address: String,
    val email: String,
    val phoneNum: String,
    val role: String,
    val dateOfBirthMillis: Long? = null,
    val uuidOrganisation: String? = null,
    val isActive: Boolean
)

package hr.foi.air.mshop.core.models

import java.sql.Date

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

package hr.foi.air.ws.models.userManagement

import com.google.gson.annotations.SerializedName

typealias AllUsersResponse = List<UserResponse>
class UserResponse (
    @SerializedName("uuid_user")
    val uuidUser: String,

    @SerializedName("first_name")
    val firstName: String,

    @SerializedName("last_name")
    val lastName: String,

    val username: String,
    val email: String,
    val address: String,

    @SerializedName("phone_number")
    val phoneNum: String,

    val role: String,

    @SerializedName("date_of_birth")
    val dateOfBirth: String?,

    @SerializedName("uuid_organisation")
    val uuidOrganisation: String,

    @SerializedName("is_active")
    val isActive: Boolean,
)
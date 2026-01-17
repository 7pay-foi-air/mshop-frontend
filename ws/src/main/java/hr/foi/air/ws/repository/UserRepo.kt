package hr.foi.air.ws.repository

import android.content.Context
import android.util.Log
import hr.foi.air.mshop.core.models.User
import hr.foi.air.mshop.core.repository.IUserRepository
import hr.foi.air.ws.NetworkService
import hr.foi.air.ws.data.SessionManager
import hr.foi.air.ws.models.MessageResponse
import hr.foi.air.ws.models.userManagement.AddUserRequest
import hr.foi.air.ws.models.userManagement.ChangePasswordRequest
import hr.foi.air.ws.models.userManagement.UpdateMyProfileRequest
import hr.foi.air.ws.models.userManagement.UpdateUserAsAdminRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import retrofit2.Response

class UserRepo : IUserRepository {
    private val api = NetworkService.accountApi

    private fun hr.foi.air.ws.models.userManagement.UserResponse.toDomainModel(): User {
        val dateInMillis = this.dateOfBirth?.let {
            try {
                java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                    .parse(it)?.time
            } catch (e: Exception) {
                null
            }
        }

        return User(
            uuidUser = this.uuidUser,
            firstName = this.firstName,
            lastName = this.lastName,
            username = this.username,
            email = this.email,
            address = this.address,
            phoneNum = this.phoneNum,
            role = this.role,
            dateOfBirthMillis = dateInMillis,
            uuidOrganisation = this.uuidOrganisation,
            isActive = this.isActive
        )
    }

    override suspend fun addUser(user: User, context: Context): Result<String> {
        return try {
            val dateOfBirthString = user.dateOfBirthMillis?.let { millis ->
                java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                    .format(java.util.Date(millis))
            } ?: return Result.failure(Exception("Datum rođenja je obavezan."))

            val req = AddUserRequest(
                first_name = user.firstName,
                last_name = user.lastName,
                username = user.username,
                email = user.email,
                address = user.address,
                phone_number = user.phoneNum,
                date_of_birth = dateOfBirthString,
                organisation_uuid = user.uuidOrganisation,
                is_admin = (user.role == "admin")
            )

            val res = api.createUser(req)
            if (res.isSuccessful) {
                Result.success(res.body()?.message ?: "Uspješno dodano")
            } else {
                val errorJson = res.errorBody()?.string()
                val msg = extractErrorMessage(errorJson)
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getAllUsers(): Flow<List<User>> = flow {
        try {
            val response = api.getUsers()
            if (response.isSuccessful) {
                val loggedInUserId = SessionManager.currentUserId
                val users = response.body()
                    ?.map { it.toDomainModel() }
                    ?.filter { it.uuidUser != loggedInUserId }
                    ?: emptyList()
                emit(users)
            } else {
                println("Error fetching users: ${response.code()}")
                emit(emptyList())
            }
        } catch (e: Exception) {
            println("Exception when fetching users: ${e.message}")
            emit(emptyList())
        }
    }

    override suspend fun getUserById(userId: String): Result<User> {
        return try {
            val response = api.getUsers(userId = userId)
            if (response.isSuccessful) {
                val userResponse = response.body()?.firstOrNull()
                if (userResponse != null) {
                    Result.success(userResponse.toDomainModel())
                } else {
                    Result.failure(Exception("Korisnik s ID-em '$userId' nije pronađen."))
                }
            } else {
                val errorMsg = extractErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUser(user: User, context: Context): Result<String> {
        val uuidToUpdate =
            user.uuidUser ?: return Result.failure(Exception("Nedostaje uuid korisnika."))
        return try {
            val loggedInUserId = SessionManager.currentUserId
            val loggedInUserRole = SessionManager.currentUserRole
            val response: Response<MessageResponse>

            val dateOfBirthString = user.dateOfBirthMillis?.let { millis ->
                java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                    .format(java.util.Date(millis))
            }

            if (uuidToUpdate == loggedInUserId) {
                Log.d(
                    "UserRepo_Update",
                    "Scenarij: Korisnik ažurira vlastiti profil (updateMyProfile)."
                )
                val request = UpdateMyProfileRequest(
                    first_name = user.firstName,
                    last_name = user.lastName,
                    email = user.email,
                    address = user.address,
                    phone_number = user.phoneNum,
                    date_of_birth = dateOfBirthString
                )
                Log.d("UserRepo_Update", "Šaljem UpdateMyProfileRequest: $request")
                response = api.updateMyProfile(request)
            } else if (loggedInUserRole == "admin" || loggedInUserRole == "owner") {
                val targetUserRole = user.role
                if (targetUserRole == "owner") {
                    return Result.failure(Exception("Nije moguće mijenjati podatke vlasnika."))
                }
                if (loggedInUserRole == "admin" && targetUserRole == "admin") {
                    return Result.failure(Exception("Admin ne može mijenjati drugog admina."))
                }

                val request = UpdateUserAsAdminRequest(
                    first_name = user.firstName,
                    last_name = user.lastName,
                    username = user.username,
                    email = user.email,
                    address = user.address,
                    phone_number = user.phoneNum,
                    date_of_birth = dateOfBirthString,
                    role = user.role,
                    is_active = user.isActive
                )
                response = api.updateUserAsAdmin(uuidToUpdate, request)
            } else {
                return Result.failure(Exception("Nemate ovlasti za ažuriranje ovog korisnika."))
            }

            if (response.isSuccessful) {
                Result.success(response.body()?.message ?: "Uspješno ažurirano")
            } else {
                val errorMsg = extractErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteUser(userId: String): Result<String> {
        return try {
            val response = api.deleteUser(userId)
            if (response.isSuccessful) {
                Result.success(response.body()?.message ?: "Korisnik uspješno obrisan")
            } else {
                val errorJson = response.errorBody()?.string()
                val msg = extractErrorMessage(errorJson)
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun changePassword(recoveryToken: String, newPassword: String): Result<String> {
        return try {
            val request = ChangePasswordRequest(recoveryToken, newPassword)
            val response = api.changePassword(request)
            if (response.isSuccessful) {
                Result.success(response.body()?.message ?: "Lozinka uspješno promijenjena")
            } else {
                val errorJson = response.errorBody()?.string()
                val msg = extractErrorMessage(errorJson)
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun extractErrorMessage(json: String?): String {
        if (json.isNullOrBlank()) return "Dogodila se greška"

        return try {
            val root = JSONObject(json)

            if (root.has("errors")) {
                val arr = root.getJSONArray("errors")
                if (arr.length() > 0) {
                    val firstError = arr.getJSONObject(0)
                    firstError.getString("message")
                } else "Dogodila se greška"
            } else if (root.has("error")) {
                root.getString("error")
            } else "Dogodila se greška"
        } catch (_: Exception) {
            "Dogodila se greška"
        }
    }
}
package hr.foi.air.ws.repository

import android.content.Context
import android.util.Log
import hr.foi.air.mshop.core.models.Article
import hr.foi.air.mshop.core.models.User
import hr.foi.air.mshop.core.repository.IUserRepository
import hr.foi.air.ws.NetworkService
import hr.foi.air.ws.models.userManagement.AddUserRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject

class UserRepo : IUserRepository {
    private val api = NetworkService.accountApi

    private fun hr.foi.air.ws.models.userManagement.UserResponse.toDomainModel(): User {
        val dateInMillis = this.dateOfBirth?.let {
            try {
                java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).parse(it)?.time
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
            isAdmin = this.isAdmin
        )
    }

    override suspend fun addUser(user: User, context: Context): Result<String>{
        return try{
            val dateOfBirthString = user.dateOfBirthMillis?.let { millis ->
                java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date(millis))
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
                is_admin = user.isAdmin
            )

            val res = api.createUser(req)
            if(res.isSuccessful){
                Result.success(res.body()?.message ?: "Uspješno dodano")
            }else{
                val errorJson = res.errorBody()?.string()
                val msg = extractErrorMessage(errorJson)
                Result.failure(Exception(msg))
            }
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    override fun getAllUsers(): Flow<List<User>> = flow {
        try {
            val response = api.getUsers()
            if (response.isSuccessful) {
                val users = response.body()?.map { it.toDomainModel() } ?: emptyList()
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
            }

            else if (root.has("error")) {
                root.getString("error")
            }
            else "Dogodila se greška"
        } catch (_: Exception) {
            "Dogodila se greška"
        }
    }
}
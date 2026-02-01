package hr.foi.air.ws.repository

import hr.foi.air.ws.NetworkService
import hr.foi.air.ws.data.SessionManager
import hr.foi.air.ws.models.login.ChangePasswordRequest
import hr.foi.air.ws.models.login.GetRecoveryCodeLocationRequest
import hr.foi.air.ws.models.login.LoginRequest
import hr.foi.air.ws.models.login.LoginResponse
import hr.foi.air.ws.models.login.SetRecoveryCodeLocationRequest
import retrofit2.Response

class LoginRepo {
    private val api = NetworkService.accountApi

    suspend fun loginUser(username: String, password: String): Response<LoginResponse> {
        val response = api.loginUser(LoginRequest(username, password))

        if (response.isSuccessful && response.body() != null) {
            val body = response.body()!!
            if (body.accessToken != null) {
                SessionManager.startSession(
                    token = body.accessToken,
                    refresh = body.refreshToken
                )
            }
        }
        return response
    }

    suspend fun changePassword(request: ChangePasswordRequest) =
        api.changePassword(request)

    suspend fun saveSecurityQuestions(request: SetRecoveryCodeLocationRequest) =
        api.setRecoveryCodeLocation(request)

    suspend fun getRecoveryCodeLocation(username: String, answers: List<String>): Result<String> {
        return try {
            val request = GetRecoveryCodeLocationRequest(
                username = username,
                answer1 = answers.getOrNull(0) ?: "",
                answer2 = answers.getOrNull(1) ?: "",
                answer3 = answers.getOrNull(2) ?: ""
            )
            val response = api.getRecoveryCodeLocation(request)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.valid && !body.recoveryCodeLocation.isNullOrBlank()) {
                    Result.success(body.recoveryCodeLocation)
                } else {
                    Result.failure(Exception("Odgovori nisu točni. (Greška: 401)"))
                }
            } else {
                val message = when (response.code()) {
                    400 -> "Neispravan zahtjev."
                    401 -> "Netočni odgovori na sigurnosna pitanja."
                    404 -> "Korisnik nije pronađen."
                    else -> "Došlo je do pogreške na poslužitelju."
                }
                Result.failure(Exception("$message (Status kod: ${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Greška u komunikaciji: ${e.localizedMessage}"))
        }
    }

    fun logout() {
        SessionManager.endSession()
    }
}
package hr.foi.air.ws.repository

import hr.foi.air.ws.NetworkService
import hr.foi.air.ws.data.SessionManager
import hr.foi.air.ws.models.login.ChangePasswordRequest
import hr.foi.air.ws.models.login.LoginRequest
import hr.foi.air.ws.models.login.LoginResponse
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

    fun logout() {
        SessionManager.endSession()
    }
}
package hr.foi.air.ws.repository

import hr.foi.air.ws.NetworkService
import hr.foi.air.ws.models.login.LoginRequest

class LoginRepo {
    private val api = NetworkService.accountApi

    suspend fun loginUser(username: String, password: String) =
        api.loginUser(LoginRequest(username, password))
}
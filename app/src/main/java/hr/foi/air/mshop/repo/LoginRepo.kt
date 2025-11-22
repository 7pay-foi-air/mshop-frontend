package hr.foi.air.mshop.repo

import hr.foi.air.mshop.network.NetworkService
import hr.foi.air.mshop.network.dto.login.LoginRequest

class LoginRepo {
    private val api = NetworkService.accountApi

    suspend fun loginUser(username: String, password: String) =
        api.loginUser(LoginRequest( username, password ))
}
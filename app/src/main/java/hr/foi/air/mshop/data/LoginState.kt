package hr.foi.air.mshop.data

import hr.foi.air.ws.models.login.LoginResponse

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val response: LoginResponse) : LoginState()
    data class Error(val message: String) : LoginState()
    data class FirstLoginRequired(val recoveryToken: String) : LoginState()

    data class AccountLocked(val message: String) : LoginState()
}
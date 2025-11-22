package hr.foi.air.mshop.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.foi.air.mshop.core.data.SessionManager
import hr.foi.air.mshop.data.LoginState
import hr.foi.air.mshop.repo.LoginRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val repository = LoginRepo()

    var username by mutableStateOf("")
    var password by mutableStateOf("")

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    private val _toastMessage = MutableStateFlow("")
    val toastMessage = _toastMessage.asSharedFlow()

    var showForgottenPasswordDialog by mutableStateOf(false)
        private set

    fun login() {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            Log.d("LoginViewModel", "Attempting to login with username: $username")

            try {
                val response = repository.loginUser(username, password)
                Log.d("LoginViewModel", "Response received: Code=${response.code()}, Successful=${response.isSuccessful}")

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    Log.d("LoginViewModel", "Successful Response Body: $loginResponse")

                    if (loginResponse.error == null && loginResponse.accessToken != null) {
                        val token = loginResponse.accessToken
                        SessionManager.startSession(token)
                        Log.d("LoginViewModel", "Session started for UserID: ${SessionManager.currentUserId}")
                        _loginState.value = LoginState.Success(loginResponse)

                    } else {
                        Log.e("LoginViewModel", "API Error from response: ${loginResponse.error}")
                        _loginState.value = LoginState.Error(loginResponse.error ?: "Unknown error occurred")

                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("LoginViewModel", "Login failed: Code=${response.code()}, Message=${response.message()}, Error Body: $errorBody")
                    _loginState.value = LoginState.Error("Login failed: ${response.message()}")

                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "An unexpected error occurred", e)
                _loginState.value = LoginState.Error("An unexpected error occurred: ${e.message}")
            }
        }
    }

    fun onForgotPasswordClick() {
        showForgottenPasswordDialog = true
    }

    fun onForgotPasswordDialogDismiss() {
        showForgottenPasswordDialog = false
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }

    fun onNextClicked(onSuccess: () -> Unit) {
        if(username.isBlank()) {
            viewModelScope.launch {
                _toastMessage.emit("Unesite korisničko ime.")
            }
        } else {
            onSuccess()
        }
    }
}


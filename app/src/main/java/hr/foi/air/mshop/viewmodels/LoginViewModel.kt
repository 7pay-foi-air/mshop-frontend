package hr.foi.air.mshop.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.foi.air.ws.data.SessionManager
import hr.foi.air.mshop.data.LoginState
import hr.foi.air.ws.repository.LoginRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class LoginViewModel : ViewModel() {
    private val repository = LoginRepo()

    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var newPassword by mutableStateOf("")
    var confirmNewPassword by mutableStateOf("")

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    private val _toastMessage = MutableStateFlow("")
    val toastMessage = _toastMessage.asSharedFlow()

    var showForgottenPasswordDialog by mutableStateOf(false)
        private set

    var recoveryToken by mutableStateOf("")
    var recoveryTokenLocation by mutableStateOf("")

    fun login() {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val response = repository.loginUser(username, password)
                Log.d(
                    "LoginViewModel",
                    "Response received: Code=${response.code()}, Successful=${response.isSuccessful}"
                )

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    Log.d("LoginViewModel", "Successful Response Body: $loginResponse")

                    if (loginResponse.error == null && loginResponse.accessToken != null) {
                        val token = loginResponse.accessToken
                        SessionManager.startSession(token)

                        // LOGGING: Check what exactly the backend is sending
                        Log.d("LoginViewModel", "Access Token: $token")
                        Log.d("LoginViewModel", "Recovery Token from Response: ${loginResponse.recoveryToken}")

                        // Assign to the ViewModel property
                        recoveryToken = loginResponse.recoveryToken ?: ""

                        Log.d("LoginViewModel", "Stored recoveryToken variable: ${recoveryToken}")

                        // Use the stored property for the check
                        if (recoveryToken.isNotBlank()) {
                            Log.d("LoginViewModel", "First login detected. Navigating to setup.")
                            _loginState.value = LoginState.FirstLoginRequired(recoveryToken)
                        } else {
                            Log.d("LoginViewModel", "Normal login detected. Navigating to Home.")
                            _loginState.value = LoginState.Success(loginResponse)
                        }
                    } else {
                        _loginState.value =
                            LoginState.Error(loginResponse.error ?: "Nepoznata greška")

                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(
                        "LoginViewModel",
                        "Login failed: Code=${response.code()}, Message=${response.message()}, Error Body: $errorBody"
                    )

                    when (response.code()) {
                        400 -> _loginState.value =
                            LoginState.Error("Loš zahtjev.")

                        401 -> _loginState.value =
                            LoginState.Error("Pogrešno korisničko ime ili lozinka.")

                        else -> _loginState.value =
                            LoginState.Error("Prijava nije uspjela (Kod: ${response.code()})")

                    }
                }
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is UnknownHostException, is SocketTimeoutException, is ConnectException -> "Nije moguće uspostaviti vezu s poslužiteljem. Provjerite internetsku vezu."
                    else -> "Došlo je do neočekivane pogreške."
                }
                _loginState.value = LoginState.Error(errorMessage)
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
        newPassword = ""
        confirmNewPassword = ""
        recoveryToken = ""
        recoveryTokenLocation = ""
    }

    fun onProceedToPassword(onSuccess: () -> Unit) {
        if (username.isBlank()) {
            viewModelScope.launch {
                _toastMessage.emit("Unesite korisničko ime.")
            }
        } else {
            onSuccess()
        }
    }

    fun onProceedToRecovery(onSuccess: () -> Unit) {
        if (newPassword.isBlank() || confirmNewPassword.isBlank()) {
            viewModelScope.launch {
                _toastMessage.emit("Popunite sva polja.")
            }
        } else if (newPassword != confirmNewPassword) {
            viewModelScope.launch {
                _toastMessage.emit("Lozinke se ne podudaraju.")
            }
        } else {
            Log.d("LoginViewModel", "Stored recoveryToken variable: ${recoveryToken}")
            onSuccess()
        }
    }

    fun saveRecoveryToken(context: android.content.Context, onComplete: () -> Unit) {
        if (recoveryTokenLocation.isBlank()) {
            viewModelScope.launch {
                _toastMessage.emit("Molimo unesite gdje ste spremili kod.")
            }
            return
        }

        viewModelScope.launch {
            try {
                val data = "Storage Location: $recoveryTokenLocation"
                context.openFileOutput("recovery_info.txt", android.content.Context.MODE_PRIVATE).use {
                    it.write(data.toByteArray())
                }
                resetState()
                onComplete()
            } catch (e: Exception) {
                _toastMessage.emit("Greška pri spremanju datoteke.")
            }
        }
    }
}


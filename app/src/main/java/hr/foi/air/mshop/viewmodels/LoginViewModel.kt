package hr.foi.air.mshop.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.foi.air.mshop.data.LoginState
import hr.foi.air.ws.models.login.ChangePasswordRequest
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

    var securityQuestion1 by mutableStateOf("Koje je ime Vašeg prvog ljubimca?")
    var securityAnswer1 by mutableStateOf("")

    var securityQuestion2 by mutableStateOf("U kojem gradu ste rođeni?")
    var securityAnswer2 by mutableStateOf("")

    var securityQuestion3 by mutableStateOf("Koja je bila marka Vašeg prvog automobila?")
    var securityAnswer3 by mutableStateOf("")

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
                Log.d("LoginViewModel", "Response received: Code=${response.code()}, Successful=${response.isSuccessful}")

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    Log.d("LoginViewModel", "Successful Response Body: $loginResponse")

                    if (loginResponse.error != null) {
                        _loginState.value = LoginState.Error(loginResponse.error!!)
                        return@launch
                    }

                    recoveryToken = loginResponse.recoveryToken ?: ""

                    if (recoveryToken.isNotBlank()) {
                        _loginState.value = LoginState.FirstLoginRequired(recoveryToken)
                    } else {
                        _loginState.value = LoginState.Success(loginResponse)
                    }
                } else {
                    val message = when (response.code()) {
                        400 -> "Loš zahtjev."
                        401 -> "Pogrešno korisničko ime ili lozinka."
                        else -> "Prijava nije uspjela (Kod: ${response.code()})"
                    }
                    _loginState.value = LoginState.Error(message)
                }
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is UnknownHostException, is SocketTimeoutException, is ConnectException ->
                        "Nije moguće uspostaviti vezu s poslužiteljem."
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

    fun onProceedToRecoveryToken(onSuccess: () -> Unit) {
        if (newPassword.isBlank() || confirmNewPassword.isBlank()) {
            viewModelScope.launch { _toastMessage.emit("Popunite sva polja.") }
        } else if (newPassword != confirmNewPassword) {
            viewModelScope.launch { _toastMessage.emit("Lozinke se ne podudaraju.") }
        } else {
            onSuccess()
        }
    }

    fun onProceedToSecurityQuestions(onSuccess: () -> Unit) {
        if (recoveryTokenLocation.isBlank()) {
            viewModelScope.launch { _toastMessage.emit("Molimo unesite gdje ste spremili kod.") }
        } else {
            onSuccess()
        }
    }

    fun saveFinalAccountSetup(context: android.content.Context, onComplete: () -> Unit) {
        if (securityAnswer1.isBlank() || securityAnswer2.isBlank() || securityAnswer3.isBlank()) {
            viewModelScope.launch { _toastMessage.emit("Molimo odgovorite na sva sigurnosna pitanja.") }
            return
        }

        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val changeReq = ChangePasswordRequest(
                    newPassword = newPassword,
                    recoveryToken = recoveryToken
                )

                val response = repository.changePassword(changeReq)
                if (response.isSuccessful) {
                    val data = "Storage Location: $recoveryTokenLocation"
                    context.openFileOutput("recovery_info.txt", android.content.Context.MODE_PRIVATE).use {
                        it.write(data.toByteArray())
                    }
                    resetState()
                    onComplete()
                } else {
                    _loginState.value = LoginState.Idle
                    _toastMessage.emit(response.message() ?: "Greška pri spremanju.")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Idle
                _toastMessage.emit("Greška u komunikaciji s poslužiteljem.")
            }
        }
    }
}


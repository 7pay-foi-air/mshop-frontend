package hr.foi.air.mshop.viewmodels

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.foi.air.mshop.data.LoginState
import hr.foi.air.ws.models.login.ChangePasswordRequest
import hr.foi.air.ws.models.login.SetRecoveryCodeLocationRequest
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

    var newPasswordError by mutableStateOf<String?>(null)
    var confirmPasswordError by mutableStateOf<String?>(null)

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
                        if (loginResponse.error!!.contains("zaključan", ignoreCase = true)) {
                            _loginState.value = LoginState.AccountLocked(loginResponse.error!!)
                        } else {
                            _loginState.value = LoginState.Error(loginResponse.error!!)
                        }
                        return@launch
                    }

                    recoveryToken = loginResponse.recoveryToken ?: ""

                    if (recoveryToken.isNotBlank()) {
                        _loginState.value = LoginState.FirstLoginRequired(recoveryToken)
                    } else {
                        _loginState.value = LoginState.Success(loginResponse)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()

                    if (errorBody?.contains("zaključan", ignoreCase = true) == true) {
                        _loginState.value = LoginState.AccountLocked(
                            "Račun je zaključan. 3 puta ste neuspješno unesli lozinku."
                        )
                        return@launch
                    }

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
        newPasswordError = null
        confirmPasswordError = null
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

    private fun validatePassword(password: String): String? {
        if (password.length < 10) return "Lozinka mora imati barem 10 znakova."
        val hasLetter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }
        if (!hasLetter || !hasDigit) return "Lozinka mora sadržavati slova i brojeve."
        return null
    }

    fun onProceedToRecoveryToken(onSuccess: () -> Unit) {
        newPasswordError = validatePassword(newPassword)
        confirmPasswordError = when {
            confirmNewPassword.isBlank() -> "Molimo ponovite lozinku."
            newPassword != confirmNewPassword -> "Lozinke se ne podudaraju."
            else -> null
        }
        if (newPasswordError == null && confirmPasswordError == null) {
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

    fun saveFinalAccountSetup(context: Context, onComplete: () -> Unit) {
        if (securityAnswer1.isBlank() || securityAnswer2.isBlank() || securityAnswer3.isBlank()) {
            viewModelScope.launch { _toastMessage.emit("Molimo odgovorite na sva sigurnosna pitanja.") }
            return
        }

        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val request = SetRecoveryCodeLocationRequest(
                    username = username,
                    answer1 = securityAnswer1.trim().lowercase(),
                    answer2 = securityAnswer2.trim().lowercase(),
                    answer3 = securityAnswer3.trim().lowercase(),
                    recoveryCodeLocation = recoveryTokenLocation.trim()
                )

                val passChangeReq = ChangePasswordRequest(
                    newPassword = newPassword,
                    recoveryToken = recoveryToken
                )

                val passResponse = repository.changePassword(passChangeReq)

                if (passResponse.isSuccessful) {
                    val securityResponse = repository.saveSecurityQuestions(request)

                    if (securityResponse.isSuccessful) {
                        resetState()
                        onComplete()
                    } else {
                        _loginState.value = LoginState.Idle
                        _toastMessage.emit("Lozinka spremljena, ali greška kod sigurnosnih pitanja.")
                    }
                } else {
                    _loginState.value = LoginState.Idle
                    _toastMessage.emit(passResponse.message() ?: "Greška pri promjeni lozinke.")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Idle
                _toastMessage.emit("Greška u komunikaciji s poslužiteljem.")
            }
        }
    }
}


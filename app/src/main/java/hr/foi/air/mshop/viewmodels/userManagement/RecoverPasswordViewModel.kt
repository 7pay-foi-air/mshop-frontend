package hr.foi.air.mshop.viewmodels.userManagement

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.foi.air.mshop.core.repository.IUserRepository
import hr.foi.air.mshop.viewmodels.LoginViewModel
import hr.foi.air.ws.repository.LoginRepo
import hr.foi.air.ws.repository.UserRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecoverPasswordViewModel : ViewModel() {
    private val userRepository: IUserRepository = UserRepo()
    private val loginRepository: LoginRepo = LoginRepo()

    var step by mutableStateOf(1)
    var recoveryCode by mutableStateOf("")
    var password by mutableStateOf("")
    var repeatPassword by mutableStateOf("")
    var showDialog by mutableStateOf(false)
    var dialogTitle by mutableStateOf("")
    var dialogMessage by mutableStateOf("")
    var username by mutableStateOf("")

    var showForgotRecoveryDialog by mutableStateOf(false)

    var recoveryLocation by mutableStateOf("")

    private val _changePasswordResult = MutableStateFlow<Result<String>?>(null)
    val changePasswordResult: StateFlow<Result<String>?> = _changePasswordResult

    fun recoverPassword(username: String, recoveryToken: String, newPassword: String) {
        viewModelScope.launch {
            val result = userRepository.recoverPassword(username, recoveryToken, newPassword)
            _changePasswordResult.value = result
        }
    }

    fun fetchRecoveryLocation(loginViewModel: LoginViewModel) {
        viewModelScope.launch {
            val answers = listOf(
                loginViewModel.securityAnswer1.trim().lowercase(),
                loginViewModel.securityAnswer2.trim().lowercase(),
                loginViewModel.securityAnswer3.trim().lowercase()
            )

            val result = loginRepository.getRecoveryCodeLocation(username, answers)

            result.onSuccess { location ->
                recoveryLocation = location
                step = 4 // Move to the display screen
            }.onFailure { error ->
                dialogTitle = "Greška"
                dialogMessage = error.message ?: "Došlo je do pogreške pri dohvaćanju lokacije."
                showDialog = true
            }
        }
    }
}

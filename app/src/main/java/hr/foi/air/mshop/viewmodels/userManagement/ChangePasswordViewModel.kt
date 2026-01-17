package hr.foi.air.mshop.viewmodels.userManagement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.foi.air.mshop.core.repository.IUserRepository
import hr.foi.air.ws.repository.UserRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChangePasswordViewModel : ViewModel() {
    private val userRepository: IUserRepository = UserRepo()

    private val _changePasswordResult = MutableStateFlow<Result<String>?>(null)
    val changePasswordResult: StateFlow<Result<String>?> = _changePasswordResult

    fun changePassword(recoveryToken: String, newPassword: String) {
        viewModelScope.launch {
            val result = userRepository.changePassword(recoveryToken, newPassword)
            _changePasswordResult.value = result
        }
    }
}

package hr.foi.air.mshop.viewmodels.userManagement

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.foi.air.mshop.repo.UserRepo
import hr.foi.air.mshop.data.UIState
import hr.foi.air.ws.models.userManagement.AddUserRequest
import kotlinx.coroutines.launch

class AddUserViewModel(private val repo: UserRepo = UserRepo()): ViewModel() {
    var state by mutableStateOf(UIState())
        private set

    fun addUser(req: AddUserRequest){
        viewModelScope.launch {
            state = state.copy(loading = true, successMessage = null, errorMessage = null)

            val result = repo.addUser(req)

            state = if (result.isSuccess) {
                state.copy(loading = false, successMessage = result.getOrNull())
            } else {
                state.copy(loading = false, errorMessage = result.exceptionOrNull()?.message)
            }
        }
    }

    fun clearMessages() {
        state = state.copy(successMessage = null, errorMessage = null)
    }

}
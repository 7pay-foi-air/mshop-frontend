package hr.foi.air.mshop.viewmodels.userManagement

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.foi.air.mshop.core.models.User
import hr.foi.air.mshop.data.UIState
import hr.foi.air.ws.repository.UserRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserFormViewModel(
    private val userRepo: UserRepo = UserRepo()
) : ViewModel() {

    private val _uiState = MutableStateFlow(UIState())
    val uiState: StateFlow<UIState> = _uiState

    var firstName by mutableStateOf("")
    var lastName by mutableStateOf("")
    var username by mutableStateOf("")
    var dateOfBirth by mutableStateOf<Long?>(null)
    var address by mutableStateOf("")
    var email by mutableStateOf("")
    var phoneNum by mutableStateOf("")
    var isAdmin by mutableStateOf<Boolean?>(false)

    private var userToEdit: User? = null
    val isEditMode: Boolean
        get() = userToEdit != null

    fun initializeState(user: User?) {
        userToEdit = user
        if (user != null) {
            firstName = user.firstName
            lastName = user.lastName
            username = user.username
            dateOfBirth = user.dateOfBirthMillis
            address = user.address
            email = user.email
            phoneNum = user.phoneNum
            isAdmin = user.isAdmin
        }
    }

    fun saveUser(context: Context) {
        val role = userToEdit?.role ?: "cashier"
        val uuidOrganisation = userToEdit?.uuidOrganisation
            ?: hr.foi.air.ws.data.SessionManager.currentOrgId?.toString()
        val isAdminValue = isAdmin ?: false

        val user = User(
            uuidUser = userToEdit?.uuidUser,
            firstName = firstName.trim(),
            lastName = lastName.trim(),
            username = username.trim(),
            address = address.trim(),
            email = email.trim(),
            phoneNum = email.trim(),
            role = role,
            dateOfBirthMillis = dateOfBirth,
            uuidOrganisation = uuidOrganisation,
            isAdmin = isAdminValue
        )

        viewModelScope.launch {
            val result = if (isEditMode) {
                userRepo.updateUSer(user, context)
            } else {
                userRepo.addUser(user, context)
            }

            _uiState.value = if (result.isSuccess) {
                UIState(successMessage = result.getOrNull())
            } else {
                UIState(errorMessage = result.exceptionOrNull()?.message)
            }
        }
    }

}
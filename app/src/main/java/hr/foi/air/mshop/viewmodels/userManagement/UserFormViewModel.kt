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

        var isActive by mutableStateOf<Boolean?>(true)

        var formErrors by mutableStateOf<Map<FormField, String>>(emptyMap())
            private set
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
                isAdmin = (user.role == "admin")
                isActive = user.isActive
            }
        }

        private fun validateForm(): Boolean {
            val errors = mutableMapOf<FormField, String>()

            if (firstName.isBlank()) {
                errors[FormField.FIRST_NAME] = "Ime je obavezno."
            }
            if (lastName.isBlank()) {
                errors[FormField.LAST_NAME] = "Prezime je obavezno."
            }
            if (username.isBlank()) {
                errors[FormField.USERNAME] = "Korisničko ime je obavezno."
            }
            if (dateOfBirth == null) {
                errors[FormField.DATE] = "Datum rođenja je obavezan."
            }
            if (address.isBlank()) {
                errors[FormField.ADDRESS] = "Adresa je obavezna."
            }
            if (email.isBlank()) {
                errors[FormField.EMAIL] = "Email je obavezan."
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                errors[FormField.EMAIL] = "Neispravan format emaila."
            }
            if (phoneNum.isBlank()) {
                errors[FormField.PHONE_NUM] = "Broj telefona je obavezan."
            }

            formErrors = errors
            return errors.isEmpty()
        }

        fun fetchAndInitializeLoggedInUser() {
            val loggedInUserId = hr.foi.air.ws.data.SessionManager.currentUserId
            if (loggedInUserId == null) {
                _uiState.value = UIState(errorMessage = "Korisnik nije prijavljen.")
                return
            }

            viewModelScope.launch {
                _uiState.value = UIState(loading = true)
                val result = userRepo.getUserById(loggedInUserId)
                if (result.isSuccess) {
                    val user = result.getOrNull()
                    initializeState(user)
                    _uiState.value = UIState(loading = false)
                } else {
                    _uiState.value = UIState(
                        loading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Greška pri dohvaćanju profila."
                    )
                }
            }
        }

        fun saveUser(context: Context) {
            if (!validateForm()) {
                return
            }

            val uuidOrganisation = userToEdit?.uuidOrganisation
                ?: hr.foi.air.ws.data.SessionManager.currentOrgId
            val isAdminValue = isAdmin ?: false
            val isActiveValue = isActive ?: true

            val user = User(
                uuidUser = userToEdit?.uuidUser,
                firstName = firstName.trim(),
                lastName = lastName.trim(),
                username = username.trim(),
                address = address.trim(),
                email = email.trim(),
                phoneNum = phoneNum.trim(),
                role = if (isAdminValue) "admin" else (userToEdit?.role ?: "cashier"),
                dateOfBirthMillis = dateOfBirth,
                uuidOrganisation = uuidOrganisation,
                isActive = isActiveValue
            )

            viewModelScope.launch {
                val result = if (isEditMode) {
                    userRepo.updateUser(user, context)
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

        fun clearMessages() {
            _uiState.value = _uiState.value.copy(successMessage = null, errorMessage = null)
        }
    }

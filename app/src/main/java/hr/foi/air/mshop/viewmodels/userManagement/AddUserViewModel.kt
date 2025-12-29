package hr.foi.air.mshop.viewmodels.userManagement

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.foi.air.mshop.core.models.User
import hr.foi.air.ws.data.SessionManager
import hr.foi.air.ws.repository.UserRepo
import hr.foi.air.ws.models.userManagement.AddUserRequest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

enum class FocusedField {
    FIRST_NAME, LAST_NAME, USERNAME, ADDRESS, EMAIL, PHONE, NONE
}
data class AddUserUIState(
    val firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val address: String = "",
    val email: String = "",
    val phoneNum: String = "",
    val isAdmin: Boolean = false,
    val dateOfBirthMillis: Long? = null,

    val fnVisited: Boolean = false,
    val lnVisited: Boolean = false,
    val unVisited: Boolean = false,
    val adVisited: Boolean = false,
    val emVisited: Boolean = false,
    val phVisited: Boolean = false,
    val dobVisited: Boolean = false,

    val showDatePicker: Boolean = false,
    val focusedField: FocusedField = FocusedField.NONE,

    val loading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
) {
    private val emailRegex: Pattern by lazy { Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$") }
    private val phoneRegex: Pattern by lazy { Pattern.compile("^((\\+\\d{1,3})?\\s?\\d{6,14})$") }

    val isFirstNameEmpty: Boolean get() = firstName.isBlank()
    val isLastNameEmpty: Boolean get() = lastName.isBlank()
    val isUsernameEmpty: Boolean get() = username.isBlank()
    val isAddressEmpty: Boolean get() = address.isBlank()
    val isDobMissing: Boolean get() = dateOfBirthMillis == null
    val isEmailFormatInvalid: Boolean get() = email.isNotBlank() && !emailRegex.matcher(email).matches()
    val isPhoneFormatInvalid: Boolean get() = phoneNum.isNotBlank() && !phoneRegex.matcher(phoneNum.replace(" ", "")).matches()

    val isFormValid: Boolean get() =
        !isFirstNameEmpty &&
                !isLastNameEmpty &&
                !isUsernameEmpty &&
                !isAddressEmpty &&
                email.isNotBlank() && !isEmailFormatInvalid &&
                phoneNum.isNotBlank() && !isPhoneFormatInvalid &&
                !isDobMissing

    val dobText: String get() = dateOfBirthMillis?.let {
        SimpleDateFormat("dd.MM.yyyy.", Locale.getDefault()).format(Date(it))
    } ?: ""

    val dobBackend: String get() = dateOfBirthMillis?.let {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it))
    } ?: ""
}

class AddUserViewModel(
    private val repo: UserRepo = UserRepo()
): ViewModel() {
    var uiState by mutableStateOf(AddUserUIState())
        private set

    fun onFirstNameChange(value: String) {
        uiState = uiState.copy(firstName = value, successMessage = null, errorMessage = null)
    }

    fun onLastNameChange(value: String) {
        uiState = uiState.copy(lastName = value, successMessage = null, errorMessage = null)
    }

    fun onUsernameChange(value: String) {
        uiState = uiState.copy(username = value, successMessage = null, errorMessage = null)
    }

    fun onAddressChange(value: String) {
        uiState = uiState.copy(address = value, successMessage = null, errorMessage = null)
    }

    fun onEmailChange(value: String) {
        uiState = uiState.copy(email = value, successMessage = null, errorMessage = null)
    }

    fun onPhoneNumChange(value: String) {
        uiState = uiState.copy(phoneNum = value, successMessage = null, errorMessage = null)
    }

    fun onDateOfBirthChange(millis: Long?) {
        uiState = uiState.copy(dateOfBirthMillis = millis, dobVisited = true, showDatePicker = false, successMessage = null, errorMessage = null)
    }

    fun onIsAdminChange(value: Boolean) {
        uiState = uiState.copy(isAdmin = value)
    }

    fun onFocusChange(field: FocusedField, isFocused: Boolean) {
        val previouslyFocusedField = uiState.focusedField

        if (isFocused) {
            uiState = uiState.copy(focusedField = field)
        } else {
            if (previouslyFocusedField == field) {
                uiState = when (field) {
                    FocusedField.FIRST_NAME -> uiState.copy(fnVisited = true, focusedField = FocusedField.NONE)
                    FocusedField.LAST_NAME -> uiState.copy(lnVisited = true, focusedField = FocusedField.NONE)
                    FocusedField.USERNAME -> uiState.copy(unVisited = true, focusedField = FocusedField.NONE)
                    FocusedField.ADDRESS -> uiState.copy(adVisited = true, focusedField = FocusedField.NONE)
                    FocusedField.EMAIL -> uiState.copy(emVisited = true, focusedField = FocusedField.NONE)
                    FocusedField.PHONE -> uiState.copy(phVisited = true, focusedField = FocusedField.NONE)
                    FocusedField.NONE -> uiState
                }
            }
        }
    }

    fun onOpenDatePicker() {
        uiState = uiState.copy(showDatePicker = true)
    }

    fun onDatePickerDismissed() {
        uiState = uiState.copy(dobVisited = true, showDatePicker = false)
    }

    fun onMessageShown() {
        uiState = uiState.copy(successMessage = null, errorMessage = null)
    }

    fun addUser(context: android.content.Context) {
        if (!uiState.isFormValid) return

        viewModelScope.launch {
            uiState = uiState.copy(loading = true, successMessage = null, errorMessage = null)

            val userToCreate = User(
                firstName = uiState.firstName.trim(),
                lastName = uiState.lastName.trim(),
                username = uiState.username.trim(),
                address = uiState.address.trim(),
                email = uiState.email.trim(),
                phoneNum = uiState.phoneNum.trim(),
                role = "cashier",
                dateOfBirthMillis = uiState.dateOfBirthMillis,
                uuidOrganisation = SessionManager.currentOrgId.toString(),
                isAdmin = uiState.isAdmin
            )

            val result = repo.addUser(userToCreate, context)

            uiState = if (result.isSuccess) {
                uiState.copy(loading = false, successMessage = result.getOrNull())
            } else {
                uiState.copy(loading = false, errorMessage = result.exceptionOrNull()?.message)
            }
        }
    }
}
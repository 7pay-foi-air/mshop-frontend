package hr.foi.air.mshop.navigation.components.userManagement

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.foi.air.mshop.ui.components.DateFieldUnderLabel
import hr.foi.air.mshop.ui.components.buttons.StyledButton
import hr.foi.air.mshop.ui.components.textFields.UnderLabelTextField
import hr.foi.air.mshop.viewmodels.userManagement.AddUserViewModel
import hr.foi.air.mshop.viewmodels.userManagement.FocusedField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserPage(
    onUserAdded: (() -> Unit)? = null
) {
    val viewModel: AddUserViewModel = viewModel()
    val uiState = viewModel.uiState
    val context = LocalContext.current

    if (uiState.showDatePicker) {
        val dateState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.dateOfBirthMillis ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = {
                viewModel.onDatePickerDismissed()
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onDateOfBirthChange(dateState.selectedDateMillis)
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.onDatePickerDismissed()
                }) { Text("Odustani") }
            }
        ) { DatePicker(state = dateState) }
    }

    LaunchedEffect(uiState.successMessage, uiState.errorMessage) {
        if (uiState.successMessage != null) {
            Toast.makeText(context, uiState.successMessage, Toast.LENGTH_LONG).show()
            onUserAdded?.invoke()
            viewModel.onMessageShown()
        }
        if (uiState.errorMessage != null) {
            Toast.makeText(context, uiState.errorMessage, Toast.LENGTH_LONG).show()
            viewModel.onMessageShown()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            "mShop",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 4.dp)
        )

        Text(
            "Unos zaposlenika",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        UnderLabelTextField(
            caption = "Ime",
            value = uiState.firstName,
            onValueChange = viewModel::onFirstNameChange,
            isError = uiState.fnVisited && uiState.isFirstNameEmpty,
            errorText = if (uiState.fnVisited && uiState.isFirstNameEmpty) "Obavezno polje" else null,
            modifier = Modifier.onFocusChanged { focusState ->
                viewModel.onFocusChange(FocusedField.FIRST_NAME, focusState.isFocused)
            }
        )
        Spacer(Modifier.height(8.dp))

        UnderLabelTextField(
            caption = "Prezime",
            value = uiState.lastName,
            onValueChange = viewModel::onLastNameChange,
            isError = uiState.lnVisited && uiState.isLastNameEmpty,
            errorText = if (uiState.lnVisited && uiState.isLastNameEmpty) "Obavezno polje" else null,
            modifier = Modifier.onFocusChanged { focusState ->
                viewModel.onFocusChange(FocusedField.LAST_NAME, focusState.isFocused)
            }
        )
        Spacer(Modifier.height(8.dp))

        UnderLabelTextField(
            caption = "Korisničko ime",
            value = uiState.username,
            onValueChange = viewModel::onUsernameChange,
            isError = uiState.unVisited && uiState.isUsernameEmpty,
            errorText = if (uiState.unVisited && uiState.isUsernameEmpty) "Obavezno polje" else null,
            modifier = Modifier.onFocusChanged { focusState ->
                viewModel.onFocusChange(FocusedField.USERNAME, focusState.isFocused)
            }
        )
        Spacer(Modifier.height(8.dp))

        DateFieldUnderLabel(
            caption = "Datum rođenja",
            value = uiState.dobText,
            onOpenPicker = viewModel::onOpenDatePicker,
            isError = uiState.dobVisited && uiState.isDobMissing,
            errorText = if (uiState.dobVisited && uiState.isDobMissing) "Obavezno polje" else null
        )
        Spacer(Modifier.height(8.dp))

        UnderLabelTextField(
            caption = "Adresa",
            value = uiState.address,
            onValueChange = viewModel::onAddressChange,
            isError = uiState.adVisited && uiState.isAddressEmpty,
            errorText = if (uiState.adVisited && uiState.isAddressEmpty) "Obavezno polje" else null,
            modifier = Modifier.onFocusChanged { focusState ->
                viewModel.onFocusChange(FocusedField.ADDRESS, focusState.isFocused)
            }
        )
        Spacer(Modifier.height(8.dp))

        UnderLabelTextField(
            caption = "E-mail",
            value = uiState.email,
            onValueChange = viewModel::onEmailChange,
            placeholder = "primjer@domena.com",
            isError = uiState.emVisited && (uiState.email.isBlank() || uiState.isEmailFormatInvalid),
            errorText = when {
                uiState.emVisited && uiState.email.isBlank() -> "Obavezno polje"
                uiState.emVisited && uiState.isEmailFormatInvalid -> "Neispravan format e-mail adrese"
                else -> null
            },
            modifier = Modifier.onFocusChanged { focusState ->
                viewModel.onFocusChange(FocusedField.EMAIL, focusState.isFocused)
            }
        )
        Spacer(Modifier.height(8.dp))

        UnderLabelTextField(
            caption = "Broj telefona",
            value = uiState.phoneNum,
            onValueChange = viewModel::onPhoneNumChange,
            placeholder = "+385 98 123 4567",
            isError = uiState.phVisited && (uiState.phoneNum.isBlank() || uiState.isPhoneFormatInvalid),
            errorText = when {
                uiState.phVisited && uiState.phoneNum.isBlank() -> "Obavezno polje"
                uiState.phVisited && uiState.isPhoneFormatInvalid -> "Neispravan broj telefona"
                else -> null
            },
            modifier = Modifier.onFocusChanged { focusState ->
                viewModel.onFocusChange(FocusedField.PHONE, focusState.isFocused)
            }
        )
        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = uiState.isAdmin,
                onCheckedChange = viewModel::onIsAdminChange
            )
            Text(
                "Admin",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        if (uiState.loading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
        }

        StyledButton(
            label = if (uiState.loading) "Spremanje..." else "Dodaj",
            enabled = uiState.isFormValid && !uiState.loading,
            onClick = viewModel::addUser,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

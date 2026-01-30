package hr.foi.air.mshop.navigation.components.userManagement

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.foi.air.mshop.ui.components.DateFieldUnderLabel
import hr.foi.air.mshop.ui.components.buttons.StyledButton
import hr.foi.air.mshop.ui.components.textFields.UnderLabelTextField
import hr.foi.air.mshop.ui.theme.Dimens
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
            onDismissRequest = { viewModel.onDatePickerDismissed() },
            confirmButton = {
                TextButton(onClick = { viewModel.onDateOfBirthChange(dateState.selectedDateMillis) }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onDatePickerDismissed() }) { Text("Odustani") }
            }
        ) { DatePicker(
            state = dateState,
            colors = DatePickerDefaults.colors(
                selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
                todayDateBorderColor = MaterialTheme.colorScheme.primary
            )
        )
        }
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
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = Dimens.lg, vertical = Dimens.md)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "mShop",
            style = MaterialTheme.typography.displayLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.lg, bottom = Dimens.xs)
        )

        Text(
            text = "Unos zaposlenika",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = Dimens.lg)
        )

        UnderLabelTextField(
            caption = "Ime",
            value = uiState.firstName,
            onValueChange = viewModel::onFirstNameChange,
            isError = uiState.fnVisited && uiState.isFirstNameEmpty,
            errorText = if (uiState.fnVisited && uiState.isFirstNameEmpty) "Obavezno polje" else null,
            modifier = Modifier.onFocusChanged { f ->
                viewModel.onFocusChange(FocusedField.FIRST_NAME, f.isFocused)
            }
        )
        Spacer(Modifier.height(Dimens.sm))

        UnderLabelTextField(
            caption = "Prezime",
            value = uiState.lastName,
            onValueChange = viewModel::onLastNameChange,
            isError = uiState.lnVisited && uiState.isLastNameEmpty,
            errorText = if (uiState.lnVisited && uiState.isLastNameEmpty) "Obavezno polje" else null,
            modifier = Modifier.onFocusChanged { f ->
                viewModel.onFocusChange(FocusedField.LAST_NAME, f.isFocused)
            }
        )
        Spacer(Modifier.height(Dimens.sm))

        UnderLabelTextField(
            caption = "Korisničko ime",
            value = uiState.username,
            onValueChange = viewModel::onUsernameChange,
            isError = uiState.unVisited && uiState.isUsernameEmpty,
            errorText = if (uiState.unVisited && uiState.isUsernameEmpty) "Obavezno polje" else null,
            modifier = Modifier.onFocusChanged { f ->
                viewModel.onFocusChange(FocusedField.USERNAME, f.isFocused)
            }
        )
        Spacer(Modifier.height(Dimens.sm))

        DateFieldUnderLabel(
            caption = "Datum rođenja",
            value = uiState.dobText,
            onOpenPicker = viewModel::onOpenDatePicker,
            isError = uiState.dobVisited && uiState.isDobMissing,
            errorText = if (uiState.dobVisited && uiState.isDobMissing) "Obavezno polje" else null
        )
        Spacer(Modifier.height(Dimens.sm))

        UnderLabelTextField(
            caption = "Adresa",
            value = uiState.address,
            onValueChange = viewModel::onAddressChange,
            isError = uiState.adVisited && uiState.isAddressEmpty,
            errorText = if (uiState.adVisited && uiState.isAddressEmpty) "Obavezno polje" else null,
            modifier = Modifier.onFocusChanged { f ->
                viewModel.onFocusChange(FocusedField.ADDRESS, f.isFocused)
            }
        )
        Spacer(Modifier.height(Dimens.sm))

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
            modifier = Modifier.onFocusChanged { f ->
                viewModel.onFocusChange(FocusedField.EMAIL, f.isFocused)
            }
        )
        Spacer(Modifier.height(Dimens.sm))

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
            modifier = Modifier.onFocusChanged { f ->
                viewModel.onFocusChange(FocusedField.PHONE, f.isFocused)
            }
        )

        Spacer(Modifier.height(Dimens.lg))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.xs)
        ) {
            Checkbox(
                checked = uiState.isAdmin,
                onCheckedChange = viewModel::onIsAdminChange
            )
            Text(
                text = "Admin",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(start = Dimens.xs)
            )
        }

        Spacer(Modifier.height(Dimens.lg))

        if (uiState.loading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(Dimens.sm))
        }

        StyledButton(
            label = if (uiState.loading) "Spremanje..." else "Dodaj",
            enabled = uiState.isFormValid && !uiState.loading,
            onClick = { viewModel.addUser(context) },
            modifier = Modifier.padding(top = Dimens.md)
        )

        Spacer(Modifier.height(Dimens.lg))
    }
}

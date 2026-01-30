package hr.foi.air.mshop.navigation.components.userManagement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import hr.foi.air.mshop.core.models.User
import hr.foi.air.mshop.ui.components.buttons.StyledButton
import hr.foi.air.mshop.ui.components.textFields.UnderLabelTextField
import hr.foi.air.mshop.ui.theme.Dimens
import hr.foi.air.mshop.viewmodels.userManagement.FormField
import hr.foi.air.mshop.viewmodels.userManagement.UserFormViewModel
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun UserFormPage(
    userToEdit: User? = null,
    viewModel: UserFormViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    isProfilePage: Boolean = false
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = userToEdit) {
        viewModel.initializeState(userToEdit)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
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
            text = if (viewModel.isEditMode) "Ažuriranje korisnika" else "Dodavanje novog korisnika",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Dimens.lg)
        )

        val firstNameError = viewModel.formErrors[FormField.FIRST_NAME]
        UnderLabelTextField(
            caption = "Ime",
            value = viewModel.firstName,
            onValueChange = { viewModel.firstName = it },
            isError = firstNameError != null,
            errorText = firstNameError
        )
        Spacer(Modifier.height(Dimens.sm))

        val lastNameError = viewModel.formErrors[FormField.LAST_NAME]
        UnderLabelTextField(
            caption = "Prezime",
            value = viewModel.lastName,
            onValueChange = { viewModel.lastName = it },
            isError = lastNameError != null,
            errorText = lastNameError
        )
        Spacer(Modifier.height(Dimens.sm))

        val usernameError = viewModel.formErrors[FormField.USERNAME]
        UnderLabelTextField(
            caption = "Korisničko ime",
            value = viewModel.username,
            onValueChange = { viewModel.username = it },
            isError = usernameError != null,
            errorText = usernameError,
            enabled = !isProfilePage
        )
        Spacer(Modifier.height(Dimens.sm))

        val dateOfBirthError = viewModel.formErrors[FormField.DATE]
        val formattedDate = remember(viewModel.dateOfBirth) {
            viewModel.dateOfBirth?.let { millis ->
                val sdf = SimpleDateFormat("dd.MM.yyyy.", java.util.Locale.getDefault())
                sdf.format(Date(millis))
            } ?: ""
        }

        var showDatePicker by remember { mutableStateOf(false) }

        UnderLabelTextField(
            caption = "Datum rođenja",
            value = formattedDate,
            onValueChange = { },
            isError = dateOfBirthError != null,
            errorText = dateOfBirthError,
            enabled = false,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = "Odaberi datum")
                }
            }
        )

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = viewModel.dateOfBirth,
                initialDisplayMode = DisplayMode.Input
            )

            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    Button(onClick = {
                        viewModel.dateOfBirth = datePickerState.selectedDateMillis
                        showDatePicker = false
                    }) { Text("Odaberi") }
                },
                dismissButton = {
                    Button(onClick = { showDatePicker = false }) { Text("Odustani") }
                }
            ) { DatePicker(
                    state = datePickerState,
                    colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                    selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
                    todayDateBorderColor = MaterialTheme.colorScheme.primary
            )) }
        }

        Spacer(Modifier.height(Dimens.sm))

        val addressError = viewModel.formErrors[FormField.ADDRESS]
        UnderLabelTextField(
            caption = "Adresa",
            value = viewModel.address,
            onValueChange = { viewModel.address = it },
            isError = addressError != null,
            errorText = addressError
        )
        Spacer(Modifier.height(Dimens.sm))

        val emailError = viewModel.formErrors[FormField.EMAIL]
        UnderLabelTextField(
            caption = "E-mail",
            value = viewModel.email,
            onValueChange = { viewModel.email = it },
            isError = emailError != null,
            errorText = emailError
        )
        Spacer(Modifier.height(Dimens.sm))

        val phoneNumError = viewModel.formErrors[FormField.PHONE_NUM]
        UnderLabelTextField(
            caption = "Broj telefona",
            value = viewModel.phoneNum,
            onValueChange = { viewModel.phoneNum = it },
            isError = phoneNumError != null,
            errorText = phoneNumError
        )

        Spacer(Modifier.height(Dimens.lg))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = viewModel.isAdmin == true,
                onCheckedChange = { viewModel.isAdmin = it },
                enabled = !isProfilePage
            )
            Text(
                text = "Admin",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(start = Dimens.xs)
            )
        }

        Spacer(Modifier.height(Dimens.xl))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (viewModel.isEditMode) {
                StyledButton(
                    label = "ODUSTANI",
                    enabled = true,
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                )
            }

            StyledButton(
                label = if (viewModel.isEditMode) "SPREMI" else "DODAJ",
                onClick = {
                    viewModel.saveUser(context)
                    onSubmit()
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(Dimens.lg))
    }
}

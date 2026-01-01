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
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.foi.air.mshop.core.models.User
import hr.foi.air.mshop.ui.components.buttons.StyledButton
import hr.foi.air.mshop.ui.components.textFields.UnderLabelTextField
import hr.foi.air.mshop.viewmodels.userManagement.FormField
import hr.foi.air.mshop.viewmodels.userManagement.UserFormViewModel
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.text.format

@Composable
fun UserFormPage(
    userToEdit: User? = null,
    viewModel: UserFormViewModel = viewModel(),
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
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
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
                if (viewModel.isEditMode) "Ažuriranje korisnika" else "Dodavanje novog korisnika",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            val firstNameError = viewModel.formErrors[FormField.FIRST_NAME]

            UnderLabelTextField(
                caption = "Ime",
                value = viewModel.firstName,
                onValueChange = { viewModel.firstName = it },
                isError = firstNameError != null,
                errorText = firstNameError
            )

            val lastNameError = viewModel.formErrors[FormField.LAST_NAME]

            UnderLabelTextField(
                caption = "Prezime",
                value = viewModel.lastName,
                onValueChange = { viewModel.lastName = it },
                isError = lastNameError != null,
                errorText = lastNameError
            )

            val usernameError = viewModel.formErrors[FormField.USERNAME]

            UnderLabelTextField(
                caption = "Korisničko ime",
                value = viewModel.username,
                onValueChange = { viewModel.username = it },
                isError = usernameError != null,
                errorText = usernameError,
                enabled = !isProfilePage
            )

            val dateOfBirthError = viewModel.formErrors[FormField.DATE]

            val formattedDate = remember(viewModel.dateOfBirth) {
                viewModel.dateOfBirth?.let { millis ->
                    val sdf = SimpleDateFormat(
                        "dd.MM.yyyy.",
                        java.util.Locale.getDefault())
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
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Odaberi datum"
                        )
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
                        Button(
                            onClick = {
                                viewModel.dateOfBirth = datePickerState.selectedDateMillis
                                showDatePicker = false
                            }
                        ) {
                            Text("Odaberi")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showDatePicker = false }) {
                            Text("Odustani")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            val addressError = viewModel.formErrors[FormField.ADDRESS]

            UnderLabelTextField(
                caption = "Adresa",
                value = viewModel.address,
                onValueChange = { viewModel.address = it },
                isError = addressError != null,
                errorText = addressError
            )

            val emailError = viewModel.formErrors[FormField.EMAIL]

            UnderLabelTextField(
                caption = "E-mail",
                value = viewModel.email,
                onValueChange = { viewModel.email = it },
                isError = emailError != null,
                errorText = emailError
            )

            val phoneNumError = viewModel.formErrors[FormField.PHONE_NUM]

            UnderLabelTextField(
                caption = "Broj telefona",
                value = viewModel.phoneNum,
                onValueChange = { viewModel.phoneNum = it },
                isError = phoneNumError != null,
                errorText = phoneNumError
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = viewModel.isAdmin == true,
                    onCheckedChange = { viewModel.isAdmin = it },
                    enabled = !isProfilePage
                )
                Text(
                    "Admin",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.padding(start = 16.dp)) {

                    StyledButton(
                        label = if (viewModel.isEditMode) "SPREMI" else "DODAJ",
                        onClick = {
                            viewModel.saveUser(context)
                            onSubmit()
                        }
                    )

                    if (viewModel.isEditMode) {
                        Spacer(Modifier.height(8.dp))
                        StyledButton(
                            label = "ODUSTANI",
                            enabled = true,
                            onClick = onCancel
                        )
                    }
                }
            }
        }
    }
}
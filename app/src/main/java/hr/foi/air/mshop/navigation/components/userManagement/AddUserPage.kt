package hr.foi.air.mshop.navigation.components.userManagement

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import hr.foi.air.mshop.ui.components.DateFieldUnderLabel
import hr.foi.air.mshop.ui.components.buttons.StyledButton
import hr.foi.air.mshop.ui.components.textFields.UnderLabelTextField
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserPage() {

    var firstName by remember { mutableStateOf("") }
    var lastName  by remember { mutableStateOf("") }
    var username  by remember { mutableStateOf("") }
    var address   by remember { mutableStateOf("") }
    var email     by remember { mutableStateOf("") }
    var phoneNum  by remember { mutableStateOf("") }
    var isAdmin   by remember { mutableStateOf(false) }

    // visited + hadFocus po polju
    var fnVisited by remember { mutableStateOf(false) };
    var fnHadFocus by remember { mutableStateOf(false) }

    var lnVisited by remember { mutableStateOf(false) };
    var lnHadFocus by remember { mutableStateOf(false) }

    var unVisited by remember { mutableStateOf(false) };
    var unHadFocus by remember { mutableStateOf(false) }

    var adVisited by remember { mutableStateOf(false) };
    var adHadFocus by remember { mutableStateOf(false) }

    var emVisited by remember { mutableStateOf(false) };
    var emHadFocus by remember { mutableStateOf(false) }

    var phVisited by remember { mutableStateOf(false) };
    var phHadFocus by remember { mutableStateOf(false) }

    var dobVisited by remember { mutableStateOf(false) } // za date koristimo zatvaranje pickera - dob - dateOfBirth

    // DOB
    var dateOfBirthMillis by remember { mutableStateOf<Long?>(null) }
    val dobText = remember(dateOfBirthMillis) {
        dateOfBirthMillis?.let {
            SimpleDateFormat("dd.MM.yyyy.", Locale.getDefault()).format(Date(it))
        } ?: ""
    }
    var showDatePicker by remember { mutableStateOf(false) }
    if (showDatePicker) {
        val dateState = rememberDatePickerState(initialSelectedDateMillis = dateOfBirthMillis ?: System.currentTimeMillis())
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false; dobVisited = true },
            confirmButton = {
                TextButton(onClick = {
                    dateOfBirthMillis = dateState.selectedDateMillis
                    showDatePicker = false
                    dobVisited = true
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false; dobVisited = true }) { Text("Odustani") } }
        ) { DatePicker(state = dateState) }
    }

    // regexi
    val emailRegex = remember { Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$") }
    val phoneRegex = remember { Pattern.compile("^((\\+\\d{1,3})?\\s?\\d{6,14})$") }

    // provjere
    val firstNameEmpty = firstName.isBlank()
    val lastNameEmpty  = lastName.isBlank()
    val usernameEmpty  = username.isBlank()
    val addressEmpty   = address.isBlank()
    val dobMissing     = dateOfBirthMillis == null

    val emailFormatInvalid = email.isNotBlank() && !emailRegex.matcher(email).matches()
    val phoneFormatInvalid = phoneNum.isNotBlank() && !phoneRegex.matcher(phoneNum.replace(" ", "")).matches()

    val allValid =
        firstName.isNotBlank() &&
        lastName.isNotBlank()  &&
        username.isNotBlank()  &&
        address.isNotBlank()   &&
        email.isNotBlank()     && !emailFormatInvalid &&
        phoneNum.isNotBlank()  && !phoneFormatInvalid &&
        !dobMissing

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            "mShop",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 4.dp)
        )

        Text(
            "Unos zaposlenika",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        UnderLabelTextField(
            caption = "Ime",
            value = firstName,
            onValueChange = { firstName = it },
            placeholder = "",
            isError = fnVisited && firstNameEmpty,
            errorText = if (fnVisited && firstNameEmpty) "Obavezno polje" else null,
            modifier = Modifier.onFocusChanged { f ->
                if (f.isFocused) fnHadFocus = true
                if (!f.isFocused && fnHadFocus) fnVisited = true
            }
        )
        Spacer(Modifier.height(8.dp))

        UnderLabelTextField(
            caption = "Prezime",
            value = lastName,
            onValueChange = { lastName = it },
            placeholder = "",
            isError = lnVisited && lastNameEmpty,
            errorText = if (lnVisited && lastNameEmpty) "Obavezno polje" else null,
            modifier = Modifier.onFocusChanged { f ->
                if (f.isFocused) lnHadFocus = true
                if (!f.isFocused && lnHadFocus) lnVisited = true
            }
        )
        Spacer(Modifier.height(8.dp))

        UnderLabelTextField(
            caption = "Korisničko ime",
            value = username,
            onValueChange = { username = it },
            placeholder = "",
            isError = unVisited && usernameEmpty,
            errorText = if (unVisited && usernameEmpty) "Obavezno polje" else null,
            modifier = Modifier.onFocusChanged { f ->
                if (f.isFocused) unHadFocus = true
                if (!f.isFocused && unHadFocus) unVisited = true
            }
        )
        Spacer(Modifier.height(8.dp))

        DateFieldUnderLabel(
            caption = "Datum rođenja",
            value = dobText,
            onOpenPicker = { showDatePicker = true },
            isError = dobVisited && dobMissing,
            errorText = if (dobVisited && dobMissing) "Obavezno polje" else null
        )
        Spacer(Modifier.height(8.dp))

        UnderLabelTextField(
            caption = "Adresa",
            value = address,
            onValueChange = { address = it },
            placeholder = "",
            isError = adVisited && addressEmpty,
            errorText = if (adVisited && addressEmpty) "Obavezno polje" else null,
            modifier = Modifier.onFocusChanged { f ->
                if (f.isFocused) adHadFocus = true
                if (!f.isFocused && adHadFocus) adVisited = true
            }
        )
        Spacer(Modifier.height(8.dp))

        UnderLabelTextField(
            caption = "E-mail",
            value = email,
            onValueChange = { email = it },
            placeholder = "primjer@domena.com",
            isError = emVisited && (email.isBlank() || emailFormatInvalid),
            errorText = when {
                emVisited && email.isBlank()     -> "Obavezno polje"
                emVisited && emailFormatInvalid  -> "Neispravan format e-mail adrese"
                else -> null
            },
            modifier = Modifier.onFocusChanged { f ->
                if (f.isFocused) emHadFocus = true
                if (!f.isFocused && emHadFocus) emVisited = true
            }
        )
        Spacer(Modifier.height(8.dp))

        UnderLabelTextField(
            caption = "Broj telefona",
            value = phoneNum,
            onValueChange = { phoneNum = it },
            placeholder = "+385 98 123 4567",
            isError = phVisited && (phoneNum.isBlank() || phoneFormatInvalid),
            errorText = when {
                phVisited && phoneNum.isBlank()   -> "Obavezno polje"
                phVisited && phoneFormatInvalid   -> "Neispravan broj telefona"
                else -> null
            },
            modifier = Modifier.onFocusChanged { f ->
                if (f.isFocused) phHadFocus = true
                if (!f.isFocused && phHadFocus) phVisited = true
            }
        )
        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isAdmin, onCheckedChange = { isAdmin = it })
            Text("Admin", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(start = 4.dp))
        }

        Spacer(Modifier.height(16.dp))

        StyledButton(
            label = "Dodaj",
            enabled = allValid,
            onClick = { /* submit */ },
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

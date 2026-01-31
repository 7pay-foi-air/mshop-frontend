package hr.foi.air.mshop.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.foi.air.mshop.ui.components.DialogMessage
import hr.foi.air.mshop.ui.components.buttons.NextArrow
import hr.foi.air.mshop.ui.components.textFields.UnderLabelPasswordField
import hr.foi.air.mshop.ui.components.textFields.UnderLabelTextField
import hr.foi.air.mshop.utils.AppMessageManager
import hr.foi.air.mshop.utils.AppMessageType
import hr.foi.air.mshop.viewmodels.userManagement.RecoverPasswordViewModel

@Composable
fun ChangePasswordScreen(viewModel: RecoverPasswordViewModel = viewModel(), initialUsername: String = "") {
    var step by remember { mutableStateOf(1) }
    var recoveryCode by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogMessage by remember { mutableStateOf("") }
    var username by remember { mutableStateOf(initialUsername) }
    val changePasswordResult by viewModel.changePasswordResult.collectAsState()
    val context = LocalContext.current

    if (showDialog) {
        DialogMessage(
            title = dialogTitle,
            message = dialogMessage,
            onDismiss = { showDialog = false },
            visible = showDialog,
            confirmText = "U redu",
            dismissText = "Izadi",
            onConfirm = { showDialog = false }
        )
    }

    LaunchedEffect(changePasswordResult) {
        changePasswordResult?.let {
            if (it.isSuccess) {
                dialogTitle = "Uspjeh"
                dialogMessage = it.getOrNull() ?: "Lozinka uspješno promijenjena."
                showDialog = true
            } else {
                val errorMessage = it.exceptionOrNull()?.message ?: "Dogodila se greška."
                AppMessageManager.show(errorMessage, AppMessageType.ERROR)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (step == 1) {
            Text(text = "Promjena lozinke")
            Spacer(modifier = Modifier.height(16.dp))
            UnderLabelTextField(
                value = username,
                onValueChange = { username = it },
                caption = "Korisničko ime",
                placeholder = "Unesite korisničko ime",
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default,
                isError = false,
                errorText = null,
                trailingIcon = null,
                enabled = true,
                onClick = null
            )
            Spacer(modifier = Modifier.height(16.dp))
            UnderLabelTextField(
                value = recoveryCode,
                onValueChange = { recoveryCode = it },
                caption = "Kod za oporavak",
                placeholder = "Unesite kod za oporavak",
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default,
                isError = false,
                errorText = null,
                trailingIcon = null,
                enabled = true,
                onClick = null
            )
            Spacer(modifier = Modifier.height(16.dp))
            NextArrow(
                onClick = {
                    if (username.isNotBlank() && recoveryCode.isNotBlank()) {
                        step = 2
                    } else {
                        dialogTitle = "Greška"
                        dialogMessage = "Molimo unesite korisničko ime i kod za oporavak."
                        showDialog = true
                    }
                },
                modifier = Modifier.align(Alignment.End)
            )
        } else {
            Text(text = "Promjena lozinke")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Zamijenite zaboravljenu lozinku novom")
            Spacer(modifier = Modifier.height(16.dp))
            UnderLabelPasswordField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                caption = "Unesite lozinku",
                placeholder = "Nova lozinka"
            )
            Spacer(modifier = Modifier.height(16.dp))
            UnderLabelPasswordField(
                value = repeatPassword,
                onValueChange = { repeatPassword = it },
                modifier = Modifier.fillMaxWidth(),
                caption = "Ponovite lozinku",
                placeholder = "Ponovite novu lozinku"
            )
            Spacer(modifier = Modifier.height(16.dp))
            NextArrow(
                onClick = {
                    if (password == repeatPassword) {
                        viewModel.recoverPassword(username, recoveryCode, password)
                    } else {
                        AppMessageManager.show("Lozinke se ne podudaraju.", AppMessageType.ERROR)
                    }
                },
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}
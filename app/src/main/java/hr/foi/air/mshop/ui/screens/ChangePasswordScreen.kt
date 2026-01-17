package hr.foi.air.mshop.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.foi.air.mshop.ui.components.buttons.NextArrowButton
import hr.foi.air.mshop.ui.components.DialogMessage
import hr.foi.air.mshop.ui.components.textFields.UnderLabelPasswordField
import hr.foi.air.mshop.ui.components.textFields.UnderLabelTextField
import hr.foi.air.mshop.viewmodels.userManagement.ChangePasswordViewModel

@Composable
fun ChangePasswordScreen(viewModel: ChangePasswordViewModel = viewModel()) {
    var step by remember { mutableStateOf(1) }
    var recoveryCode by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogMessage by remember { mutableStateOf("") }

    val changePasswordResult by viewModel.changePasswordResult.collectAsState()

    if (showDialog) {
        DialogMessage(
            title = dialogTitle,
            message = dialogMessage,
            onDismiss = { showDialog = false }
        )
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
                value = recoveryCode,
                onValueChange = { recoveryCode = it },
                label = "Kod za oporavak",
                keyboardType = KeyboardType.Text
            )
            Spacer(modifier = Modifier.height(16.dp))
            NextArrowButton(onClick = { step = 2 }, modifier = Modifier.align(Alignment.End))
        } else {
            Text(text = "Promjena lozinke")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Zamijenite zaboravljenu lozinku novom")
            Spacer(modifier = Modifier.height(16.dp))
            UnderLabelPasswordField(
                value = password,
                onValueChange = { password = it },
                label = "Unesite lozinku"
            )
            Spacer(modifier = Modifier.height(16.dp))
            UnderLabelPasswordField(
                value = repeatPassword,
                onValueChange = { repeatPassword = it },
                label = "Ponovite lozinku"
            )
            Spacer(modifier = Modifier.height(16.dp))
            NextArrowButton(
                onClick = {
                    if (password == repeatPassword) {
                        viewModel.changePassword(recoveryCode, password)
                    } else {
                        dialogTitle = "Greška"
                        dialogMessage = "Lozinke se ne podudaraju."
                        showDialog = true
                    }
                },
                modifier = Modifier.align(Alignment.End)
            )
        }
    }

    changePasswordResult?.let {
        if (it.isSuccess) {
            dialogTitle = "Uspjeh"
            dialogMessage = it.getOrNull() ?: "Lozinka uspješno promijenjena."
            showDialog = true
        } else {
            dialogTitle = "Greška"
            dialogMessage = it.exceptionOrNull()?.message ?: "Dogodila se greška."
            showDialog = true
        }
    }
}

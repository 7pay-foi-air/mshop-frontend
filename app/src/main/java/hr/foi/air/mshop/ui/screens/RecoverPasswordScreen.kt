package hr.foi.air.mshop.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.foi.air.mshop.ui.components.DialogMessage
import hr.foi.air.mshop.ui.components.buttons.NextArrow
import hr.foi.air.mshop.ui.components.textFields.UnderLabelPasswordField
import hr.foi.air.mshop.ui.components.textFields.UnderLabelTextField
import hr.foi.air.mshop.ui.theme.Dimens
import hr.foi.air.mshop.utils.AppMessageManager
import hr.foi.air.mshop.utils.AppMessageType
import hr.foi.air.mshop.viewmodels.userManagement.RecoverPasswordViewModel

@Composable
fun ChangePasswordScreen(
    viewModel: RecoverPasswordViewModel = viewModel(),
    initialUsername: String = ""
) {
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
            dismissText = "Izađi",
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
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = Dimens.screenHPadding, vertical = Dimens.screenVPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header identičan login screenovima
        Spacer(modifier = Modifier.height(Dimens.xxxl))

        Text(
            text = "mShop",
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 48.sp,
                lineHeight = 52.sp
            ),
            modifier = Modifier.padding(top = Dimens.lg, bottom = Dimens.lg)
        )

        Text(
            text = "Promjena lozinke",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = Dimens.xxl)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (step == 1) {
                Text(
                    text = "Unesite korisničko ime i kod za oporavak",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = Dimens.xxl)
                )

                UnderLabelTextField(
                    caption = "Korisničko ime",
                    value = username,
                    onValueChange = { username = it },
                    placeholder = "",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(Dimens.md))

                UnderLabelTextField(
                    caption = "Kod za oporavak",
                    value = recoveryCode,
                    onValueChange = { recoveryCode = it },
                    placeholder = "",
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = "Zamijenite zaboravljenu lozinku novom",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = Dimens.xxl)
                )

                UnderLabelPasswordField(
                    caption = "Nova lozinka",
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(Dimens.md))

                UnderLabelPasswordField(
                    caption = "Ponovite lozinku",
                    value = repeatPassword,
                    onValueChange = { repeatPassword = it },
                    placeholder = "",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        NextArrow(
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 16.dp, bottom = 96.dp),
            size = Dimens.fab,
            onClick = {
                if (step == 1) {
                    if (username.isNotBlank() && recoveryCode.isNotBlank()) {
                        step = 2
                    } else {
                        dialogTitle = "Greška"
                        dialogMessage = "Molimo unesite korisničko ime i kod za oporavak."
                        showDialog = true
                    }
                } else {
                    if (password.isBlank() || repeatPassword.isBlank()) {
                        AppMessageManager.show("Unesite i ponovite lozinku.", AppMessageType.ERROR)
                        return@NextArrow
                    }
                    if (password != repeatPassword) {
                        AppMessageManager.show("Lozinke se ne podudaraju.", AppMessageType.ERROR)
                        return@NextArrow
                    }
                    viewModel.recoverPassword(username, recoveryCode, password)
                }
            }
        )
    }
}

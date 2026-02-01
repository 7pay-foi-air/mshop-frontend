package hr.foi.air.mshop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.foi.air.mshop.navigation.components.login.FirstLoginSecurityQuestions
import hr.foi.air.mshop.ui.components.DialogMessage
import hr.foi.air.mshop.ui.components.buttons.NextArrow
import hr.foi.air.mshop.ui.components.textFields.UnderLabelPasswordField
import hr.foi.air.mshop.ui.components.textFields.UnderLabelTextField
import hr.foi.air.mshop.ui.theme.Dimens
import hr.foi.air.mshop.utils.AppMessageManager
import hr.foi.air.mshop.utils.AppMessageType
import hr.foi.air.mshop.viewmodels.LoginViewModel
import hr.foi.air.mshop.viewmodels.userManagement.RecoverPasswordViewModel

@Composable
fun RecoverPasswordScreen(
    viewModel: RecoverPasswordViewModel = viewModel(),
    loginViewModel: LoginViewModel = viewModel(),
    initialUsername: String = "",
    onNavigateBack: () -> Unit
) {
    val changePasswordResult by viewModel.changePasswordResult.collectAsState()

    viewModel.username = initialUsername

    if (viewModel.showDialog) {
        DialogMessage(
            title = viewModel.dialogTitle,message = viewModel.dialogMessage,
            visible = viewModel.showDialog,
            confirmText = "U redu",
            onConfirm = {
                viewModel.showDialog = false
                onNavigateBack()
            }
        )
    }

    LaunchedEffect(changePasswordResult) {
        changePasswordResult?.let {
            if (it.isSuccess) {
                viewModel.dialogTitle = "Uspjeh"
                viewModel.dialogMessage = it.getOrNull() ?: "Lozinka uspješno promijenjena."
                viewModel.showDialog = true
            } else {
                val errorMessage = it.exceptionOrNull()?.message ?: "Dogodila se greška."
                AppMessageManager.show(errorMessage, AppMessageType.ERROR)
            }
        }
    }

    if (viewModel.step == 3) {
        FirstLoginSecurityQuestions(
            onNext = { },
            viewModel = loginViewModel,
            manualAction = {
                viewModel.fetchRecoveryLocation(loginViewModel)
            }
        )
    } else if (viewModel.step == 4) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = Dimens.screenHPadding, vertical = Dimens.screenVPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(Dimens.xxxl))

            Text(
                text = "mShop",
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 48.sp),
                modifier = Modifier.padding(top = Dimens.lg, bottom = Dimens.lg)
            )

            Text(
                text = "Lokacija koda",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = Dimens.xxl)
            )

            Text(
                text = "Prilikom prve prijave naveli ste da ste kod spremili na sljedeće mjesto:",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = Dimens.xl)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(Dimens.cardRadius))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(Dimens.tokenBoxPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = viewModel.recoveryLocation.ifBlank { "Nepoznata lokacija" },
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            NextArrow(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 16.dp, bottom = 96.dp),
                size = Dimens.fab,
                onClick = { viewModel.step = 1 }
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = Dimens.screenHPadding, vertical = Dimens.screenVPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                if (viewModel.step == 1) {
                    Text(
                        text = "Unesite korisničko ime i kod za oporavak",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = Dimens.xxl)
                    )

                    UnderLabelTextField(
                        caption = "Korisničko ime",
                        value = viewModel.username,
                        onValueChange = { viewModel.username = it },
                        placeholder = "",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(Dimens.md))

                    UnderLabelTextField(
                        caption = "Kod za oporavak",
                        value = viewModel.recoveryCode,
                        onValueChange = { viewModel.recoveryCode = it },
                        placeholder = "",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Zaboravili ste kod za oporavak?",
                        modifier = Modifier
                            .padding(top = Dimens.md)
                            .clickable { viewModel.showForgotRecoveryDialog = true },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.primary,
                            textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                        ),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                } else {
                    Text(
                        text = "Zamijenite zaboravljenu lozinku novom",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = Dimens.xxl)
                    )

                    UnderLabelPasswordField(
                        caption = "Nova lozinka",
                        value = viewModel.password,
                        onValueChange = { viewModel.password = it },
                        placeholder = "",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(Dimens.md))

                    UnderLabelPasswordField(
                        caption = "Ponovite lozinku",
                        value = viewModel.repeatPassword,
                        onValueChange = { viewModel.repeatPassword = it },
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
                    when (viewModel.step) {
                        1 -> {
                            if (viewModel.username.isNotBlank() && viewModel.recoveryCode.isNotBlank()) {
                                viewModel.step = 2
                            } else {
                                AppMessageManager.show("Unesite korisničko ime i kod.", AppMessageType.ERROR)
                            }
                        }
                        2 -> {
                            if (viewModel.password.length < 10) {
                                AppMessageManager.show("Lozinka mora imati barem 10 znakova.", AppMessageType.ERROR)
                                return@NextArrow
                            }
                            if (viewModel.password != viewModel.repeatPassword) {
                                AppMessageManager.show("Lozinke se ne podudaraju.", AppMessageType.ERROR)
                                return@NextArrow
                            }
                            viewModel.recoverPassword(viewModel.username, viewModel.recoveryCode, viewModel.password)
                        }
                    }
                }
            )

            DialogMessage(
                visible = viewModel.showForgotRecoveryDialog,
                title = "Zaboravili ste kod?",
                message = "Ako ste izgubili kod za oporavak, lozinku možete resetirati odgovaranjem na sigurnosna pitanja.",
                confirmText = "Koristi pitanja",
                dismissText = "Zatvori",
                onConfirm = {
                    viewModel.showForgotRecoveryDialog = false
                    viewModel.step = 3
                },
                onDismiss = { viewModel.showForgotRecoveryDialog = false }
            )
        }
    }
}
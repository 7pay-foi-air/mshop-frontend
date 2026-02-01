package hr.foi.air.mshop.navigation.components.login

import android.widget.Toast
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hr.foi.air.mshop.R
import hr.foi.air.mshop.data.LoginState
import hr.foi.air.mshop.utils.AppMessageManager
import hr.foi.air.mshop.utils.AppMessageType
import hr.foi.air.mshop.ui.components.DialogMessage
import hr.foi.air.mshop.ui.components.FullScreenLoadingIndicator
import hr.foi.air.mshop.ui.components.buttons.NextArrow
import hr.foi.air.mshop.ui.components.textFields.UnderLabelPasswordField
import hr.foi.air.mshop.ui.theme.Dimens
import hr.foi.air.mshop.viewmodels.LoginViewModel

@Composable
fun LoginPassword(
    onForgotPassword: () -> Unit,
    onLoginSuccess: () -> Unit,
    onFirstLogin: () -> Unit,
    onAccountLocked: () -> Unit,
    viewModel: LoginViewModel
) {
    val context = LocalContext.current
    val loginState by viewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is LoginState.Success -> {
                AppMessageManager.show("Prijava uspješna!", AppMessageType.SUCCESS)
                onLoginSuccess()
            }
            is LoginState.FirstLoginRequired -> {
                onFirstLogin()
            }
            is LoginState.AccountLocked -> {
                AppMessageManager.show(
                    "Račun zaključan! Koristite kod za oporavak.",
                    AppMessageType.ERROR
                )
                onAccountLocked()
            }
            is LoginState.Error -> {
                AppMessageManager.show("Prijava neuspješna!", AppMessageType.ERROR)
            }
            else -> {}
        }
    }


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
            text = "Prijava",
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
            Text(
                text = "Dobrodošli natrag",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = Dimens.xxl)
            )

            Text(
                text = "Unesite Vašu lozinku",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = Dimens.xxl)
            )

            UnderLabelPasswordField(
                caption = "Lozinka",
                value = viewModel.password,
                onValueChange = { viewModel.password = it },
                placeholder = ""
            )

            Text(
                text = "Zaboravili ste lozinku?",
                modifier = Modifier
                    .padding(top = Dimens.md)
                    .clickable { viewModel.onForgotPasswordClick() },
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline
                ),
                textAlign = TextAlign.Center
            )
        }


        NextArrow(
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 16.dp, bottom = 96.dp),
            size = Dimens.fab,
            onClick = {
                if (viewModel.password.isBlank()) {
                    Toast.makeText(context, "Unesite lozinku!", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.login()
                }
            }
        )
    }

    DialogMessage(
        visible = viewModel.showForgottenPasswordDialog,
        title = "Zaboravili ste lozinku?",
        message = "Bez brige! Za oporavak Vam je potreban samo kod za oporavak!",
        confirmText = "Nastavi",
        dismissText = "Otkaži",
        onConfirm = {
            viewModel.onForgotPasswordDialogDismiss()
            onForgotPassword()
        },
        onDismiss = { viewModel.onForgotPasswordDialogDismiss() }
    )

    if (loginState is LoginState.Loading) {
        FullScreenLoadingIndicator()
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPasswordPreview() {
    LoginPassword(
        onForgotPassword = {},
        onLoginSuccess = {},
        onFirstLogin = {},
        viewModel = remember { LoginViewModel() },
        onAccountLocked = {}
    )
}

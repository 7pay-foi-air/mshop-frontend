package hr.foi.air.mshop.navigation.components.login

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hr.foi.air.mshop.data.LoginState
import hr.foi.air.mshop.ui.components.DialogMessage
import hr.foi.air.mshop.ui.components.FullScreenLoadingIndicator
import hr.foi.air.mshop.ui.components.buttons.NextArrow
import hr.foi.air.mshop.ui.components.textFields.UnderLabelPasswordField
import hr.foi.air.mshop.viewmodels.LoginViewModel

@Composable
fun LoginPassword(
    onForgotPassword: () -> Unit,
    onLoginSuccess: () -> Unit,
    onFirstLogin: () -> Unit,
    viewModel: LoginViewModel
) {
    val context = LocalContext.current
    val loginState by viewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is LoginState.Success -> {
                Toast.makeText(context, "Prijava uspješna!", Toast.LENGTH_SHORT).show()
                onLoginSuccess()
                //viewModel.resetState()
            }
            is LoginState.FirstLoginRequired -> {
                onFirstLogin()
                //viewModel.resetState()
            }
            is LoginState.Error -> {
                val errorMessage = state.message
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                //viewModel.resetState()
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ){
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "mShop",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
            )

            Text(
                text = "Prijava",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Unesite lozinku",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 32.dp)
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
                        .padding(top = 16.dp)
                        .clickable { viewModel.onForgotPasswordClick() },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline
                    ),
                    textAlign = TextAlign.Center,
                )
            }

            NextArrow(
                modifier = Modifier
                    .align(Alignment.End)
                    .offset(y = (-30).dp)
                    .padding(bottom = 32.dp),
                size = 64.dp,
                onClick = {
                    if(viewModel.password.isBlank()){
                        Toast.makeText(
                            context,
                            "Unesite lozinku!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        viewModel.login()
                    }
                }
            )
        }
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
fun LoginPasswordPreview(){
    LoginPassword(
        onForgotPassword = {}, onLoginSuccess = {}, viewModel = LoginViewModel(), onFirstLogin = {},
    )
}
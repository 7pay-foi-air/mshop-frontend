package hr.foi.air.mshop.navigation.components.login

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import hr.foi.air.mshop.data.LoginState
import hr.foi.air.mshop.utils.AppMessageManager
import hr.foi.air.mshop.utils.AppMessageType
import hr.foi.air.mshop.ui.components.FullScreenLoadingIndicator
import hr.foi.air.mshop.ui.components.buttons.NextArrow
import hr.foi.air.mshop.ui.theme.Dimens
import hr.foi.air.mshop.viewmodels.LoginViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun FirstLoginRecoveryToken(
    onFinish: () -> Unit,
    viewModel: LoginViewModel
) {
    val context = LocalContext.current
    val loginState by viewModel.loginState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.toastMessage.collectLatest { message ->
            if (message.isNotBlank()) {
                AppMessageManager.show(message, AppMessageType.ERROR)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.screenHPadding, vertical = Dimens.screenVPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(Dimens.xl))

        Text(
            text = "mShop",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.padding(top = Dimens.lg, bottom = Dimens.lg)
        )

        Text(
            text = "Kod za oporavak",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = Dimens.sm)
        )

        Text(
            text = "Zapišite ovaj kod na sigurno mjesto. On je jedini način da vratite račun ako zaboravite lozinku.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = Dimens.xl)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // token box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimens.cardRadius))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(Dimens.tokenBoxPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = viewModel.recoveryToken,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(Dimens.xl))

            OutlinedTextField(
                value = viewModel.recoveryTokenLocation,
                onValueChange = { viewModel.recoveryTokenLocation = it },
                label = { Text("Gdje ste pohranili kod?") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                shape = RoundedCornerShape(Dimens.inputRadius)
            )
        }

        NextArrow(
            modifier = Modifier
                .align(Alignment.End)
                .padding(bottom = Dimens.xl),
            size = Dimens.fab,
            onClick = { viewModel.saveRecoveryToken(context, onFinish) }
        )
    }

    if (loginState is LoginState.Loading) {
        FullScreenLoadingIndicator()
    }
}

@Preview(showBackground = true)
@Composable
fun FirstLoginRecoveryTokenPreview() {
    val mockViewModel = LoginViewModel().apply {
        recoveryToken = "ABCD-1234-EFGH-5678"
        recoveryTokenLocation = "Spremljeno u sefu"
    }
    FirstLoginRecoveryToken(onFinish = {}, viewModel = mockViewModel)
}

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hr.foi.air.mshop.utils.AppMessageManager
import hr.foi.air.mshop.utils.AppMessageType
import hr.foi.air.mshop.ui.components.buttons.NextArrow
import hr.foi.air.mshop.ui.components.textFields.UnderLabelPasswordField
import hr.foi.air.mshop.ui.theme.Dimens
import hr.foi.air.mshop.viewmodels.LoginViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun FirstLoginPassword(
    onNext: () -> Unit,
    viewModel: LoginViewModel
) {
    val context = LocalContext.current

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
            text = "Prva prijava",
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
                text = "Postavite novu lozinku",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = Dimens.xxl)
            )

            UnderLabelPasswordField(
                caption = "Nova lozinka",
                value = viewModel.newPassword,
                onValueChange = {
                    viewModel.newPassword = it
                    viewModel.newPasswordError = null
                },
                placeholder = "",
                errorMessage = viewModel.newPasswordError
            )

            Spacer(modifier = Modifier.height(Dimens.xl))

            UnderLabelPasswordField(
                caption = "Ponovite lozinku",
                value = viewModel.confirmNewPassword,
                onValueChange = {
                    viewModel.confirmNewPassword = it
                    viewModel.confirmPasswordError = null
                },
                placeholder = "",
                errorMessage = viewModel.confirmPasswordError
            )
        }

        NextArrow(
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 16.dp, bottom = 96.dp),
            size = Dimens.fab,
            onClick = { viewModel.onProceedToRecoveryToken(onSuccess = onNext) }
        )
    }
}


@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun FirstLoginPasswordPreview() {
    FirstLoginPassword(onNext = {}, viewModel = LoginViewModel())
}

package hr.foi.air.mshop.navigation.components.login

import android.widget.Toast
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
            if (message.isNotBlank()) Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
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
            text = "Prva prijava",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = Dimens.sm)
        )

        Text(
            text = "Postavite novu lozinku",
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
            UnderLabelPasswordField(
                caption = "Nova lozinka",
                value = viewModel.newPassword,
                onValueChange = { viewModel.newPassword = it },
                placeholder = "Unesite lozinku"
            )

            Spacer(modifier = Modifier.height(Dimens.xl))

            UnderLabelPasswordField(
                caption = "Ponovite lozinku",
                value = viewModel.confirmNewPassword,
                onValueChange = { viewModel.confirmNewPassword = it },
                placeholder = "Ponovite lozinku"
            )
        }

        NextArrow(
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 16.dp, bottom = 96.dp),
            size = Dimens.fab,
            onClick = { viewModel.onProceedToRecovery(onSuccess = onNext) }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FirstLoginPasswordPreview() {
    FirstLoginPassword(onNext = {}, viewModel = LoginViewModel())
}

package hr.foi.air.mshop.navigation.components.login

import android.widget.Toast
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import hr.foi.air.mshop.ui.components.buttons.NextArrow
import hr.foi.air.mshop.ui.components.textFields.UnderLabelPasswordField
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
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
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
                text = "Prva prijava",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Postavite novu lozinku",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 32.dp)
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

                Spacer(modifier = Modifier.height(24.dp))

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
                    .offset(y = (-30).dp)
                    .padding(bottom = 32.dp),
                size = 64.dp,
                onClick = {
                    viewModel.onProceedToRecovery(onSuccess = onNext)
                }
            )
        }
    }
}
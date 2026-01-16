package hr.foi.air.mshop.navigation.components.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import hr.foi.air.mshop.ui.components.buttons.NextArrow
import hr.foi.air.mshop.viewmodels.LoginViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun FirstLoginRecoveryToken(
    onFinish: () -> Unit,
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "mShop", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Kod za oporavak", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))

        Text(
            text = "Zapišite ovaj kod na sigurno mjesto. On je jedini način da vratite račun ako zaboravite lozinku.",
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = viewModel.recoveryToken, style = MaterialTheme.typography.headlineMedium.copy(fontFamily = FontFamily.Monospace))
        }

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = viewModel.recoveryTokenLocation,
            onValueChange = { viewModel.recoveryTokenLocation = it },
            label = { Text("Gdje ste pohranili kod?") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Spacer(modifier = Modifier.weight(1f))

        NextArrow(
            modifier = Modifier
                .align(Alignment.End)
                .padding(bottom = 32.dp),
            size = 64.dp,
            onClick = {
                viewModel.saveRecoveryToken(context, onFinish)
            }
        )
    }
}
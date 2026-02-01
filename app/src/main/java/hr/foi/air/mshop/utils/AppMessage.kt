package hr.foi.air.mshop.utils

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import hr.foi.air.mshop.ui.theme.ErrorContainer
import hr.foi.air.mshop.ui.theme.InfoContainer
import hr.foi.air.mshop.ui.theme.SuccessContainer

@Composable
fun AppMessage(
    message: String,
    type: AppMessageType,
    modifier: Modifier = Modifier
) {
    val containerColor = when (type) {
        AppMessageType.ERROR -> ErrorContainer
        AppMessageType.SUCCESS -> SuccessContainer
        AppMessageType.INFO -> InfoContainer
    }

    val textColor = when (type) {
        AppMessageType.ERROR -> MaterialTheme.colorScheme.onErrorContainer
        AppMessageType.SUCCESS -> MaterialTheme.colorScheme.onPrimaryContainer
        AppMessageType.INFO -> MaterialTheme.colorScheme.onPrimaryContainer
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Text(
            text = message,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            color = textColor,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

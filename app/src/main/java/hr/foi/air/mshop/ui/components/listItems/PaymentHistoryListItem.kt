package hr.foi.air.mshop.ui.components.listItems

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import hr.foi.air.mshop.viewmodels.transaction.TransactionSummaryUI

@Composable
fun PaymentHistoryListItem(
    transaction: TransactionSummaryUI,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("ID: ${transaction.id}", fontWeight = FontWeight.Bold)
                Text("Iznos: ${transaction.amountText}")
                Text("Vrijeme: ${transaction.dateText} ${transaction.timeText}")

                if (transaction.isRefunded) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Refundirano",
                        fontWeight = FontWeight.Bold,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.error
                    )
                }
            }

            Icon(
                imageVector = if (transaction.isSuccessful) Icons.Default.Check else Icons.Default.Schedule,
                contentDescription = null
            )
        }
    }
}

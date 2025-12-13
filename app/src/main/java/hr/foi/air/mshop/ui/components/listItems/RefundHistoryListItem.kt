package hr.foi.air.mshop.ui.components.listItems

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import hr.foi.air.mshop.viewmodels.transaction.RefundSummaryUI

@Composable
fun RefundHistoryListItem(
    refund: RefundSummaryUI,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 0.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = refund.amountText,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "${refund.dateText} • ${refund.timeText}",
                style = MaterialTheme.typography.bodySmall
            )

            if (refund.originalTransactionId.isNotEmpty()) {
                Text(
                    text = "Povrat za transakciju: ${refund.originalTransactionId}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

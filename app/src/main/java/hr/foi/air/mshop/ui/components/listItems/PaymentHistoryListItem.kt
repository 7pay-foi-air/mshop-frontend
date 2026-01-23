package hr.foi.air.mshop.ui.components.listItems

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import hr.foi.air.mshop.ui.theme.Dimens
import hr.foi.air.mshop.ui.theme.MShopCard
import hr.foi.air.mshop.viewmodels.transaction.TransactionSummaryUI
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun PaymentHistoryListItem(
    transaction: TransactionSummaryUI,
    onClick: () -> Unit
) {
    val statusText = if (transaction.isRefunded) "Refundirano" else ""

    val inputFormatter = remember { DateTimeFormatter.ISO_LOCAL_DATE }
    val outputFormatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy.", Locale("hr", "HR")) }

    val hrDate = remember(transaction.dateText) {
        runCatching { LocalDate.parse(transaction.dateText, inputFormatter).format(outputFormatter) }
            .getOrElse { transaction.dateText }
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = MShopCard.elevatedColors(),
        shape = MShopCard.shape,
        elevation = MShopCard.elevatedElevation()
    )  {
        ListItem(
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            headlineContent = {
                Text(
                    text = "Plaćanje",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            supportingContent = {
                Text(
                    text = "$hrDate • ${transaction.timeText}",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingContent = {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = transaction.amountText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    if (statusText.isNotBlank()) {
                        Text(
                            text = statusText,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            },
            modifier = Modifier.height(Dimens.historyRowHeight)
        )
    }
}

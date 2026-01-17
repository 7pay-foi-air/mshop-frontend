package hr.foi.air.mshop.ui.components.listItems

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import hr.foi.air.mshop.viewmodels.transaction.TransactionSummaryUI
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private fun shortId(id: String): String =
    if (id.length <= 10) id else "${id.take(8)}…"

@Composable
fun PaymentHistoryListItem(
    transaction: TransactionSummaryUI,
    onClick: () -> Unit
) {
    val (statusText, statusColor) = when {
        transaction.isRefunded -> "Refundirano" to MaterialTheme.colorScheme.error
        transaction.isSuccessful -> "Uspješno" to MaterialTheme.colorScheme.primary
        else -> "U tijeku" to MaterialTheme.colorScheme.tertiary
    }

    val leadingIcon = when {
        transaction.isRefunded -> Icons.Default.CheckCircle
        transaction.isSuccessful -> Icons.Default.CheckCircle
        else -> Icons.Default.Schedule
    }

    val inputFormatter = remember { DateTimeFormatter.ISO_LOCAL_DATE }
    val outputFormatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy.", Locale("hr", "HR")) }

    val hrDate = remember(transaction.dateText) {
        runCatching { LocalDate.parse(transaction.dateText, inputFormatter).format(outputFormatter) }
            .getOrElse { transaction.dateText }
    }

    val rowHeight = 100.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        ListItem(
            leadingContent = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = statusColor
                )
            },
            headlineContent = {
                Text(
                    text = "Plaćanje",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.SemiBold
                )
            },
            supportingContent = {
                Text(
                    //text = "${transaction.dateText} • ${transaction.timeText}  •  ID ${shortId(transaction.id)}",
                    text = "$hrDate • ${transaction.timeText}",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            trailingContent = {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = transaction.amountText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = statusText,
                        color = statusColor,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            modifier = Modifier.height(rowHeight),
        )
    }
}

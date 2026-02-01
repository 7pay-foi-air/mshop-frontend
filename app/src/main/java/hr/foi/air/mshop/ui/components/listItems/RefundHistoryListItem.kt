package hr.foi.air.mshop.ui.components.listItems

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import hr.foi.air.mshop.ui.theme.Dimens
import hr.foi.air.mshop.ui.theme.MShopCard
import hr.foi.air.mshop.viewmodels.transaction.RefundSummaryUI
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ListItemDefaults

@Composable
fun RefundHistoryListItem(
    refund: RefundSummaryUI,
    onClick: () -> Unit
) {
    val statusColor = MaterialTheme.colorScheme.error

    val inputFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    val outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.", Locale("hr", "HR"))
    val hrDate = runCatching { LocalDate.parse(refund.dateText, inputFormatter).format(outputFormatter) }
        .getOrElse { refund.dateText }

    val leadingIcon =
        if (refund.originalTransactionId.isNotEmpty()) Icons.Default.Replay else Icons.Default.Warning

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
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = statusColor
                )
            },
            headlineContent = {
                Text(
                    text = "Povrat",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            supportingContent = {
                Text(
                    text = "$hrDate • ${refund.timeText}",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingContent = {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = refund.amountText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            modifier = Modifier.height(Dimens.historyRowHeight),
            colors = ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

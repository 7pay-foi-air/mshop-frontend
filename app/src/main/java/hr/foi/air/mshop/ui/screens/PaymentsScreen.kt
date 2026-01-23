package hr.foi.air.mshop.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import hr.foi.air.mshop.ui.components.listItems.PaymentHistoryListItem
import hr.foi.air.mshop.ui.theme.Dimens
import hr.foi.air.mshop.viewmodels.transaction.TransactionHistoryViewModel

@Composable
fun PaymentsScreen(
    viewModel: TransactionHistoryViewModel,
    onTransactionClick: (String) -> Unit
) {
    val payments by viewModel.payments.collectAsState()

    if (payments.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.screenPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Nema transakcija za prikazati.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = Dimens.screenPadding,
            vertical = Dimens.lg
        ),
        verticalArrangement = Arrangement.spacedBy(Dimens.md)
    ) {
        items(payments, key = { it.id }) { t ->
            PaymentHistoryListItem(
                transaction = t,
                onClick = { onTransactionClick(t.id) }
            )
        }
    }
}

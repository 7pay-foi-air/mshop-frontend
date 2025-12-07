package hr.foi.air.mshop.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import hr.foi.air.mshop.ui.components.listItems.PaymentHistoryListItem
import hr.foi.air.mshop.viewmodels.transaction.TransactionHistoryViewModel

@Composable
fun PaymentsScreen(
    viewModel: TransactionHistoryViewModel,
    onTransactionClick: (String) -> Unit
){
    val payments by viewModel.payments.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(payments) { t ->
            PaymentHistoryListItem(
                transaction = t,
                onClick = { onTransactionClick(t.id) }
            )
        }
    }

}
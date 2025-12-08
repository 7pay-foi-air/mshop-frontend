package hr.foi.air.mshop.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import hr.foi.air.mshop.ui.components.listItems.RefundHistoryListItem
import hr.foi.air.mshop.viewmodels.transaction.TransactionHistoryViewModel

@Composable
fun RefundsScreen(
    viewModel: TransactionHistoryViewModel,
    onTransactionClick: (String) -> Unit
) {
    val refunds by viewModel.refunds.collectAsState()

    if (refunds.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Još nema zaprimljenih povrata.")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(refunds) { refund ->
                RefundHistoryListItem(
                    refund = refund,
                    onClick = { onTransactionClick(refund.id) }
                )
            }
        }
    }
}

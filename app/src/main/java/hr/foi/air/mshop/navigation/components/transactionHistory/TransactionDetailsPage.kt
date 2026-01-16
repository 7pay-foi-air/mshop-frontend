package hr.foi.air.mshop.navigation.components.transactionHistory

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import hr.foi.air.mshop.ui.components.listItems.TransactionItemRow
import hr.foi.air.mshop.viewmodels.transaction.TransactionDetailsViewModel
import hr.foi.air.mshop.viewmodels.transaction.TransactionHistoryViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import hr.foi.air.mshop.navigation.AppRoutes


@Composable
fun TransactionDetailsPage(
    navController: NavHostController,
    transactionId: String,
    vm: TransactionDetailsViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()
    val details by vm.details.collectAsState()



    val currentBackStack by navController.currentBackStackEntryAsState()
    val historyEntry = remember(currentBackStack) {
        navController.getBackStackEntry(AppRoutes.TRANSACTION_HISTORY)
    }
    val historyVm: TransactionHistoryViewModel = viewModel(historyEntry)

    LaunchedEffect(transactionId) {
        vm.loadTransactionDetails(transactionId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "mShop",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Text(
            text = "Detalji transakcije",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        if (uiState.loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }

        uiState.errorMessage?.let { msg ->
            Text(
                text = msg,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Button(onClick = { vm.loadTransactionDetails(transactionId) }) {
                Text("Pokušaj ponovno")
            }
            return
        }

        val d = details
        if (d == null) {
            Text("Nema podataka za prikaz.")
            return
        }

        val refunds by historyVm.refunds.collectAsState()
        val isRefunded = remember(d, refunds) {
            refunds.any { it.originalTransactionId == d.uuidTransaction }
        }
        val matchingRefund = remember(d, refunds) {
            refunds.find { it.originalTransactionId == d.uuidTransaction }
        }

        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.40f)),
            colors = CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    "ID: ${d.uuidTransaction}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(Modifier.height(6.dp))

                Text("Tip: ${d.transactionType}")
                Text("Datum: ${d.transactionDate}")
                Text("Valuta: ${d.currency}")

                if (d.paymentMethod.isNotBlank()) {
                    Text("Način plaćanja: ${d.paymentMethod}")
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "UKUPNO: ${"%.2f".format(d.totalAmount)} ${d.currency}",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )

                if (isRefunded) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Refundirano" + (matchingRefund?.id?.let { " (ID: $it)" } ?: ""),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }


        Spacer(Modifier.height(16.dp))

        if (d.transactionType == "Purchase") {
            if (!isRefunded) {
                Button(
                    onClick = {
                        vm.refundTransaction { success ->
                            if (success) {
                                historyVm.loadTransactions() // refresh povijesti
                                navController.popBackStack()
                            } else {
                                Toast.makeText(navController.context, "Refund nije uspio!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("REFUND TRANSACTION")
                }
            } else {
                Text(
                    text = "Ova transakcija je već refundirana.",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        if (d.items.isNotEmpty()) {
            Text(
                text = "Stavke",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(d.items) { item ->
                    TransactionItemRow(
                        itemName = item.itemName,
                        qty = item.quantity,
                        price = item.itemPrice,
                        subtotal = item.subtotal
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "\uD83E\uDD16",
                    style = MaterialTheme.typography.displayLarge
                )
                Text(
                    text = "AI inicirana transakcija",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 12.dp)
                )
                Text(
                    text = "Ova transakcija je automatski generirana i nema artikala.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
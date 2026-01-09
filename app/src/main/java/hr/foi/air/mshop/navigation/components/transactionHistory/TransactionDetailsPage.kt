package hr.foi.air.mshop.navigation.components.transactionHistory

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import hr.foi.air.mshop.ui.components.listItems.TransactionItemRow
import hr.foi.air.mshop.viewmodels.transaction.TransactionDetailsViewModel


@Composable
fun TransactionDetailsPage(
    navController: NavHostController,
    transactionId: String,
    vm: TransactionDetailsViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()
    val details by vm.details.collectAsState()

    LaunchedEffect(transactionId) {
        vm.loadTransactionDetails(transactionId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // naslov
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

        // loading
        if (uiState.loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }

        // error
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

        // summary info
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
            }
        }

        Spacer(Modifier.height(16.dp))

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
    }
}
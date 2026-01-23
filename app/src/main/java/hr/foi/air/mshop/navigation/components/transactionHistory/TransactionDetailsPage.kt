package hr.foi.air.mshop.navigation.components.transactionHistory

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import hr.foi.air.mshop.navigation.AppRoutes
import hr.foi.air.mshop.ui.components.listItems.TransactionItemRow
import hr.foi.air.mshop.ui.theme.Dimens
import hr.foi.air.mshop.ui.theme.MShopCard
import hr.foi.air.mshop.viewmodels.transaction.TransactionDetailsViewModel
import hr.foi.air.mshop.viewmodels.transaction.TransactionHistoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
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

    val refunds by historyVm.refunds.collectAsState()
    var openRefundSheet by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(transactionId) { vm.loadTransactionDetails(transactionId) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {

                Text(
                    text = "mShop",
                    style = MaterialTheme.typography.displayLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimens.lg, bottom = Dimens.sm)
                )

                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Detalji transakcije",
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                )
            }
        }
    ) { innerPadding ->

        when {
            uiState.loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.errorMessage != null -> {
                val msg = uiState.errorMessage!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(Dimens.lg),
                    verticalArrangement = Arrangement.spacedBy(Dimens.md),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.WarningAmber,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = msg,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    FilledTonalButton(onClick = { vm.loadTransactionDetails(transactionId) }) {
                        Text("Pokušaj ponovno")
                    }
                }
            }

            details == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nema podataka za prikaz.")
                }
            }

            else -> {
                val d = details!!
                val isRefunded = remember(d, refunds) {
                    refunds.any { it.originalTransactionId == d.uuidTransaction }
                }
                val matchingRefund = remember(d, refunds) {
                    refunds.find { it.originalTransactionId == d.uuidTransaction }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(Dimens.lg),
                    verticalArrangement = Arrangement.spacedBy(Dimens.md)
                ) {
                    item {
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = MShopCard.elevatedColors(),
                            shape = MShopCard.shape,
                            elevation = MShopCard.elevatedElevation()
                        ) {
                            Column(
                                modifier = Modifier.padding(Dimens.lg),
                                verticalArrangement = Arrangement.spacedBy(Dimens.sm)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(Modifier.weight(1f)) {
                                        Text(
                                            text = "UKUPNO",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "%.2f".format(d.totalAmount) + " " + d.currency,
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }

                                    val statusText = when {
                                        isRefunded -> "Refundirano"
                                        d.transactionType == "Purchase" -> ""
                                        else -> "Povrat"
                                    }
                                    val chipColor = when {
                                        isRefunded -> MaterialTheme.colorScheme.error
                                        else -> MaterialTheme.colorScheme.primary
                                    }

                                    if (statusText.isNotBlank()) {
                                        AssistChip(
                                            onClick = {},
                                            enabled = false,
                                            label = { Text(statusText) },
                                            colors = AssistChipDefaults.assistChipColors(
                                                disabledContainerColor = chipColor.copy(alpha = 0.12f),
                                                disabledLabelColor = chipColor
                                            )
                                        )
                                    }
                                }

                                HorizontalDivider()

                                Text("ID: ${d.uuidTransaction}", style = MaterialTheme.typography.bodyMedium)
                                Text("Tip: ${d.transactionType}", style = MaterialTheme.typography.bodyMedium)
                                Text("Datum: ${d.transactionDate}", style = MaterialTheme.typography.bodyMedium)

                                val paymentMethodText = when {
                                    d.paymentMethod == "card_payment" -> "Kartično plaćanje"
                                    else -> d.paymentMethod
                                }

                                if (d.paymentMethod.isNotBlank()) {
                                    Text(
                                        "Način plaćanja: $paymentMethodText",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                                if (isRefunded) {
                                    Text(
                                        text = "Refund: " + (matchingRefund?.id ?: "—"),
                                        color = MaterialTheme.colorScheme.error,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Dimens.sm),
                            modifier = Modifier.padding(top = Dimens.sm)
                        ) {
                            Icon(Icons.Outlined.ReceiptLong, contentDescription = null)
                            Text(
                                text = "Stavke",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (d.items.isNotEmpty()) {
                        items(d.items) { item ->
                            TransactionItemRow(
                                itemName = item.itemName,
                                qty = item.quantity,
                                price = item.itemPrice,
                                subtotal = item.subtotal
                            )
                        }
                    } else {
                        item {
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                colors = MShopCard.elevatedColors(),
                                shape = MShopCard.shape,
                                elevation = MShopCard.elevatedElevation()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(Dimens.xl),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(Dimens.sm)
                                ) {
                                    Text(
                                        text = "\uD83E\uDD16",
                                        style = MaterialTheme.typography.displayLarge
                                    )
                                    Text(
                                        text = "AI inicirana transakcija",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Ova transakcija je automatski generirana i nema artikala.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    if (d.transactionType == "Purchase") {
                        item {
                            if (!isRefunded) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Button(onClick = { openRefundSheet = true }) {
                                        Text("Izvrši povrat")
                                    }
                                }
                            } else {
                                ElevatedCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = MShopCard.elevatedColors(),
                                    shape = MShopCard.shape,
                                    elevation = MShopCard.elevatedElevation()
                                ){
                                    Text(
                                        text = "Ova transakcija je već refundirana.",
                                        modifier = Modifier.padding(Dimens.lg),
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }

                if (openRefundSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { openRefundSheet = false }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Dimens.lg)
                                .padding(bottom = Dimens.xl),
                            verticalArrangement = Arrangement.spacedBy(Dimens.md)
                        ) {
                            Text(
                                text = "Potvrdi povrat",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Želiš li refundirati ovu transakciju? Ova radnja se ne može poništiti.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(Dimens.md)
                            ) {
                                OutlinedButton(
                                    onClick = { openRefundSheet = false },
                                    modifier = Modifier.weight(1f)
                                ) { Text("Odustani") }

                                Button(
                                    onClick = {
                                        openRefundSheet = false
                                        vm.refundTransaction { success ->
                                            if (success) {
                                                historyVm.loadTransactions()
                                            } else {
                                                Toast.makeText(
                                                    navController.context,
                                                    "Refund nije uspio!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) { Text("Refund") }
                            }
                        }
                    }
                }
            }
        }
    }
}

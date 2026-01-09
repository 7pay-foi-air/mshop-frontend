package hr.foi.air.mshop.navigation.components.transactionHistory

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import hr.foi.air.mshop.navigation.AppRoutes
import hr.foi.air.mshop.ui.components.DateFieldUnderLabel
import hr.foi.air.mshop.ui.screens.PaymentsScreen
import hr.foi.air.mshop.ui.screens.RefundsScreen
import hr.foi.air.mshop.viewmodels.transaction.TransactionHistoryViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun LocalDate.toEpochMillis(): Long =
    this.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

fun Long.toLocalDate(): LocalDate =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryPage(
    navController: NavHostController,
    viewModel: TransactionHistoryViewModel = viewModel(),
    initialFromDate: LocalDate? = null,
    initialToDate: LocalDate? = null
) {
    Log.d("TransactionHistoryPage", "initialFromDate: $initialFromDate, initialToDate: $initialToDate")


    val selectedTabIndex by viewModel.selectedTabIndex.collectAsState()
    val tabTitles = listOf("Plaćanja", "Povrati")

    var showFromPicker by remember { mutableStateOf(false) }
    var showToPicker by remember { mutableStateOf(false) }

    LaunchedEffect(initialFromDate) {
        if (initialFromDate != null) viewModel.fromDate.value = initialFromDate
    }

    LaunchedEffect(initialToDate) {
        if (initialToDate != null) viewModel.toDate.value = initialToDate
    }

    val fromDate by viewModel.fromDate.collectAsState()
    val toDate by viewModel.toDate.collectAsState()

    val fromDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialFromDate?.toEpochMillis()
    )

    val toDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialToDate?.toEpochMillis()
    )

    LaunchedEffect(fromDate) {
        fromDatePickerState.selectedDateMillis = fromDate?.toEpochMillis()
    }

    LaunchedEffect(toDate) {
        toDatePickerState.selectedDateMillis = toDate?.toEpochMillis()
    }

    val displayFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.", Locale("hr"))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "mShop",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            textAlign = TextAlign.Center
        )
        Text(
            text = "Povijest transakcija",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { viewModel.onTabSelected(index) },
                    text = { Text(title) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DateFieldUnderLabel(
                caption = "Od datuma",
                value = fromDate?.format(displayFormatter) ?: "",
                onOpenPicker = { showFromPicker = true },
                isError = false,
                modifier = Modifier.weight(1f)
            )

            DateFieldUnderLabel(
                caption = "Do datuma",
                value = toDate?.format(displayFormatter) ?: "",
                onOpenPicker = { showToPicker = true },
                isError = false,
                modifier = Modifier.weight(1f)
            )
        }

        if (showFromPicker) {
            DatePickerDialog(
                onDismissRequest = { showFromPicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        fromDatePickerState.selectedDateMillis?.let {
                            val newFrom = it.toLocalDate()
                            viewModel.setDateRange(newFrom, viewModel.toDate.value)
                        }
                        showFromPicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showFromPicker = false }) { Text("Cancel") }
                },
                properties = DialogProperties()
            ) {
                DatePicker(state = fromDatePickerState)
            }
        }

        if (showToPicker) {
            DatePickerDialog(
                onDismissRequest = { showToPicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        toDatePickerState.selectedDateMillis?.let {
                            val newTo = it.toLocalDate()
                            viewModel.setDateRange(viewModel.fromDate.value, newTo)
                        }
                        showToPicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showToPicker = false }) { Text("Cancel") }
                },
                properties = DialogProperties()
            ) {
                DatePicker(state = toDatePickerState)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTabIndex) {
            0 -> PaymentsScreen(
                viewModel = viewModel,
                onTransactionClick = { id ->  navController.navigate(AppRoutes.transactionDetails(id))
                }
            )
            1 -> RefundsScreen(
                viewModel = viewModel,
                onTransactionClick = { id -> navController.navigate(AppRoutes.transactionDetails(id))
                }
            )
        }
    }
}

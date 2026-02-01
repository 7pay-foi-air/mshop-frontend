package hr.foi.air.mshop.navigation.components.transactionHistory

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import hr.foi.air.mshop.navigation.AppRoutes
import hr.foi.air.mshop.ui.screens.PaymentsScreen
import hr.foi.air.mshop.ui.screens.RefundsScreen
import hr.foi.air.mshop.ui.theme.Dimens
import hr.foi.air.mshop.viewmodels.transaction.SortOption
import hr.foi.air.mshop.viewmodels.transaction.TransactionHistoryViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import hr.foi.air.mshop.ui.theme.MShopSheetDefaults
import hr.foi.air.mshop.utils.toHrCurrency

fun LocalDate.toUtcEpochMillis(): Long =
    this.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

fun Long.utcMillisToLocalDate(): LocalDate =
    Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryPage(
    navController: NavHostController,
    viewModel: TransactionHistoryViewModel = viewModel(),
    initialFromDate: LocalDate? = null,
    initialToDate: LocalDate? = null
) {
    val selectedTabIndex by viewModel.selectedTabIndex.collectAsState()
    val tabTitles = listOf("Plaćanja", "Povrati")

    var initialArgsConsumed by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(initialFromDate, initialToDate) {
        if (!initialArgsConsumed && (initialFromDate != null || initialToDate != null)) {
            viewModel.setFilters(
                from = initialFromDate,
                to = initialToDate,
                minAmount = viewModel.minAmount.value,
                maxAmount = viewModel.maxAmount.value,
                sort = viewModel.sortOption.value
            )
            initialArgsConsumed = true
        }
    }

    val fromDate by viewModel.fromDate.collectAsState()
    val toDate by viewModel.toDate.collectAsState()
    val minAmount by viewModel.minAmount.collectAsState()
    val maxAmount by viewModel.maxAmount.collectAsState()
    val sortOption by viewModel.sortOption.collectAsState()

    val displayFormatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy.", Locale("hr")) }

    var showFilterSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var draftFrom by remember { mutableStateOf<LocalDate?>(null) }
    var draftTo by remember { mutableStateOf<LocalDate?>(null) }
    var draftMinAmountText by remember { mutableStateOf("") }
    var draftMaxAmountText by remember { mutableStateOf("") }
    var draftSort by remember { mutableStateOf(SortOption.NEWEST) }

    var showFromPicker by remember { mutableStateOf(false) }
    var showToPicker by remember { mutableStateOf(false) }

    fun openFilters() {
        draftFrom = fromDate
        draftTo = toDate
        draftMinAmountText = minAmount?.toString()?.replace('.', ',') ?: ""
        draftMaxAmountText = maxAmount?.toString()?.replace('.', ',') ?: ""
        draftSort = sortOption
        showFilterSheet = true
    }

    fun parseAmountOrNull(text: String): Double? {
        val t = text.trim()
        if (t.isBlank()) return null
        return t.replace(',', '.').toDoubleOrNull()
    }

    val minParsed = remember(draftMinAmountText) { parseAmountOrNull(draftMinAmountText) }
    val maxParsed = remember(draftMaxAmountText) { parseAmountOrNull(draftMaxAmountText) }
    val amountRangeInvalid = remember(minParsed, maxParsed) {
        minParsed != null && maxParsed != null && minParsed > maxParsed
    }

    val dateFilterText = remember(fromDate, toDate) {
        when {
            fromDate == null && toDate == null -> null
            fromDate != null && toDate == null -> "Od ${fromDate!!.format(displayFormatter)}"
            fromDate == null && toDate != null -> "Do ${toDate!!.format(displayFormatter)}"
            else -> "${fromDate!!.format(displayFormatter)} – ${toDate!!.format(displayFormatter)}"
        }
    }

    val min = minAmount
    val max = maxAmount

    val amountFilterText = remember(min, max) {
        when {
            min == null && max == null -> null
            min != null && max == null -> "≥ ${min.toHrCurrency()}"
            min == null && max != null -> "≤ ${max.toHrCurrency()}"
            else -> "${min!!.toHrCurrency()} – ${max!!.toHrCurrency()}"
        }
    }

    val sortText = remember(sortOption) {
        when (sortOption) {
            SortOption.NEWEST -> "Najnovije"
            SortOption.OLDEST -> "Najstarije"
            SortOption.AMOUNT_DESC -> "Iznos ↓"
            SortOption.AMOUNT_ASC -> "Iznos ↑"
        }
    }

    if (showFromPicker) {
        val fromSelectableDates = remember(draftTo) {
            object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val picked = utcTimeMillis.utcMillisToLocalDate()
                    return draftTo == null || !picked.isAfter(draftTo)
                }
            }
        }

        val fromPickerState = rememberDatePickerState(
            initialSelectedDateMillis = draftFrom?.toUtcEpochMillis(),
            selectableDates = fromSelectableDates
        )

        DatePickerDialog(
            onDismissRequest = { showFromPicker = false },
            confirmButton = {
                TextButton(
                    enabled = fromPickerState.selectedDateMillis != null,
                    onClick = {
                        draftFrom = fromPickerState.selectedDateMillis?.utcMillisToLocalDate()
                        showFromPicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showFromPicker = false }) { Text("Odustani") } },
            properties = DialogProperties(),
            colors = MShopSheetDefaults.datePickerColors()
        ) {
            DatePicker(
                state = fromPickerState,
                colors = MShopSheetDefaults.datePickerColors()
            )

        }
    }

    if (showToPicker) {
        val toSelectableDates = remember(draftFrom) {
            object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val picked = utcTimeMillis.utcMillisToLocalDate()
                    return draftFrom == null || !picked.isBefore(draftFrom)
                }
            }
        }

        val toPickerState = rememberDatePickerState(
            initialSelectedDateMillis = draftTo?.toUtcEpochMillis(),
            selectableDates = toSelectableDates
        )

        DatePickerDialog(
            onDismissRequest = { showToPicker = false },
            confirmButton = {
                TextButton(
                    enabled = toPickerState.selectedDateMillis != null,
                    onClick = {
                        draftTo = toPickerState.selectedDateMillis?.utcMillisToLocalDate()
                        showToPicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showToPicker = false }) { Text("Odustani") } },
            properties = DialogProperties(),
            colors = MShopSheetDefaults.datePickerColors()

        ) {
            DatePicker(
                state = toPickerState,
                colors = MShopSheetDefaults.datePickerColors()
            )
        }
    }

    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = sheetState,
            containerColor = MShopSheetDefaults.containerColor(),
            contentColor = MaterialTheme.colorScheme.onBackground,
            tonalElevation = 0.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.lg)
                    .padding(bottom = Dimens.xl)
            ) {
                Text(
                    text = "Filtriranje i sortiranje",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(Dimens.md))

                Text(text = "Datum", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(Dimens.sm))

                Row(horizontalArrangement = Arrangement.spacedBy(Dimens.sm)) {
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = draftFrom?.format(displayFormatter) ?: "",
                            onValueChange = {},
                            readOnly = true,
                            singleLine = true,
                            label = { Text("Od") },
                            colors = MShopSheetDefaults.textFieldColors(),
                            trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { showFromPicker = true }
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = draftTo?.format(displayFormatter) ?: "",
                            onValueChange = {},
                            readOnly = true,
                            singleLine = true,
                            colors = MShopSheetDefaults.textFieldColors(),
                            label = { Text("Do") },
                            trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { showToPicker = true }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.lg))

                Text(text = "Iznos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(Dimens.sm))

                Row(horizontalArrangement = Arrangement.spacedBy(Dimens.sm)) {
                    OutlinedTextField(
                        value = draftMinAmountText,
                        onValueChange = { draftMinAmountText = it },
                        singleLine = true,
                        label = { Text("Min") },
                        colors = MShopSheetDefaults.textFieldColors(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        isError = amountRangeInvalid
                    )
                    OutlinedTextField(
                        value = draftMaxAmountText,
                        onValueChange = { draftMaxAmountText = it },
                        singleLine = true,
                        colors = MShopSheetDefaults.textFieldColors(),
                        label = { Text("Max") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        isError = amountRangeInvalid
                    )
                }

                if (amountRangeInvalid) {
                    Spacer(modifier = Modifier.height(Dimens.xs))
                    Text(
                        text = "Min ne smije biti veći od Max.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(Dimens.lg))

                Text(text = "Sortiranje", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(Dimens.sm))

                var sortExpanded by remember { mutableStateOf(false) }
                val sortLabel = when (draftSort) {
                    SortOption.NEWEST -> "Najnovije prvo"
                    SortOption.OLDEST -> "Najstarije prvo"
                    SortOption.AMOUNT_DESC -> "Iznos: veći prvo"
                    SortOption.AMOUNT_ASC -> "Iznos: manji prvo"
                }

                ExposedDropdownMenuBox(
                    expanded = sortExpanded,
                    onExpandedChange = { sortExpanded = !sortExpanded }
                ) {
                    OutlinedTextField(
                        value = sortLabel,
                        onValueChange = {},
                        readOnly = true,
                        singleLine = true,
                        colors = MShopSheetDefaults.textFieldColors(),
                        label = { Text("Sort") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sortExpanded) }
                    )
                    ExposedDropdownMenu(
                        expanded = sortExpanded,
                        onDismissRequest = { sortExpanded = false },
                        containerColor = MShopSheetDefaults.containerColor()
                    ) {
                        DropdownMenuItem(text = { Text("Najnovije prvo") }, onClick = { draftSort = SortOption.NEWEST; sortExpanded = false })
                        DropdownMenuItem(text = { Text("Najstarije prvo") }, onClick = { draftSort = SortOption.OLDEST; sortExpanded = false })
                        DropdownMenuItem(text = { Text("Iznos: veći prvo") }, onClick = { draftSort = SortOption.AMOUNT_DESC; sortExpanded = false })
                        DropdownMenuItem(text = { Text("Iznos: manji prvo") }, onClick = { draftSort = SortOption.AMOUNT_ASC; sortExpanded = false })
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.xl))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.md)
                ) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            draftFrom = null
                            draftTo = null
                            draftMinAmountText = ""
                            draftMaxAmountText = ""
                            draftSort = SortOption.NEWEST
                        }
                    ) { Text("Očisti") }

                    Button(
                        modifier = Modifier.weight(1f),
                        enabled = !amountRangeInvalid,
                        onClick = {
                            viewModel.setFilters(
                                from = draftFrom,
                                to = draftTo,
                                minAmount = parseAmountOrNull(draftMinAmountText),
                                maxAmount = parseAmountOrNull(draftMaxAmountText),
                                sort = draftSort
                            )
                            showFilterSheet = false
                        }
                    ) { Text("Primijeni") }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier
                .fillMaxWidth()
            ) {
                Text(
                    text = "mShop",
                    style = MaterialTheme.typography.displayLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimens.lg, bottom = Dimens.sm)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.screenPadding)
                        .padding(bottom = Dimens.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.size(48.dp))

                    Text(
                        text = "Povijest transakcija",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(onClick = { openFilters() }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                }

                TabRow(selectedTabIndex = selectedTabIndex) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { viewModel.onTabSelected(index) },
                            text = { Text(title) }
                        )
                    }
                }
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val hasAnyFilter =
                (dateFilterText != null) || (amountFilterText != null) || (sortOption != SortOption.NEWEST)

            if (hasAnyFilter) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.lg)
                        .padding(top = Dimens.sm, bottom = Dimens.xs)
                ) {
                    Text(
                        text = "Primijenjeno:",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(Dimens.xs))

                    val chips = buildList {
                        if (dateFilterText != null) add("Datum: $dateFilterText")
                        if (amountFilterText != null) add("Iznos: $amountFilterText")
                        if (sortOption != SortOption.NEWEST) add("Sort: $sortText")
                    }

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.sm),
                        contentPadding = PaddingValues(end = Dimens.lg)
                    ) {
                        items(chips) { label ->
                            AssistChip(
                                onClick = { openFilters() },
                                label = {
                                    Text(
                                        text = label,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            )
                        }
                    }
                }
            }

            when (selectedTabIndex) {
                0 -> PaymentsScreen(
                    viewModel = viewModel,
                    onTransactionClick = { id ->
                        Log.d("TX_CLICK", "clicked id=$id")
                        navController.navigate(AppRoutes.transactionDetails(id))
                    }
                )
                1 -> RefundsScreen(
                    viewModel = viewModel,
                    onTransactionClick = { id -> navController.navigate(AppRoutes.transactionDetails(id)) }
                )
            }
        }
    }
}

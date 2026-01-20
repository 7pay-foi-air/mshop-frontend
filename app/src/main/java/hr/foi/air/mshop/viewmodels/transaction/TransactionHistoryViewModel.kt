package hr.foi.air.mshop.viewmodels.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.foi.air.mshop.core.models.TransactionHistoryRecord
import hr.foi.air.mshop.core.repository.ITransactionRepository
import hr.foi.air.ws.NetworkService
import hr.foi.air.ws.repository.TransactionRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.math.abs

enum class SortOption {
    NEWEST, OLDEST, AMOUNT_DESC, AMOUNT_ASC
}

data class TransactionSummaryUI(
    val id: String,
    val amountText: String,
    val amountValue: Double,
    val currency: String,
    val dateText: String,
    val timeText: String,
    val createdAtMillis: Long,
    val isSuccessful: Boolean,
    val isRefunded: Boolean
)

data class RefundSummaryUI(
    val id: String,
    val amountText: String,
    val amountValue: Double,
    val currency: String,
    val dateText: String,
    val timeText: String,
    val createdAtMillis: Long,
    val originalTransactionId: String
)

class TransactionHistoryViewModel(
    private val repository: ITransactionRepository = TransactionRepo(NetworkService.transactionApi)
) : ViewModel() {

    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex: StateFlow<Int> = _selectedTabIndex
    fun onTabSelected(index: Int) { _selectedTabIndex.value = index }

    private val _payments = MutableStateFlow<List<TransactionSummaryUI>>(emptyList())
    val payments: StateFlow<List<TransactionSummaryUI>> = _payments

    private val _refunds = MutableStateFlow<List<RefundSummaryUI>>(emptyList())
    val refunds: StateFlow<List<RefundSummaryUI>> = _refunds

    private var rawPayments: List<TransactionSummaryUI> = emptyList()
    private var rawRefunds: List<RefundSummaryUI> = emptyList()

    val fromDate = MutableStateFlow<LocalDate?>(null)
    val toDate = MutableStateFlow<LocalDate?>(null)
    val minAmount = MutableStateFlow<Double?>(null)
    val maxAmount = MutableStateFlow<Double?>(null)
    val sortOption = MutableStateFlow(SortOption.NEWEST)

    init {
        loadTransactions()
    }

    fun setFilters(
        from: LocalDate?,
        to: LocalDate?,
        minAmount: Double?,
        maxAmount: Double?,
        sort: SortOption
    ) {
        fromDate.value = from
        toDate.value = to
        this.minAmount.value = minAmount
        this.maxAmount.value = maxAmount
        sortOption.value = sort

        loadTransactions(from, to)
    }

    fun loadTransactions(startDate: LocalDate? = fromDate.value, endDate: LocalDate? = toDate.value) {
        viewModelScope.launch {
            try {
                val domain = repository.getTransactionsForCurrentUser(startDate, endDate)
                rawPayments = domain.payments.map { it.toPaymentUI() }
                rawRefunds = domain.refunds.map { it.toRefundUI() }
                applyLocalFiltersAndSort()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun applyLocalFiltersAndSort() {
        val min = minAmount.value
        val max = maxAmount.value
        val sort = sortOption.value

        val filteredPayments = rawPayments
            .asSequence()
            .filter { t ->
                val v = abs(t.amountValue)
                (min == null || v >= min) && (max == null || v <= max)
            }
            .toList()
            .let { list ->
                when (sort) {
                    SortOption.NEWEST -> list.sortedByDescending { it.createdAtMillis }
                    SortOption.OLDEST -> list.sortedBy { it.createdAtMillis }
                    SortOption.AMOUNT_DESC -> list.sortedByDescending { abs(it.amountValue) }
                    SortOption.AMOUNT_ASC -> list.sortedBy { abs(it.amountValue) }
                }
            }

        val filteredRefunds = rawRefunds
            .asSequence()
            .filter { r ->
                val v = abs(r.amountValue)
                (min == null || v >= min) && (max == null || v <= max)
            }
            .toList()
            .let { list ->
                when (sort) {
                    SortOption.NEWEST -> list.sortedByDescending { it.createdAtMillis }
                    SortOption.OLDEST -> list.sortedBy { it.createdAtMillis }
                    SortOption.AMOUNT_DESC -> list.sortedByDescending { abs(it.amountValue) }
                    SortOption.AMOUNT_ASC -> list.sortedBy { abs(it.amountValue) }
                }
            }

        _payments.value = filteredPayments
        _refunds.value = filteredRefunds
    }

    private fun parseCreatedAtMillis(createdAt: String): Long {
        runCatching { return Instant.parse(createdAt).toEpochMilli() }

        return runCatching {
            LocalDateTime.parse(createdAt).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }.getOrDefault(0L)
    }

    private fun TransactionHistoryRecord.toPaymentUI(): TransactionSummaryUI {
        val date = createdAt.substring(0, 10)
        val time = if (createdAt.length >= 16) createdAt.substring(11, 16) else ""

        val amount = totalAmount
        val millis = parseCreatedAtMillis(createdAt)

        return TransactionSummaryUI(
            id = id,
            amountText = "$totalAmount $currency",
            amountValue = amount,
            currency = currency,
            dateText = date,
            timeText = time,
            createdAtMillis = millis,
            isSuccessful = true,
            isRefunded = refundToTransactionId != null
        )
    }

    private fun TransactionHistoryRecord.toRefundUI(): RefundSummaryUI {
        val date = createdAt.substring(0, 10)
        val time = if (createdAt.length >= 16) createdAt.substring(11, 16) else ""

        val amount = totalAmount
        val millis = parseCreatedAtMillis(createdAt)

        return RefundSummaryUI(
            id = id,
            amountText = "-$totalAmount $currency",
            amountValue = -amount,
            currency = currency,
            dateText = date,
            timeText = time,
            createdAtMillis = millis,
            originalTransactionId = refundToTransactionId ?: ""
        )
    }
}

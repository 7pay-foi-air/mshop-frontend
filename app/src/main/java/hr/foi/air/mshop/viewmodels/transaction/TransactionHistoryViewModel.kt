package hr.foi.air.mshop.viewmodels.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.foi.air.mshop.core.models.TransactionHistoryRecord
import hr.foi.air.mshop.core.models.TransactionType
import hr.foi.air.mshop.core.repository.ITransactionRepository
import hr.foi.air.ws.NetworkService
import hr.foi.air.ws.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class TransactionSummaryUI(
    val id: String,
    val amountText: String,
    val dateText: String,
    val timeText: String,
    val isSuccessful: Boolean
)

data class RefundSummaryUI(
    val id: String,
    val amountText: String,
    val dateText: String,
    val timeText: String,
    val originalTransactionId: String  // "Povrat transakcije ID 123456"
)


class TransactionHistoryViewModel(
    private val repository: ITransactionRepository = TransactionRepository(NetworkService.transactionApi)
) : ViewModel() {
    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex: StateFlow<Int> = _selectedTabIndex

    fun onTabSelected(index: Int){
        _selectedTabIndex.value = index
    }

    private val _payments = MutableStateFlow<List<TransactionSummaryUI>>(emptyList())
    val payments: StateFlow<List<TransactionSummaryUI>> = _payments

    private val _refunds = MutableStateFlow<List<RefundSummaryUI>>(emptyList())
    val refunds: StateFlow<List<RefundSummaryUI>> = _refunds

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            try {
                val domain = repository.getTransactionsForCurrentUser()

                _payments.value = domain.payments.map { it.toPaymentUI() }
                _refunds.value = domain.refunds.map { it.toRefundUI() }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun TransactionHistoryRecord.toPaymentUI(): TransactionSummaryUI {
        val date = createdAt.substring(0, 10)
        val time = if (createdAt.length >= 16) createdAt.substring(11, 16) else ""

        return TransactionSummaryUI(
            id = id,
            amountText = "$totalAmount $currency",
            dateText = date,
            timeText = time,
            isSuccessful = true
        )
    }

    private fun TransactionHistoryRecord.toRefundUI(): RefundSummaryUI {
        val date = createdAt.substring(0, 10)
        val time = if (createdAt.length >= 16) createdAt.substring(11, 16) else ""

        return RefundSummaryUI(
            id = id,
            amountText = "-$totalAmount $currency",
            dateText = date,
            timeText = time,
            originalTransactionId = refundToTransactionId ?: ""
        )
    }


}
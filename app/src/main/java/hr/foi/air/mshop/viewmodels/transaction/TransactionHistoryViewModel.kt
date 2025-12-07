package hr.foi.air.mshop.viewmodels.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.foi.air.mshop.core.models.TransactionHistoryRecord
import hr.foi.air.mshop.core.models.TransactionType
import hr.foi.air.mshop.core.repository.ITransactionRepository
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
    private val repository: ITransactionRepository
) : ViewModel() {
    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex: StateFlow<Int> = _selectedTabIndex

    fun onTabSelected(index: Int){
        _selectedTabIndex.value = index
    }

    //MOCK
   /* private val _payments = MutableStateFlow(
        listOf(
            TransactionSummaryUI(
                id = "123456",
                amountText = "33€",
                dateText = "12.3.2025.",
                timeText = "18:07",
                isSuccessful = true
            ),
            TransactionSummaryUI(
                id = "789012",
                amountText = "15€",
                dateText = "10.3.2025.",
                timeText = "12:30",
                isSuccessful = true
            )
        )
    ) */

    private val _payments = MutableStateFlow<List<TransactionSummaryUI>>(emptyList())
    val payments: StateFlow<List<TransactionSummaryUI>> = _payments

    private val _refunds = MutableStateFlow<List<TransactionSummaryUI>>(emptyList())
    val refunds: StateFlow<List<TransactionSummaryUI>> = _refunds

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            try {
                val domainList = repository.getTransactionsForCurrentUser()

                val paymentList = domainList
                    .filter { it.type == TransactionType.PAYMENT }
                    .map { it.toUi() }

               /* val refundList = domainList
                    .filter { it.type == TransactionType.REFUND }
                    .map { it.toUi() } */

                _payments.value = paymentList
               // _refunds.value = refundList

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun TransactionHistoryRecord.toUi(): TransactionSummaryUI {
        return TransactionSummaryUI(
            id = id,
            amountText = "$totalAmount $currency",
            dateText = completedAt.substring(0, 10),
            timeText = completedAt.substring(11, 16),
            isSuccessful = isSuccessful
        )
    }

}
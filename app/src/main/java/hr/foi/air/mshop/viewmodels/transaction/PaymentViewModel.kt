package hr.foi.air.mshop.viewmodels.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.foi.air.mshop.core.models.Transaction
import hr.foi.air.mshop.core.repository.ITransactionRepository
import hr.foi.air.mshop.data.UIState
import hr.foi.air.ws.NetworkService
import hr.foi.air.ws.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PaymentViewModel(
    private val transactionRepository: ITransactionRepository =
        TransactionRepository(NetworkService.transactionApi)
): ViewModel() {
    private val _uiState = MutableStateFlow(UIState())
    val uiState: StateFlow<UIState> = _uiState

    fun processPayment(
        transaction: Transaction,
        onSuccess: (String)-> Unit,
        onError: (String)-> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = UIState(loading = true)

            val result = transactionRepository.createTransaction(transaction)

            _uiState.value = if (result.isSuccess) {
                val tr = result.getOrNull()
                val id = tr?.transactionId ?: ""
                onSuccess(id)
                UIState(successMessage = "Transakcija uspješna")
            } else {
                val message = result.exceptionOrNull()?.message ?: "Greška pri plaćanju"
                onError(message)
                UIState(errorMessage = result.exceptionOrNull()?.message)
            }
        }
    }
}
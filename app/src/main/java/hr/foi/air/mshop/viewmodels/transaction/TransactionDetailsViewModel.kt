package hr.foi.air.mshop.viewmodels.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.foi.air.mshop.core.models.TransactionDetails
import hr.foi.air.mshop.core.repository.ITransactionRepository
import hr.foi.air.mshop.data.UIState
import hr.foi.air.ws.NetworkService
import hr.foi.air.ws.repository.TransactionRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TransactionDetailsViewModel(
    private val repository: ITransactionRepository =
        TransactionRepo(NetworkService.transactionApi)
) : ViewModel() {

    private val _uiState = MutableStateFlow(UIState())
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    private val _details = MutableStateFlow<TransactionDetails?>(null)
    val details: StateFlow<TransactionDetails?> = _details.asStateFlow()

    private fun formatDate(iso: String): String {
        val parts = iso.substring(0, 10).split("-")
        return "${parts[2]}.${parts[1]}.${parts[0]}"
    }

    private fun formatTime(iso: String): String {
        return if (iso.length >= 16) iso.substring(11, 16) else ""
    }

    fun loadTransactionDetails(id: String) {
        viewModelScope.launch {
            _uiState.value = UIState(loading = true)

            val result = repository.getTransactionDetails(id)
            if (result.isFailure) {
                _uiState.value = UIState(errorMessage =
                    result.exceptionOrNull()?.message ?: "Greška pri dohvaćanju detalja.")
                return@launch
            }

            val raw = result.getOrNull()
            if (raw == null) {
                _details.value = null
                _uiState.value = UIState()
                return@launch
            }

            val refundFormatted = raw.copy(
                transactionDate = "${formatDate(raw.transactionDate)} u ${formatTime(raw.transactionDate)}"
            )

            val finalDetails =
                if (refundFormatted.transactionType == "Refund" && !refundFormatted.transactionRefundId.isNullOrBlank()) {
                    val originalResult = repository.getTransactionDetails(refundFormatted.transactionRefundId!!)
                    val original = originalResult.getOrNull()

                    if (original != null) {
                        refundFormatted.copy(items = original.items)
                    } else refundFormatted
                } else refundFormatted

            _details.value = finalDetails
            _uiState.value = UIState()
        }
    }

    fun refundTransaction(
        description: String = "Refund transaction",
        onComplete: (success: Boolean) -> Unit
    ) {
        val transactionId = details.value?.uuidTransaction ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true)

            val result = repository.refundTransaction(
                transactionId = transactionId,
                description = description
            )

            _uiState.value = if (result.isSuccess) {
                onComplete(true)
                UIState()
            } else {
                onComplete(false)
                UIState(errorMessage = result.exceptionOrNull()?.message)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

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
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class TransactionDetailsViewModel(
    private val repository: ITransactionRepository =
        TransactionRepo(NetworkService.transactionApi)
) : ViewModel() {

    private val _uiState = MutableStateFlow(UIState())
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    private val _details = MutableStateFlow<TransactionDetails?>(null)
    val details: StateFlow<TransactionDetails?> = _details.asStateFlow()

    private fun formatDate(iso: String): String {
        return DateTimeFormatter.ofPattern("dd.MM.yyyy")
            .withZone(ZoneId.systemDefault())
            .format(Instant.parse(iso))
    }

    private fun formatTime(iso: String): String {
        return DateTimeFormatter.ofPattern("HH:mm")
            .withZone(ZoneId.systemDefault())
            .format(Instant.parse(iso))
    }

    fun loadTransactionDetails(id: String) {
        viewModelScope.launch {
            _uiState.value = UIState(loading = true)

            val result = repository.getTransactionDetails(id)

            _uiState.value = if (result.isSuccess) {
                val raw = result.getOrNull()
                _details.value = raw?.copy(
                    transactionDate = "${formatDate(raw.transactionDate)} u ${formatTime(raw.transactionDate)}"
                )
                UIState()
            } else {
                UIState(errorMessage = result.exceptionOrNull()?.message
                    ?: "Greška pri dohvaćanju detalja.")
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

}

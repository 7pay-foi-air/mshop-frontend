package hr.foi.air.mshop.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ChargeAmountUIState(
    val text: String = "0,00€",
    val isFocused: Boolean = false,
    val wasVisited: Boolean = false
){
    val errorMessage: String?
        get() = if (wasVisited && text.isBlank()) "Unesite vrijednost transakcije" else null
}

class HomepageViewModel(
    private val productRepository: ProductRepository = MockProductRepository()
) : ViewModel() {
    private val _selectedProducts = MutableStateFlow<Map<Int, Int>>(emptyMap())
    private val _chargeAmountUIState = MutableStateFlow(ChargeAmountUIState())
    private val _searchQuery = MutableStateFlow("")

    val selectedProducts: StateFlow<Map<Int, Int>> = _selectedProducts.asStateFlow()
    val chargeAmountUIState: StateFlow<ChargeAmountUIState> = _chargeAmountUIState.asStateFlow()
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredProducts: StateFlow<List<Product>> = _searchQuery
        .combine(productRepository.getAllProducts()) { query, products ->
            if (query.isBlank()) {
                products
            } else {
                products.filter { it.name.contains(query, ignoreCase = true) }
            }
        }.stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun addProduct(product: Product) {
        viewModelScope.launch {
            val currentQuantity = _selectedProducts.value[product.id] ?: 0
            _selectedProducts.value += (product.id to (currentQuantity + 1))
            updateChargeAmountFromPrice()
        }
    }

    fun removeProduct(product: Product) {
        viewModelScope.launch {
            val currentQuantity = _selectedProducts.value[product.id] ?: 0
            if (currentQuantity > 1) {
                _selectedProducts.value += (product.id to (currentQuantity - 1))
            } else {
                _selectedProducts.value -= product.id
            }
            updateChargeAmountFromPrice()
        }
    }

    fun removeProductCompletely(product: Product){
        viewModelScope.launch {
            _selectedProducts.value -= product.id
            updateChargeAmountFromPrice()
        }
    }

    fun clearSelection() {
        viewModelScope.launch {
            _selectedProducts.value = emptyMap()
            updateChargeAmountFromPrice()
        }
    }

    fun onChargeAmountChange(newText: String){
        _chargeAmountUIState.value = _chargeAmountUIState.value.copy(text = newText)
    }

    fun onChargeAmountFocusChange(isFocused: Boolean){
        val currentState = _chargeAmountUIState.value
        _chargeAmountUIState.value = currentState.copy(
            isFocused = isFocused,
            wasVisited = if (!isFocused) true else currentState.wasVisited
        )
    }

    private fun updateChargeAmountFromPrice(){
        val allProducts = productRepository.getAllProducts().value
        val currentTotalPrice = _selectedProducts.value.entries.sumOf { ( productId, quantity) ->
            val product = allProducts.find { it.id == productId }
            (product?.price ?: 0.0) * quantity
        }
        _chargeAmountUIState.value = _chargeAmountUIState.value.copy(
            text = String.format("%.2f€", currentTotalPrice)
        )
    }
}
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

private val allProducts = listOf(
    Product(1, "Laptop Pro 15", "Moćan laptop s Intel i7, 16GB RAM", 1299.99),
    Product(2, "Bežični Miš X", "Ergonomski bežični miš s dugom baterijom", 25.50),
    Product(3, "Mehanička Tipkovnica K7", "RGB mehanička tipkovnica sa taktilnim prekidačima", 89.90),
    Product(4, "4K Monitor 27-inčni", "27\" 4K IPS monitor s HDR podrškom", 349.00),
    Product(5, "USB-C Hub 8-u-1", "Hub s više priključaka: HDMI, USB, Ethernet", 45.00),
    Product(6, "Gaming Slušalice G-Pro", "Slušalice s surround zvukom i mikrofonom", 119.99),
    Product(7, "Prijenosni SSD 1TB", "Brzi NVMe SSD za prijenos podataka", 99.99)
)

class HomepageViewModel : ViewModel() {
    private val _selectedProducts = MutableStateFlow<Map<Int, Int>>(emptyMap())
    private val _chargeAmountUIState = MutableStateFlow(ChargeAmountUIState())
    private val _searchQuery = MutableStateFlow("")

    val selectedProducts: StateFlow<Map<Int, Int>> = _selectedProducts.asStateFlow()
    val chargeAmountUIState: StateFlow<ChargeAmountUIState> = _chargeAmountUIState.asStateFlow()
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredProducts: StateFlow<List<Product>> = _searchQuery
        .combine(MutableStateFlow(allProducts)) { query, products ->
            if (query.isBlank()) {
                products
            } else {
                products.filter { it.name.contains(query, ignoreCase = true) }
            }
        }.stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = allProducts
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
        val currentTotalPrice = _selectedProducts.value.entries.sumOf { ( productId, quantity) ->
            val product = allProducts.find { it.id == productId }
            (product?.price ?: 0.0) * quantity
        }
        _chargeAmountUIState.value = _chargeAmountUIState.value.copy(
            text = String.format("%.2f€", currentTotalPrice)
        )
    }
}
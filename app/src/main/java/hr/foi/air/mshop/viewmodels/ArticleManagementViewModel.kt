package hr.foi.air.mshop.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

private val allProducts = MutableStateFlow(listOf(
    Product(1, "Laptop Pro 15", "Moćan laptop s Intel i7, 16GB RAM", 1299.99),
    Product(2, "Bežični Miš X", "Ergonomski bežični miš s dugom baterijom", 25.50),
    Product(3, "Mehanička Tipkovnica K7", "RGB mehanička tipkovnica sa taktilnim prekidačima", 89.90),
    Product(4, "4K Monitor 27-inčni", "27\" 4K IPS monitor s HDR podrškom", 349.00),
    Product(5, "USB-C Hub 8-u-1", "Hub s više priključaka: HDMI, USB, Ethernet", 45.00),
    Product(6, "Gaming Slušalice G-Pro", "Slušalice s surround zvukom i mikrofonom", 119.99),
    Product(7, "Prijenosni SSD 1TB", "Brzi NVMe SSD za prijenos podataka", 99.99)
))

class ArticleManagementViewModel: ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _productToDelete = MutableStateFlow<Product?>(null)
    val productToDelete: StateFlow<Product?> = _productToDelete.asStateFlow()

    val filteredProducts: StateFlow<List<Product>> = _searchQuery
        .combine(allProducts) { query, products ->
            if (query.isBlank()) {
                products
            } else {
                products.filter { it.name.contains(query, ignoreCase = true) }
            }
        }.stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = allProducts.value
        )

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onOpenDeleteDialog(product: Product) {
        _productToDelete.value = product
    }

    fun onDismissDeleteDialog() {
        _productToDelete.value = null
    }

    fun deleteProduct() {
        _productToDelete.value?.let { productToRemove ->
            allProducts.update { currentList ->
                currentList.filterNot { it.id == productToRemove.id }
            }
            println("Deleting product: ${productToRemove.name}")
        }
        onDismissDeleteDialog()
    }
}
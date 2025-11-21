package hr.foi.air.mshop.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class ArticleManagementViewModel(
    private val productRepository: ProductRepository = MockProductRepository()
): ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _productToDelete = MutableStateFlow<Product?>(null)
    val productToDelete: StateFlow<Product?> = _productToDelete.asStateFlow()

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

    fun onOpenDeleteDialog(product: Product) {
        _productToDelete.value = product
    }

    fun onDismissDeleteDialog() {
        _productToDelete.value = null
    }

    fun deleteProduct() {
        _productToDelete.value?.let { productToRemove ->
            productRepository.deleteProduct(productToRemove.id)
            println("Deleting product: ${productToRemove.name}")
        }
        onDismissDeleteDialog()
    }
}
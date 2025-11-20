package hr.foi.air.mshop.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import hr.foi.air.mshop.ui.components.ListItems.ProductListItem
import hr.foi.air.mshop.viewmodels.HomepageViewModel
import hr.foi.air.mshop.viewmodels.Product

private fun findProductById(id: Int, allProducts: List<Product>): Product? {
    return allProducts.find { it.id == id }
}

@Composable
fun CartScreen( viewModel: HomepageViewModel ) {
    val selectedProductsMap by viewModel.selectedProducts.collectAsState()
    val allProducts by viewModel.filteredProducts.collectAsState()

    if (selectedProductsMap.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Vaša košarica je prazna.")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(selectedProductsMap.entries.toList()) { (productId, quantity) ->
                val product = findProductById(productId, allProducts)

                if (product != null) {
                    ProductListItem(
                        product = product,
                        quantity = quantity,
                        onClick = { },
                        onIncrement = { viewModel.addProduct(product) },
                        onDecrement = { viewModel.removeProduct(product) },
                        showRemoveButton = true,
                        onRemove = { viewModel.removeProductCompletely(product) }
                    )
                }
            }
        }
    }
}
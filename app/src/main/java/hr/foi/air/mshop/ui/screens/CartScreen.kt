package hr.foi.air.mshop.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import hr.foi.air.mshop.core.models.Article
import hr.foi.air.mshop.ui.components.listItems.ProductListItem
import hr.foi.air.mshop.ui.theme.Dimens
import hr.foi.air.mshop.viewmodels.HomepageViewModel

private fun findProductById(id: Int, allProducts: List<Article>): Article? {
    return allProducts.find { it.id == id }
}

@Composable
fun CartScreen(viewModel: HomepageViewModel) {
    val selectedProductsMap by viewModel.selectedArticles.collectAsState()
    val allProducts by viewModel.filteredArticles.collectAsState()

    if (selectedProductsMap.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.screenPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Vaša košarica je prazna.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(Dimens.screenPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.md)
    ) {
        items(selectedProductsMap.entries.toList(), key = { it.key }) { (productId, quantity) ->
            val product = findProductById(productId, allProducts) ?: return@items

            ProductListItem(
                product = product,
                quantity = quantity,
                onClick = { },
                onIncrement = { viewModel.addArticle(product) },
                onDecrement = { viewModel.removeArticle(product) },
                showRemoveButton = true,
                onRemove = { viewModel.removeArticleCompletely(product) }
            )
        }
    }
}

package hr.foi.air.mshop.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import hr.foi.air.mshop.ui.components.listItems.ProductListItem
import hr.foi.air.mshop.ui.components.textFields.SearchField
import hr.foi.air.mshop.ui.theme.Dimens
import hr.foi.air.mshop.viewmodels.HomepageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaleScreen(viewModel: HomepageViewModel) {
    val query by viewModel.searchQuery.collectAsState()
    val filteredArticles by viewModel.filteredArticles.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.screenPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.md)
    ){
        SearchField(
            value = query,
            onValueChange = { viewModel.onSearchQueryChange(it) },
            placeholder = "Pretraži artikle...",
            leadingIcon = Icons.Default.Search
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(Dimens.md)

        ) {
            items(filteredArticles) { product ->
                ProductListItem(
                    product = product,
                    onClick = {  },
                    quantity = viewModel.selectedArticles.collectAsState().value[product.id] ?: 0,
                    onIncrement = { viewModel.addArticle(product) },
                    onDecrement = { viewModel.removeArticle(product) }
                )
            }
        }
    }
}
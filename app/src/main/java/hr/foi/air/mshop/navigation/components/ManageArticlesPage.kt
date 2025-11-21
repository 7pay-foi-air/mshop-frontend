package hr.foi.air.mshop.navigation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.foi.air.mshop.ui.components.ListItems.ArticleManagementListItem
import hr.foi.air.mshop.ui.components.SearchField
import hr.foi.air.mshop.viewmodels.ArticleManagementViewModel

@Composable
fun ManageArticlesPage(viewModel: ArticleManagementViewModel = viewModel()) {
    val query by viewModel.searchQuery.collectAsState()
    val filteredArticles by viewModel.filteredProducts.collectAsState()
    val productToDelete by viewModel.productToDelete.collectAsState()

    if (productToDelete != null) {
        AlertDialog(
            onDismissRequest = { viewModel.onDismissDeleteDialog() },
            title = { Text("Potvrda brisanja") },
            text = { Text("Jeste li sigurni da želite obrisati artikal '${productToDelete!!.name}'?") },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.deleteProduct() }
                ) {
                    Text("Obriši")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onDismissDeleteDialog() }) {
                    Text("Odustani")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "mShop",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 4.dp)
        )

        Text(
            "Upravljanje artiklima",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SearchField(
                value = query,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                placeholder = "Pretraži artikle...",
                leadingIcon = Icons.Default.Search,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = { /* TODO */ },
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Dodaj novi artikal",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredArticles) { product ->
                ArticleManagementListItem(
                    product = product,
                    onEditClicked = { /* TODO */ },
                    onDeleteClicked = { viewModel.onOpenDeleteDialog(product) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ManageArticlesPagePreview() {
    ManageArticlesPage()
}
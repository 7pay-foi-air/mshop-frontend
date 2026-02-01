package hr.foi.air.mshop.navigation.components.articleManagement

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import hr.foi.air.mshop.navigation.AppRoutes
import hr.foi.air.mshop.ui.components.listItems.ArticleManagementListItem
import hr.foi.air.mshop.ui.components.textFields.SearchField
import hr.foi.air.mshop.ui.theme.Dimens
import hr.foi.air.mshop.viewmodels.articleManagement.ArticleManagementViewModel

@Composable
fun ManageArticlesPage(
    navController: NavHostController,
    viewModel: ArticleManagementViewModel = viewModel()
) {
    val query by viewModel.searchQuery.collectAsState()
    val filteredArticles by viewModel.filteredArticles.collectAsState()
    val articleToDelete by viewModel.articleToDelete.collectAsState()

    if (articleToDelete != null) {
        AlertDialog(
            onDismissRequest = { viewModel.onDismissDeleteDialog() },
            title = { Text("Potvrda brisanja") },
            text = { Text("Jeste li sigurni da želite obrisati artikal '${articleToDelete!!.articleName}'?") },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteArticle() }) {
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
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = Dimens.screenPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "mShop",
            style = MaterialTheme.typography.displayLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.lg, bottom = Dimens.sm)
        )

        Text(
            text = "Upravljanje artiklima",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = Dimens.xl),
            textAlign = TextAlign.Center
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Dimens.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.sm)
        ) {
            SearchField(
                value = query,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                placeholder = "Pretraži artikle...",
                leadingIcon = Icons.Default.Search,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = { navController.navigate(AppRoutes.ADD_ARTICLE) },
                modifier = Modifier
                    .clip(RoundedCornerShape(Dimens.md))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Dodaj novi artikal",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Dimens.sm),
            verticalArrangement = Arrangement.spacedBy(Dimens.md),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = Dimens.md)
        ) {
            items(filteredArticles) { article ->
                ArticleManagementListItem(
                    modifier = Modifier.padding(bottom = Dimens.xs),
                    article = article,
                    onEditClicked = {
                        viewModel.onStartEditArticle(article)
                        navController.navigate(AppRoutes.EDIT_ARTICLE)
                    },
                    onDeleteClicked = { viewModel.onOpenDeleteDialog(article) }
                )
            }
        }
    }
}

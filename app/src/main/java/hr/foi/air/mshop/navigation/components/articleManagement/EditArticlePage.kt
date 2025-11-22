package hr.foi.air.mshop.navigation.components.articleManagement

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.foi.air.mshop.core.models.Article
import hr.foi.air.mshop.viewmodels.ArticleManagementViewModel
import hr.foi.air.mshop.viewmodels.HomepageViewModel

@Composable
fun EditArticlePage(
    viewModel: ArticleManagementViewModel = viewModel(),
    onSave: (Article) -> Unit,
    onCancel: () -> Unit
){
    val articleToEdit by viewModel.articleToEdit.collectAsState()

    articleToEdit?.let { article ->
        ArticleFormPage(
            articleToEdit = article,
            onSubmit = { updatedArticle ->
                onSave(updatedArticle)
            },
            onCancel = onCancel
        )
    }
}
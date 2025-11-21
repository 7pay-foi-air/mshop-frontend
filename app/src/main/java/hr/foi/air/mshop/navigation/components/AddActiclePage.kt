package hr.foi.air.mshop.navigation.components

import androidx.compose.runtime.Composable
import hr.foi.air.mshop.core.models.Article

@Composable
fun AddArticlePage(
    onAdd:(Article) -> Unit,
    onCancel: () -> Unit
){
    ArticleFormPage(
        articleToEdit = null,
        onSubmit = { newArticle -> onAdd(newArticle) },
        onCancel = onCancel
    )
}
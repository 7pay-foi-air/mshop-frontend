package hr.foi.air.mshop.navigation.components

import androidx.compose.runtime.Composable
import hr.foi.air.mshop.core.models.Article

@Composable
fun EditArticlePage(
    article: Article,
    onSave: (Article) -> Unit,
    onCancel: () -> Unit
){
    ArticleFormPage(
        articleToEdit = article,
        onSubmit = { updatedArticle -> onSave(updatedArticle) },
        onCancel = onCancel
    )
}
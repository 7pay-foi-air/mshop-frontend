package hr.foi.air.mshop.navigation.components.articleManagement

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.foi.air.mshop.core.models.Article
import hr.foi.air.mshop.viewmodels.ArticleManagementViewModel
import hr.foi.air.mshop.viewmodels.EditArticleViewModel

@Composable
fun EditArticlePage(
    onCancel: () -> Unit,
    onUpdatedSuccessfully: () -> Unit,
    articleVm: ArticleManagementViewModel = viewModel(),
    editVm: EditArticleViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by editVm.uiState.collectAsState()
    val articleToEdit by articleVm.articleToEdit.collectAsState()

    LaunchedEffect(uiState.successMessage, uiState.errorMessage) {

        uiState.successMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            editVm.clearMessages()
            articleVm.onFinishEditArticle()
            onUpdatedSuccessfully()
        }

        uiState.errorMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            editVm.clearMessages()
        }
    }

    articleToEdit?.let { article ->
        ArticleFormPage(
            articleToEdit = article,
            onSubmit = { updatedArticle ->
                editVm.updateArticle(updatedArticle, context)
            },
            onCancel = onCancel
        )
    }
}

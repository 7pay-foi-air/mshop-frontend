package hr.foi.air.mshop.navigation.components.articleManagement

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.foi.air.mshop.utils.AppMessageManager
import hr.foi.air.mshop.utils.AppMessageType
import hr.foi.air.mshop.viewmodels.articleManagement.ArticleFormViewModel
import hr.foi.air.mshop.viewmodels.articleManagement.ArticleManagementViewModel

@Composable
fun EditArticlePage(
    onCancel: () -> Unit,
    onUpdatedSuccessfully: () -> Unit,
    articleVm: ArticleManagementViewModel = viewModel(),
    editVm: ArticleFormViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by editVm.uiState.collectAsState()
    val articleToEdit by articleVm.articleToEdit.collectAsState()

    LaunchedEffect(uiState.successMessage, uiState.errorMessage) {

        uiState.successMessage?.let { msg ->
            AppMessageManager.show(msg, AppMessageType.SUCCESS)
            editVm.clearMessages()
            articleVm.onFinishEditArticle()
            onUpdatedSuccessfully()
        }

        uiState.errorMessage?.let { msg ->
            AppMessageManager.show(msg, AppMessageType.ERROR)
            editVm.clearMessages()
        }
    }

    articleToEdit?.let { article ->
        ArticleFormPage(
            articleToEdit = article,
            viewModel = editVm,
            onSubmit = {
            },
            onCancel = onCancel
        )
    }
}

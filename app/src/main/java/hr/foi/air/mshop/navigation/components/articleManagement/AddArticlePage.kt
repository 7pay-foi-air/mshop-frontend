package hr.foi.air.mshop.navigation.components.articleManagement

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.foi.air.mshop.utils.AppMessageManager
import hr.foi.air.mshop.utils.AppMessageType
import hr.foi.air.mshop.viewmodels.articleManagement.ArticleFormViewModel

@Composable
fun AddArticlePage(
    onCancel: () -> Unit,
    onAddedSuccessfully: () -> Unit,
    viewModel: ArticleFormViewModel = viewModel()
){
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.successMessage, uiState.errorMessage) {

        uiState.successMessage?.let { msg ->
            AppMessageManager.show(msg, AppMessageType.SUCCESS)
            viewModel.clearMessages()
            onAddedSuccessfully()
        }

        uiState.errorMessage?.let { msg ->
            AppMessageManager.show(msg, AppMessageType.ERROR)
            viewModel.clearMessages()
        }
    }

    ArticleFormPage(
        articleToEdit = null,
        viewModel = viewModel,
        onSubmit = {
        },
        onCancel = onCancel
    )
}
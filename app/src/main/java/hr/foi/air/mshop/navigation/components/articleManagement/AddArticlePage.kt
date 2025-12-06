package hr.foi.air.mshop.navigation.components.articleManagement

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
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
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
            onAddedSuccessfully()
        }

        uiState.errorMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
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
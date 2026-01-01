package hr.foi.air.mshop.navigation.components.userManagement

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.foi.air.mshop.navigation.components.articleManagement.ArticleFormPage
import hr.foi.air.mshop.viewmodels.articleManagement.ArticleFormViewModel
import hr.foi.air.mshop.viewmodels.articleManagement.ArticleManagementViewModel
import hr.foi.air.mshop.viewmodels.userManagement.UserFormViewModel
import hr.foi.air.mshop.viewmodels.userManagement.UserManagementViewModel

@Composable
fun EditUserPage(
    onCancel: () -> Unit,
    onUpdatedSuccessfully: () -> Unit,
    userVm: UserManagementViewModel = viewModel(),
    editVm: UserFormViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by editVm.uiState.collectAsState()
    val userToEdit by userVm.userToEdit.collectAsState()

    LaunchedEffect(uiState.successMessage, uiState.errorMessage) {

        uiState.successMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            editVm.clearMessages()
            userVm.onFinishEditUser()
            onUpdatedSuccessfully()
        }

        uiState.errorMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            editVm.clearMessages()
        }
    }

    userToEdit?.let { user ->
        UserFormPage(
            userToEdit = user,
            viewModel = editVm,
            onSubmit = {
            },
            onCancel = onCancel
        )
    }
}
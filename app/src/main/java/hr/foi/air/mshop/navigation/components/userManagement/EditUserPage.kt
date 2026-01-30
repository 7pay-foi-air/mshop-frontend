package hr.foi.air.mshop.navigation.components.userManagement

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.foi.air.mshop.utils.AppMessageManager
import hr.foi.air.mshop.utils.AppMessageType
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
            AppMessageManager.show(msg, AppMessageType.SUCCESS)
            editVm.clearMessages()
            userVm.onFinishEditUser()
            onUpdatedSuccessfully()
        }

        uiState.errorMessage?.let { msg ->
            AppMessageManager.show(msg, AppMessageType.ERROR)
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
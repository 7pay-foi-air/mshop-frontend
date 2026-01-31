package hr.foi.air.mshop.navigation.components.userManagement

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import hr.foi.air.mshop.utils.AppMessageManager
import hr.foi.air.mshop.utils.AppMessageType
import hr.foi.air.mshop.viewmodels.userManagement.UserFormViewModel

@Composable
fun ProfilePage(
    navController: NavHostController,
    viewModel: UserFormViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.fetchAndInitializeLoggedInUser()
    }

    LaunchedEffect(uiState.successMessage, uiState.errorMessage) {
        uiState.successMessage?.let { msg ->
            AppMessageManager.show(msg, AppMessageType.SUCCESS)
            viewModel.clearMessages()
            navController.popBackStack()
        }
        uiState.errorMessage?.let { msg ->
            AppMessageManager.show(msg, AppMessageType.ERROR)
            viewModel.clearMessages()
        }
    }

    UserFormPage(
        viewModel = viewModel,
        isProfilePage = true,
        onSubmit = { },
        onCancel = { navController.popBackStack() }
    )
}
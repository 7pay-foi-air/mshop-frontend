package hr.foi.air.mshop.navigation.components.userManagement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import hr.foi.air.mshop.navigation.AppRoutes
import hr.foi.air.mshop.utils.AppMessageManager
import hr.foi.air.mshop.utils.AppMessageType
import hr.foi.air.mshop.ui.components.listItems.UserManagementListItem
import hr.foi.air.mshop.ui.components.textFields.SearchField
import hr.foi.air.mshop.ui.theme.Dimens
import hr.foi.air.mshop.viewmodels.userManagement.UserManagementViewModel
import hr.foi.air.ws.data.SessionManager
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ManageUsersPage(
    navController: NavHostController,
    viewModel: UserManagementViewModel = viewModel()
) {
    val context = LocalContext.current
    val query by viewModel.searchQuery.collectAsState()
    val filteredUsers by viewModel.filteredUsers.collectAsState()
    val currentUserRole = SessionManager.currentUserRole?.lowercase() ?: ""

    LaunchedEffect(Unit) {
        viewModel.toastMessage.collectLatest { message ->
            AppMessageManager.show(message, AppMessageType.ERROR)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = Dimens.lg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "mShop",
            style = MaterialTheme.typography.displayLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.lg, bottom = Dimens.xs)
        )

        Text(
            text = "Upravljanje korisnicima",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = Dimens.lg)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Dimens.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.sm)
        ) {
            SearchField(
                value = query,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                placeholder = "Pretraži korisnike...",
                leadingIcon = Icons.Default.Search,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = { navController.navigate(AppRoutes.ADD_USER) },
                modifier = Modifier
                    .clip(RoundedCornerShape(Dimens.md))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Dodaj novog korisnika",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(Dimens.sm),
            contentPadding = PaddingValues(bottom = Dimens.md)
        ) {
            items(filteredUsers) { user ->
                val targetRole = user.role.lowercase()

                val canModerate = when (currentUserRole) {
                    "owner" -> true
                    "admin" -> {
                        targetRole != "owner" && targetRole != "admin"
                    }
                    else -> false
                }

                UserManagementListItem(
                    modifier = Modifier.padding(bottom = Dimens.xs),
                    user = user,
                    canModerate = canModerate,
                    onEditClicked = {
                        viewModel.onStartEditUser(user)
                        navController.navigate(AppRoutes.EDIT_USER)
                    },
                    onDeleteClicked = { viewModel.onDeleteUser(user) }
                )
            }
        }
    }
}

package hr.foi.air.mshop.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import hr.foi.air.mshop.navigation.components.AddUserPage
import hr.foi.air.mshop.navigation.components.Homepage
import hr.foi.air.mshop.navigation.components.LoginPassword
import hr.foi.air.mshop.navigation.components.LoginUsername
import hr.foi.air.mshop.navigation.components.ManageUsersPage
import hr.foi.air.mshop.navigation.components.RegistrationOrganizationPage
import hr.foi.air.mshop.navigation.drawerItems
import kotlinx.coroutines.launch

data class DrawerItem(
    val icon: ImageVector,
    val title: String,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationDrawer(
    drawerState: DrawerState,
    items: List<DrawerItem>,
    currentRoute: String?,
    onItemClick: (DrawerItem) -> Unit,
    navigationIcon: @Composable () -> Unit,
    content: @Composable (Modifier) -> Unit
) {
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = currentRoute in drawerItems.map {it.route},
        drawerContent = {
            ModalDrawerSheet {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "mShop", style = MaterialTheme.typography.headlineLarge)
                }

                items.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item.title) },
                        selected = currentRoute == item.route,
                        onClick = {
                            onItemClick(item)
                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        },
        content = {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { },
                        navigationIcon = navigationIcon
                    )
                }
            ) { paddingValues ->
                content(Modifier.padding(paddingValues))
            }
        }
    )
}

@Composable
fun MenuIconButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Izbornik"
        )
    }
}

@Composable
fun BackArrowButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Natrag"
        )
    }
}
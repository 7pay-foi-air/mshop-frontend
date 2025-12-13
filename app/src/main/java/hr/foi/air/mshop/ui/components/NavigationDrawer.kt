package hr.foi.air.mshop.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.DrawerState
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import hr.foi.air.ws.data.SessionManager
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
    onLogout: (() -> Unit)? = null,
    navigationIcon: @Composable () -> Unit,
    content: @Composable (Modifier) -> Unit
) {
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = currentRoute in drawerItems.map {it.route},
        drawerContent = {
            ModalDrawerSheet {
                Column(modifier = Modifier.fillMaxHeight()) {
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
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title
                                )
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = MaterialTheme.colorScheme.tertiary.copy(
                                    alpha = 0.20f
                                ),
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f) )

                    if (onLogout != null) {
                        NavigationDrawerItem(
                            label = { Text("Odjava") },
                            selected = false,
                            onClick = {
                                SessionManager.endSession()
                                scope.launch { drawerState.close() }
                                onLogout()
                            },
                            icon = { Icon(imageVector = Icons.Default.PowerSettingsNew, contentDescription = "Odjava") },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.20f),
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    SessionManager.currentUserRole.let { userRole ->
                        Text(
                            text = "Role: $userRole",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
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
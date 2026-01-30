package hr.foi.air.mshop.ui.components

import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import hr.foi.air.mshop.navigation.drawerItems
import hr.foi.air.mshop.ui.theme.Dimens
import hr.foi.air.ws.data.SessionManager
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
    val layoutDirection = LocalLayoutDirection.current


    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = currentRoute in drawerItems.map { it.route },
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxHeight(),
                drawerContainerColor = MaterialTheme.colorScheme.background
            ) {
                Column(modifier = Modifier.fillMaxHeight()) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Dimens.xxxl, bottom = Dimens.lg),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "mShop",
                            style = MaterialTheme.typography.displayLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(Dimens.md))
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Dimens.xl),
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(Modifier.height(Dimens.lg))
                    }

                    val itemColors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    items.forEach { item ->
                        NavigationDrawerItem(
                            label = { Text(item.title) },
                            selected = currentRoute == item.route,
                            onClick = {
                                onItemClick(item)
                                scope.launch { drawerState.close() }
                            },
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                            colors = itemColors
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    if (onLogout != null) {
                        NavigationDrawerItem(
                            label = { Text("Odjava") },
                            selected = false,
                            onClick = {
                                SessionManager.endSession()
                                scope.launch { drawerState.close() }
                                onLogout()
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.PowerSettingsNew,
                                    contentDescription = "Odjava"
                                )
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                            colors = itemColors
                        )
                    }

                    Text(
                        text = "Role: ${SessionManager.currentUserRole}",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimens.lg)
                    )
                }
            }
        },
        content = {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.background
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(paddingValues)
                ) {
                    content(
                        Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                    )

                    Box(
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(start = Dimens.sm, top = Dimens.xs)
                            .align(Alignment.TopStart)
                    ) {
                        navigationIcon()
                    }
                }
            }
        }

    )
}

@Composable
fun MenuIconButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(imageVector = Icons.Default.Menu, contentDescription = "Izbornik")
    }
}

@Composable
fun BackArrowButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Natrag")
    }
}

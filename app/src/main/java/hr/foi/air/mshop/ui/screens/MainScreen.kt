package hr.foi.air.mshop.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import hr.foi.air.mshop.MainActivity
import hr.foi.air.mshop.languagemodels.LlmChatDialog
import hr.foi.air.mshop.languagemodels.createAssistantIntentHandler
import hr.foi.air.mshop.navigation.AppNavHost
import hr.foi.air.mshop.navigation.authRoutes
import hr.foi.air.mshop.navigation.drawerItems
import hr.foi.air.mshop.navigation.menuRoutes
import hr.foi.air.mshop.ui.components.BackArrowButton
import hr.foi.air.mshop.ui.components.MenuIconButton
import hr.foi.air.mshop.ui.components.NavigationDrawer
import kotlinx.coroutines.launch

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val showNavigationUI = currentRoute !in authRoutes
    var showDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val mainActivity = (navController.context as? MainActivity)
    val assistantVm = mainActivity?.assistantViewModel
    val assistantHandler = mainActivity?.let { createAssistantIntentHandler(navController, it) }

    if (showDialog && assistantVm != null && assistantHandler != null) {
        LlmChatDialog(
            onDismissRequest = { showDialog = false },
            assistantViewModel = assistantVm,
            assistantHandler = assistantHandler
        )
    }

    Scaffold(
        floatingActionButton = {
            SmallFloatingActionButton(
                onClick = {
                    showDialog = true
                }
            ) {
                Icon(Icons.Default.Chat, contentDescription = "Test LLM")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            if (showNavigationUI) {
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

                NavigationDrawer(
                    drawerState = drawerState,
                    items = drawerItems,
                    currentRoute = currentRoute,
                    onItemClick = { item ->
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    navigationIcon = {
                        when (currentRoute) {
                            in menuRoutes -> MenuIconButton { scope.launch { drawerState.open() } }
                            else -> BackArrowButton { navController.navigateUp() }
                        }
                    }
                ) { modifier ->
                    AppNavHost(
                        navController = navController,
                        modifier = modifier.fillMaxSize()
                    )
                }
            } else {
                AppNavHost(
                    navController = navController,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
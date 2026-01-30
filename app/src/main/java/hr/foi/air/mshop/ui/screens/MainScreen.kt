package hr.foi.air.mshop.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import hr.foi.air.mshop.MainActivity
import hr.foi.air.mshop.languagemodels.LlmChatDialog
import hr.foi.air.mshop.languagemodels.createAssistantIntentHandler
import hr.foi.air.mshop.navigation.*
import hr.foi.air.mshop.ui.components.BackArrowButton
import hr.foi.air.mshop.ui.components.MenuIconButton
import hr.foi.air.mshop.ui.components.NavigationDrawer
import hr.foi.air.ws.repository.RepositoryProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.LayoutDirection
import hr.foi.air.mshop.utils.AppMessage
import hr.foi.air.mshop.utils.AppMessageManager

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
    val assistantHandler = mainActivity?.let {
        createAssistantIntentHandler(navController, it) { showDialog = false }
    }

    if (showDialog && assistantVm != null && assistantHandler != null) {
        LlmChatDialog(
            onDismissRequest = { showDialog = false },
            assistantViewModel = assistantVm,
            assistantHandler = assistantHandler,
            transactionRepo = RepositoryProvider.transactionRepo,
            userRepo = RepositoryProvider.userRepo
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            SmallFloatingActionButton(
                onClick = { showDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Chat, contentDescription = "AI chat")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
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
                    onLogout = {
                        navController.navigate(AppRoutes.LOGIN_GRAPH) {
                            popUpTo(0) { inclusive = true }
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
                        modifier = modifier
                            .fillMaxSize()
                            .padding(
                                start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                                top = paddingValues.calculateTopPadding(),
                                end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                                bottom = 0.dp
                            )
                    )
                }
            } else {
                AppNavHost(
                    navController = navController,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                            top = paddingValues.calculateTopPadding(),
                            end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                            bottom = 0.dp
                        )
                )
            }

            val message by AppMessageManager.message.collectAsState()

            message?.let {
                AppMessage(
                    message = it.text,
                    type = it.type,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp)
                )

                LaunchedEffect(it) {
                    delay(3000)
                    AppMessageManager.clear()
                }
            }
        }
    }
}


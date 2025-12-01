package hr.foi.air.mshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import hr.foi.air.mshop.languagemodels.ILanguageModel
import hr.foi.air.mshop.languagemodels.LlmTestDialog
import hr.foi.air.mshop.languagemodels.OnDeviceLLM
import hr.foi.air.mshop.navigation.*
import hr.foi.air.mshop.ui.components.BackArrowButton
import hr.foi.air.mshop.ui.components.MenuIconButton
import hr.foi.air.mshop.ui.components.NavigationDrawer
import hr.foi.air.mshop.ui.theme.MShopTheme
import kotlinx.coroutines.launch

class MainActivity() : ComponentActivity() {
    val languageModel : ILanguageModel = OnDeviceLLM(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        languageModel.initializeModel()

        setContent {
            MShopTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }

    override fun onDestroy() {
        languageModel.closeModel()
        super.onDestroy()
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val showNavigationUI = currentRoute !in authRoutes
    var showDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    if (showDialog) {
        val mainActivity = (navController.context as? MainActivity)
        LlmTestDialog(
            onDismissRequest = { showDialog = false },
            onQuery = { userInput ->
                mainActivity?.languageModel?.getResponse(userInput)
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            SmallFloatingActionButton(
                onClick = {
                    showDialog = true
                }
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Test LLM")
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

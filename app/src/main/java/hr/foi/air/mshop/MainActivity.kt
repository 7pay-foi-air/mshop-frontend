package hr.foi.air.mshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import hr.foi.air.mshop.ui.theme.MShopTheme
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import hr.foi.air.mshop.navigation.AppNavHost
import hr.foi.air.mshop.navigation.authRoutes
import hr.foi.air.mshop.navigation.drawerItems
import hr.foi.air.mshop.navigation.menuRoutes
import hr.foi.air.mshop.ui.components.BackArrowButton
import hr.foi.air.mshop.ui.components.MenuIconButton
import hr.foi.air.mshop.ui.components.NavigationDrawer
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MShopTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val showNavigationUI = currentRoute !in authRoutes

    if(showNavigationUI){
        val scope = rememberCoroutineScope()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

        NavigationDrawer(
            drawerState = drawerState,
            items = drawerItems,
            currentRoute = currentRoute,
            onItemClick = { item ->
                if (currentRoute != item.route) {
                    navController.navigate(item.route){
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            navigationIcon = {
                when (currentRoute){
                    in menuRoutes -> {
                        MenuIconButton { scope.launch { drawerState.open() } }
                    }
                    else -> {
                        BackArrowButton { navController.navigateUp() }
                    }
                }
            }
        ) { modifier ->
            AppNavHost(navController = navController, modifier = modifier)
        }
    } else {
        AppNavHost(navController = navController)
    }
}

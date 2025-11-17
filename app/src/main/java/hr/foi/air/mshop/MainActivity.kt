package hr.foi.air.mshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import hr.foi.air.mshop.navigation.components.RegistrationOrganizationPage
import hr.foi.air.mshop.ui.theme.MShopTheme
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import hr.foi.air.mshop.navigation.components.Homepage
import hr.foi.air.mshop.navigation.components.LoginPassword
import hr.foi.air.mshop.navigation.components.LoginUsername
import hr.foi.air.mshop.navigation.components.AddUserPage
import hr.foi.air.mshop.ui.components.DrawerItem
import hr.foi.air.mshop.ui.components.NavigationDrawer

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

    val authRoutes = setOf("logUsername", "logPassword")
    val showNavigationUI = currentRoute !in authRoutes

    val drawerItems = listOf(
        DrawerItem(
            icon = Icons.Default.Home,
            title = "Početna",
            route = "home"
        ),
        DrawerItem(
            icon = Icons.Default.Settings,
            title = "Upravljanje korisnicima",
            route = "manageUsers"
        )
    )

    if(showNavigationUI){
        NavigationDrawer(
            items = drawerItems,
            currentRoute = currentRoute,
            onItemClick = { item ->
                if (currentRoute != item.route) {
                    navController.navigate(item.route){
                        launchSingleTop = true
                        restoreState = true
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

@Composable
fun AppNavHost(
    navController: androidx.navigation.NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "logUsername",
        modifier = modifier
    ){
        composable("regOrg") {
            RegistrationOrganizationPage(
                onNext = { navController.navigate("addUser") }
            )
        }
        composable("manageUsers") {
            hr.foi.air.mshop.navigation.components.ManageUsersPage(
                onAddUser = { navController.navigate("addUser") }
            )
        }
        composable("logUsername") {
            LoginUsername(
                navController,
                onNext = {navController.navigate("logPassword")}
            )
        }
        composable("logPassword") {
            LoginPassword(
                onNext = { navController.navigate("manageUsers") }
            )
        }
        composable("home") {
            Homepage()
        }
        composable("addUser") {
            AddUserPage()
        }
    }
}

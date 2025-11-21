package hr.foi.air.mshop.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import hr.foi.air.mshop.core.models.Article
import hr.foi.air.mshop.navigation.components.AddArticlePage
import hr.foi.air.mshop.navigation.components.AddUserPage
import hr.foi.air.mshop.navigation.components.EditArticlePage
import hr.foi.air.mshop.navigation.components.Homepage
import hr.foi.air.mshop.navigation.components.LoginPassword
import hr.foi.air.mshop.navigation.components.LoginUsername
import hr.foi.air.mshop.navigation.components.ManageUsersPage
import hr.foi.air.mshop.navigation.components.RegistrationOrganizationPage
import hr.foi.air.mshop.ui.components.DrawerItem

object AppRoutes {
    const val LOGIN_USERNAME = "logUsername"
    const val LOGIN_PASSWORD = "logPassword"
    const val HOME = "home"
    const val MANAGE_USERS = "manageUsers"
    const val ADD_USER = "addUser"
    const val REGISTER_ORGANIZATION = "regOrg"
    const val ADD_ARTICLE = "addArticle"
}

// Used for routes where no icons appear in the top left corner
val authRoutes = setOf(AppRoutes.LOGIN_USERNAME, AppRoutes.LOGIN_PASSWORD)

val drawerItems = listOf(
    DrawerItem(
        icon = Icons.Default.Home,
        title = "Početna",
        route = AppRoutes.HOME
    ),
    DrawerItem(
        icon = Icons.Default.Settings,
        title = "Upravljanje korisnicima",
        route = AppRoutes.MANAGE_USERS
    ),

)
//Used for defining routes where the menu icon is displayed; others display the back arrow
val menuRoutes = drawerItems.map {it.route}.toSet()

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppRoutes.LOGIN_USERNAME,
        modifier = modifier
    ) {
        composable(AppRoutes.REGISTER_ORGANIZATION) {
            RegistrationOrganizationPage(
                onNext = { navController.navigate(AppRoutes.ADD_USER) }
            )
        }
        composable(AppRoutes.MANAGE_USERS) {
            ManageUsersPage(
                onAddUser = { navController.navigate(AppRoutes.ADD_USER) }
            )
        }
        composable(AppRoutes.LOGIN_USERNAME) {
            LoginUsername(
                navController,
                onNext = { navController.navigate(AppRoutes.LOGIN_PASSWORD) }
            )
        }
        composable(AppRoutes.LOGIN_PASSWORD) {
            LoginPassword(
                onNext = { navController.navigate(AppRoutes.MANAGE_USERS) }
            )
        }
        composable(AppRoutes.HOME) {
            Homepage()
        }
        composable(AppRoutes.ADD_USER) {
            AddUserPage()
        }


        composable(AppRoutes.ADD_ARTICLE) {
            AddArticlePage(
                onAdd = { newArticle ->
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }



    }
}
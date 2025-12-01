package hr.foi.air.mshop.navigation

import android.view.SurfaceControl
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import hr.foi.air.mshop.navigation.components.articleManagement.AddArticlePage
import hr.foi.air.mshop.navigation.components.userManagement.AddUserPage
import hr.foi.air.mshop.navigation.components.articleManagement.EditArticlePage
import hr.foi.air.mshop.navigation.components.Homepage
import hr.foi.air.mshop.navigation.components.login.LoginPassword
import hr.foi.air.mshop.navigation.components.login.LoginUsername
import hr.foi.air.mshop.navigation.components.articleManagement.ManageArticlesPage
import hr.foi.air.mshop.navigation.components.userManagement.ManageUsersPage
import hr.foi.air.mshop.navigation.components.RegistrationOrganizationPage
import hr.foi.air.mshop.navigation.components.transaction.PaymentPage
import hr.foi.air.mshop.ui.components.DrawerItem
import hr.foi.air.mshop.viewmodels.ArticleManagementViewModel
import hr.foi.air.mshop.viewmodels.HomepageViewModel
import hr.foi.air.mshop.viewmodels.LoginViewModel

object AppRoutes {
    // LOGIN
    const val LOGIN_GRAPH = "login"
    const val LOGIN_USERNAME = "logUsername"
    const val LOGIN_PASSWORD = "logPassword"
    
    // HOME
    const val HOME = "home"
    
    // USER MANAGEMENT
    const val MANAGE_USERS = "manageUsers"
    const val ADD_USER = "addUser"
    const val REGISTER_ORGANIZATION = "regOrg"
    
    // ARTICLE MANAGEMENT
    const val MANAGE_ARTICLES = "manageArticles"
    const val ADD_ARTICLE = "addArticle"
    const val EDIT_ARTICLE = "editArticle"

    const val PAYMENT = "payment"
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
        icon = Icons.Default.Person,
        title = "Upravljanje korisnicima",
        route = AppRoutes.MANAGE_USERS
    ),
    DrawerItem(
        icon = Icons.Default.Settings,
        title = "Upravljanje artiklima",
        route = AppRoutes.MANAGE_ARTICLES
    )
)

// Used for defining routes where the menu icon is displayed; others display the back arrow
val menuRoutes = drawerItems.map {it.route}.toSet()

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppRoutes.LOGIN_GRAPH,
        modifier = modifier
    ) {
        navigation(
            startDestination = AppRoutes.LOGIN_USERNAME,
            route = AppRoutes.LOGIN_GRAPH
        ){
            composable(AppRoutes.LOGIN_USERNAME) { backStackEntry ->
                // Get the NavGraph's backStackEntry and create the ViewModel
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(AppRoutes.LOGIN_GRAPH)
                }
                val loginViewModel: LoginViewModel = viewModel(parentEntry)

                LoginUsername(
                    viewModel = loginViewModel,
                    onNext = { navController.navigate(AppRoutes.LOGIN_PASSWORD) }
                )
            }
            composable(AppRoutes.LOGIN_PASSWORD) { backStackEntry ->
                // Do the same for the password screen
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(AppRoutes.LOGIN_GRAPH)
                }
                val loginViewModel: LoginViewModel = viewModel(parentEntry)

                LoginPassword(
                    viewModel = loginViewModel,
                    onForgotPassword = { /* TODO */ },
                    onLoginSuccess = {
                        navController.navigate(AppRoutes.HOME) {
                            popUpTo(AppRoutes.LOGIN_GRAPH) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        }
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
        composable(AppRoutes.HOME) {
            Homepage(navController)
        }
        composable(AppRoutes.ADD_USER) {
            AddUserPage()
        }
        composable(AppRoutes.MANAGE_ARTICLES){
            ManageArticlesPage(navController)
        }
        composable(AppRoutes.ADD_ARTICLE) {
            AddArticlePage(
                onCancel = { navController.navigateUp() },
                onAddedSuccessfully = { navController.navigateUp() }
            )
        }

        composable(AppRoutes.EDIT_ARTICLE) { entry ->
            val graphEntry = remember(entry) {
                navController.getBackStackEntry(AppRoutes.MANAGE_ARTICLES)
            }
            val articleViewModel: ArticleManagementViewModel = viewModel(graphEntry)

            EditArticlePage(
                articleVm = articleViewModel,
                onCancel = { navController.popBackStack() },
                onUpdatedSuccessfully = {
                    navController.popBackStack()
                }
            )
        }

        composable(AppRoutes.PAYMENT) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(AppRoutes.HOME)
            }
            val homepageViewModel: HomepageViewModel = viewModel(parentEntry)

            val chargeAmountState = homepageViewModel.chargeAmountUIState.collectAsState().value

            PaymentPage(
                totalAmount = chargeAmountState.text,   // npr. "123,45€"
                onPay = { cardData ->
                    //homepageViewModel.onPayClicked(cardData)
                }
            )
        }



    }
}
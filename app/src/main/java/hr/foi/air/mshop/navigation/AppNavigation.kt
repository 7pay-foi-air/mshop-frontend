package hr.foi.air.mshop.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Groups3
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
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
import hr.foi.air.mshop.navigation.components.login.FirstLoginPassword
import hr.foi.air.mshop.navigation.components.login.FirstLoginRecoveryToken
import hr.foi.air.mshop.navigation.components.login.FirstLoginSecurityQuestions
import hr.foi.air.mshop.navigation.components.transactionHistory.TransactionDetailsPage
import hr.foi.air.mshop.navigation.components.transactionHistory.TransactionHistoryPage
import hr.foi.air.mshop.navigation.components.userManagement.EditUserPage
import hr.foi.air.mshop.navigation.components.userManagement.ProfilePage
import hr.foi.air.mshop.ui.components.DrawerItem
import hr.foi.air.mshop.ui.screens.RecoverPasswordScreen
import hr.foi.air.mshop.viewmodels.articleManagement.ArticleManagementViewModel
import hr.foi.air.mshop.viewmodels.LoginViewModel
import hr.foi.air.mshop.viewmodels.userManagement.UserManagementViewModel
import hr.foi.air.ws.data.SessionManager
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import hr.foi.air.mshop.navigation.graphs.paymentGraph


object AppRoutes {
    // LOGIN
    const val LOGIN_GRAPH = "login"
    const val LOGIN_USERNAME = "logUsername"
    const val LOGIN_PASSWORD = "logPassword"
    const val RECOVER_PASSWORD = "recoverPassword"
    const val FIRST_LOGIN_PASSWORD = "firstLoginPassword"
    const val FIRST_LOGIN_SECURITY_QUESTIONS = "firstLoginSecQuestions"
    const val FIRST_LOGIN_RECOVERY = "firstLoginRecovery"

    // HOME
    const val HOME = "home"
    // USER MANAGEMENT
    const val MANAGE_USERS = "manageUsers"
    const val ADD_USER = "addUser"
    const val EDIT_USER = "editUser"

    const val PROFILE_USER = "profileUser"
    const val REGISTER_ORGANIZATION = "regOrg"

    // ARTICLE MANAGEMENT
    const val MANAGE_ARTICLES = "manageArticles"
    const val ADD_ARTICLE = "addArticle"
    const val EDIT_ARTICLE = "editArticle"

    //PAYMENTS
    const val PAYMENT = "payment?amount={amount}&assistant={assistant}"
    const val PAYMENT_PROCESSING = "payment_processing"
    const val PAYMENT_DONE = "payment_done"

    //TRANSACTION HISTORY
    const val TRANSACTION_HISTORY = "transaction_history?from={from}&to={to}"

    const val TRANSACTION_DETAILS = "transaction_details/{id}"
    fun transactionDetails(id: String) = "transaction_details/$id"
}

// Used for routes where no icons appear in the top left corner
val authRoutes = setOf(
    AppRoutes.LOGIN_USERNAME,
    AppRoutes.LOGIN_PASSWORD,
    AppRoutes.FIRST_LOGIN_PASSWORD,
    AppRoutes.FIRST_LOGIN_SECURITY_QUESTIONS,
    AppRoutes.FIRST_LOGIN_RECOVERY,
    AppRoutes.RECOVER_PASSWORD
)

val drawerItems: List<DrawerItem>
    get(){
        val allItems = listOf(
            DrawerItem(
                icon = Icons.Default.Home,
                title = "Početna",
                route = AppRoutes.HOME
            ),
            DrawerItem(
                icon = Icons.Default.Groups3,
                title = "Upravljanje korisnicima",
                route = AppRoutes.MANAGE_USERS
            ),
            DrawerItem(
                icon = Icons.Default.Category,
                title = "Upravljanje artiklima",
                route = AppRoutes.MANAGE_ARTICLES
            ),
            DrawerItem(
                icon = Icons.Default.MonetizationOn,
                title = "Povijest transakcija",
                route = AppRoutes.TRANSACTION_HISTORY
            ),
            DrawerItem(
                icon = Icons.Default.AccountCircle,
                title = "Korisnički profil",
                route = AppRoutes.PROFILE_USER
            )
        )
        if (SessionManager.currentUserRole == "cashier"){
            return allItems.filter {
                it.route != AppRoutes.MANAGE_USERS && it.route != AppRoutes.MANAGE_ARTICLES
            }
        }
        return allItems
    }

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
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(AppRoutes.LOGIN_GRAPH)
                }
                val loginViewModel: LoginViewModel = viewModel(parentEntry)

                LoginPassword(
                    viewModel = loginViewModel,
                    onForgotPassword = {
                        navController.navigate(AppRoutes.RECOVER_PASSWORD)
                    },
                    onLoginSuccess = {
                        navController.navigate(AppRoutes.HOME) {
                            popUpTo(AppRoutes.LOGIN_GRAPH) { inclusive = true }
                        }
                    },
                    onFirstLogin = {
                        navController.navigate(AppRoutes.FIRST_LOGIN_PASSWORD) {
                            popUpTo(AppRoutes.LOGIN_USERNAME) { inclusive = true }
                        }
                    }
                )
            }
            composable(AppRoutes.FIRST_LOGIN_PASSWORD) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(AppRoutes.LOGIN_GRAPH)
                }
                val loginViewModel: LoginViewModel = viewModel(parentEntry)

                FirstLoginPassword(
                    viewModel = loginViewModel,
                    onNext = {
                        navController.navigate(AppRoutes.FIRST_LOGIN_RECOVERY) {
                            popUpTo(AppRoutes.FIRST_LOGIN_PASSWORD) { inclusive = true }
                        }
                    }
                )
            }
            composable(AppRoutes.FIRST_LOGIN_RECOVERY) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(AppRoutes.LOGIN_GRAPH)
                }
                val loginViewModel: LoginViewModel = viewModel(parentEntry)

                FirstLoginRecoveryToken(
                    viewModel = loginViewModel,
                    onFinish = {
                        navController.navigate(AppRoutes.FIRST_LOGIN_SECURITY_QUESTIONS) {
                            popUpTo(AppRoutes.FIRST_LOGIN_RECOVERY) { inclusive = true }
                        }
                    }
                )
            }
            composable(AppRoutes.FIRST_LOGIN_SECURITY_QUESTIONS) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(AppRoutes.LOGIN_GRAPH)
                }
                val loginViewModel: LoginViewModel = viewModel(parentEntry)

                FirstLoginSecurityQuestions(
                    viewModel = loginViewModel,
                    onNext = {
                        navController.navigate(AppRoutes.HOME) {
                            popUpTo(AppRoutes.LOGIN_GRAPH) { inclusive = true }
                        }
                    }
                )
            }
            composable(AppRoutes.RECOVER_PASSWORD) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(AppRoutes.LOGIN_GRAPH)
                }
                val loginViewModel: LoginViewModel = viewModel(parentEntry)

                RecoverPasswordScreen(
                    viewModel = viewModel(),
                    initialUsername = loginViewModel.username
                )
            }
        }
        composable(AppRoutes.REGISTER_ORGANIZATION) {
            RegistrationOrganizationPage(
                onNext = { navController.navigate(AppRoutes.ADD_USER) }
            )
        }
        composable(AppRoutes.MANAGE_USERS) {
            ManageUsersPage(navController)
        }
        composable(AppRoutes.HOME) {
            Homepage(navController)
        }
        composable(AppRoutes.ADD_USER) {
            AddUserPage(
                onUserAdded = {
                    navController.popBackStack()
                }
            )
        }
        composable(AppRoutes.EDIT_USER) { entry ->
            val graphEntry = remember(entry) {
                navController.getBackStackEntry(AppRoutes.MANAGE_USERS)
            }
            val userViewModel: UserManagementViewModel = viewModel(graphEntry)

            EditUserPage(
                userVm = userViewModel,
                onCancel = { navController.popBackStack() },
                onUpdatedSuccessfully = {
                    navController.popBackStack()
                }
            )
        }

        composable(AppRoutes.PROFILE_USER) {
            ProfilePage(navController)
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

        paymentGraph(navController)

        composable(
            route = "transaction_history?from={from}&to={to}",
            arguments = listOf(
                navArgument("from") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("to") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val fromArg = backStackEntry.arguments?.getString("from")
            val toArg = backStackEntry.arguments?.getString("to")

            val formatter = DateTimeFormatter.ISO_DATE
            val fromDate = fromArg?.takeIf { it != "{from}" }?.let { LocalDate.parse(it, formatter) }
            val toDate = toArg?.takeIf { it != "{to}" }?.let { LocalDate.parse(it, formatter) }

            TransactionHistoryPage(
                navController = navController,
                initialFromDate = fromDate,
                initialToDate = toDate
            )
        }

        composable(
            route = AppRoutes.TRANSACTION_DETAILS,
            arguments = listOf(
                navArgument("id") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")!!
            TransactionDetailsPage(
                navController = navController,
                transactionId = id
            )
        }

    }
}
package hr.foi.air.mshop.navigation.graphs

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import hr.foi.air.mshop.core.models.Transaction
import hr.foi.air.mshop.navigation.AppRoutes
import hr.foi.air.mshop.navigation.components.transaction.PaymentDonePage
import hr.foi.air.mshop.navigation.components.transaction.PaymentPage
import hr.foi.air.mshop.navigation.components.transaction.PaymentProcessingPage
import hr.foi.air.mshop.utils.AppMessageManager
import hr.foi.air.mshop.utils.AppMessageType
import hr.foi.air.mshop.viewmodels.HomepageViewModel
import hr.foi.air.mshop.viewmodels.transaction.PaymentViewModel

fun NavGraphBuilder.paymentGraph(navController: NavHostController) {

    composable(AppRoutes.PAYMENT_PROCESSING) {
        PaymentProcessingPage()
    }

    composable(
        route = AppRoutes.PAYMENT,
        arguments = listOf(
            navArgument("amount") {
                type = NavType.StringType
                defaultValue = ""
                nullable = true
            },
            navArgument("assistant") {
                type = NavType.StringType
                nullable = true
            }
        )
    ) { backStackEntry ->

        val context = LocalContext.current

        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry(AppRoutes.HOME)
        }
        val homepageViewModel: HomepageViewModel = viewModel(parentEntry)
        val paymentViewModel: PaymentViewModel = viewModel()

        val chargeAmountState = homepageViewModel.chargeAmountUIState.collectAsState().value

        val amountFromArguments = backStackEntry.arguments?.getString("amount")
        val assistantFromArgumentsString = backStackEntry.arguments?.getString("assistant")
        val assistantFromArguments = assistantFromArgumentsString?.toBooleanStrictOrNull() ?: false

        val confirmedAmount =  homepageViewModel.confirmedAmount.collectAsState().value ?: 0.0

        val finalTotalAmount: Double =
            if (assistantFromArguments && amountFromArguments != null) {
                amountFromArguments
                    .replace("€", "")
                    .replace(".", "")
                    .replace(",", ".")
                    .trim()
                    .toDoubleOrNull() ?: 0.0
            } else {
                chargeAmountState.text
                    .replace("€", "")
                    .replace(".", "")
                    .replace(",", ".")
                    .trim()
                    .toDoubleOrNull() ?: 0.0
            }

        PaymentPage(
            totalAmount = finalTotalAmount,
            onPay = { cardData -> // cardData se ne salje na backend

                if (!assistantFromArguments) {

                    if(homepageViewModel.hasSelectedItems()){
                        val transaction = homepageViewModel.buildTransaction()

                        if (transaction != null) {
                            navController.navigate(AppRoutes.PAYMENT_PROCESSING)

                            paymentViewModel.processPayment(
                                transaction = transaction,
                                onSuccess = { transactionId ->
                                    navController.navigate("${AppRoutes.PAYMENT_DONE}/$transactionId") {
                                        popUpTo(AppRoutes.PAYMENT_PROCESSING) { inclusive = true }
                                    }
                                    homepageViewModel.clearSelection()
                                },
                                onError = { errorMsg ->
                                    navController.popBackStack()
                                    AppMessageManager.show("Dogodila se greška!", AppMessageType.ERROR)
                                }
                            )
                        }
                    }
                    else{
                        val transaction = Transaction(
                            description = "Kupnja u mShopu",
                            items = emptyList(),
                            totalAmount = confirmedAmount,
                            currency = "EUR"
                        )

                        Log.d("AppNavHost", "transaction: $transaction")

                        navController.navigate(AppRoutes.PAYMENT_PROCESSING)
                        paymentViewModel.processPayment(
                            transaction = transaction,
                            onSuccess = { transactionId ->
                                navController.navigate("${AppRoutes.PAYMENT_DONE}/$transactionId") {
                                    popUpTo(AppRoutes.PAYMENT_PROCESSING) { inclusive = true }
                                }
                            },
                            onError = { errorMsg ->
                                navController.popBackStack()
                                AppMessageManager.show("Dogodila se greška!", AppMessageType.ERROR)
                            }
                        )
                    }
                }
                else if (amountFromArguments != null) {

                    val amount = amountFromArguments
                        .replace("€", "")
                        .replace(",", ".")
                        .trim()
                        .toDoubleOrNull() ?: 0.0

                    val transaction = Transaction(
                        description = "Kupnja u mShopu",
                        items = emptyList(),
                        totalAmount = amount,
                        currency = "EUR"
                    )

                    Log.d("AppNavHost", "transaction: $transaction")

                    navController.navigate(AppRoutes.PAYMENT_PROCESSING)
                    paymentViewModel.processPayment(
                        transaction = transaction,
                        onSuccess = { transactionId ->
                            navController.navigate("${AppRoutes.PAYMENT_DONE}/$transactionId") {
                                popUpTo(AppRoutes.PAYMENT_PROCESSING) { inclusive = true }
                            }
                        },
                        onError = { errorMsg ->
                            navController.popBackStack()
                            AppMessageManager.show("Dogodila se greška!", AppMessageType.ERROR)
                        }
                    )
                } else {
                    AppMessageManager.show("Košarica je prazna!", AppMessageType.INFO)
                }
            }
        )
    }

    composable("${AppRoutes.PAYMENT_DONE}/{transactionId}") { backStackEntry ->
        val transactionId = backStackEntry.arguments?.getString("transactionId") ?: "—"

        PaymentDonePage(
            transactionId = transactionId,
            onBackToHome = {
                navController.popBackStack(AppRoutes.HOME, inclusive = false)
            }
        )
    }
}

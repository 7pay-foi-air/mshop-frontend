package hr.foi.air.mshop.navigation.components.transactionHistory

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import hr.foi.air.mshop.ui.screens.PaymentsScreen
import hr.foi.air.mshop.ui.screens.RefundsScreen
import hr.foi.air.mshop.viewmodels.transaction.TransactionHistoryViewModel

@Composable
fun TransactionHistoryPage(
    navController: NavHostController,
    viewModel: TransactionHistoryViewModel = viewModel()
) {
    val selectedTabIndex by viewModel.selectedTabIndex.collectAsState()

    val tabTitles = listOf("Plaćanja", "Povrati")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "mShop",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Povijest transakcija",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { viewModel.onTabSelected(index) },
                    text = { Text(title) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTabIndex) {

            0 -> PaymentsScreen(
                viewModel = viewModel,
                onTransactionClick = { id ->
                    //navController.navigate("${AppRoutes.TRANSACTION_DETAILS}/$id")
                }
            )

            1 -> RefundsScreen(
                viewModel = viewModel,
                onTransactionClick = { id ->
                    //navController.navigate("${AppRoutes.TRANSACTION_DETAILS}/$id")
                }
            )
        }
    }
}
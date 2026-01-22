package hr.foi.air.mshop.navigation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import hr.foi.air.mshop.ui.components.textFields.UnderLabelTextField
import hr.foi.air.mshop.ui.screens.CartScreen
import hr.foi.air.mshop.ui.screens.SaleScreen
import hr.foi.air.mshop.viewmodels.HomepageViewModel

@Composable
fun Homepage(
    navController: NavHostController,
    homepageViewModel: HomepageViewModel = viewModel()
) {
    val chargeAmountState by homepageViewModel.chargeAmountUIState.collectAsState()

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("Svi artikli", "Košarica")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "mShop",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp)
        )

        Text(
            "Prodaja:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(start = 8.dp, bottom = 5.dp)
                .fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            UnderLabelTextField(
                value = chargeAmountState.text,
                placeholder = "00,00€",
                onValueChange = { homepageViewModel.onChargeAmountChange(it) },
                caption = "Iznos",
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { focusState ->
                        homepageViewModel.onChargeAmountFocusChange(focusState.isFocused)
                    },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default,
                isError = chargeAmountState.errorMessage != null,
                errorText = chargeAmountState.errorMessage,
            )

            IconButton(onClick = { homepageViewModel.clearSelection() }) {
                Icon(
                    Icons.Filled.Cancel,
                    contentDescription = "Cancel",
                    modifier = Modifier.padding(top = 5.dp)
                )
            }

            IconButton(onClick = {
                navController.navigate("payment")
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Continue",
                    modifier = Modifier.padding(top = 5.dp)
                )
            }
        }

        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp)
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title)
                    }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> SaleScreen( viewModel = homepageViewModel )
            1 -> CartScreen( viewModel = homepageViewModel )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomepagePreview() {
    val navController = androidx.navigation.compose.rememberNavController()
    Homepage(navController)
}
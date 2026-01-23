package hr.foi.air.mshop.navigation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import hr.foi.air.mshop.ui.components.textFields.UnderLabelTextField
import hr.foi.air.mshop.ui.screens.CartScreen
import hr.foi.air.mshop.ui.screens.SaleScreen
import hr.foi.air.mshop.ui.theme.Dimens
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
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.screenPadding, vertical = Dimens.lg),
            verticalArrangement = Arrangement.spacedBy(Dimens.md),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "mShop",
                style = MaterialTheme.typography.displayLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Prodaja:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.sm)
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
                        imageVector = Icons.Filled.Cancel,
                        contentDescription = "Očisti"
                    )
                }

                IconButton(onClick = { navController.navigate("payment") }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Nastavi"
                    )
                }
            }

            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTabIndex) {
                0 -> SaleScreen(viewModel = homepageViewModel)
                1 -> CartScreen(viewModel = homepageViewModel)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomepagePreview() {
    val navController = androidx.navigation.compose.rememberNavController()
    Homepage(navController)
}

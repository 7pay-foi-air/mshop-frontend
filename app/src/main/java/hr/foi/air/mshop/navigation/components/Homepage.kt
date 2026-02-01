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
import hr.foi.air.mshop.utils.AppMessageManager
import hr.foi.air.mshop.utils.AppMessageType
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
                .padding(horizontal = Dimens.screenPadding)
                .padding(top = Dimens.sm, bottom = Dimens.sm),
            verticalArrangement = Arrangement.spacedBy(Dimens.sm),
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
                    value = if (chargeAmountState.text == "0,00") "" else "${chargeAmountState.text}€",
                    placeholder = "0,00€",
                    onValueChange = { newValue ->
                        val cleanValue = newValue.replace("€", "")
                        homepageViewModel.onChargeAmountChange(cleanValue)
                    },
                    caption = "Iznos",
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged { focusState ->
                            homepageViewModel.onChargeAmountFocusChange(focusState.isFocused)
                        },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    isError = chargeAmountState.errorMessage != null,
                    errorText = chargeAmountState.errorMessage,
                )

                IconButton(onClick = { homepageViewModel.clearSelection() }) {
                    Icon(
                        imageVector = Icons.Filled.Cancel,
                        contentDescription = "Očisti"
                    )
                }

                IconButton(onClick = {
                    fun isGreaterThanZero(input: String): Boolean {
                        val number = input
                            .replace("€", "")
                            .replace(".", "")
                            .replace(",", ".")
                            .trim()
                            .toDoubleOrNull()

                        return number != null && number > 0.0
                    }

                    if(isGreaterThanZero(homepageViewModel.chargeAmountUIState.value.text))
                    {
                        homepageViewModel.confirmAmountFromText()
                        navController.navigate("payment")
                    }
                    else{
                        AppMessageManager.show("Iznos more biti veći od nule!",AppMessageType.ERROR)
                    }
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Nastavi"
                    )
                }
            }

            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.xs)
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

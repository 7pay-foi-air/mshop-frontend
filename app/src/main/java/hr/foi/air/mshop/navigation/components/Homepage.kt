package hr.foi.air.mshop.navigation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import hr.foi.air.mshop.ui.components.UnderLabelTextField

@Composable
fun Homepage() {
    var chargeAmount by remember { mutableStateOf("00,00€") }
    val chargeAmountVisited = false
    val chargeAmountEmpty = chargeAmount.isEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
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
                value = chargeAmount,
                placeholder = "00,00€",
                onValueChange = { chargeAmount = it},
                caption = "Iznos",
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { f ->
                        if (f.isFocused) {
                            if (chargeAmount == "00,00€") {
                                chargeAmount = ""
                            }
                        } else {
                            if (chargeAmount.isEmpty()) {
                                chargeAmount = "00,00€"
                            }
                        }
                    },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default,
                isError = chargeAmountVisited && chargeAmountEmpty,
                errorText = if (chargeAmountVisited && chargeAmountEmpty) "Potrebna vrijednost transakcije" else null,
            )

            IconButton(onClick = { /* TODO */ }) {
                Icon(
                    Icons.Filled.Cancel,
                    contentDescription = "Cancel",
                    modifier = Modifier.padding(top = 5.dp)
                )
            }

            IconButton(onClick = { /* TODO */ }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Continue",
                    modifier = Modifier.padding(top = 5.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomepagePreview() {
    Homepage()
}
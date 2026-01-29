package hr.foi.air.mshop.navigation.components.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import hr.foi.air.mshop.core.models.CardPaymentData
import hr.foi.air.mshop.ui.components.buttons.StyledButton
import hr.foi.air.mshop.ui.components.textFields.UnderLabelTextField
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.graphics.Color
import hr.foi.air.mshop.BuildConfig
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import hr.foi.air.mshop.utils.randomCardNumber
import hr.foi.air.mshop.utils.randomCvc
import hr.foi.air.mshop.utils.randomExpiry
import hr.foi.air.mshop.utils.toHrCurrency

@Composable
fun PaymentPage(
    totalAmount: Double,
    onPay: (CardPaymentData) -> Unit
) {
    var cardNumber by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }

    val cardNumberInvalid = cardNumber.replace(" ", "").length !in 13..19
    val expiryInvalid = !expiry.matches(Regex("^(0[1-9]|1[0-2])/\\d{2}\$"))
    val cvvInvalid = !cvv.matches(Regex("^\\d{3,4}\$"))

    val allValid = cardNumber.isNotBlank() &&
            expiry.isNotBlank() &&
            cvv.isNotBlank() &&
            !cardNumberInvalid &&
            !expiryInvalid &&
            !cvvInvalid

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                "mShop",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 4.dp)
            )

            Text(
                "Plaćanje",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            if (BuildConfig.DEBUG) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(bottom = 24.dp)
                        .clickable {
                            cardNumber = randomCardNumber()
                            expiry = randomExpiry()
                            cvv = randomCvc()
                        }
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoFixHigh,
                        contentDescription = "Automatski popuni",
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "Automatski popuni",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    )
                }
            }


            UnderLabelTextField(
                caption = "Broj kartice",
                value = cardNumber,
                onValueChange = { cardNumber = it },
                placeholder = "1234 5678 9012 3456",
                isError = cardNumber.isNotBlank() && cardNumberInvalid,
                errorText = if (cardNumber.isNotBlank() && cardNumberInvalid)
                    "Neispravan broj kartice" else null
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                UnderLabelTextField(
                    caption = "Vrijedi do",
                    value = expiry,
                    onValueChange = { expiry = it },
                    placeholder = "MM/YY",
                    modifier = Modifier.weight(1f),
                    isError = expiry.isNotBlank() && expiryInvalid,
                    errorText = if (expiry.isNotBlank() && expiryInvalid)
                        "Format MM/YY" else null
                )

                UnderLabelTextField(
                    caption = "CVV",
                    value = cvv,
                    onValueChange = { cvv = it },
                    placeholder = "123",
                    modifier = Modifier.weight(1f),
                    isError = cvv.isNotBlank() && cvvInvalid,
                    errorText = if (cvv.isNotBlank() && cvvInvalid)
                        "3–4 znamenke" else null
                )
            }

            Spacer(Modifier.height(32.dp))

            Text(
                text = "UKUPNO: ${totalAmount.toHrCurrency()} €",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Spacer(Modifier.height(24.dp))

            StyledButton(
                label = "PLATI",
                enabled = allValid,
                onClick = {
                    onPay(
                        CardPaymentData(
                            cardNumber = cardNumber.trim(),
                            expiry = expiry.trim(),
                            cvv = cvv.trim()
                        )
                    )
                }
            )
        }
    }

}
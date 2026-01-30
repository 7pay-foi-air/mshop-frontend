package hr.foi.air.mshop.navigation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import hr.foi.air.mshop.ui.components.buttons.NextArrow
import hr.foi.air.mshop.ui.components.textFields.UnderLabelTextField
import hr.foi.air.mshop.ui.theme.Dimens

@Composable
fun RegistrationOrganizationPage(
    onNext: () -> Unit = {}
) {
    var organizationName by remember { mutableStateOf("") }
    var organizationOIB by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phoneNum by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.screenPadding)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.md)
    ) {
        Spacer(modifier = Modifier.height(Dimens.lg))

        Text(
            text = "mShop",
            style = MaterialTheme.typography.displayMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Registracija organizacije",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Dimens.md))

        UnderLabelTextField(
            caption = "Ime organizacije",
            value = organizationName,
            onValueChange = { organizationName = it },
            placeholder = ""
        )

        UnderLabelTextField(
            caption = "OIB organizacije",
            value = organizationOIB,
            onValueChange = { organizationOIB = it },
            placeholder = ""
        )

        UnderLabelTextField(
            caption = "Adresa organizacije",
            value = address,
            onValueChange = { address = it },
            placeholder = ""
        )

        UnderLabelTextField(
            caption = "Broj telefona",
            value = phoneNum,
            onValueChange = { phoneNum = it },
            placeholder = "+385 98 123 4567"
        )

        UnderLabelTextField(
            caption = "E-mail",
            value = email,
            onValueChange = { email = it },
            placeholder = "mShop@gmail.com"
        )

        Spacer(modifier = Modifier.height(Dimens.lg))

        NextArrow(
            modifier = Modifier.align(Alignment.End),
            size = Dimens.nextArrowSize,
            onClick = onNext
        )

        Spacer(modifier = Modifier.height(Dimens.lg))
    }
}

package hr.foi.air.mshop.navigation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import hr.foi.air.mshop.ui.components.UnderLabelTextField
import androidx.compose.material3.Icon
import hr.foi.air.mshop.ui.components.NextArrow

@Composable
fun RegistrationOrganizationPage(
    onNext: () -> Unit = {}
){
    var organizationName by remember {
        mutableStateOf("")
    }

    var organizationOIB by remember {
        mutableStateOf("")
    }

    var address by remember {
        mutableStateOf("")
    }

    var phoneNum by remember {
        mutableStateOf("")
    }

    var email by remember {
        mutableStateOf("")
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "mShop",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp, bottom = 4.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Registracija organizacije",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 32.dp)
        )

        UnderLabelTextField(caption = "Ime organizacije", value = organizationName, onValueChange = { organizationName = it }, placeholder = "")
        Spacer(modifier = Modifier.height(16.dp))

        UnderLabelTextField(caption = "OIB organizacije", value = organizationOIB, onValueChange = { organizationOIB = it }, placeholder = "")
        Spacer(modifier = Modifier.height(16.dp))

        UnderLabelTextField(caption = "Adresa organizacije", value = address, onValueChange = { address = it }, placeholder = "")
        Spacer(modifier = Modifier.height(16.dp))

        UnderLabelTextField(caption = "Broj telefona", value = phoneNum, onValueChange = { phoneNum = it }, placeholder = "+385 98 123 4567")
        Spacer(modifier = Modifier.height(16.dp))

        UnderLabelTextField(caption = "E-mail", value = email, onValueChange = { email = it }, placeholder = "mShop@gmail.com")
        Spacer(modifier = Modifier.height(40.dp))


        NextArrow(
            modifier = Modifier
                .align(Alignment.End),
            size = 64.dp,
            onClick = onNext
        )


        Spacer(modifier = Modifier.height(32.dp))

    }
}
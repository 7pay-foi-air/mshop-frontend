package hr.foi.air.mshop.navigation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
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
import androidx.core.content.contentValuesOf
import hr.foi.air.mshop.ui.components.StyledButton
import hr.foi.air.mshop.ui.components.StyledTextField
import hr.foi.air.mshop.ui.components.UnderLabelPasswordField
import hr.foi.air.mshop.ui.components.UnderLabelTextField

@Composable
fun RegistrationPage(){
    var firstName by remember {
        mutableStateOf("")
    }

    var lastName by remember {
        mutableStateOf("")
    }

    var email by remember {
        mutableStateOf("")
    }

    var phoneNum by remember {
        mutableStateOf("")
    }

    var username by remember {
        mutableStateOf("")
    }

    /*var password by remember {
        mutableStateOf("")
    } */

    var isAdmin by remember {
        mutableStateOf(false)
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Text(
            text = "mShop",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp, bottom = 4.dp)
        )

        Text(
            text = "Registracija",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        UnderLabelTextField(caption = "Ime", value = firstName, onValueChange = { firstName = it }, placeholder = "")
        Spacer(modifier = Modifier.height(8.dp))

        UnderLabelTextField(caption = "Prezime", value = lastName, onValueChange = { lastName = it }, placeholder = "")
        Spacer(modifier = Modifier.height(8.dp))

        UnderLabelTextField(caption = "E-mail", value = email, onValueChange = { email = it }, placeholder = "mShop@gmail.com")
        Spacer(modifier = Modifier.height(8.dp))

        UnderLabelTextField(caption = "Broj telefona", value = phoneNum, onValueChange = { phoneNum = it }, placeholder = "+385 98 123 4567")
        Spacer(modifier = Modifier.height(8.dp))

        UnderLabelTextField(caption = "Korisničko ime", value = username, onValueChange = { username = it }, placeholder = "")
        Spacer(modifier = Modifier.height(8.dp))

        /*UnderLabelPasswordField(caption = "Default lozinka", value = password, onValueChange = { password = it }, placeholder = "")
        Spacer(modifier = Modifier.height(16.dp)) */

        /*Row(
            modifier = Modifier.padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
            ) {
            Checkbox(checked = isAdmin, onCheckedChange = { isAdmin = it })
            Text(
                text = "Admin",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp)) */

        StyledButton(
            label = "Registriraj se",
            onClick = {
                // akcija kad klikneš
            },
            modifier = Modifier.padding(top = 16.dp)
        )



    }
}

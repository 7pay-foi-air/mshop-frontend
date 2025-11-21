package hr.foi.air.mshop.navigation.components.login

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import hr.foi.air.mshop.ui.components.NextArrow
import hr.foi.air.mshop.ui.components.textFields.UnderLabelTextField

@Composable
fun LoginUsername(
    navController: NavController,
    onNext: () -> Unit = {}
){
    var username by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier
            .height(24.dp))

        Text(
            text = "mShop",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold),
            modifier = Modifier
                .padding(top = 16.dp, bottom = 16.dp)
        )

        Text(
            text = "Prijava",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold),
            modifier = Modifier
                .padding(bottom = 32.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Predstavite nam se",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .padding(bottom = 32.dp)
            )

            Text(
                text = "Unesite Vaše korisničko ime",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(bottom = 32.dp)
            )

            UnderLabelTextField(
                caption = "Korisničko ime",
                value = username,
                onValueChange = { username = it },
                placeholder = ""
            )
        }

//        StyledButton(
//            label = "Registrirajte organizaciju",
//            onClick = {
//                navController.navigate("regOrg")
//            },
//            modifier = Modifier
//                .padding(bottom = 16.dp)
//        )

        NextArrow(
            modifier = Modifier
                .align(Alignment.End)
                .padding(bottom = 32.dp),
            size = 64.dp,
            onClick = {
                if(username.isBlank()){
                    Toast.makeText(
                        context,
                        "Unesite korisničko ime!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else{
                    onNext()
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginUsernamePreview(){
    val navController = rememberNavController()
    LoginUsername(navController = navController)
}
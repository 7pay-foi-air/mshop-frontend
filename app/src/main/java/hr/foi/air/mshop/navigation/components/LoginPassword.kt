package hr.foi.air.mshop.navigation.components

import android.R.attr.text
import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hr.foi.air.mshop.ui.components.NextArrow
import hr.foi.air.mshop.ui.components.StyledButton
import hr.foi.air.mshop.ui.components.UnderLabelTextField

@Composable
fun LoginPassword(
    onNext: () -> Unit = {},
    onForgotPassword: () -> Unit = {}
) {
    var password by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "mShop",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .padding(top = 16.dp, bottom = 16.dp)
        )

        Text(
            text = "Prijava",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Unesite lozinku",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            UnderLabelTextField(
                caption = "Lozinka",
                value = password,
                onValueChange = { password = it },
                placeholder = ""
            )

            Text(
                text = "Zaboravili ste lozinku?",
                modifier = Modifier
                    .padding(top = 16.dp)
                    .clickable { onForgotPassword() },
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline
                ),
                textAlign = TextAlign.Center,
            )
        }

        NextArrow(
            modifier = Modifier
                .align(Alignment.End)
                .padding(bottom = 32.dp),
            size = 64.dp,
            onClick = {
                if(password.isBlank()){
                    Toast.makeText(
                        context,
                        "Unesite lozinku!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    onNext()
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPasswordPreview(){
    LoginPassword()
}
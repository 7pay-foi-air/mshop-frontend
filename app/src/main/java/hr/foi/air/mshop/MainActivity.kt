package hr.foi.air.mshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import hr.foi.air.mshop.navigation.components.RegistrationOrganizationPage
import hr.foi.air.mshop.navigation.components.RegistrationPage
import hr.foi.air.mshop.ui.theme.MShopTheme
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MShopTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "regOrg"
                    ){
                        composable("regOrg") {
                            RegistrationOrganizationPage(
                                onNext = { navController.navigate("regAdmin") }
                            )
                        }
                        composable("regAdmin") {
                            RegistrationPage()
                        }
                    }
                }

            }
        }
    }
}

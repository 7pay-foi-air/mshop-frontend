package hr.foi.air.mshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import hr.foi.air.mshop.languagemodels.BackendLLM
import hr.foi.air.mshop.languagemodels.ILanguageModel
import hr.foi.air.mshop.languagemodels.OnDeviceLLM
import hr.foi.air.mshop.ui.screens.MainScreen
import hr.foi.air.mshop.ui.theme.MShopTheme
import hr.foi.air.mshop.viewmodels.LLM.AssistantViewModel

class MainActivity() : ComponentActivity() {
    //val languageModel : ILanguageModel = OnDeviceLLM(this)
    val languageModel : ILanguageModel = BackendLLM()

    lateinit var assistantViewModel: AssistantViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        languageModel.initializeModel()
        assistantViewModel = AssistantViewModel(languageModel)

        setContent {
            MShopTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }

    override fun onDestroy() {
        languageModel.closeModel()
        super.onDestroy()
    }
}
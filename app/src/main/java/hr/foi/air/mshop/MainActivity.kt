package hr.foi.air.mshop

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import hr.foi.air.mshop.navigation.*
import hr.foi.air.mshop.ui.components.BackArrowButton
import hr.foi.air.mshop.ui.components.MenuIconButton
import hr.foi.air.mshop.ui.components.NavigationDrawer
import hr.foi.air.mshop.ui.theme.MShopTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private var llmInference : LlmInference? = null
    private val MODEL_PATH = "/data/local/tmp/llm/gemma2-2b-it-gpu-int8.bin"
    private val SYSTEM_INSTRUCTION = """
        Based on the user's prompt, identify the intent and any associated parameters.
        Your response must be ONLY a JSON object with two keys: "intent" and "parameters".
        The "intent" can be one of the following: 
        -"SET_ALARM" - when the user wants to set an alarm at a given time
        -"OPEN_SETTINGS" - when the user wants to open the settings page in the app
        -"VIEW_PRODUCTS" - when the user wants to see the product page in the app 
        -or "UNKNOWN" - if it's something unrelated to the defined intents.
        The "parameters" should be a JSON object containing relevant details, or an empty object if none are found.
        
        Example:
        User: "Set an alarm for 7:30 AM"
        Assistant: {"intent": "SET_ALARM", "parameters": {"time": "07:30"}}
        
        Example:
        User: "Take me to the  page"
        Assistant: {"intent": "OPEN_SETTINGS", "parameters": {null}}
        
        Example:
        User: "Open the product page"
        Assistant: {"intent": "VIEW_PRODUCTS", "parameters": {null}}
        
        Example:
        User: *something unrelated to your app*
        Assistant: {"intent": "UNKNOWN", "parameters": {null}}
    """.trimIndent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        initializeLlmInference()

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

    private fun initializeLlmInference() {
        try {
            val options = LlmInference.LlmInferenceOptions.builder()
                .setModelPath(MODEL_PATH)
                .setMaxTopK(64)
                .build()

            llmInference = LlmInference.createFromOptions(this, options)
            Log.i("LLM", "MediaPipe LlmInference uspješno inicijaliziran.")

        } catch (e: Exception) {
            Log.e("LLM", "Greška pri inicijalizaciji LlmInference: ${e.message}")
            llmInference = null
        }
    }

    fun runPromptTest(userPrompt: String): String {
        val model = llmInference

        if (model == null) {
            return "Greška: LLM model nije inicijaliziran (provjerite ADB putanju)."
        }

        // Kreiramo puni prompt kombinirajući sistemsku uputu i korisnički unos
        val fullPrompt = "$SYSTEM_INSTRUCTION\n\nUser: \"$userPrompt\"\nAssistant:"
        Log.d("LLM", "Šaljem prompt: \"$fullPrompt\"")

        try {
            val response = model.generateResponse(fullPrompt)
            val generatedText = response
            Log.d("LLM", "Generirani tekst: $generatedText")
            return generatedText

        } catch (e: Exception) {
            Log.e("LLM", "Greška pri generiranju sadržaja: ${e.message}")
            return "Greška pri generiranju: ${e.message}"
        }
    }

    override fun onDestroy() {
        llmInference?.close()
        super.onDestroy()
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val showNavigationUI = currentRoute !in authRoutes
    val fabResult = remember { mutableStateOf("Klikni gumb za test Gemma 2") }

    val scope = rememberCoroutineScope()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        // Primjer korisničkog unosa. Kasnije ovo možete povezati s TextField-om.
                        val userInput = "Please open the settings for me"
                        val result = (navController.context as? MainActivity)?.runPromptTest(userInput)
                        fabResult.value = "Korisnik: $userInput\nOdgovor: ${result ?: "Greška"}"
                    }
                }
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Test Gemini Nano")
            }
        }
    ) { paddingValues ->

        Column(modifier = Modifier.padding(paddingValues)) {

            // Prikaz rezultata testa ispod FAB-a
            Text(
                text = fabResult.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            if (showNavigationUI) {
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

                NavigationDrawer(
                    drawerState = drawerState,
                    items = drawerItems,
                    currentRoute = currentRoute,
                    onItemClick = { item ->
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    navigationIcon = {
                        when (currentRoute) {
                            in menuRoutes -> MenuIconButton { scope.launch { drawerState.open() } }
                            else -> BackArrowButton { navController.navigateUp() }
                        }
                    }
                ) { modifier ->
                    AppNavHost(
                        navController = navController,
                        modifier = modifier.fillMaxSize()
                    )
                }
            } else {
                AppNavHost(
                    navController = navController,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

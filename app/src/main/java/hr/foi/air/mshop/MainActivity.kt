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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.mlkit.genai.common.DownloadStatus
import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.prompt.Generation
import com.google.mlkit.genai.prompt.TextPart
import com.google.mlkit.genai.prompt.generateContentRequest
import hr.foi.air.mshop.navigation.*
import hr.foi.air.mshop.ui.components.BackArrowButton
import hr.foi.air.mshop.ui.components.MenuIconButton
import hr.foi.air.mshop.ui.components.NavigationDrawer
import hr.foi.air.mshop.ui.theme.MShopTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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

    // --- suspend fun za test Gemini Nano ---
    suspend fun runPromptTest(): String {
        val generativeModel = Generation.getClient()

        val status = generativeModel.checkStatus()

        when (status) {
            FeatureStatus.UNAVAILABLE -> {
                Log.d("GEMINI", "FeatureStatus.UNAVAILABLE")
            }

            FeatureStatus.DOWNLOADABLE -> {
                Log.d("GEMINI", "FeatureStatus.DOWNLOADABLE")
                // Gemini Nano can be downloaded on this device, but is not currently downloaded
                generativeModel.download().collect { status ->
                    when (status) {
                        is DownloadStatus.DownloadStarted ->
                            Log.d("GEMINI", "starting download for Gemini Nano")

                        is DownloadStatus.DownloadProgress ->
                            Log.d("GEMINI", "Nano ${status.totalBytesDownloaded} bytes downloaded")

                        DownloadStatus.DownloadCompleted -> {
                            Log.d("GEMINI", "Gemini Nano download complete")
                            //modelDownloaded = true
                        }

                        is DownloadStatus.DownloadFailed -> {
                            Log.e("GEMINI", "Nano download failed ${status.e.message}")
                        }
                    }
                }
            }

            FeatureStatus.DOWNLOADING -> {
                Log.d("GEMINI", "FeatureStatus.DOWNLOADING")
            }

            FeatureStatus.AVAILABLE -> {
                Log.d("GEMINI", "FeatureStatus.AVAILABLE")
            }
        }


        if (status != FeatureStatus.AVAILABLE) {
            return "Gemini Nano nije dostupan"
        }

        val response = generativeModel.generateContent("Write a 3 sentence story about a magical dog.")

        return response.toString()
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val showNavigationUI = currentRoute !in authRoutes
    val fabResult = remember { mutableStateOf("Klikni gumb za test Gemini Nano") }

    val scope = rememberCoroutineScope()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        val result = (navController.context as? MainActivity)?.runPromptTest()
                        fabResult.value = result ?: "Greška: MainActivity nije dostupan"
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

package hr.foi.air.mshop.languagemodels

import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LlmTestDialog(
    onDismissRequest: () -> Unit,
    onQuery: suspend (String) -> String?
) {
    var userInput by remember { mutableStateOf("") }
    var llmResult by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Testiraj LLM") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    label = { Text("Upišite naredbu") }
                )

                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(
                        Alignment.CenterHorizontally
                    ))
                } else if (llmResult.isNotEmpty()) {
                    Text(text = "Odgovor:\n$llmResult")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    scope.launch {
                        isLoading = true
                        val result = onQuery(userInput)
                        llmResult = result ?: "Greška prilikom dohvaćanja odgovora."
                        isLoading = false
                    }
                },
                enabled = !isLoading
            ) {
                Text("Pošalji")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Zatvori")
            }
        }
    )
}
package hr.foi.air.mshop.languagemodels

import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.filled.Mic
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
fun LlmChatDialog(
    onDismissRequest: () -> Unit,
    onQuery: suspend (String) -> String?
) {
    var userInput by remember { mutableStateOf("") }
    var llmResult by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Razgovor s AI") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    label = { Text("Upišite naredbu") },
                    trailingIcon = {
                        androidx.compose.material3.IconButton(onClick = {
                            // TODO: ovdje pozvati Speech-to-Text i upisati rezultat u userInput
                        }) {
                            androidx.compose.material3.Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.Mic,
                                contentDescription = "Mikrofon"
                            )
                        }
                    }
                )

                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else if (llmResult.isNotEmpty()) {
                    Text(text = "Odgovor:\n$llmResult")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (userInput.isBlank()) return@TextButton
                    scope.launch {
                        isLoading = true
                        llmResult = try {
                            onQuery(userInput) ?: "Greška prilikom dohvaćanja odgovora."
                        } catch (e: Exception) {
                            "Greška: ${e.message}"
                        }
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

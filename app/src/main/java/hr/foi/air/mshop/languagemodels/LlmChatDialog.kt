package hr.foi.air.mshop.languagemodels

import android.Manifest
import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class Sender { User, Bot }

data class ChatMessage(
    val id: Long,
    val text: String,
    val sender: Sender,
    val isLoading: Boolean = false
)

@Composable
fun MessageBubble(message: ChatMessage) {
    val isUser = message.sender == Sender.User
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(
                    color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(10.dp)
        ) {
            if (message.isLoading) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Razmišlja...", fontSize = 14.sp)
                }
            } else {
                Text(
                    text = message.text,
                    color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun LlmChatDialog(
    onDismissRequest: () -> Unit,
    onQuery: suspend (String) -> String?
) {
    var userInput by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<ChatMessage>() }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var isSending by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    // id generator (ostavi kako imaš)
    val idGen = remember { java.util.concurrent.atomic.AtomicLong(System.currentTimeMillis()) }
    fun nextId() = idGen.getAndIncrement()

    // STT manager single-shot
    val sttManager = remember {
        SpeechToTextManagerSingle(
            context = context,
            onPartialResult = { partial ->
                // prikaz partiala u inputu (ako želiš append umjesto replace, prilagodi)
                userInput = partial
            },
            onResult = { result ->
                // finalni rezultat -> postavi u input
                userInput = result
            },
            onError = { err ->
                // opcionalno pokaži toast
                android.widget.Toast.makeText(context, "STT: $err", android.widget.Toast.LENGTH_SHORT).show()
            }
        )
    }

    // request RECORD_AUDIO permission
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                // start listening immediately after grant
                sttManager.startListeningOnce()
            } else {
                android.widget.Toast.makeText(context, "Record audio permission required", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    )

    // cleanup
    DisposableEffect(Unit) {
        onDispose {
            sttManager.destroy()
        }
    }

    AlertDialog(
        onDismissRequest = {
            sttManager.stopListening()
            onDismissRequest()
        },
        title = { Text("Razgovor s AI") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 400.dp, max = 600.dp)
            ) {
                // ... LazyColumn for messages (iste kao prije) ...
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    state = listState
                ) {
                    items(items = messages, key = { it.id }) { msg ->
                        MessageBubble(msg)
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = userInput,
                        onValueChange = { userInput = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("Upišite poruku") },
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = {
                                // on mic click: start single-shot listening
                                if (sttManager.isListening) {
                                    // ako već slušaš, možeš zaustaviti
                                    sttManager.stopListening()
                                } else {
                                    // check permission
                                    val hasPermission = androidx.core.content.ContextCompat.checkSelfPermission(
                                        context, android.Manifest.permission.RECORD_AUDIO
                                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED

                                    if (!hasPermission) {
                                        permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                                    } else {
                                        sttManager.startListeningOnce()
                                    }
                                }
                            }) {
                                // promijeni ikonu/izgled kad sluša
                                if (sttManager.isListening) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = "Slušam...",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                } else {
                                    Icon(imageVector = Icons.Default.Mic, contentDescription = "Mikrofon")
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (userInput.isBlank() || isSending) return@IconButton

                            focusManager.clearFocus(force = true)

                            val userText = userInput.trim()
                            val userMsg = ChatMessage(id = nextId(), text = userText, sender = Sender.User)
                            messages.add(userMsg)
                            userInput = ""
                            isSending = true

                            val loadingId = nextId()
                            val loadingMsg = ChatMessage(id = loadingId, text = "Razmišlja...", sender = Sender.Bot, isLoading = true)
                            messages.add(loadingMsg)

                            scope.launch {
                                val reply = try {
                                    withContext(Dispatchers.IO) { onQuery(userText) }
                                        ?: "Greška prilikom dohvaćanja odgovora."
                                } catch (e: Exception) {
                                    "Greška: ${e.message}"
                                }

                                val idx = messages.indexOfFirst { it.id == loadingId }
                                if (idx != -1) messages[idx] = messages[idx].copy(text = reply, isLoading = false)
                                else messages.add(ChatMessage(id = nextId(), text = reply, sender = Sender.Bot))

                                isSending = false
                            }
                        },
                        enabled = !isSending
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Pošalji")
                    }
                }
            }

            LaunchedEffect(messages.size) {
                if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
            }
        },
        confirmButton = {
            TextButton(onClick = { /* disabled */ }, enabled = false) { Text("Pošalji") }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) { Text("Zatvori") }
        }
    )
}
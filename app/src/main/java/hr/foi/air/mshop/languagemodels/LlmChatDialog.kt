package hr.foi.air.mshop.languagemodels

import android.Manifest
import android.content.pm.PackageManager
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
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.AlertDialog
import androidx.core.content.ContextCompat

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

    val idGen = remember { AtomicLong(System.currentTimeMillis()) }
    fun nextId() = idGen.getAndIncrement()

    var isSttListening by remember { mutableStateOf(false) }

    val sttManager = remember {
        SpeechToTextManagerSingle(
            context = context,
            onPartialResult = { partial ->
                userInput = partial
            },
            onResult = { result ->
                userInput = result
                isSttListening = false
            },
            onError = { err ->
                isSttListening = false
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            isSttListening = true
            sttManager.startListeningOnce()
        } else {
            Toast.makeText(context, "Record audio permission required", Toast.LENGTH_SHORT).show()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            sttManager.destroy()
        }
    }

    AlertDialog(
        onDismissRequest = {
            sttManager.stopListening()
            isSttListening = false
            onDismissRequest()
        },
        title = { Text("Razgovor s AI") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 400.dp, max = 600.dp)
            ) {
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
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 56.dp, max = 160.dp),
                        label = { Text("Upišite poruku") },
                        singleLine = false,
                        maxLines = 6,
                        trailingIcon = {
                            IconButton(onClick = {
                                if (isSttListening) {
                                    sttManager.stopListening()
                                    isSttListening = false
                                } else {
                                    val hasPermission = ContextCompat.checkSelfPermission(
                                        context, Manifest.permission.RECORD_AUDIO
                                    ) == PackageManager.PERMISSION_GRANTED

                                    if (!hasPermission) {
                                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    } else {
                                        isSttListening = true
                                        sttManager.startListeningOnce()
                                    }
                                }
                            }) {
                                if (isSttListening) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = "Slušam...",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = "Mikrofon"
                                    )
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

                if (isSttListening) {
                    Text("Slušam…", modifier = Modifier.padding(top = 6.dp), fontSize = 12.sp)
                }

                LaunchedEffect(messages.size) {
                    if (messages.isNotEmpty()) {
                        listState.animateScrollToItem(messages.size - 1)
                    }
                }
            }
        },
        confirmButton = {  },
        dismissButton = {  },
        properties = DialogProperties(dismissOnClickOutside = true)
    )
}

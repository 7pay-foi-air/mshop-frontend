package hr.foi.air.mshop.languagemodels

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.launch
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
import hr.foi.air.mshop.viewmodels.LLM.AssistantViewModel
import hr.foi.air.ws.data.SessionManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.serialization.json.JsonObject
import kotlin.coroutines.cancellation.CancellationException

enum class Sender { User, Bot }

data class ChatMessage(
    val id: Long,
    val text: String,
    val sender: Sender,
    val isLoading: Boolean = false
)



@Composable
fun MessageBubble(
    message: ChatMessage,
    isCancellable: Boolean = false,
    onCancel: (() -> Unit)? = null
) {
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
                Column {
                    Text(
                        text = message.text,
                        color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (isCancellable && onCancel != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = onCancel) {
                                Text("Odustani")
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun LlmChatDialog(
    onDismissRequest: () -> Unit,
    assistantViewModel: AssistantViewModel,
    assistantHandler: LlmIntentHandler
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

    var pendingMessageId by remember { mutableStateOf<Long?>(null) }
    var pendingJob by remember { mutableStateOf<Job?>(null) }
    var pendingIntent: String? by remember { mutableStateOf(null) }
    var pendingParams: JsonObject? by remember { mutableStateOf(null) }

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
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Spacer(modifier = Modifier.weight(1f))

                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(messages, key = { it.id }) { msg ->
                                MessageBubble(
                                    message = msg,
                                    isCancellable = (msg.id == pendingMessageId),
                                    onCancel = {
                                        val intentToCancel = pendingIntent

                                        pendingJob?.cancel(CancellationException("User canceled"))
                                        pendingJob = null

                                        val idx = messages.indexOfFirst { it.id == pendingMessageId }
                                        if (idx != -1) {
                                            val cancelText = intentToCancel?.let { cancellationTextForIntent(it) } ?: "Operacija otkazana ❌"
                                            messages[idx] = messages[idx].copy(text = cancelText, isLoading = false)
                                        }

                                        pendingMessageId = null
                                        pendingIntent = null
                                        pendingParams = null
                                    }

                                )
                            }
                        }
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
                                val (aiText, result) = assistantViewModel.processMessage(userText)
                                Log.d("LlmChatDialog", "text: $aiText, result: $result")

                                val intent = result.intent
                                val intentObj = AssistantIntent.fromIntent(intent)
                                val requiresLoginButNotLogged = intentObj.requiresLogin && SessionManager.accessToken == null

                                val displayText = when (intentObj) {
                                    AssistantIntent.WANTS_INFO -> userFriendlyMessageForIntent(intent, result.params)
                                    else -> {
                                        if (requiresLoginButNotLogged) loginRequiredMessage(intent)
                                        else userFriendlyMessageForIntent(intent, result.params)
                                    }
                                }

                                Log.d("LlmChatDialog", "displayText: $displayText")

                                val idx = messages.indexOfFirst { it.id == loadingId }
                                if (idx != -1) {
                                    messages[idx] = messages[idx].copy(text = displayText, isLoading = false)
                                } else {
                                    messages.add(ChatMessage(id = nextId(), text = displayText, sender = Sender.Bot))
                                }

                                if (intentObj.isCritical && !requiresLoginButNotLogged) {
                                    pendingIntent = intent
                                    pendingParams = result.params

                                    val countdownId = nextId()
                                    pendingMessageId = countdownId
                                    val start = 5

                                    messages.add(
                                        ChatMessage(
                                            id = countdownId,
                                            text = "Počinjem za $start sek.",
                                            sender = Sender.Bot
                                        )
                                    )

                                    pendingJob = scope.launch {
                                        var c = start
                                        try {
                                            while (c > 0) {
                                                delay(1000)
                                                c--
                                                val idx2 = messages.indexOfFirst { it.id == countdownId }
                                                if (idx2 != -1) {
                                                    messages[idx2] = messages[idx2].copy(text = "Počinjem za $c sek.")
                                                }
                                            }
                                            assistantHandler(pendingIntent!!, pendingParams)
                                        } catch (ex: CancellationException) {
                                        } finally {
                                            pendingJob = null
                                            pendingMessageId = null
                                            pendingIntent = null
                                            pendingParams = null
                                        }
                                    }

                                } else {
                                    if (!intentObj.isCritical) {
                                        assistantHandler(intent, result.params)
                                    }
                                }

                                isSending = false
                            }
                        },
                        enabled = !isSending
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Pošalji")
                    }
                }

                if (isSttListening) {
                    Text("Slušam...", modifier = Modifier.padding(top = 6.dp), fontSize = 12.sp)
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

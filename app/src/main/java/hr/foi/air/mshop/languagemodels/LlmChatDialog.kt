package hr.foi.air.mshop.languagemodels

import android.Manifest
import android.content.Context
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
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.SmartToy
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import hr.foi.air.mshop.ui.theme.Dimens
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
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = if (isUser) 0.dp else 2.dp,
            shadowElevation = 0.dp,
            modifier = Modifier.widthIn(max = 420.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                if (message.isLoading) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Razmišlja…",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (isCancellable && onCancel != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
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

    var isDialogOpen by remember { mutableStateOf(true) }

    val sendMessage = remember {
        { text: String ->
            if (text.isBlank() || isSending) return@remember

            focusManager.clearFocus(force = true)
            val userText = text.trim()
            val userMsg = ChatMessage(id = nextId(), text = text, sender = Sender.User)
            messages.add(userMsg)
            userInput = ""
            isSending = true

            val loadingId = nextId()
            val loadingMsg = ChatMessage(id = loadingId, text = "Razmišlja...", sender = Sender.Bot, isLoading = true)
            messages.add(loadingMsg)

            scope.launch {
                try {
                    val (aiText, result) = assistantViewModel.processMessage(userText)
                    Log.d("LlmChatDialog", "text: $aiText, result: $result")

                    if (!isDialogOpen) return@launch

                    val intent = result.intent
                    val intentObj = AssistantIntent.fromIntent(intent)
                    val requiresLoginButNotLogged = intentObj.requiresLogin && SessionManager.accessToken == null

                    val displayText = when (intentObj) {
                        AssistantIntent.WANTS_INFO -> userFriendlyMessageForIntent(intent, result.params)
                        AssistantIntent.RECOVERY_HINT_GET -> userFriendlyMessageForIntent(intent, result.params, context)
                        else -> {
                            if (requiresLoginButNotLogged) loginRequiredMessage(intent)
                            else userFriendlyMessageForIntent(intent, result.params)
                        }
                    }

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
                            var countdown = start
                            try {
                                while (countdown > 0) {
                                    delay(1000)
                                    countdown--
                                    val idx2 = messages.indexOfFirst { it.id == countdownId }
                                    if (idx2 != -1) {
                                        messages[idx2] = messages[idx2].copy(text = "Počinjem za $countdown sek.")
                                    }
                                }
                                pendingIntent?.let { assistantHandler(it, pendingParams) }
                            } catch (_: CancellationException) {
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
                } catch (e: Exception) {
                    Log.e("LlmChatDialog", "LLM error", e)
                    val lmmErrorMessage = userFriendlyMessageForIntent(AssistantIntent.ERROR.intent)

                    val idx = messages.indexOfFirst { it.id == loadingId }
                    if (idx != -1) {
                        messages[idx] = messages[idx].copy(isLoading = false, text = lmmErrorMessage)
                    }
                } finally {
                    isSending = false
                }
            }
        }
    }

    val sttManager = remember {
        SpeechToTextManagerSingle(
            context = context,
            onPartialResult = { partial -> userInput = partial },
            onResult = { result ->
                isSttListening = false
                userInput = ""
                sendMessage(result)
            },
            onError = {
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
        onDispose { sttManager.destroy() }
    }

    Dialog(
        onDismissRequest = {
            sttManager.stopListening()
            isDialogOpen = false
            pendingJob?.cancel(CancellationException("Dialog zatvoren"))
            onDismissRequest()
        },
        properties = DialogProperties(
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier
                .fillMaxWidth(0.94f)          // ✅ širina dijaloga (probaj 0.98f)
                .heightIn(min = 460.dp, max = 560.dp) // ✅ visina
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {

                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        imageVector = Icons.Outlined.SmartToy, // ili Person / Psychology
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(Modifier.width(Dimens.sm))

                    Text(
                        text = "mShop asistent",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )


                    IconButton(onClick = {
                        sttManager.stopListening()
                        isDialogOpen = false
                        pendingJob?.cancel(CancellationException("Dialog zatvoren"))
                        onDismissRequest()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Cancel,
                            contentDescription = "Zatvori",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(Modifier.height(6.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.primary) // ✅ ako želiš crvenu liniju
                Spacer(Modifier.height(6.dp))

                // Messages
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(vertical = 6.dp)
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
                                    val cancelText = intentToCancel?.let { cancellationTextForIntent(it) }
                                        ?: "Operacija otkazana ❌"
                                    messages[idx] = messages[idx].copy(text = cancelText, isLoading = false)
                                }

                                pendingMessageId = null
                                pendingIntent = null
                                pendingParams = null
                            }
                        )
                    }
                }

                // Auto-scroll
                LaunchedEffect(messages.size) {
                    if (messages.isNotEmpty()) {
                        listState.animateScrollToItem(messages.size - 1)
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Composer (input)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = userInput,
                            onValueChange = { userInput = it },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 52.dp, max = 140.dp),
                            placeholder = { Text("Upišite poruku") },
                            singleLine = false,
                            maxLines = 5,
                            shape = RoundedCornerShape(14.dp),
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
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = "Mikrofon",
                                        tint = if (isSttListening) MaterialTheme.colorScheme.error
                                        else MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        )

                        Spacer(Modifier.width(8.dp))

                        FilledIconButton(
                            onClick = {
                                val text = userInput.trim()
                                userInput = ""
                                sendMessage(text)
                            },
                            enabled = !isSending && userInput.isNotBlank()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Pošalji"
                            )
                        }
                    }
                }
            }
        }
    }
}

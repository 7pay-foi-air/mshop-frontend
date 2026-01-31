package hr.foi.air.mshop.languagemodels

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.History
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import hr.foi.air.mshop.core.room.DbProvider
import hr.foi.air.mshop.core.room.dao.ConversationPreview
import hr.foi.air.mshop.core.room.entity.Sender
import hr.foi.air.mshop.core.room.repository.LlmChatRepository
import hr.foi.air.mshop.data.UIState
import hr.foi.air.mshop.navigation.components.transactionHistory.utcMillisToLocalDate
import hr.foi.air.mshop.ui.theme.Dimens
import hr.foi.air.mshop.utils.AppMessage
import hr.foi.air.mshop.utils.AppMessageManager
import hr.foi.air.mshop.utils.AppMessageType
import hr.foi.air.mshop.viewmodels.LLM.AssistantViewModel
import hr.foi.air.ws.data.SessionManager
import hr.foi.air.ws.repository.TransactionRepo
import hr.foi.air.ws.repository.UserRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.abs

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
                            text = message.text.ifBlank { "Razmišlja..." },
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
    assistantHandler: LlmIntentHandler,
    transactionRepo: TransactionRepo,
    userRepo: UserRepo
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

    val db = remember { DbProvider.get(context) }
    val chatRepo = remember { LlmChatRepository(db.llmChatDao()) }

    fun Long.toHrRelativeShort(nowMillis: Long = System.currentTimeMillis()): String {
        val diff = nowMillis - this
        if (diff <= 0L) return "prije 0 sek" // budućnost ili točno sad

        val seconds = diff / 1_000L
        val minutes = diff / 60_000L
        val hours   = diff / 3_600_000L
        val days    = diff / 86_400_000L
        val years   = days / 365L

        return when {
            years >= 1 -> "prije ${years}g"
            days >= 1  -> "prije ${days}d"
            hours >= 1 -> "prije ${hours}h"
            minutes >= 1 -> "prije ${minutes} min"
            else -> "prije ${seconds} sek"
        }
    }

    suspend fun ensureConversationIdOnMainSafe(uid: String): Long {
        val existing = assistantViewModel.activeConversationId
        if (existing != null) return existing

        val newId = chatRepo.createConversation(uid)
        withContext(Dispatchers.Main) { assistantViewModel.selectConversation(newId) }
        return newId
    }

    suspend fun saveUser(cid: Long, text: String) = chatRepo.insertUser(cid, text)
    suspend fun saveBot(cid: Long, text: String) = chatRepo.insertBot(cid, text)

    var showHistory by remember { mutableStateOf(false) }
    var historyItems by remember { mutableStateOf(emptyList<ConversationPreview>()) }
    var isHistoryLoading by remember { mutableStateOf(false) }

    var isMessagesLoading by remember { mutableStateOf(false) }

    suspend fun loadConversationToUi(cid: Long) {
        val start = System.currentTimeMillis()
        isMessagesLoading = true
        try {
            val dbMsgs = withContext(Dispatchers.IO) { chatRepo.getMessages(cid) }
            messages.clear()
            messages.addAll(dbMsgs.map { m -> ChatMessage(m.id, m.text, m.sender) })
        } finally {
            val elapsed = System.currentTimeMillis() - start
            val minMs = 300L
            if (elapsed < minMs) delay(minMs - elapsed)
            isMessagesLoading = false
        }
    }

    LaunchedEffect(SessionManager.currentUserId) {
        assistantViewModel.resetIfUserChanged(SessionManager.currentUserId)
    }

    LaunchedEffect(assistantViewModel.activeConversationId, showHistory) {
        val cid = assistantViewModel.activeConversationId ?: return@LaunchedEffect
        if (showHistory) return@LaunchedEffect
        if (messages.isNotEmpty()) return@LaunchedEffect
        loadConversationToUi(cid)
    }


    fun getAsyncHandlerIfAny(
        intentObj: AssistantIntent,
        params: JsonObject?,
    ): Pair<String, suspend (JsonObject) -> String>? {
        if (intentObj != AssistantIntent.VIEW_TRANSACTIONS_LAST && intentObj != AssistantIntent.VIEW_TRANSACTIONS_RANGE) return null
        val metric = params?.get("metric")?.jsonPrimitive?.contentOrNull ?: return null

        val needsMailSending  = params["sendMail"]?.jsonPrimitive?.booleanOrNull

        return when (metric.uppercase()) {
            "COUNT" -> Pair("Računam broj transakcija...") { p ->
                val fromDateStr = p["from"]?.jsonObject["date"]?.jsonPrimitive?.content
                val toDateStr = p["to"]?.jsonObject["date"]?.jsonPrimitive?.content

                val value = p["value"]?.jsonPrimitive?.int
                val unit = p["unit"]?.jsonPrimitive?.content

                var count: Int? = null
                var startDate: String? = null
                var endDate: String? = null

                if(fromDateStr != null && toDateStr != null){
                    val fromDate = LocalDate.parse(fromDateStr)
                    val toDate = LocalDate.parse(toDateStr)

                    if(fromDate.isAfter(toDate)) return@Pair "Neispravni parametri."

                    startDate = fromDateStr
                    endDate = toDateStr

                }else if(value != null && unit != null){
                    val (startDateTemp, endDateTemp) = getDateRange(value, unit)
                    startDate = startDateTemp
                    endDate = endDateTemp
                }
                else{
                    return@Pair "Neispravni parametri."
                }

                count = try {
                    transactionRepo.getTransactionsCountPeriod(startDate, endDate)
                } catch (e: Exception) {
                    null
                }

                if (count != null) "Broj transakcija: $count" else "Nisam uspio dohvatiti broj transakcija."
            }

            "SUM" -> Pair("Računam iznos transakcija...") { p ->

                val fromDateStr = p["from"]?.jsonObject["date"]?.jsonPrimitive?.content
                val toDateStr = p["to"]?.jsonObject["date"]?.jsonPrimitive?.content

                val value = p["value"]?.jsonPrimitive?.int
                val unit = p["unit"]?.jsonPrimitive?.content

                var total: Double? = null
                var startDate: String? = null
                var endDate: String? = null


                if(fromDateStr != null && toDateStr != null){
                    val fromDate = LocalDate.parse(fromDateStr)
                    val toDate = LocalDate.parse(toDateStr)

                    if(fromDate.isAfter(toDate)) return@Pair "Neispravni parametri."

                    Log.d("AssistantActionsDate", "fromDate: $fromDate, toDate: $toDate")


                    startDate = fromDateStr
                    endDate = toDateStr

                }else if(value != null && unit != null){
                    val (startDateTemp, endDateTemp) = getDateRange(value, unit)
                    startDate = startDateTemp
                    endDate = endDateTemp
                }
                else{
                    return@Pair "Neispravni parametri."
                }

                total = try {
                    transactionRepo.getTransactionsSumPeriod(startDate, endDate)
                } catch (e: Exception) {
                    null
                }
                if (total != null) String.format("Ukupni iznos: %.2f %s", total, "€") else "Nisam uspio dohvatiti iznos transakcija."
            }

            "LIST" -> {
                if(needsMailSending != null && needsMailSending){
                    Pair("Šaljem transakcijski izvještaj na email...") { p ->

                        if(SessionManager.currentUserId == null){
                            return@Pair "Nema prijavljenog korisnika."
                        }

                        val fromDateStr = p["from"]?.jsonObject["date"]?.jsonPrimitive?.content
                        val toDateStr = p["to"]?.jsonObject["date"]?.jsonPrimitive?.content

                        val value = p["value"]?.jsonPrimitive?.int
                        val unit = p["unit"]?.jsonPrimitive?.content

                        var startDate: String? = null
                        var endDate: String? = null

                        if(fromDateStr != null && toDateStr != null){
                            startDate = fromDateStr
                            endDate = toDateStr
                        }
                        else if(value != null && unit != null) {
                            val (startDateTemp, endDateTemp) = getDateRange(value, unit)
                            startDate = startDateTemp
                            endDate = endDateTemp
                        }
                        else{
                            return@Pair "Neispravni parametri."
                        }

                        val result = userRepo.getUserById(SessionManager.currentUserId!!)
                        if (result.isSuccess) {
                            val user = result.getOrNull() ?: return@Pair "Nema prijavljenog korisnika."

                            val email = user.email
                            val result = transactionRepo.postEmailReport(startDate, endDate, email)

                            if(result) return@Pair "Poslan je transakcijski izvještaj na email: $email"

                        }


                        return@Pair "Dogodila se greška pri slanju izvještaja. Molimo pokušajte ponovo."
                    }
                }
                else{
                    return null
                }
            }

            else -> null
        }
    }


    val sendMessage = remember {
        { text: String ->

            if (text.isBlank() || isSending) return@remember

            scope.launch {
                focusManager.clearFocus(force = true)
                val userText = text.trim()
                val userMsg = ChatMessage(id = nextId(), text = text, sender = Sender.User)
                messages.add(userMsg)

                val uid = SessionManager.currentUserId

                val cid: Long? = if (uid != null) {
                    val createdCid = withContext(Dispatchers.IO) { ensureConversationIdOnMainSafe(uid) }
                    withContext(Dispatchers.IO) { saveUser(createdCid, userText) }
                    createdCid
                } else {
                    null
                }

                userInput = ""
                isSending = true

                val loadingId = nextId()
                val loadingMsg = ChatMessage(id = loadingId, text = "Razmišlja...", sender = Sender.Bot, isLoading = true)
                messages.add(loadingMsg)

                try {
                    val (aiText, result) = assistantViewModel.processMessage(userText)
                    Log.d("LlmChatDialog", "text: $aiText, result: $result")

                    if (!isDialogOpen) return@launch

                    val intent = result.intent
                    val intentObj = AssistantIntent.fromIntent(intent)
                    val requiresLoginButNotLogged = intentObj.requiresLogin && SessionManager.accessToken == null
                    val requiresAdminButNotAdmin = intentObj.requiresAdmin && SessionManager.currentUserRole == "cashier"

                    val idxLoading = messages.indexOfFirst { it.id == loadingId }

                    if (requiresLoginButNotLogged) {
                        val msg = loginRequiredMessage(intent)
                        if (idxLoading != -1) {
                            messages[idxLoading] = messages[idxLoading].copy(text = msg, isLoading = false)
                        } else {
                            messages.add(ChatMessage(id = nextId(), text = msg, sender = Sender.Bot))
                        }
                        cid?.let{
                            withContext(Dispatchers.IO) { saveBot(cid, msg) }
                        }

                        return@launch
                    }

                    if (requiresAdminButNotAdmin) {
                        val msg = adminRequiredMessage(intent)
                        if (idxLoading != -1) {
                            messages[idxLoading] = messages[idxLoading].copy(text = msg, isLoading = false)
                        } else {
                            messages.add(ChatMessage(id = nextId(), text = msg, sender = Sender.Bot))
                        }
                        cid?.let{
                            withContext(Dispatchers.IO) { saveBot(cid, msg) }
                        }
                        return@launch
                    }

                    if(intentObj == AssistantIntent.VIEW_TRANSACTIONS_RANGE){
                        val fromDateStr = result.params?.get("from")?.jsonObject["date"]?.jsonPrimitive?.content
                        val toDateStr = result.params?.get("to")?.jsonObject["date"]?.jsonPrimitive?.content

                        if(fromDateStr != null && toDateStr != null){
                            val fromDate = LocalDate.parse(fromDateStr)
                            val toDate = LocalDate.parse(toDateStr)

                            if(fromDate.isAfter(toDate)){
                                val msg = "Nažalost ne mogu prikazati transakcije za određeni period. Početni datum ne smije biti kasnije od završnog datuma. ⚠️"
                                if (idxLoading != -1) {
                                    messages[idxLoading] = messages[idxLoading].copy(text = msg, isLoading = false)
                                } else {
                                    messages.add(ChatMessage(id = nextId(), text = msg, sender = Sender.Bot))
                                }
                                cid?.let {
                                    withContext(Dispatchers.IO) { saveBot(cid, msg) }
                                }
                                return@launch
                            }
                        }
                    }

                    val asyncPair = getAsyncHandlerIfAny(intentObj, result.params)

                    Log.d("AssistantActionsDate", "asyncPair: $asyncPair")

                    if (asyncPair != null) {

                        val placeholder = asyncPair.first
                        if (!isDialogOpen) return@launch
                        if (idxLoading != -1) {
                            messages[idxLoading] =
                                messages[idxLoading].copy(text = placeholder, isLoading = true)
                        } else {
                            messages.add(
                                ChatMessage(
                                    id = nextId(),
                                    text = placeholder,
                                    sender = Sender.Bot,
                                    isLoading = true
                                )
                            )
                        }

                        val handler = asyncPair.second

                        delay(300)

                        try {

                            val finalText =
                                withContext(Dispatchers.IO) {
                                    handler(result.params!!)
                                }
                            if (!isDialogOpen) return@launch
                            val idx2 = messages.indexOfFirst { it.id == loadingId }
                            if (idx2 != -1) {
                                messages[idx2] =
                                    messages[idx2].copy(text = finalText, isLoading = false)
                            } else {
                                messages.add(
                                    ChatMessage(
                                        id = nextId(),
                                        text = finalText,
                                        sender = Sender.Bot
                                    )
                                )
                            }
                            cid?.let {
                                withContext(Dispatchers.IO) { saveBot(cid, finalText) }
                            }
                        } catch (e: Exception) {
                            val errText = "Greška pri izračunu."
                            if (!isDialogOpen) return@launch
                            val idx2 = messages.indexOfFirst { it.id == loadingId }
                            if (idx2 != -1) {
                                messages[idx2] =
                                    messages[idx2].copy(text = errText, isLoading = false)
                            } else {
                                messages.add(
                                    ChatMessage(
                                        id = nextId(),
                                        text = errText,
                                        sender = Sender.Bot
                                    )
                                )
                                cid?.let {
                                    withContext(Dispatchers.IO) { saveBot(cid, errText) }
                                }
                            }
                        }
                    }

                    else{
                        val displayText = when (intentObj) {
                            AssistantIntent.WANTS_INFO -> userFriendlyMessageForIntent(intent, result.params)
                            AssistantIntent.RECOVERY_HINT_GET -> userFriendlyMessageForIntent(intent, result.params, context)
                            else -> userFriendlyMessageForIntent(intent, result.params, context)
                        }

                        Log.d("LlmChatDialog", "displayText: $displayText")

                        val idx = messages.indexOfFirst { it.id == loadingId }
                        if (idx != -1) {
                            messages[idx] = messages[idx].copy(text = displayText, isLoading = false)
                        } else {
                            messages.add(ChatMessage(id = nextId(), text = displayText, sender = Sender.Bot))
                        }
                        cid?.let{
                            withContext(Dispatchers.IO) { saveBot(cid, displayText) }
                        }

                        if(requiresLoginButNotLogged || requiresAdminButNotAdmin){
                            return@launch
                        }


                        if (intentObj.isCritical) {
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
                                    pendingIntent?.let { intent ->
                                        assistantHandler(intent, pendingParams)
                                    }
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
                    }
                } catch (e: Exception) {
                    Log.e("LlmChatDialog", "LLM error", e)
                    val lmmErrorMessage = userFriendlyMessageForIntent(AssistantIntent.ERROR.intent)

                    val idx = messages.indexOfFirst { it.id == loadingId }
                    if (idx != -1) {
                        messages[idx] = messages[idx].copy(isLoading = false, text = lmmErrorMessage)
                    }
                    cid?.let {
                        withContext(Dispatchers.IO) { saveBot(cid, lmmErrorMessage) }
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
            AppMessageManager.show("Potrebno je dopuštenje za snimanje zvuka.", AppMessageType.INFO)
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
                .fillMaxWidth(0.94f)
                .heightIn(min = 460.dp, max = 560.dp)
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

                    if(SessionManager.currentUserId!=null){
                        IconButton(
                            enabled = !isSending,
                            onClick = {

                                val uid = SessionManager.currentUserId

                                sttManager.stopListening()
                                isSttListening = false
                                pendingJob?.cancel(CancellationException("Conversation history"))
                                pendingJob = null
                                pendingMessageId = null
                                pendingIntent = null
                                pendingParams = null

                                userInput = ""

                                showHistory = true
                                isHistoryLoading = true

                                scope.launch(Dispatchers.IO) {
                                    val previews = chatRepo.getConversationPreviews(uid!!)
                                    withContext(Dispatchers.Main) {
                                        historyItems = previews
                                        isHistoryLoading = false
                                    }
                                }
                            }) {
                            Icon(
                                imageVector = Icons.Outlined.History,
                                contentDescription = "History",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }



                    IconButton(
                        enabled = !isSending,
                        onClick = {

                            sttManager.stopListening()
                            isSttListening = false
                            pendingJob?.cancel(CancellationException("New conversation"))
                            pendingJob = null
                            pendingMessageId = null
                            pendingIntent = null
                            pendingParams = null


                            messages.clear()
                            userInput = ""

                            assistantViewModel.selectConversation(null)

                            showHistory = false

                            /*AppMessageManager.show(
                                "Započet je novi razgovor.",
                                AppMessageType.INFO
                            )*/
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Novi razgovor",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }


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


                Spacer(Modifier.height(3.dp))
                Text(
                    text = "Powered by LLaMA 3.1 8B (Meta)",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)

                )
                Spacer(Modifier.height(3.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(6.dp))



                if (showHistory) {
                    // HISTORY VIEW (umjesto chata)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Povijest razgovora",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(onClick = { showHistory = false }) {
                                Text("Natrag")
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        if (isHistoryLoading) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(Modifier.width(10.dp))
                                Text("Učitavam...")
                            }
                        } else if (historyItems.isEmpty()) {
                            Text("Nema spremljenih razgovora.")
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(vertical = 6.dp)
                            ) {
                                items(historyItems, key = { it.conversationId }) { item ->
                                    ListItem(
                                        headlineContent = {
                                            Text(
                                                text = item.lastText.orEmpty(),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        },
                                        supportingContent = {
                                            Text(
                                                text = item.lastAt?.toHrRelativeShort()
                                                    ?: "Nepoznato vrijeme"
                                            )
                                        },
                                        trailingContent = {
                                            IconButton(
                                                enabled = !isSending,
                                                onClick = {
                                                    val uid = SessionManager.currentUserId
                                                        ?: return@IconButton

                                                    scope.launch(Dispatchers.IO) {
                                                        chatRepo.deleteConversation(item.conversationId) // dodaj u repo/dao

                                                        withContext(Dispatchers.Main) {

                                                            if (assistantViewModel.activeConversationId == item.conversationId) {
                                                                assistantViewModel.selectConversation(
                                                                    null
                                                                )
                                                                messages.clear()
                                                            }

                                                            historyItems =
                                                                historyItems.filterNot { it.conversationId == item.conversationId }
                                                        }
                                                    }
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Delete,
                                                    contentDescription = "Obriši razgovor"
                                                )
                                            }
                                        },
                                        modifier = Modifier.clickable {
                                            isMessagesLoading = true
                                            assistantViewModel.selectConversation(item.conversationId)

                                            userInput = ""
                                            messages.clear()
                                            showHistory = false
                                        }
                                    )
                                    HorizontalDivider()
                                }
                            }
                        }
                    }
                } else {
                    if (isMessagesLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    else{
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

                                        val idx =
                                            messages.indexOfFirst { it.id == pendingMessageId }
                                        if (idx != -1) {
                                            val cancelText =
                                                intentToCancel?.let { cancellationTextForIntent(it) }
                                                    ?: "Operacija otkazana ❌"
                                            messages[idx] = messages[idx].copy(
                                                text = cancelText,
                                                isLoading = false
                                            )
                                            val cid = assistantViewModel.activeConversationId
                                                ?: return@MessageBubble
                                            scope.launch(Dispatchers.IO) {
                                                saveBot(cid, cancelText)
                                            }
                                        }


                                        pendingMessageId = null
                                        pendingIntent = null
                                        pendingParams = null
                                    }
                                )
                            }
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
                                            val hasPermission =
                                                ContextCompat.checkSelfPermission(
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
}

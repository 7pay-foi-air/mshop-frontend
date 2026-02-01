package hr.foi.air.mshop.languagemodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.navigation.NavController
import hr.foi.air.mshop.navigation.AppRoutes
import hr.foi.air.mshop.utils.AppMessageManager
import hr.foi.air.mshop.utils.AppMessageType
import hr.foi.air.ws.data.SessionManager
import hr.foi.air.ws.repository.RepositoryProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun loginRequiredMessage(intent: String): String {
    val intentObj = AssistantIntent.fromIntent(intent)
    return intentObj.requiresLoginMessage ?: "Morate se prijaviti da biste izvršili tu radnju. ⚠️"
}

fun adminRequiredMessage(intent: String): String {
    val intentObj = AssistantIntent.fromIntent(intent)
    return intentObj.requiresAdminMessage ?: "Samo administratori mogu izvršiti tu radnju. ⚠️"
}



fun cancellationTextForIntent(intent: String): String {
    val intentObj = AssistantIntent.fromIntent(intent)
    return intentObj.cancellationText ?: "Operacija otkazana ❌"
}

fun userFriendlyMessageForIntent(
    intent: String,
    params: JsonObject? = null,
    context: Context? = null
): String {
    val intentObj = AssistantIntent.fromIntent(intent)
    return when (intentObj) {
        AssistantIntent.WANTS_INFO -> {
            val msg = params?.get("message")?.jsonPrimitive?.contentOrNull
            msg ?: "Dogodila se greška, molim Vas pokušajte ponovo."
        }
        else -> {
            intentObj.defaultUserFriendlyMessage ?: "Pokrenuo sam proces... ⚙️"
        }
    }
}

fun getDateRange(value: Int, unit: String): Pair<String, String> {
    val today = LocalDate.now()
    val startDate = when (unit.uppercase()) {
        "DAY", "DAYS" -> today.minusDays(value.toLong())
        "WEEK", "WEEKS" -> today.minusWeeks(value.toLong())
        "MONTH", "MONTHS" -> today.minusMonths(value.toLong())
        "YEAR", "YEARS" -> today.minusMonths(value.toLong() * 12)
        else -> today.minusDays(value.toLong())
    }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    Log.d("AssistantActions", "startDate: $startDate, endDate: $today")

    return Pair(startDate.format(formatter), today.format(formatter))
}

fun createAssistantIntentHandler(
    navController: NavController,
    context: Context,
    onCloseChatDialog: () -> Unit
): LlmIntentHandler = { intent, params ->

    Log.d("AssistantActions", "Intent: $intent, Params: $params")

    val intentObj = AssistantIntent.fromIntent(intent)

    when (intentObj) {
        AssistantIntent.VIEW_TRANSACTIONS -> {
            navController.navigate(AppRoutes.TRANSACTION_HISTORY)
            onCloseChatDialog()
        }

        AssistantIntent.VIEW_TRANSACTIONS_LAST -> {
            val value = params?.get("value")?.jsonPrimitive?.int
            val unit = params?.get("unit")?.jsonPrimitive?.content
            val metric = params?.get("metric")?.jsonPrimitive?.content

            if(metric != "LIST"){

            }
            else{
                if(value != null && unit != null){
                    val (startDate, endDate) = getDateRange(value, unit)
                    Log.d("AssistantActions", "startDate: $startDate, endDate: $endDate")
                    navController.navigate(
                        "transaction_history?from=${Uri.encode(startDate)}&to=${Uri.encode(endDate)}"
                    )
                    onCloseChatDialog()
                } else {
                    navController.navigate(AppRoutes.TRANSACTION_HISTORY)
                    onCloseChatDialog()
                }
            }
        }

        AssistantIntent.VIEW_TRANSACTIONS_RANGE -> {
            val startDate = params?.get("from")?.jsonObject?.get("date")?.jsonPrimitive?.content
            val endDate = params?.get("to")?.jsonObject?.get("date")?.jsonPrimitive?.content

            val metric = params?.get("metric")?.jsonPrimitive?.content

            if(metric != "LIST"){

            }
            else{
                if(startDate != null && endDate != null){
                    Log.d("AssistantActions", "startDate: $startDate, endDate: $endDate")
                    navController.navigate(
                        "transaction_history?from=${Uri.encode(startDate)}&to=${Uri.encode(endDate)}"
                    )
                    onCloseChatDialog()
                } else {
                    navController.navigate(AppRoutes.TRANSACTION_HISTORY)
                    onCloseChatDialog()
                }
            }
        }

        AssistantIntent.MANAGE_USERS -> {
            navController.navigate(AppRoutes.MANAGE_USERS)
            onCloseChatDialog()
        }

        AssistantIntent.MANAGE_ITEMS -> {
            navController.navigate(AppRoutes.MANAGE_ARTICLES)
            onCloseChatDialog()
        }

        AssistantIntent.EDIT_PROFILE -> {
            navController.navigate(AppRoutes.PROFILE_USER)
            onCloseChatDialog()
        }


        AssistantIntent.NEW_TRANSACTION -> {
            val amountStr = params?.get("value")?.jsonPrimitive?.content ?: "0"
            val amount = amountStr.replace(",", ".").toDoubleOrNull() ?: 0.0
            val formattedAmount = String.format("%.2f€", amount)
            navController.navigate("payment?amount=${Uri.encode(formattedAmount)}&assistant=true")
            onCloseChatDialog()
        }

        AssistantIntent.LOGOUT -> {
            SessionManager.endSession()
            AppMessageManager.show("Odjavio sam Vas!", AppMessageType.INFO)
            onCloseChatDialog()
            navController.navigate(AppRoutes.LOGIN_GRAPH) {
                popUpTo(0) { inclusive = true }
            }
        }

        AssistantIntent.RECOVERY_HINT_GET -> {
            // handled elsewhere
        }

        AssistantIntent.WANTS_INFO ->{
            // handled elsewhere
        }

        else -> {
            //AppMessageManager.show("Nije prepoznat zadatak.", AppMessageType.ERROR)
        }
    }
}
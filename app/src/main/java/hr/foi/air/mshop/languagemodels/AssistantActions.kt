package hr.foi.air.mshop.languagemodels

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import hr.foi.air.mshop.navigation.AppRoutes
import hr.foi.air.ws.data.SessionManager
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun loginRequiredMessage(intent: String): String {
    val intentObj = AssistantIntent.fromIntent(intent)
    return intentObj.requiresLoginMessage ?: "Morate se prijaviti da biste izvršili tu radnju. ⚠️"
}

fun cancellationTextForIntent(intent: String): String {
    val intentObj = AssistantIntent.fromIntent(intent)
    return intentObj.cancellationText ?: "Operacija otkazana ❌"
}

fun userFriendlyMessageForIntent(intent: String, params: JsonObject? = null): String {
    val intentObj = AssistantIntent.fromIntent(intent)
    return when (intentObj) {
        AssistantIntent.WANTS_INFO -> {
            val msg = params?.get("message")?.jsonPrimitive?.contentOrNull
            msg ?: "Dogodila se greška, molim Vas pokušajte ponovo."
        }
        AssistantIntent.VIEW_TRANSACTIONS_PERIOD -> {
            intentObj.defaultUserFriendlyMessage ?: "Prebacio sam Vas na stranicu za pregled transakcija i primijenio tražene filtre. 🧾"
        }
        else -> {
            intentObj.defaultUserFriendlyMessage ?: "Pokrenuo sam proces... ⚙️"
        }
    }
}



fun getDateRange(value: Int, unit: String): Pair<String, String> {
    val today = LocalDate.now()
    val startDate = when (unit.uppercase()) {
        "DAYS" -> today.minusDays(value.toLong())
        "WEEK", "WEEKS" -> today.minusWeeks(value.toLong())
        "MONTH", "MONTHS" -> today.minusMonths(value.toLong())
        else -> today.minusDays(value.toLong()) // default fallback
    }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
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
        }

        AssistantIntent.VIEW_TRANSACTIONS_PERIOD -> {
            val value = params?.get("value")?.jsonPrimitive?.int
            val unit = params?.get("unit")?.jsonPrimitive?.content

            if(value != null && unit != null){
                val (startDate, endDate) = getDateRange(value, unit)
                Log.d("AssistantActions", "startDate: $startDate, endDate: $endDate")
                navController.navigate(
                    "transaction_history?from=${Uri.encode(startDate)}&to=${Uri.encode(endDate)}"
                )
            } else {
                navController.navigate(AppRoutes.TRANSACTION_HISTORY)
            }
        }


        AssistantIntent.NEW_TRANSACTION -> {
            val amountStr = params?.get("value")?.jsonPrimitive?.content ?: "0"
            val amount = amountStr.replace(",", ".").toDoubleOrNull() ?: 0.0
            val formattedAmount = String.format("%.2f€", amount)
            navController.navigate("payment?amount=${Uri.encode(formattedAmount)}&assistant=true")
        }

        AssistantIntent.LOGOUT -> {
            SessionManager.endSession()
            Toast.makeText(context, "Odjavio sam Vas.", Toast.LENGTH_SHORT).show()
            onCloseChatDialog()
            navController.navigate(AppRoutes.LOGIN_GRAPH) {
                popUpTo(0) { inclusive = true }
            }
        }

        AssistantIntent.WANTS_INFO ->{
            //nista
        }

        else -> {
            Toast.makeText(context, "Nije prepoznat zadatak.", Toast.LENGTH_SHORT).show()
        }
    }
}
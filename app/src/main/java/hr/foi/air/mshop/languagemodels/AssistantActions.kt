package hr.foi.air.mshop.languagemodels

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import hr.foi.air.mshop.navigation.AppRoutes
import hr.foi.air.mshop.viewmodels.HomepageViewModel
import hr.foi.air.ws.data.SessionManager
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive
import java.time.LocalDate
import java.time.format.DateTimeFormatter


val criticalIntents = setOf(
    "LOGOUT",
)

val intentRequiresLogin = setOf(
    "LOGOUT",
    "VIEW_TRANSACTIONS",
    "VIEW_TRANSACTIONS_PERIOD",
    "NEW_TRANSACTION"
)

fun loginRequiredMessage(intent: String): String {
    return when (intent) {
        "LOGOUT" -> "Niste prijavljeni pa Vas ne mogu odjaviti. ⚠️"
        "VIEW_TRANSACTIONS" -> "Morate biti prijavljeni kako biste mogli vidjeti popis transakcija. ⚠️"
        "VIEW_TRANSACTIONS_PERIOD" -> "Morate biti prijavljeni kako biste mogli vidjeti popis transakcija. ⚠️"
        "NEW_TRANSACTION" -> "Morate biti prijavljeni kako biste mogli izraditi novu transakciju. ⚠️"
        else -> "Morate se prijaviti da biste izvršili tu radnju. ⚠️"
    }
}

fun cancellationTextForIntent(intent: String): String {
    return when (intent) {
        "LOGOUT" -> "Odjava otkazana ❌"
        else -> "Operacija otkazana ❌"
    }
}

fun userFriendlyMessageForIntent(intent: String, params: JsonObject? = null): String {
    return when (intent) {
        "LOGOUT" -> "Pokrenuo sam proces odjave 🚪"
        "VIEW_TRANSACTIONS" -> "Prebacio sam Vas na stranicu za pregled transakcija. 🧾"
        "VIEW_TRANSACTIONS_PERIOD" -> "Prebacio sam Vas na stranicu za pregled transakcija u primijenio tražene filtre. 🧾"
        "NEW_TRANSACTION" -> "Prebacio sam Vas na stranicu za kreiranje nove transakcije. 🧾"
        "WANTS_INFO" -> {
            val msg = params?.get("message")?.jsonPrimitive?.contentOrNull
            msg ?: "Dogodila se greška, molim Vas pokušajte ponovo."
        }
        "UNKNOWN" -> "Nažalost nisam u potpunosti razumio Vaš zahtjev. 😅 \nLjubazno Vas molim da pokušate ponovo. 😊"
        else -> "Pokrenuo sam proces... ⚙️"
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

    when (intent) {
        "VIEW_TRANSACTIONS" -> {
            navController.navigate(AppRoutes.TRANSACTION_HISTORY)
        }

        "VIEW_TRANSACTIONS_PERIOD" -> {
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


        "NEW_TRANSACTION" -> {
            val amountStr = params?.get("value")?.jsonPrimitive?.content ?: "0"
            val amount = amountStr.replace(",", ".").toDoubleOrNull() ?: 0.0
            val formattedAmount = String.format("%.2f€", amount)
            navController.navigate("payment?amount=${Uri.encode(formattedAmount)}&assistant=true")
        }

        "LOGOUT" -> {
            SessionManager.endSession()
            Toast.makeText(context, "Odjavio sam Vas.", Toast.LENGTH_SHORT).show()
            onCloseChatDialog()
            navController.navigate(AppRoutes.LOGIN_GRAPH) {
                popUpTo(0) { inclusive = true }
            }
        }

        else -> {
            Toast.makeText(context, "Nije prepoznat zadatak.", Toast.LENGTH_SHORT).show()
        }
    }
}
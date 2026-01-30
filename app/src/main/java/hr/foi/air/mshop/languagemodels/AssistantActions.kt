package hr.foi.air.mshop.languagemodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.navigation.NavController
import hr.foi.air.mshop.navigation.AppRoutes
import hr.foi.air.mshop.utils.AppMessageManager
import hr.foi.air.mshop.utils.AppMessageType
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

fun userFriendlyMessageForIntent(intent: String, params: JsonObject? = null, context: Context? = null): String {
    val intentObj = AssistantIntent.fromIntent(intent)
    return when (intentObj) {
        AssistantIntent.WANTS_INFO -> {
            val msg = params?.get("message")?.jsonPrimitive?.contentOrNull
            msg ?: "Dogodila se greška, molim Vas pokušajte ponovo."
        }
        AssistantIntent.RECOVERY_HINT_GET -> {
            context?.let { ctx ->
                val locationOrNull = try {
                    ctx.openFileInput("recovery_info.txt").bufferedReader().use { reader ->
                        reader.readText().let { raw ->
                            val prefix = "Storage Location: "
                            val cleaned = if (raw.startsWith(prefix)) raw.removePrefix(prefix).trimStart() else raw.trim()
                            if (cleaned.isNotEmpty()) cleaned else null
                        }
                    }
                } catch (e: Exception) {
                    null
                }
                locationOrNull?.let { "Lokacija Vašeg koda za oporavak:\n$it" }
            } ?: intentObj.defaultUserFriendlyMessage ?: "Nema informacije o lokaciji koda."
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
            AppMessageManager.show("Nije prepoznat zadatak.", AppMessageType.ERROR)
        }
    }
}
package hr.foi.air.mshop.languagemodels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import hr.foi.air.mshop.navigation.AppRoutes
import hr.foi.air.ws.data.SessionManager

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
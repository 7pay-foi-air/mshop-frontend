package hr.foi.air.mshop.languagemodels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import hr.foi.air.mshop.navigation.AppRoutes
import hr.foi.air.ws.data.SessionManager

fun createAssistantIntentHandler(navController: NavController, context: Context): LlmIntentHandler = { intent, params ->

    Log.d("AssistantActions", "Intent: $intent, Params: $params")

    when (intent) {
        /*"NEW_TRANSACTION" -> {
            val isLoggedIn = SessionManager.accessToken != null
            if (!isLoggedIn) {
                Toast.makeText(context, "Morate se prijaviti da biste inicirali transakciju.", Toast.LENGTH_LONG).show()
                navController.navigate(AppRoutes.LOGIN_GRAPH)
            } else {
                val value = params?.get("value") ?: ""
                navController.navigate("${AppRoutes.PAYMENT}/$value")
            }
        }*/

        "VIEW_TRANSACTIONS" -> {
            if (SessionManager.accessToken == null) {
                Toast.makeText(context, "Morate se prijaviti da biste vidjeli povijest transakcija.", Toast.LENGTH_LONG).show()
                navController.navigate(AppRoutes.LOGIN_GRAPH)
            } else {
                navController.navigate(AppRoutes.TRANSACTION_HISTORY)
            }
        }

        "VIEW_PRODUCTS", "PRODUCTS" -> {
            navController.navigate(AppRoutes.HOME)
        }

        "LOGOUT" -> {
            if (SessionManager.accessToken == null) {
                Toast.makeText(context, "Niste prijavljeni.", Toast.LENGTH_LONG).show()
            }
            else{
                SessionManager.endSession()
                Toast.makeText(context, "Odjavljeni ste.", Toast.LENGTH_SHORT).show()
                navController.navigate(AppRoutes.LOGIN_GRAPH) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }

        else -> {
            Toast.makeText(context, "Nije prepoznat zadatak.", Toast.LENGTH_SHORT).show()
        }
    }
}
package hr.foi.air.mshop.viewmodels.LLM

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import hr.foi.air.mshop.languagemodels.ILanguageModel
import hr.foi.air.mshop.languagemodels.LLMResult
import hr.foi.air.mshop.languagemodels.parseLLMResult

class AssistantViewModel(
    private val languageModel: ILanguageModel
) : ViewModel() {

    var lastIntent: LLMResult? = null
        private set

    var activeConversationId by mutableStateOf<Long?>(null)
        private set

    fun selectConversation(id: Long?) {
        activeConversationId = id
    }

    private var sessionUserId: String? = null

    fun resetIfUserChanged(newUserId: String?) {
        if (newUserId != sessionUserId) {
            sessionUserId = newUserId
            selectConversation(null)
        }
    }

    suspend fun processMessage(message: String): Pair<String, LLMResult> {
        return try {
            val responseJson = languageModel.getResponseAsync(message)

            Log.d("processMessage","responseJson: $responseJson")

            val result = parseLLMResult(responseJson) ?: LLMResult("UNKNOWN")
            lastIntent = result

            val textForChat = responseJson
            textForChat to result
        } catch (e: Exception) {
            "Greška pri dohvaćanju odgovora: ${e.message}" to LLMResult("LLM_ERROR")
        }
    }
}
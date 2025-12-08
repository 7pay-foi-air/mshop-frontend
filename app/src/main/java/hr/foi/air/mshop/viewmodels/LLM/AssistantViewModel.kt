package hr.foi.air.mshop.viewmodels.LLM

import androidx.lifecycle.ViewModel
import hr.foi.air.mshop.languagemodels.ILanguageModel
import hr.foi.air.mshop.languagemodels.LLMResult
import hr.foi.air.mshop.languagemodels.parseLLMResult
import hr.foi.air.ws.data.SessionManager

class AssistantViewModel(
    private val languageModel: ILanguageModel
) : ViewModel() {

    var lastIntent: LLMResult? = null
        private set
    
    suspend fun processMessage(message: String): Pair<String, LLMResult> {
        return try {
            val responseJson = languageModel.getResponseAsync(message)

            val result = parseLLMResult(responseJson) ?: LLMResult("UNKNOWN")
            lastIntent = result

            val textForChat = responseJson
            textForChat to result
        } catch (e: Exception) {
            "Greška pri dohvaćanju odgovora: ${e.message}" to LLMResult("UNKNOWN")
        }
    }
}
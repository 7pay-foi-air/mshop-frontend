package hr.foi.air.mshop.languagemodels

import android.util.Log
import hr.foi.air.ws.NetworkService.llmApi
import hr.foi.air.ws.models.llm.PromptRequest

class BackendLLM : ILanguageModel {


    override fun initializeModel() {

    }

    override fun closeModel() {

    }

    override fun getResponse(userPrompt: String): String {
        throw IllegalStateException("Use getResponseAsync() - synchronous version is not supported.")
    }

    override suspend fun getResponseAsync(userPrompt: String): String {
        return try {

            val completePrompt = "${SystemPrompt.prompt}\n\"$userPrompt\"\n[END OF REAL USER PROMPT]"
            val request = PromptRequest(prompt = completePrompt)
            Log.d("LLM", "Šaljem prompt: \"$completePrompt\"")
            val response = llmApi.getResponseAsync(request)

            if (response.isSuccessful) {
                val aiResponse = response.body()


                if (aiResponse?.response != null) {
                    Log.d("LLM", "Generirani tekst: $aiResponse?.response")
                    aiResponse.response
                } else {
                    "Empty response from server."
                }
            } else {
                "Error: ${response.code()} - ${response.message()}"
            }
        } catch (e: Exception) {
            Log.e("LLM", "Greška pri generiranju sadržaja: ${e.message}")
            "Greška pri generiranju: ${e.message}"
        }
    }


}
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
        try {
            val completePrompt =
                "${SystemPrompt.prompt}\n\"$userPrompt\"\n[END OF REAL USER PROMPT]"

            val request = PromptRequest(prompt = completePrompt)
            Log.d("LLM", "Šaljem prompt: \"$completePrompt\"")

            val response = llmApi.getResponseAsync(request)

            if (!response.isSuccessful) {
                throw RuntimeException(
                    "LLM API error ${response.code()}: ${response.message()}"
                )
            }

            val body = response.body()
                ?: throw RuntimeException("Empty LLM response body")

            return body.response
                ?: throw RuntimeException("Empty LLM response text")

        } catch (e: Exception) {
            Log.e("LLM", "Greška pri komunikaciji s LLM-om", e)
            throw e
        }
    }


}
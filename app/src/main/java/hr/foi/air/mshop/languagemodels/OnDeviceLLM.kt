package hr.foi.air.mshop.languagemodels

import android.util.Log
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import hr.foi.air.mshop.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OnDeviceLLM(
    private val context: android.content.Context
) : ILanguageModel {
    var llmInference : LlmInference? = null

    override fun initializeModel() {
        try {
            val options = LlmInference.LlmInferenceOptions.builder()
                .setModelPath(BuildConfig.MODEL_PATH)
                .setMaxTokens(4096)
                .build()

            llmInference = LlmInference.createFromOptions(context, options)
            Log.i("LLM", "MediaPipe LlmInference uspješno inicijaliziran.")

        } catch (e: Exception) {
            Log.e("LLM", "Greška pri inicijalizaciji LlmInference: ${e.message}")
            llmInference = null
        }
    }

    override fun closeModel() {
        llmInference?.close()
    }

    override fun getResponse(userPrompt: String): String {
        val model =
            llmInference ?: return "Greška: LLM model nije inicijaliziran (provjerite ADB putanju)."

        val fullPrompt = "${SystemPrompt.prompt}\n\"$userPrompt\"\n[END OF REAL USER PROMPT]"
        Log.d("LLM", "Šaljem prompt: \"$fullPrompt\"")

        try {
            var response = model.generateResponse(fullPrompt)
            Log.d("LLM", "Generirani tekst: $response")

            response = filterJson(response)

            return response

        } catch (e: Exception) {
            Log.e("LLM", "Greška pri generiranju sadržaja: ${e.message}")
            return "Greška pri generiranju: ${e.message}"
        }
    }

    override suspend fun getResponseAsync(userPrompt: String): String = withContext(Dispatchers.IO) {
        val model = llmInference ?: return@withContext "Greška: LLM model nije inicijaliziran."
        val fullPrompt = "${SystemPrompt.prompt}\n\"$userPrompt\"\n[END OF REAL USER PROMPT]"
        Log.d("LLM", "Šaljem prompt: \"$fullPrompt\"")
        try {
            var response = model.generateResponse(fullPrompt)
            Log.d("LLM", "Generirani tekst: $response")
            response = filterJson(response)
            Log.d("LLM", "Filtrirani JSON: $response")
            response
        } catch (e: Exception) {
            Log.e("LLM", "Greška pri generiranju sadržaja: ${e.message}")
            "Greška pri generiranju: ${e.message}"
        }
    }

    private fun filterJson(text: String): String {
        val start = text.indexOf('{')
        val end = text.lastIndexOf('}')
        return if (start == -1 || end == -1 || end < start) {
            """{"intent":"UNKNOWN"}"""
        } else {
            text.substring(start, end + 1)
        }
    }
}
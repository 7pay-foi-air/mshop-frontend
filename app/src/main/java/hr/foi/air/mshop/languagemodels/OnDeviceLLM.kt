package hr.foi.air.mshop.languagemodels

import android.util.Log
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import hr.foi.air.mshop.BuildConfig

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

        val fullPrompt = "${SystemPrompt.prompt}\n\nUser: \"$userPrompt\"\nAssistant:"
        Log.d("LLM", "Šaljem prompt: \"$fullPrompt\"")

        try {
            val response = model.generateResponse(fullPrompt)
            Log.d("LLM", "Generirani tekst: $response")
            return response

        } catch (e: Exception) {
            Log.e("LLM", "Greška pri generiranju sadržaja: ${e.message}")
            return "Greška pri generiranju: ${e.message}"
        }
    }
}
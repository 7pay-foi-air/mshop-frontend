package hr.foi.air.mshop.languagemodels

import android.util.Log
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

typealias LlmIntentHandler = (intent: String, parameters: JsonObject?) -> Unit

@Serializable
data class LLMResult(
    val intent: String,
    val params: JsonObject? = null
)

fun parseLLMResult(json: String): LLMResult? {
    return try {
        val obj = Json { ignoreUnknownKeys = true }.decodeFromString<LLMResult>(json)
        Log.d("ParserJSON", obj.toString())
        obj
    } catch (err: Exception) {
        Log.d("ParserJSON", err.toString())
        null
    }
}
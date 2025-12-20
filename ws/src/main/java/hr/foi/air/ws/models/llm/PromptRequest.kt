package hr.foi.air.ws.models.llm

data class PromptRequest(
    val prompt: String,
    val max_tokens: Int? = null
)
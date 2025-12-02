package hr.foi.air.mshop.languagemodels

interface ILanguageModel {
    fun initializeModel()

    fun closeModel()

    fun getResponse(userPrompt: String): String
    suspend fun getResponseAsync(userPrompt: String): String
}
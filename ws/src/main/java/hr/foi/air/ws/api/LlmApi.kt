package hr.foi.air.mshop.network.api

import hr.foi.air.ws.models.llm.LlmResponse
import hr.foi.air.ws.models.llm.PromptRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LlmApi {
    @POST("ai")
    suspend fun getResponseAsync(
        @Body request: PromptRequest
    ): Response<LlmResponse>
}
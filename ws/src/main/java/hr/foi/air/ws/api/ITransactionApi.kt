package hr.foi.air.ws.api

import hr.foi.air.mshop.network.dto.transaction.CreateTransactionRequest
import hr.foi.air.mshop.network.dto.transaction.TransactionResponse
import hr.foi.air.ws.models.transaction.TransactionHistoryDetails
import hr.foi.air.ws.models.transaction.TransactionHistoryResponse
import hr.foi.air.ws.models.transaction.TransactionSummary
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ITransactionApi {
    @POST("transactions")
    suspend fun createTransaction(
        @Body request: CreateTransactionRequest
    ): Response<TransactionResponse>

    @GET("transactions")
    suspend fun getTransactionsForCurrentUser(): Response<TransactionHistoryResponse>

   /* @GET("transactions/{id}")
    suspend fun getTransactionDetails(
        @Path("id") id: String
    ): TransactionHistoryDetails */
}
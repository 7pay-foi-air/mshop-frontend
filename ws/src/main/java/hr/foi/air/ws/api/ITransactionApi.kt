package hr.foi.air.ws.api

import hr.foi.air.mshop.network.dto.transaction.CreateTransactionRequest
import hr.foi.air.mshop.network.dto.transaction.TransactionResponse
import hr.foi.air.ws.models.transaction.RefundTransactionRequest
import hr.foi.air.ws.models.transaction.SendEmailReportTransactionRequest
import hr.foi.air.ws.models.transaction.SendEmailReportTransactionResponseDto
import hr.foi.air.ws.models.transaction.TransactionDetailsResponseDto
import hr.foi.air.ws.models.transaction.TransactionHistoryResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ITransactionApi {
    @POST("transactions")
    suspend fun createTransaction(
        @Body request: CreateTransactionRequest
    ): Response<TransactionResponse>

    @GET("transactions")
    suspend fun getTransactionsForCurrentUser(
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null
    ): Response<TransactionHistoryResponse>

    @GET("transactions/{id}")
    suspend fun getTransactionDetails(@Path("id") id: String): Response<TransactionDetailsResponseDto>

    @POST("transactions/refund")
    suspend fun refundTransaction(
        @Body request: RefundTransactionRequest
    ): Response<TransactionDetailsResponseDto>

    @POST("transactions/report")
    suspend fun sendEmailReport(
        @Body request: SendEmailReportTransactionRequest
    ): Response<SendEmailReportTransactionResponseDto>

}
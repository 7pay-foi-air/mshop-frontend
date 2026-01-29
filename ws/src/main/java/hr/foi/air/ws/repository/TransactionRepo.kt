package hr.foi.air.ws.repository

import android.util.Log
import hr.foi.air.mshop.core.models.RefundTransactionResponse
import hr.foi.air.mshop.core.models.Transaction
import hr.foi.air.mshop.core.models.TransactionDetails
import hr.foi.air.mshop.core.models.TransactionHistoryDomain
import hr.foi.air.mshop.core.models.TransactionHistoryRecord
import hr.foi.air.mshop.core.models.TransactionItemDetail
import hr.foi.air.mshop.core.models.TransactionResult
import hr.foi.air.mshop.core.models.TransactionType
import hr.foi.air.mshop.core.repository.ITransactionRepository
import hr.foi.air.mshop.network.dto.transaction.CreateTransactionRequest
import hr.foi.air.mshop.network.dto.transaction.TransactionItemRequest
import hr.foi.air.mshop.network.dto.transaction.TransactionResponse
import hr.foi.air.ws.api.ITransactionApi
import hr.foi.air.ws.models.transaction.RefundTransactionRequest
import hr.foi.air.ws.models.transaction.TransactionDetailsResponseDto
import hr.foi.air.ws.models.transaction.TransactionSummary
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TransactionRepo(
    private val api: ITransactionApi
): ITransactionRepository {
    override suspend fun createTransaction(
        transaction: Transaction
    ): Result<TransactionResult> {
        return try{
            val request = transaction.toRequest()
            val response = api.createTransaction(request)

            if(response.isSuccessful){
                val body = response.body()
                    ?: return Result.failure(Exception("Prazan odgovor servera"))
                Result.success(body.toDomainResult())
            }else{
                Result.failure(
                    Exception(
                        "Greška pri stvaranju transakcije: ${response.code()} ${response.message()}"
                    )
                )
            }

        }catch(e: Exception){
            Result.failure(e)
        }
    }

    private fun Transaction.toRequest(): CreateTransactionRequest = CreateTransactionRequest(
        payment_method = "card_payment",
        currency = this.currency,
        description = this.description,
        total_amount = this.totalAmount,
        items = this.items.map {
            TransactionItemRequest(
                uuid_item = it.uuidItem,
                item_name = it.name,
                item_price = it.price,
                quantity = it.quantity,
            )
        }
    )

    private fun TransactionResponse.toDomainResult(): TransactionResult =
        TransactionResult(
            transactionId = this.uuid_transaction,
            totalAmount = this.total_amount,
            currency = this.currency,
            isSuccessful = this.is_successful
        )

    override suspend fun getTransactionsForCurrentUser(
        startDate: LocalDate?,
        endDate: LocalDate?
    ): TransactionHistoryDomain {
        return try {
            val formatter = DateTimeFormatter.ISO_DATE
            val startStr = startDate?.format(formatter)
            val endStr = endDate?.format(formatter)

            val response = api.getTransactionsForCurrentUser(startStr, endStr)

            if (!response.isSuccessful) {
                TransactionHistoryDomain(emptyList(), emptyList())
            } else {
                val body = response.body() ?: return TransactionHistoryDomain(emptyList(), emptyList())

                val paymentsDto = body.successfulTransactions
                val refundsDto = body.refundedTransactions ?: emptyList()

                val refundedTransactionIds = refundsDto.mapNotNull { it.transaction_refund_id }.toSet()

                val payments = paymentsDto.map { dto ->
                    val isRefunded = refundedTransactionIds.contains(dto.uuid_transaction)
                    dto.toDomain(TransactionType.PAYMENT).copy(
                        refundToTransactionId = if (isRefunded) dto.uuid_transaction else null
                    )
                }

                val refunds = refundsDto.map { it.toDomain(TransactionType.REFUND) }

                TransactionHistoryDomain(
                    payments = payments,
                    refunds = refunds
                )
            }
        } catch (e: Exception) {
            Log.e("TransactionRepo", "getTransactionsForCurrentUser failed", e)
            TransactionHistoryDomain(emptyList(), emptyList())
        }
    }


    private fun TransactionSummary.toDomain(
        type: TransactionType
    ): TransactionHistoryRecord {
        return TransactionHistoryRecord(
            id = uuid_transaction,
            totalAmount = total_amount,
            currency = currency,
            createdAt = transaction_date,
            type = type,
            refundToTransactionId = transaction_refund_id
        )
    }


    private fun TransactionDetailsResponseDto.toDomain(): TransactionDetails =
        TransactionDetails(
            uuidTransaction = uuid_transaction,
            transactionType = transaction_type,
            totalAmount = total_amount,
            currency = currency,
            transactionDate = transaction_date,
            transactionRefundId = transaction_refund_id,
            paymentMethod = payment_method,
            items = items.map {
                TransactionItemDetail(
                    uuidItem = it.uuid_item,
                    itemName = it.item_name,
                    itemPrice = it.item_price,
                    quantity = it.quantity,
                    subtotal = it.subtotal
                )
            }
        )

    override suspend fun getTransactionDetails(id: String): Result<TransactionDetails> {
        return try {
            val response = api.getTransactionDetails(id)
            if (!response.isSuccessful) {
                Result.failure(Exception("HTTP ${response.code()}"))
            } else {
                val body = response.body() ?: return Result.failure(Exception("Empty body"))
                Result.success(body.toDomain())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun refundTransaction(
        transactionId: String,
        description: String?
    ): Result<RefundTransactionResponse> {
        return try {
            val request = RefundTransactionRequest(
                uuidTransaction = transactionId,
                description = description ?: "Refund transaction"
            )

            val response = api.refundTransaction(request)

            if (!response.isSuccessful) {
                return Result.failure(Exception("HTTP ${response.code()} ${response.message()}"))
            }

            val body = response.body() ?: return Result.failure(Exception("Empty body"))
            Result.success(
                RefundTransactionResponse(
                    refundTransactionId = body.transaction_refund_id ?: "unknown"
                )
            )

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTransactionsCountPeriod(startDate: String, endDate: String): Int {
        val start = LocalDate.parse(startDate)
        val end = LocalDate.parse(endDate)

        val transactions  = getTransactionsForCurrentUser(start, end)
        val count = transactions.payments.size + transactions.refunds.size
        return count
    }

    suspend fun getTransactionsSumPeriod(startDate: String, endDate: String): Double {
        val start = LocalDate.parse(startDate)
        val end = LocalDate.parse(endDate)
        val transactions = getTransactionsForCurrentUser(start, end)
        val paymentsSum = transactions.payments.sumOf { it.totalAmount }
        val refundsSum = transactions.refunds.sumOf { it.totalAmount }
        return paymentsSum - refundsSum
    }



}

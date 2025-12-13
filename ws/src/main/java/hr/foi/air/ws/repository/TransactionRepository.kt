package hr.foi.air.ws.repository

import hr.foi.air.mshop.core.models.Transaction
import hr.foi.air.mshop.core.models.TransactionHistoryDomain
import hr.foi.air.mshop.core.models.TransactionHistoryRecord
import hr.foi.air.mshop.core.models.TransactionResult
import hr.foi.air.mshop.core.models.TransactionType
import hr.foi.air.mshop.core.repository.ITransactionRepository
import hr.foi.air.mshop.network.dto.transaction.CreateTransactionRequest
import hr.foi.air.mshop.network.dto.transaction.TransactionItemRequest
import hr.foi.air.mshop.network.dto.transaction.TransactionResponse
import hr.foi.air.ws.api.ITransactionApi
import hr.foi.air.ws.models.transaction.TransactionSummary
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TransactionRepository(
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
        items = this.items.map {
            TransactionItemRequest(
                uuid_item = it.uuidItem,
                item_name = it.name,
                item_price = it.price,
                quantity = it.quantity
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

    override suspend fun getTransactionsForCurrentUser(startDate: LocalDate?, endDate: LocalDate?): TransactionHistoryDomain {
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

                val payments = paymentsDto.map { it.toDomain(TransactionType.PAYMENT) }
                val refunds = refundsDto.map { it.toDomain(TransactionType.REFUND) }

                TransactionHistoryDomain(
                    payments = payments,
                    refunds = refunds
                )
            }
        } catch (e: Exception) {
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
}

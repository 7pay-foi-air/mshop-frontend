package hr.foi.air.mshop.core.repository

import hr.foi.air.mshop.core.models.RefundTransactionResponse
import hr.foi.air.mshop.core.models.Transaction
import hr.foi.air.mshop.core.models.TransactionDetails
import hr.foi.air.mshop.core.models.TransactionHistoryDomain
import hr.foi.air.mshop.core.models.TransactionResult
import java.time.LocalDate

interface ITransactionRepository {
    suspend fun createTransaction(transaction: Transaction): Result<TransactionResult>
    suspend fun getTransactionsForCurrentUser(startDate: LocalDate? = null, endDate: LocalDate? = null): TransactionHistoryDomain

    suspend fun getTransactionDetails(id: String): Result<TransactionDetails>

    suspend fun refundTransaction(transactionId: String, description: String? = null): Result<RefundTransactionResponse>
}
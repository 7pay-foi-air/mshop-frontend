package hr.foi.air.mshop.core.repository

import hr.foi.air.mshop.core.models.CardPaymentData
import hr.foi.air.mshop.core.models.Transaction
import hr.foi.air.mshop.core.models.TransactionHistoryRecord
import hr.foi.air.mshop.core.models.TransactionResult

interface ITransactionRepository {
    suspend fun createTransaction(transaction: Transaction): Result<TransactionResult>
    suspend fun getTransactionsForCurrentUser(): List<TransactionHistoryRecord>
}
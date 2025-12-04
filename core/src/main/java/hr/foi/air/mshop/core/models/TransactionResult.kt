package hr.foi.air.mshop.core.models

data class TransactionResult(
    val transactionId: String,
    val totalAmount: Double,
    val currency: String,
    val isSuccessful: Boolean
)

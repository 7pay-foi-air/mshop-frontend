package hr.foi.air.mshop.core.models

enum class TransactionType {
    PAYMENT,
    REFUND
}

data class TransactionHistoryRecord(
    val id: String,
    val totalAmount: Double,
    val currency: String,
    val createdAt: String,
    val type: TransactionType,
    val refundToTransactionId: String? = null
)

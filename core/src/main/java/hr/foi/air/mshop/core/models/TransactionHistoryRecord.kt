package hr.foi.air.mshop.core.models

enum class TransactionType {
    PAYMENT,
    REFUND
}

data class TransactionHistoryRecord(
    val id: String,
    val totalAmount: Double,
    val currency: String,
    val isSuccessful: Boolean,
    val completedAt: String,
    val type: TransactionType,
    val refundToTransactionId: String? = null,   // samo za REFUND
    val refundedAt: String? = null             // samo za REFUND
)

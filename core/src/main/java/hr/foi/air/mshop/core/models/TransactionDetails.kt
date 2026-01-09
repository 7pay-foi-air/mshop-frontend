package hr.foi.air.mshop.core.models

data class TransactionDetails(
    val uuidTransaction: String,
    val transactionType: String,
    val totalAmount: Double,
    val currency: String,
    val transactionDate: String,
    val transactionRefundId: String?,
    val paymentMethod: String,
    val items: List<TransactionItemDetail>
)

package hr.foi.air.mshop.core.models

data class Transaction(
    val description: String,
    val items: List<TransactionItem>,
    val currency: String = "EUR",
    val totalAmount: Double
)

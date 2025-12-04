package hr.foi.air.mshop.core.models

data class TransactionItem(
    val uuidItem: String,
    val name: String,
    val price: Double,
    val quantity: Int
)

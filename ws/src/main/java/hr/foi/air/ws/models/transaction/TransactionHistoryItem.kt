package hr.foi.air.ws.models.transaction

data class TransactionHistoryItem(
    val item_name: String,
    val item_price: Double,
    val quantity: Int,
    val subtotal: Double
)

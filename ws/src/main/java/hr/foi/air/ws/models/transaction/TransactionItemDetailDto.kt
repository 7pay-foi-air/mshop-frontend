package hr.foi.air.ws.models.transaction

data class TransactionItemDetailDto(
    val uuid_item: String,
    val item_name: String,
    val item_price: Double,
    val quantity: Int,
    val subtotal: Double
)

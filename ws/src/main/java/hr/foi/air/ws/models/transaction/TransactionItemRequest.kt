package hr.foi.air.mshop.network.dto.transaction

data class TransactionItemRequest(
    val uuid_item: String,
    val item_name: String,
    val item_price: Double,
    val quantity: Int
)

package hr.foi.air.mshop.core.models

data class TransactionItemDetail(
    val uuidItem: String,
    val itemName: String,
    val itemPrice: Double,
    val quantity: Int,
    val subtotal: Double
)

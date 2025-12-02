package hr.foi.air.mshop.network.dto.transaction

data class CreateTransactionRequest(
    val payment_method: String = "card_payment",
    val currency: String = "EUR",
    val description: String,
    val items: List<TransactionItemRequest>
)

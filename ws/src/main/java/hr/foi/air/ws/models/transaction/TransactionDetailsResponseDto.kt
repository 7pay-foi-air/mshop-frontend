package hr.foi.air.ws.models.transaction

data class TransactionDetailsResponseDto(
    val uuid_transaction: String,
    val transaction_type: String,
    val total_amount: Double,
    val currency: String,
    val transaction_date: String,
    val transaction_refund_id: String?,
    val payment_method: String,
    val items: List<TransactionItemDetailDto>
)
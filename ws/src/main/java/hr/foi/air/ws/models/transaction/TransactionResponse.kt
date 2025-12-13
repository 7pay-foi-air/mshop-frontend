package hr.foi.air.mshop.network.dto.transaction

data class TransactionResponse(
    val uuid_transaction: String,
    val total_amount: Double,
    val currency: String,
    val is_successful: Boolean
)


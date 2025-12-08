package hr.foi.air.ws.models.transaction

data class TransactionSummary(
    val uuid_transaction: String,
    val total_amount: Double,
    val currency: String,
   // val is_successful: Boolean,
    //val transaction_type: String,
    val transaction_date: String,
    val transaction_refund_id: String?

)

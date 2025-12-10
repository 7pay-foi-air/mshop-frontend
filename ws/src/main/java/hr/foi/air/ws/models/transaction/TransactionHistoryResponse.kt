package hr.foi.air.ws.models.transaction

import com.google.gson.annotations.SerializedName


data class TransactionHistoryResponse(
@SerializedName("successful_transactions")
val successfulTransactions: List<TransactionSummary>,

@SerializedName("refunded_transactions")
val refundedTransactions: List<TransactionSummary>?
)


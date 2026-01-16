package hr.foi.air.ws.models.transaction

import com.google.gson.annotations.SerializedName

data class RefundTransactionRequest(
    @SerializedName("uuid_transaction")
    val uuidTransaction: String,
    val description: String
)

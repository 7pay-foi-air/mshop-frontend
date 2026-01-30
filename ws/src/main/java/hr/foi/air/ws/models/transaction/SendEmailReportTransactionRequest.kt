package hr.foi.air.ws.models.transaction

import com.google.gson.annotations.SerializedName

data class SendEmailReportTransactionRequest (
    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("end_date")
    val endDate: String,
    val email: String
)
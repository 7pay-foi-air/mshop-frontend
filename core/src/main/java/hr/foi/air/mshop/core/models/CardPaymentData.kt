package hr.foi.air.mshop.core.models

data class CardPaymentData(
    val cardNumber: String,
    val expiry: String,
    val cvv: String
)

package hr.foi.air.mshop.core.models

data class TransactionHistoryDomain(
    val payments: List<TransactionHistoryRecord>,
    val refunds: List<TransactionHistoryRecord>
)

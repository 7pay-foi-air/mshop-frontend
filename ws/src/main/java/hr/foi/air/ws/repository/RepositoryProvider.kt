package hr.foi.air.ws.repository

import hr.foi.air.ws.NetworkService

object RepositoryProvider {
    val transactionRepo: TransactionRepo by lazy {
        TransactionRepo(NetworkService.transactionApi)
    }
}
package hr.foi.air.ws.repository
import hr.foi.air.mshop.core.models.Transaction
import hr.foi.air.mshop.core.models.TransactionResult
import hr.foi.air.mshop.core.repository.ITransactionRepository
import hr.foi.air.mshop.network.dto.transaction.CreateTransactionRequest
import hr.foi.air.mshop.network.dto.transaction.TransactionItemRequest
import hr.foi.air.mshop.network.dto.transaction.TransactionResponse
import hr.foi.air.ws.api.ITransactionApi

class TransactionRepository(
    private val api: ITransactionApi
): ITransactionRepository {
    override suspend fun createTransaction(
        transaction: Transaction
    ): Result<TransactionResult> {
        return try{
            val request = transaction.toRequest()
            val response = api.createTransaction(request)

            if(response.isSuccessful){
                val body = response.body()
                    ?: return Result.failure(Exception("Prazan odgovor servera"))
                Result.success(body.toDomainResult())
            }else{
                Result.failure(
                    Exception(
                        "Greška pri stvaranju transakcije: ${response.code()} ${response.message()}"
                    )
                )
            }

        }catch(e: Exception){
            Result.failure(e)
        }
    }

    // pretvaranje transaction(core) u CreateTransactionRequest(DTO)
    private fun Transaction.toRequest(): CreateTransactionRequest = CreateTransactionRequest(
        payment_method = "card_payment",
        currency = this.currency,
        description = this.description,
        items = this.items.map {
            TransactionItemRequest(
                uuid_item = it.uuidItem,
                item_name = it.name,
                item_price = it.price,
                quantity = it.quantity
            )
        }
    )

    // TransactionResponse (DTO) -> TransactionResult (core)
    private fun TransactionResponse.toDomainResult(): TransactionResult =
        TransactionResult(
            transactionId = this.uuid_transaction,
            totalAmount = this.total_amount,
            currency = this.currency,
            isSuccessful = this.is_successful
        )
}

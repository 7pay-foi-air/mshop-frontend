package hr.foi.air.mshop.repo

import android.content.Context
import android.net.Uri
import android.util.Log
import hr.foi.air.mshop.core.models.Article
import hr.foi.air.mshop.core.repository.ArticleRepository
import hr.foi.air.mshop.network.NetworkService
import hr.foi.air.mshop.network.dto.ArticleResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class ArticleRepo : ArticleRepository {
    private val api = NetworkService.articleApi

    private fun ArticleResponse.toDomainModel() = Article(
        id = this.uuidItem.hashCode(),
        uuidItem = this.uuidItem,
        articleName = this.name,
        description = this.description,
        price = this.price,
        imageUrl = this.imageUrl,
        ean = this.sku,
        stockQuantity = this.stockQuantity
    )

    override fun getAllArticles(): Flow<List<Article>> = flow {
        try {
            val response = api.getArticles()
            if (response.isSuccessful) {
                val articles = response.body()?.map { it.toDomainModel() } ?: emptyList()
                emit(articles)
            } else {
                println("Error fetching articles: ${response.code()}")
                emit(emptyList())
            }
        } catch (e: Exception) {
            println("Exception when fetching articles: ${e.message}")
            emit(emptyList())
        }
    }

    override suspend fun deleteArticle(articleId: String): Result<Unit> {
        val logTag = "ArticleDelete"
        Log.d(logTag, "Attempting to delete article with ID: $articleId")
        return try {
            val response = api.deleteArticle(articleId)
            Log.d(logTag, "Response received. Code: ${response.code()}, Successful: ${response.isSuccessful}")

            if (response.isSuccessful) {
                Log.d(logTag, "Article successfully deleted on server.")
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(logTag, "Failed to delete article. Code: ${response.code()}, Message: ${response.message()}, Body: $errorBody")
                Result.failure(Exception("Greška pri brisanju: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(logTag, "Exception during article deletion: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun createArticle(article: Article, context: Context): Result<String> {
        return try {
            val nameBody = article.articleName
                .toRequestBody("text/plain".toMediaType())

            val descBody = (article.description ?: "")
                .toRequestBody("text/plain".toMediaType())

            val priceBody = article.price.toString()
                .toRequestBody("text/plain".toMediaType())

            val currencyBody = (article.currency.ifBlank { "EUR" })
                .toRequestBody("text/plain".toMediaType())

            // mapiramo ean -> sku (dok nemate posebno sku polje)
            val skuBody = article.ean
                .toRequestBody("text/plain".toMediaType())

            val stockQuantityBody = (article.stockQuantity.takeIf { it > 0 } ?: 1)
                .toString()
                .toRequestBody("text/plain".toMediaType())

            // slika je opcionalna
            val imagePart = article.imageUri?.let { uriStr ->
                val uri = Uri.parse(uriStr)
                val bytes = context.contentResolver.openInputStream(uri)!!.readBytes()
                val fileBody = bytes.toRequestBody("image/*".toMediaType())
                MultipartBody.Part.createFormData(
                    name = "image",
                    filename = "item.jpg",
                    body = fileBody
                )
            }

            val response = api.createItem(
                name = nameBody,
                description = descBody,
                price = priceBody,
                currency = currencyBody,
                sku = skuBody,
                stockQuantity = stockQuantityBody,
                image = imagePart
            )

            if (response.isSuccessful) {
                Result.success(response.body()?.message ?: "Uspješno dodano")
            } else {
                Result.failure(Exception("Greška ${response.code()}"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateArticle(article: Article, context: Context): Result<String> {
        return Result.failure(Exception("Update još nije podržan na backendu."))
    }
}
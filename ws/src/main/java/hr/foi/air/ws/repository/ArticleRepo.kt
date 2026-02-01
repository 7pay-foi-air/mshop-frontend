package hr.foi.air.ws.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.gson.Gson
import hr.foi.air.mshop.core.models.Article
import hr.foi.air.mshop.core.repository.IArticleRepository
import hr.foi.air.ws.NetworkService
import hr.foi.air.ws.NetworkService.ARTICLE_BASE_URL
import hr.foi.air.ws.models.articleManagement.ArticleResponse
import hr.foi.air.ws.models.articleManagement.UpdateItemRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class ArticleRepo : IArticleRepository {
    private val api = NetworkService.articleApi

    private fun ArticleResponse.toDomainModel() = Article(
        id = this.uuidItem.hashCode(),
        uuidItem = this.uuidItem,
        articleName = this.name,
        description = this.description,
        price = this.price,
        imageUrl = this.imageUrl?.let {
            "${ARTICLE_BASE_URL.removeSuffix("/api/v1/")}/${it.removePrefix("/")}"
        },
        ean = this.sku,
        stockQuantity = this.stockQuantity
    )

    private fun buildImagePart(uriStr: String, context: Context): MultipartBody.Part? {
        val uri = Uri.parse(uriStr)

        val mimeType = context.contentResolver.getType(uri) ?: return null

        val bytes = context.contentResolver.openInputStream(uri)
            ?.use { it.readBytes() }
            ?: return null

        val fileBody = bytes.toRequestBody(mimeType.toMediaType())

        val ext = when (mimeType) {
            "image/png" -> "png"
            "image/gif" -> "gif"
            "image/webp" -> "webp"
            else -> "jpg"
        }

        return MultipartBody.Part.createFormData(
            name = "image",
            filename = "item.$ext",
            body = fileBody
        )
    }


    override fun getAllArticles(): Flow<List<Article>> = flow {
        try {
            val response = api.getArticles()
            if (response.isSuccessful) {
                val articles = response.body()?.map { it.toDomainModel() } ?: emptyList()
                emit(articles)
                Log.d("slika", "$articles")
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

            // ean -> sku
            val skuBody = article.ean
                .toRequestBody("text/plain".toMediaType())

            val stockQuantityBody = (article.stockQuantity.takeIf { it > 0 } ?: 1)
                .toString()
                .toRequestBody("text/plain".toMediaType())

            val imagePart = article.imageUri?.let { uriStr ->
                buildImagePart(uriStr, context)
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
        val uuid = article.uuidItem
            ?: return Result.failure(Exception("Nedostaje uuid artikla."))

        return try {
            val reqObj = UpdateItemRequest(
                name = article.articleName.trim(),
                description = article.description?.trim().orEmpty(),
                price = article.price,
                currency = article.currency.ifBlank { "EUR" },
                sku = article.ean.trim().ifBlank { null },
                stockQuantity = article.stockQuantity.takeIf { it > 0 } ?: 1
            )

            val jsonString = Gson().toJson(reqObj)

            val dataBody = jsonString
                .toRequestBody("application/json".toMediaType())

            val imagePart = article.imageUri?.let { uriStr ->
                buildImagePart(uriStr, context)
            }

            val response = api.updateItem(
                uuid = uuid,
                data = dataBody,
                image = imagePart
            )

            if (response.isSuccessful) {
                Result.success(response.body()?.message ?: "Uspješno ažurirano")
            } else {
                Result.failure(Exception("Greška ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
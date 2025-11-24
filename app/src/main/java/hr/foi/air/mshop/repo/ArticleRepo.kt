package hr.foi.air.mshop.repo

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import hr.foi.air.mshop.core.models.Article
import hr.foi.air.mshop.core.repository.ArticleRepository
import hr.foi.air.mshop.network.NetworkService
import hr.foi.air.mshop.network.dto.ArticleRequest
import hr.foi.air.mshop.network.dto.ArticleResponse
import hr.foi.air.mshop.network.dto.UpdateItemRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class ArticleRepo : ArticleRepository {
    private val api = NetworkService.articleApi

    private fun Article.toRequest() = ArticleRequest(
        ean = this.ean,
        name = this.articleName,
        description = this.description,
        price = this.price,
        imageUri = this.imageUri
    )

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

    private fun buildImagePart(uriStr: String, context: Context): MultipartBody.Part? {
        val uri = Uri.parse(uriStr)

        // stvarni MIME tip slike
        val mimeType = context.contentResolver.getType(uri) ?: return null

        val bytes = context.contentResolver.openInputStream(uri)
            ?.use { it.readBytes() }   // use{} zatvara stream
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
            } else {
                println("Error fetching articles: ${response.code()}")
                emit(emptyList())
            }
        } catch (e: Exception) {
            println("Exception when fetching articles: ${e.message}")
            emit(emptyList())
        }
    }

    override suspend fun deleteArticle(articleId: Int): Result<Unit> {
        TODO("Not yet implemented")
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

            // mapiramo ean -> sku
            val skuBody = article.ean
                .toRequestBody("text/plain".toMediaType())

            val stockQuantityBody = (article.stockQuantity.takeIf { it > 0 } ?: 1)
                .toString()
                .toRequestBody("text/plain".toMediaType())

            // slika je opcionalna
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
                sku = article.ean.trim().ifBlank { null },     // ean šaljem kao sku
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
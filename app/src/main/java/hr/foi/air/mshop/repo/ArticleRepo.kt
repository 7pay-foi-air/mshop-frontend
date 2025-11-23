package hr.foi.air.mshop.repo

import android.content.Context
import android.net.Uri
import hr.foi.air.mshop.core.models.Article
import hr.foi.air.mshop.core.repository.ArticleRepository
import hr.foi.air.mshop.network.NetworkService
import hr.foi.air.mshop.network.dto.ArticleRequest
import hr.foi.air.mshop.network.dto.ArticleResponse
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

//    suspend fun createArticle(article: Article) : Result<String>{
//        return try{
//            val request = article.toRequest()
//            val response = api.createArticle(request)
//
//            if(response.isSuccessful) {
//                Result.success(response.body()?.message ?: "Uspješno dodano")
//            }else{
//                Result.failure(Exception("Greška ${response.code()}"))
//            }
//
//        }catch (e: Exception){
//            Result.failure(e)
//        }
//    }
//
//    suspend fun updateArticle(article: Article): Result<String> {
//        val id = article.id
//            ?: return Result.failure(Exception("Odaberite artikl (nedostaje ID artikla)"))
//        return try {
//            //BACKEND: Ako je imageUri null → ne mijenjaj sliku
//            val request = article.toRequest()
//            val response = api.updateArticle(id, request)
//
//            if (response.isSuccessful) {
//                Result.success(response.body()?.message ?: "Uspješno ažurirano")
//            } else {
//                Result.failure(Exception("Greška ${response.code()}"))
//            }
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
}
package hr.foi.air.mshop.repo

import hr.foi.air.mshop.core.models.Article
import hr.foi.air.mshop.core.repository.ArticleRepository
import hr.foi.air.mshop.network.NetworkService
import hr.foi.air.mshop.network.dto.ArticleRequest
import hr.foi.air.mshop.network.dto.ArticleResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

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

    override suspend fun createArticle(article: Article): Result<String> {
        TODO("Not yet implemented")
    }

    override suspend fun updateArticle(article: Article): Result<String> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteArticle(articleId: Int): Result<Unit> {
        TODO("Not yet implemented")
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
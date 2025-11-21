package hr.foi.air.mshop.repo

import hr.foi.air.mshop.core.models.Article
import hr.foi.air.mshop.network.NetworkService
import hr.foi.air.mshop.network.dto.ArticleRequest

class ArticleRepo {
    private val api = NetworkService.articleApi

    private fun Article.toRequest() = ArticleRequest(
        ean = this.ean,
        name = this.articleName,
        description = this.description,
        price = this.price,
        imageUri = this.imageUri
    )

    suspend fun createArticle(article: Article) : Result<String>{
        return try{
            val request = article.toRequest()
            val response = api.createArticle(request)

            if(response.isSuccessful) {
                Result.success(response.body()?.message ?: "Uspješno dodano")
            }else{
                Result.failure(Exception("Greška ${response.code()}"))
            }

        }catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun updateArticle(article: Article): Result<String> {
        val id = article.id
            ?: return Result.failure(Exception("Odaberite artikl (nedostaje ID artikla)"))
        return try {
            //BACKEND: Ako je imageUri null → ne mijenjaj sliku
            val request = article.toRequest()
            val response = api.updateArticle(id, request)

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
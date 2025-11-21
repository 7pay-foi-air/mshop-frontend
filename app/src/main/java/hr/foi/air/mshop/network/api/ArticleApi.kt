package hr.foi.air.mshop.network.api

import hr.foi.air.mshop.network.dto.ArticleRequest
import hr.foi.air.mshop.network.dto.ArticleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ArticleApi {
    @GET("articles")
    suspend fun getArticles(
    ): Response<ArticleResponse>

    @POST("articles")
    suspend fun  createArticle(
        @Body article: ArticleRequest
    ): Response<ArticleResponse>

    @PUT("articles/{id}")
    suspend fun updateArticle(
        @Path("id") id: Int,
        @Body request: ArticleRequest
    ): Response<ArticleResponse>

    @DELETE("articles/{id}")
    suspend fun deleteArticle(
        @Path("id") id: Int
    ): Response<ArticleResponse>
}
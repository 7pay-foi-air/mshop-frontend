package hr.foi.air.mshop.network.api

import hr.foi.air.mshop.network.dto.AllArticlesResponse
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
    @GET("items")
    suspend fun getArticles(
    ): Response<AllArticlesResponse>

    @POST("items")
    suspend fun  createArticle(
        @Body article: ArticleRequest
    ): Response<ArticleResponse>

    @PUT("items/{id}")
    suspend fun updateArticle(
        @Path("id") id: Int,
        @Body request: ArticleRequest
    ): Response<ArticleResponse>

    @DELETE("items/{id}")
    suspend fun deleteArticle(
        @Path("id") id: Int
    ): Response<ArticleResponse>
}
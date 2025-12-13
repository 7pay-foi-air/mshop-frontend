package hr.foi.air.ws.api

import hr.foi.air.ws.models.MessageResponse
import hr.foi.air.ws.models.articleManagement.AllArticlesResponse
import hr.foi.air.ws.models.articleManagement.ArticleResponse
import hr.foi.air.ws.models.articleManagement.CreateItemResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface IArticleApi {
    @GET("items")
    suspend fun getArticles(
    ): Response<AllArticlesResponse>

    @Multipart
    @POST("items")
    suspend fun createItem(
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("price") price: RequestBody,
        @Part("currency") currency: RequestBody,
        @Part("sku") sku: RequestBody?,                 // optional
        @Part("stock_quantity") stockQuantity: RequestBody,
        @Part image: MultipartBody.Part?                // optional
    ): Response<CreateItemResponse>

    @Multipart
    @PUT("items/{uuid}")
    suspend fun updateItem(
        @Path("uuid") uuid: String,
        @Part("data") data: RequestBody,
        @Part image: MultipartBody.Part? = null
    ): Response<MessageResponse>


    @DELETE("items/{uuid}")
    suspend fun deleteArticle(
        @Path("uuid") uuid: String
    ): Response<ArticleResponse>
}
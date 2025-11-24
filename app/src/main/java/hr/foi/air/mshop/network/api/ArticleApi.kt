package hr.foi.air.mshop.network.api

import hr.foi.air.mshop.network.dto.AllArticlesResponse
import hr.foi.air.mshop.network.dto.ArticleRequest
import hr.foi.air.mshop.network.dto.ArticleResponse
import hr.foi.air.mshop.network.dto.CreateItemResponse
import hr.foi.air.mshop.network.dto.MessageResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ArticleApi {
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


    @DELETE("items/{id}")
    suspend fun deleteArticle(
        @Path("id") id: Int
    ): Response<ArticleResponse>
}
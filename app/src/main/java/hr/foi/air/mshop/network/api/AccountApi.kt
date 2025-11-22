package hr.foi.air.mshop.network.api

import hr.foi.air.mshop.network.dto.AddUserRequest
import hr.foi.air.mshop.network.dto.ArticleRequest
import hr.foi.air.mshop.network.dto.ArticleResponse
import hr.foi.air.mshop.network.dto.MessageResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AccountApi {
    @POST("register")
    suspend fun  createUser(
        @Body req: AddUserRequest
    ): Response<MessageResponse>
}
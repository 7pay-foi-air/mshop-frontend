package hr.foi.air.ws.api

import hr.foi.air.ws.models.MessageResponse
import hr.foi.air.ws.models.articleManagement.AllArticlesResponse
import hr.foi.air.ws.models.userManagement.AddUserRequest
import hr.foi.air.ws.models.login.LoginRequest
import hr.foi.air.ws.models.login.LoginResponse
import hr.foi.air.ws.models.userManagement.AllUsersResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface IAccountApi {
    @GET("users")
    suspend fun getUsers(
    ): Response<AllUsersResponse>
    @POST("register")
    suspend fun  createUser(
        @Body req: AddUserRequest
    ): Response<MessageResponse>
  
    @POST("login")
    suspend fun  loginUser(
        @Body loginRequest: LoginRequest,
    ): Response<LoginResponse>
}
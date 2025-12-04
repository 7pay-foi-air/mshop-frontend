package hr.foi.air.ws.api

import hr.foi.air.ws.models.MessageResponse
import hr.foi.air.ws.models.userManagement.AddUserRequest
import hr.foi.air.ws.models.login.LoginRequest
import hr.foi.air.ws.models.login.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AccountApi {
    @POST("register")
    suspend fun  createUser(
        @Body req: AddUserRequest
    ): Response<MessageResponse>
  
    @POST("login")
    suspend fun  loginUser(
        @Body loginRequest: LoginRequest,
    ): Response<LoginResponse>
}
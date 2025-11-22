package hr.foi.air.mshop.network.api

import hr.foi.air.mshop.network.dto.login.LoginRequest
import hr.foi.air.mshop.network.dto.login.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AccountApi {
    @POST("login")
    suspend fun  loginUser(
        @Body loginRequest: LoginRequest,
    ): Response<LoginResponse>
}
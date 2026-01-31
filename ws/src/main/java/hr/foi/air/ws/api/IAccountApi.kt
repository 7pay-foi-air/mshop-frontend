package hr.foi.air.ws.api

import hr.foi.air.ws.models.MessageResponse
import hr.foi.air.ws.models.login.ChangePasswordRequest
import hr.foi.air.ws.models.userManagement.AddUserRequest
import hr.foi.air.ws.models.login.LoginRequest
import hr.foi.air.ws.models.login.LoginResponse
import hr.foi.air.ws.models.userManagement.AllUsersResponse
import hr.foi.air.ws.models.userManagement.RecoverPasswordRequest
import hr.foi.air.ws.models.userManagement.UpdateMyProfileRequest
import hr.foi.air.ws.models.userManagement.UpdateUserAsAdminRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface IAccountApi {
    @GET("users")
    suspend fun getUsers(
        @Query("uuid") userId: String? = null
    ): Response<AllUsersResponse>
    @POST("register")
    suspend fun  createUser(
        @Body req: AddUserRequest
    ): Response<MessageResponse>

    @PATCH("users/{uuid}")
    suspend fun updateUserAsAdmin(
        @Path("uuid") userUuid: String,
        @Body request: UpdateUserAsAdminRequest
    ): Response<MessageResponse>

    @DELETE("users/{uuid}")
    suspend fun deleteUser(
        @Path("uuid") userUuid: String
    ): Response<MessageResponse>

    @PATCH("profile")
    suspend fun updateMyProfile(
        @Body request: UpdateMyProfileRequest
    ): Response<MessageResponse>
  
    @POST("login")
    suspend fun  loginUser(
        @Body loginRequest: LoginRequest,
    ): Response<LoginResponse>

    @POST("password/change")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest
    ): Response<MessageResponse>

    @POST("password/reset")
    suspend fun changePassword(
        @Body request: RecoverPasswordRequest
    ): Response<MessageResponse>
}
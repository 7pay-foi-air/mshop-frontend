package hr.foi.air.ws

import android.util.Log
import hr.foi.air.mshop.network.api.LlmApi
import hr.foi.air.ws.api.IAccountApi
import hr.foi.air.ws.api.IArticleApi
import hr.foi.air.ws.api.ITransactionApi
import hr.foi.air.ws.data.SessionManager
import hr.foi.air.ws.models.tokenRefresh.RefreshRequest
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.Authenticator
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkService {
    private const val ACCOUNT_BASE_URL = "http://${BuildConfig.SUBDOMAIN}:8080/api/v1/"
    private const val TRANSACTION_BASE_URL = "http://${BuildConfig.SUBDOMAIN}:8081/api/v1/"
    const val ARTICLE_BASE_URL = "http://${BuildConfig.SUBDOMAIN}:8082/api/v1/"

    private const val LLM_BASE_URL = "http://${BuildConfig.SUBDOMAIN}:8083/api/v1/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val authInterceptor = { chain: okhttp3.Interceptor.Chain ->
        val original = chain.request()
        val builder = original.newBuilder()

        val token = SessionManager.accessToken
        if (!token.isNullOrBlank()) {
            builder.addHeader("Authorization", "Bearer $token")
        }

        chain.proceed(builder.build())
    }

    private val tokenAuthenticator = object : Authenticator {
        override fun authenticate(route: Route?, response: Response): Request? {
            if (response.code != 401) {
                return null
            }
            Log.d("NetworkService", "Authenticator: 401 Unauthorized detected for URL: ${response.request.url}")

            val refreshToken = SessionManager.refreshToken
            if (refreshToken.isNullOrBlank()){
                Log.e("NetworkService", "Authenticator: No refresh token found. Ending session.")
                SessionManager.endSession()
                return null
            }

            Log.d("NetworkService", "Authenticator: Attempting to refresh access token...")
            val refreshApi = Retrofit.Builder()
                .baseUrl(ACCOUNT_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(IAccountApi::class.java)

            val refreshResponse = refreshApi.refreshAccessToken(RefreshRequest(refreshToken)).execute()

            return if (refreshResponse.isSuccessful && refreshResponse.body() != null) {
                val newTokens = refreshResponse.body()!!
                SessionManager.startSession(newTokens.accessToken, newTokens.refreshToken)

                response.request.newBuilder()
                    .header("Authorization", "Bearer ${newTokens.accessToken}")
                    .build()
            } else {
                Log.e("NetworkService", "Authenticator: Refresh failed (Code ${refreshResponse.code()}). Logging out.")
                SessionManager.endSession()
                null
            }
        }
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)
        .authenticator(tokenAuthenticator)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(100, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val accountRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(ACCOUNT_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    val articleRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(ARTICLE_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    val transactionRetrofit : Retrofit = Retrofit.Builder()
        .baseUrl(TRANSACTION_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    val llmRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(LLM_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()
    val accountApi: IAccountApi = accountRetrofit.create(IAccountApi::class.java)
    val articleApi: IArticleApi = articleRetrofit.create(IArticleApi::class.java)

    val transactionApi: ITransactionApi by lazy {
        transactionRetrofit.create(ITransactionApi::class.java)
    }

    val llmApi: LlmApi = llmRetrofit.create(LlmApi::class.java)
}
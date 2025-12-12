package hr.foi.air.ws

import hr.foi.air.ws.api.AccountApi
import hr.foi.air.ws.api.ArticleApi
import hr.foi.air.ws.api.ITransactionApi
import hr.foi.air.ws.data.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkService {
    private const val ACCOUNT_BASE_URL = "http://161.35.74.46:8080/api/v1/"
    const val ARTICLE_BASE_URL = "http://161.35.74.46:8082/api/v1/"

    private const val TRANSACTION_BASE_URL = "http://161.35.74.46:8081/api/v1/"
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

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)
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
    val accountApi: AccountApi = accountRetrofit.create(AccountApi::class.java)
    val articleApi: ArticleApi = articleRetrofit.create(ArticleApi::class.java)

    val transactionApi: ITransactionApi by lazy {
        transactionRetrofit.create(ITransactionApi::class.java)
    }

}
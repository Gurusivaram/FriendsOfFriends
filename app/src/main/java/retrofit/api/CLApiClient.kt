package retrofit.api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit.api.CLUrls.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CLApiClient {
    private val interceptor by lazy {
        OkHttpClient().newBuilder()
            .addInterceptor(CLAuthTokenInterceptor())
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }
    private val loggingInterceptor by lazy {
        OkHttpClient().newBuilder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }
    private val retrofitClient by lazy {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(interceptor)
            .build()
    }
    private val retrofitClientWithoutAuthentication by lazy {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(loggingInterceptor)
            .build()
    }
    val apiServices: CLApiServices by lazy {
        retrofitClient.create(CLApiServices::class.java)
    }
    val apiServicesWithoutAuthentication: CLApiServices by lazy {
        retrofitClientWithoutAuthentication.create(CLApiServices::class.java)
    }
}
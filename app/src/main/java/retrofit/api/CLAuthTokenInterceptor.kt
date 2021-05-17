package retrofit.api

import android.util.Log
import common.CLApplication
import common.CLErrors
import common.CLErrors.NETWORK_ERROR
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import repository.CLRepository
import java.io.IOException

class CLAuthTokenInterceptor : Interceptor {
    companion object {
        private const val TAG = "CLAuthTokenInterceptor"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!CLApplication.isNetworkConnected()) {
            throw IOException(NETWORK_ERROR)
        }
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
        var authToken = CLApplication.getPref().getAuthToken()
        val refreshToken = CLApplication.getPref().getRefreshToken()
        when {
            chain.request().url.toString() == "https://jwt-api-test.herokuapp.com/users" || chain.request().url.toString()
                .contains("relationships") -> {
                requestBuilder.addHeader("Content-Type", "application/json")
            }
            !chain.request().url.toString().contains("requests") -> {
                requestBuilder.addHeader("Content-Type", "application/x-www-form-urlencoded")
            }
        }
        requestBuilder.addHeader("Accept", "application/json")
        if (!authToken.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $authToken")
        }
        if (chain.request().method == "DELETE" || chain.request().url.toString()
                .contains("get_auth_token") && !refreshToken.isNullOrEmpty()
        ) {
            requestBuilder.addHeader("Refresh", refreshToken)
        }
        val request = requestBuilder.build()
        val response = chain.proceed(request)
        var count = 0
        if (response.code == 440 || response.code == 401 && !response.isSuccessful) {
            while (count++ < 3) {
                val job = GlobalScope.launch(Dispatchers.IO) {
                    val (token, errors) = CLRepository.refreshAuthToken()
                    launch(Dispatchers.Main) {
                        when {
                            token != "" -> {
                                authToken = token
                            }
                            else -> Log.e(TAG, errors)
                        }
                    }
                }
                runBlocking {
                    job.join()
                }
                try {
                    val newRequest = chain.request().newBuilder()
                    if (!authToken.isNullOrEmpty()) {
                        newRequest.addHeader("Authorization", "Bearer $authToken")
                    }
                    if (chain.request().method == "DELETE" || chain.request().url.toString()
                            .contains("get_auth_token") && !refreshToken.isNullOrEmpty()
                    ) {
                        newRequest.addHeader("Refresh", refreshToken)
                    }
                    when {
                        chain.request().url.toString() == "https://jwt-api-test.herokuapp.com/users" || chain.request().url.toString()
                            .contains("relationships") -> {
                            requestBuilder.addHeader("Content-Type", "application/json")
                        }
                        !chain.request().url.toString().contains("requests") -> {
                            requestBuilder.addHeader(
                                "Content-Type",
                                "application/x-www-form-urlencoded"
                            )
                        }
                    }
                    newRequest.addHeader("Accept", "application/json")
                    return chain.proceed(newRequest.build())
                } catch (e: IOException) {
                    Log.e(TAG, e.toString())

                } catch (e: Exception) {
                    Log.e(TAG, e.toString())
                }
            }
        }
        return response
    }
}
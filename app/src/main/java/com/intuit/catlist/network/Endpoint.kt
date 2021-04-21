package com.intuit.catlist.network

import com.intuit.catlist.model.Cat
import com.intuit.catlist.model.NetworkResponse
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

private fun <T> Response<T>.toNetworkResponse() : NetworkResponse<T> {
    return if (isSuccessful) {
        NetworkResponse.Success(body()!!)
    } else {
        NetworkResponse.Error("[${code()}] API failed")
    }
}

interface Endpoint {
    @GET("v1/images/search")
    suspend fun getCatList(@Header("x-api-key")apiKey: String,
                           @Query("order") order: String = "ASC", @Query("page") page: Int,
                           @Query("limit") limit: Int = 20, @Query("mime_types")mimeType: String = "jpg,png",
                           @Query("has_breeds") hasBreeds: Boolean = true): Response<List<Cat>>

    companion object {
        private const val BASE_URL = "https://api.thecatapi.com/"
        fun create(): Endpoint {
            val okHttpClient = OkHttpClient.Builder()
                .callTimeout(10, TimeUnit.SECONDS)
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(Endpoint::class.java)
        }

        suspend fun <T> safeApiCall(block: suspend () -> Response<T>): NetworkResponse<T> {
            return try {
                block().toNetworkResponse()
            } catch (ex1: TimeoutException) {
                NetworkResponse.Error("API Timed out")
            } catch (ex2: UnknownHostException) {
                NetworkResponse.Error("No Connection")
            }
        }
    }
}
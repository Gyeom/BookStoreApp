package com.example.bookstore.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface BookService {
    @GET("1.0/search/{query}/{page}")
    suspend fun searchBooks(
        @Path("query") query: String,
        @Path("page") page: Int
    ): BookSearchResponse

    @GET("1.0/search/{id}")
    suspend fun searchBook(
        @Path("id") id: String
    ): BookSearchResponse

    companion object {
        private const val BASE_URL = "https://api.itbook.store/"

        fun create(): BookService {
            val logger = HttpLoggingInterceptor()
            logger.level = Level.BASIC

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookService::class.java)
        }
    }
}

package com.mena97villalobos.ltvblog.data.network

import com.mena97villalobos.ltvblog.data.model.BlogsResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://www.beenverified.com/articles/"

interface BlogsService {
    @GET("index.mobile-android.json") suspend fun getAllBlogs(): BlogsResponse
}

object BlogsNetworkApi {

    val retrofitServices: BlogsService by lazy {
        val retrofit =
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        retrofit.create(BlogsService::class.java)
    }
}

package com.users.onboarding.data.network

import com.google.gson.GsonBuilder
import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

interface RetrofitHelper {
    fun buildRetrofitInstance(baseURL: String): Retrofit
}

class RetrofitHelperImpl: RetrofitHelper {

    override fun buildRetrofitInstance(baseURL: String): Retrofit {
        val httpBuilder: OkHttpClient.Builder = OkHttpClient.Builder()

        val httpClient = httpBuilder
            .connectTimeout(90, TimeUnit.SECONDS)
            .writeTimeout(90, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseURL)
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .client(httpClient)
            .build()
    }
}
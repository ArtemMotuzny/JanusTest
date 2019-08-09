package com.test.janustestlib

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Retrofit(private val context: Context) {

    private fun httpClient(): OkHttpClient {
        return unsafeOkHttpClient
    }

    fun <T> genService(service: Class<T>): T {
        return Retrofit.Builder()
            .baseUrl("https://janus.conf.meetecho.com/")
            .client(httpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(service)
    }

    private val unsafeOkHttpClient = OkHttpClient.Builder().apply {
        addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
    }.build()
}
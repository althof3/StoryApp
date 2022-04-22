package com.altop.mystoryapp.data.remote.retrofit

import com.altop.mystoryapp.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
  fun getApiService(token: String?): ApiService {
    val loggingInterceptor = if (BuildConfig.DEBUG) {
      HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    } else {
      HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
    }
    
    
    val headerInterceptor = Interceptor { chain ->
      var request = chain.request()
      request = request.newBuilder().addHeader("Authorization", "Bearer $token").build()
      chain.proceed(request)
    }
    
    var client: OkHttpClient.Builder = OkHttpClient.Builder().addInterceptor(loggingInterceptor)
    if (token != null) {
      client = client.addInterceptor(headerInterceptor)
    }
    val retrofit = Retrofit.Builder().baseUrl("https://story-api.dicoding.dev/v1/")
      .addConverterFactory(GsonConverterFactory.create()).client(client.build()).build()
    return retrofit.create(ApiService::class.java)
  }
}
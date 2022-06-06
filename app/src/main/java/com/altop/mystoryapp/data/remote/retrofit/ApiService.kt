package com.altop.mystoryapp.data.remote.retrofit

import com.altop.mystoryapp.data.remote.response.LoginResponse
import com.altop.mystoryapp.data.remote.response.Response
import com.altop.mystoryapp.data.remote.response.StoryListResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
  @GET("stories")
  suspend fun getStories(
    @Query("location") location: Int, @Query("page") page: Int, @Query("size") size: Int
  ): StoryListResponse
  
  @Multipart
  @POST("stories")
  fun postStories(
    @Part file: MultipartBody.Part,
    @Part("description") description: RequestBody,
    @Part("lat") lat: RequestBody,
    @Part("lon") lon: RequestBody,
  ): Call<Response>
  
  @POST("login")
  fun postLogin(@Body params: RequestBody): Call<LoginResponse>
  
  @POST("register")
  fun postRegister(@Body params: RequestBody): Call<Response>
}
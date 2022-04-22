package com.altop.mystoryapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.altop.mystoryapp.createJsonRequestBody
import com.altop.mystoryapp.data.Result
import com.altop.mystoryapp.data.local.UserPreference
import com.altop.mystoryapp.data.remote.response.Response
import com.altop.mystoryapp.data.remote.retrofit.ApiConfig
import com.altop.mystoryapp.getErrorMessage
import retrofit2.Call
import retrofit2.Callback


class RegisterRepository(mUserPreference: UserPreference.UserPreference) {
  
  private val apiService = ApiConfig.getApiService(mUserPreference.getUser().token)
  private val registerResult = MutableLiveData<Result<Boolean>>()
  
  fun register(username: String, email: String, password: String): LiveData<Result<Boolean>> {
    val params = createJsonRequestBody("name" to username, "email" to email, "password" to password)
    val client = apiService.postRegister(params)
    registerResult.value = Result.Loading
    
    client.enqueue(object : Callback<Response> {
      override fun onResponse(
        call: Call<Response>, response: retrofit2.Response<Response>
      ) {
        if (response.isSuccessful) {
          val isError = response.body()!!.error
          registerResult.value = Result.Success(!isError)
        } else {
          val errorMessage = getErrorMessage(response.errorBody()?.string())
          registerResult.value = Result.Error(errorMessage)
        }
      }
      
      override fun onFailure(call: Call<Response>, t: Throwable) {
        registerResult.value = Result.Error(t.message.toString())
      }
    })
    
    return registerResult
  }
  
  companion object {
    @Volatile
    private var instance: RegisterRepository? = null
    
    fun getInstance(
      mUserPreference: UserPreference.UserPreference
    ): RegisterRepository = instance ?: synchronized(this) {
      instance ?: RegisterRepository(mUserPreference)
    }.also { instance = it }
    
  }
}
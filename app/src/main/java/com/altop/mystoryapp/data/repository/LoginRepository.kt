package com.altop.mystoryapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.altop.mystoryapp.createJsonRequestBody
import com.altop.mystoryapp.data.Result
import com.altop.mystoryapp.data.local.UserPreference
import com.altop.mystoryapp.data.remote.response.LoginResponse
import com.altop.mystoryapp.data.remote.response.User
import com.altop.mystoryapp.data.remote.retrofit.ApiConfig
import com.altop.mystoryapp.getErrorMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginRepository(private val userPreference: UserPreference.UserPreference) {
  private val apiService = ApiConfig.getApiService(userPreference.getUser().token)
  private val loginResult = MutableLiveData<Result<User>>()
  
  fun setUser(user: User) = userPreference.setUser(user)
  
  fun isLoggedIn() = userPreference.isLoggedIn()
  
  fun login(email: String, password: String): LiveData<Result<User>> {
    val params = createJsonRequestBody("email" to email, "password" to password)
    
    val client = apiService.postLogin(params)
    loginResult.value = Result.Loading
    
    client.enqueue(object : Callback<LoginResponse> {
      override fun onResponse(
        call: Call<LoginResponse>, response: Response<LoginResponse>
      ) {
        if (response.isSuccessful) {
          val user = response.body()!!.loginResult
          setUser(user)
          loginResult.value = Result.Success(user)
        } else {
          val errorMessage = getErrorMessage(response.errorBody()?.string())
          loginResult.value = Result.Error(errorMessage)
        }
      }
      
      override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
        loginResult.value = Result.Error(t.message.toString())
      }
    })
    
    return loginResult
  }
  
  companion object {
    @Volatile
    private var instance: LoginRepository? = null
    
    fun getInstance(
      mUserPreference: UserPreference.UserPreference
    ): LoginRepository = instance ?: synchronized(this) {
      instance ?: LoginRepository(mUserPreference)
    }.also { instance = it }
  }
}
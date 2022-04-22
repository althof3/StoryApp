package com.altop.mystoryapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.altop.mystoryapp.data.Result
import com.altop.mystoryapp.data.local.UserPreference
import com.altop.mystoryapp.data.remote.response.Response
import com.altop.mystoryapp.data.remote.response.Story
import com.altop.mystoryapp.data.remote.response.StoryListResponse
import com.altop.mystoryapp.data.remote.retrofit.ApiConfig
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response as RetrofitResponse

class StoryRepository (private val mUserPreference: UserPreference.UserPreference) {
  
  private val storiesResult = MutableLiveData<Result<List<Story>>>()
  private val _uploadImageResult = MutableLiveData<Result<Response>>()
  val uploadImageResult: LiveData<Result<Response>> = _uploadImageResult
  
  private val apiService = ApiConfig.getApiService(mUserPreference.getUser().token)
  
  
  fun getUserName() = mUserPreference.getUser().name
  
  fun logout() = mUserPreference.clearSession()
  
  fun isLoggedIn() = mUserPreference.isLoggedIn()
  
  fun getStories(): LiveData<Result<List<Story>>> {
    val client = apiService.getStories()
    storiesResult.value = Result.Loading
    
    client.enqueue(object : Callback<StoryListResponse> {
      override fun onResponse(
        call: Call<StoryListResponse>, response: RetrofitResponse<StoryListResponse>
      ) {
        
        if (response.isSuccessful) {
          val stories = response.body()?.listStory
          val storyList = ArrayList<Story>()
          stories?.forEach { story ->
            val news = Story(
              id = story.id,
              name = story.name,
              description = story.description,
              photoUrl = story.photoUrl,
              createdAt = story.createdAt
            )
            storyList.add(news)
          }
          storiesResult.value = Result.Success(storyList)
        }
      }
      
      override fun onFailure(call: Call<StoryListResponse>, t: Throwable) {
        storiesResult.value = Result.Error(t.message.toString())
      }
    })
    
    return storiesResult
  }
  
  fun postStory(
    imageMultipart: MultipartBody.Part, description: RequestBody
  ) {
    val client = apiService.postStories(imageMultipart, description)
    
    _uploadImageResult.value = Result.Loading
    client.enqueue(object : Callback<Response> {
      override fun onResponse(
        call: Call<Response>, response: RetrofitResponse<Response>
      ) {
        if (response.isSuccessful) {
          _uploadImageResult.value = Result.Success(response.body()!!)
        } else {
          _uploadImageResult.value = Result.Error(response.message())
        }
      }
      
      override fun onFailure(call: Call<Response>, t: Throwable) {
        _uploadImageResult.value = Result.Error(t.message.toString())
      }
    })
    
  }
  
  companion object {
    @Volatile
    private var instance: StoryRepository? = null
    
    fun getInstance(
      mUserPreference: UserPreference.UserPreference
    ): StoryRepository = instance ?: synchronized(this) {
      instance ?: StoryRepository(mUserPreference)
    }.also { instance = it }
  }
}
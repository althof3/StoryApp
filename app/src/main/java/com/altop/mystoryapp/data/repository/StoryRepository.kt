package com.altop.mystoryapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import com.altop.mystoryapp.data.Result
import com.altop.mystoryapp.data.entity.StoryEntity
import com.altop.mystoryapp.data.local.IUserPreference
import com.altop.mystoryapp.data.paging.StoryRemoteMediator
import com.altop.mystoryapp.data.remote.response.Response
import com.altop.mystoryapp.data.remote.retrofit.ApiConfig
import com.altop.mystoryapp.database.StoryDatabase
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response as RetrofitResponse

class StoryRepository(
  private val storyDatabase: StoryDatabase, private val mUserPreference: IUserPreference
) {
  
  
  private val _uploadImageResult = MutableLiveData<Result<Response>>()
  val uploadImageResult: LiveData<Result<Response>> = _uploadImageResult
  
  fun getStoriesFromDB() = storyDatabase.storyDao().getStories()
  
  fun getUserName() = mUserPreference.getUser().name
  
  fun logout() = mUserPreference.clearSession()
  
  fun isLoggedIn() = mUserPreference.isLoggedIn()
  
  @OptIn(ExperimentalPagingApi::class)
  fun getStories(): LiveData<PagingData<StoryEntity>> {
    val apiService = ApiConfig.getApiService(mUserPreference.getUser().token)
    return Pager(config = PagingConfig(pageSize = 5),
      remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
      pagingSourceFactory = { storyDatabase.storyDao().getAllStory() }).liveData
  }
  
  fun postStory(
    imageMultipart: MultipartBody.Part,
    description: RequestBody,
    lat: RequestBody,
    lon: RequestBody,
  ) {
    val apiService = ApiConfig.getApiService(mUserPreference.getUser().token)
    val client = apiService.postStories(imageMultipart, description, lat, lon)
    
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
      storyDatabase: StoryDatabase, mUserPreference: IUserPreference
    ): StoryRepository = instance ?: synchronized(this) {
      instance ?: StoryRepository(storyDatabase, mUserPreference)
    }.also { instance = it }
    
    const val WITH_LOCATION = 1
  }
}
package com.altop.mystoryapp.ui.listStory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.altop.mystoryapp.data.entity.StoryEntity
import com.altop.mystoryapp.data.repository.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {
  
  val uploadStatus = storyRepository.uploadImageResult
  
  fun allStories(): LiveData<List<StoryEntity>> = storyRepository.getStoriesFromDB()
  
  fun getStories() = storyRepository.getStories().cachedIn(viewModelScope)
  fun getUserName() = storyRepository.getUserName()
  fun logout() = storyRepository.logout()
  fun postStory(
    imageMultipart: MultipartBody.Part,
    description: RequestBody,
    lat: RequestBody,
    lon: RequestBody,
  ) = storyRepository.postStory(imageMultipart, description, lat, lon)
  
  fun isLoggedIn() = storyRepository.isLoggedIn()
}
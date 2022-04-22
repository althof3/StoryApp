package com.altop.mystoryapp.ui.listStory

import androidx.lifecycle.ViewModel
import com.altop.mystoryapp.data.repository.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {
  
  val uploadStatus = storyRepository.uploadImageResult
  
  fun getStories() = storyRepository.getStories()
  fun getUserName() = storyRepository.getUserName()
  fun logout() = storyRepository.logout()
  fun postStory(imageMultipart: MultipartBody.Part, description: RequestBody) =
    storyRepository.postStory(imageMultipart, description)
  
  fun isLoggedIn() = storyRepository.isLoggedIn()
}
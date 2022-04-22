package com.altop.mystoryapp.ui.listStory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.altop.mystoryapp.data.repository.StoryRepository
import com.altop.mystoryapp.di.Injection

class StoryViewModelFactory private constructor(private val storyRepository: StoryRepository) :
  ViewModelProvider.NewInstanceFactory() {
  
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
      return StoryViewModel(storyRepository) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
  
  companion object {
    @Volatile
    private var instance: StoryViewModelFactory? = null
    
    fun getInstance(context: Context): StoryViewModelFactory = instance ?: synchronized(this) {
      instance ?: StoryViewModelFactory(Injection.provideStoryRepository(context))
    }.also { instance = it }
    
  }
}
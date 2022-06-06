package com.altop.mystoryapp.di

import android.content.Context
import com.altop.mystoryapp.data.local.UserPreference
import com.altop.mystoryapp.data.repository.LoginRepository
import com.altop.mystoryapp.data.repository.RegisterRepository
import com.altop.mystoryapp.data.repository.StoryRepository
import com.altop.mystoryapp.database.StoryDatabase

object Injection {
  fun provideLoginRepository(context: Context): LoginRepository {
    val mUserPreference = UserPreference.UserPreference(context)
    return LoginRepository.getInstance(mUserPreference)
  }
  
  fun provideRegisterRepository(context: Context): RegisterRepository {
    val mUserPreference = UserPreference.UserPreference(context)
    return RegisterRepository.getInstance(mUserPreference)
  }
  
  fun provideStoryRepository(context: Context): StoryRepository {
    val database = StoryDatabase.getDatabase(context)
    val mUserPreference = UserPreference.UserPreference(context)
    return StoryRepository.getInstance(database, mUserPreference)
  }
}
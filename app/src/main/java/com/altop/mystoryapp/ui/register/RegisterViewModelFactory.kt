package com.altop.mystoryapp.ui.register

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.altop.mystoryapp.data.repository.RegisterRepository
import com.altop.mystoryapp.di.Injection

class RegisterViewModelFactory private constructor(private val registerRepository: RegisterRepository) :
  ViewModelProvider.NewInstanceFactory() {
  
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
      return RegisterViewModel(registerRepository) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
  
  companion object {
    @Volatile
    private var instance: RegisterViewModelFactory? = null
    
    fun getInstance(context: Context): RegisterViewModelFactory = instance ?: synchronized(this) {
      instance ?: RegisterViewModelFactory(Injection.provideRegisterRepository(context))
    }.also { instance = it }
  }
}
package com.altop.mystoryapp.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.altop.mystoryapp.data.repository.RegisterRepository
import com.altop.mystoryapp.isEmailValid
import com.altop.mystoryapp.isPasswordValid


class RegisterViewModel(private val registerRepository: RegisterRepository) : ViewModel() {
  
  private val _isFormValid = MutableLiveData<Boolean>()
  
  fun registerFormChanged(name: String, email: String, password: String): LiveData<Boolean> {
    _isFormValid.value = isEmailValid(email) && isPasswordValid(password) && name.length > 4
    return _isFormValid
  }
  
  fun register(name: String, email: String, password: String) =
    registerRepository.register(name, email, password)
  
}
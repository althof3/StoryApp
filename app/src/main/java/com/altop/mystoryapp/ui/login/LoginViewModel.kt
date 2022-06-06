package com.altop.mystoryapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.altop.mystoryapp.data.repository.LoginRepository
import com.altop.mystoryapp.isEmailValid
import com.altop.mystoryapp.isPasswordValid


class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {
  
  private val _isFormValid = MutableLiveData<Boolean>()
  
  fun login(email: String, password: String) = loginRepository.login(email, password)
  
  fun isLoggedIn() = loginRepository.isLoggedIn()
  
  fun loginDataChanged(email: String, password: String): LiveData<Boolean> {
    _isFormValid.value = isEmailValid(email) && isPasswordValid(password)
    return _isFormValid
  }
  
}
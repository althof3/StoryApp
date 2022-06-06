package com.altop.mystoryapp.ui.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.altop.mystoryapp.data.Result
import com.altop.mystoryapp.data.repository.RegisterRepository
import com.altop.mystoryapp.getOrAwaitValue
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RegisterViewModelTest {
  @get:Rule
  var instantExecutorRule = InstantTaskExecutorRule()
  
  @Mock
  private lateinit var registerRepository: RegisterRepository
  private lateinit var registerViewModel: RegisterViewModel
  
  @Mock
  private lateinit var mockRegisterViewModel: RegisterViewModel
  
  @Before
  fun setUp() {
    registerViewModel = RegisterViewModel(registerRepository)
  }
  
  @Test
  fun `when Register Success Should Return Success`() {
    val expectedReturn = MutableLiveData<Result<Boolean>>()
    expectedReturn.value = Result.Success(true)
    
    `when`(registerViewModel.register("altop", "altop@gmail.com", "altop")).thenReturn(
      expectedReturn
    )
    
    val actualReturn =
      registerViewModel.register("altop", "altop@gmail.com", "altop").getOrAwaitValue()
    Assert.assertNotNull(actualReturn)
    Assert.assertTrue(actualReturn is Result.Success)
  }
  
  @Test
  fun `when Register Error Should Return Error`() {
    val expectedReturn = MutableLiveData<Result<Boolean>>()
    expectedReturn.value = Result.Error("error")
    
    `when`(registerViewModel.register("altop", "altop@gmail.com", "altop")).thenReturn(
      expectedReturn
    )
    
    val actualReturn =
      registerViewModel.register("altop", "altop@gmail.com", "altop").getOrAwaitValue()
    Assert.assertNotNull(actualReturn)
    Assert.assertTrue(actualReturn is Result.Error)
  }
  
  @Test
  fun `when registerFormChanged and valid Should Return True`() {
    val isValid = MutableLiveData<Boolean>()
    isValid.value = true
    
    `when`(
      mockRegisterViewModel.registerFormChanged(
        "altop@gmail.com", "altopaltop", "password"
      )
    ).thenReturn(isValid)
    
    val actualResult =
      mockRegisterViewModel.registerFormChanged("altop@gmail.com", "altopaltop", "password")
        .getOrAwaitValue()
    Assert.assertNotNull(actualResult)
    Assert.assertEquals(true, actualResult)
  }
  
  @Test
  fun `when registerFormChanged and not valid Should Return False`() {
    val isValid = MutableLiveData<Boolean>()
    isValid.value = false
    
    `when`(
      mockRegisterViewModel.registerFormChanged(
        "altop@gmail.com", "alt", "password"
      )
    ).thenReturn(isValid)
    
    val actualResult =
      mockRegisterViewModel.registerFormChanged("altop@gmail.com", "alt", "password")
        .getOrAwaitValue()
    Assert.assertNotNull(actualResult)
    Assert.assertEquals(false, actualResult)
  }
  
}
package com.altop.mystoryapp.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.altop.mystoryapp.DataDummy
import com.altop.mystoryapp.data.Result
import com.altop.mystoryapp.data.remote.response.User
import com.altop.mystoryapp.data.repository.LoginRepository
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
class LoginViewModelTest {
  @get:Rule
  var instantExecutorRule = InstantTaskExecutorRule()
  
  @Mock
  private lateinit var loginRepository: LoginRepository
  private lateinit var loginViewModel: LoginViewModel
  private val dummyUser = DataDummy.generateDummyUserEntity()
  
  @Mock
  private lateinit var mockLoginViewModel: LoginViewModel
  
  
  @Before
  fun setUp() {
    loginViewModel = LoginViewModel(loginRepository)
  }
  
  @Test
  fun `when Login Success Should Not Null and Return Success`() {
    val expectedUser = MutableLiveData<Result<User>>()
    expectedUser.value = Result.Success(dummyUser)
    
    `when`(loginViewModel.login("altop@gmail.com", "altop")).thenReturn(expectedUser)
    
    val actualUser = loginViewModel.login("altop@gmail.com", "altop").getOrAwaitValue()
    Assert.assertNotNull(actualUser)
    Assert.assertTrue(actualUser is Result.Success)
    Assert.assertEquals(dummyUser.name, (actualUser as Result.Success).data.name)
    Assert.assertEquals(dummyUser.token, actualUser.data.token)
    Assert.assertEquals(dummyUser.userId, actualUser.data.userId)
  }
  
  @Test
  fun `when Network Error Login Should Return Error`() {
    val user = MutableLiveData<Result<User>>()
    user.value = Result.Error("Error")
    
    `when`(loginViewModel.login("altop@gmail.com", "altop")).thenReturn(user)
    
    val actualNews = loginViewModel.login("altop@gmail.com", "altop").getOrAwaitValue()
    Assert.assertNotNull(actualNews)
    Assert.assertTrue(actualNews is Result.Error)
  }
  
  @Test
  fun `when loggedIn should return true`() {
    val expectedValue = true
    
    `when`(loginViewModel.isLoggedIn()).thenReturn(expectedValue)
    
    val actualNews = loginViewModel.isLoggedIn()
    Assert.assertNotNull(actualNews)
    Assert.assertEquals(true, actualNews)
  }
  
  @Test
  fun `when loggedOut should return false`() {
    val expectedValue = false
    
    `when`(loginViewModel.isLoggedIn()).thenReturn(expectedValue)
    
    val actualNews = loginViewModel.isLoggedIn()
    Assert.assertNotNull(actualNews)
    Assert.assertEquals(false, actualNews)
  }
  
  
  @Test
  fun `when loginDataChanged and valid Should Return True`() {
    val isValid = MutableLiveData<Boolean>()
    isValid.value = true
    
    `when`(mockLoginViewModel.loginDataChanged("altop@gmail.com", "altopaltop")).thenReturn(isValid)
    
    val actualResult = mockLoginViewModel.loginDataChanged("altop@gmail.com", "altopaltop").getOrAwaitValue()
    Assert.assertNotNull(actualResult)
    Assert.assertEquals(true, actualResult)
  }
  
  @Test
  fun `when loginDataChanged and not valid Should Return False`() {
    val isValid = MutableLiveData<Boolean>()
    isValid.value = false
    
    `when`(mockLoginViewModel.loginDataChanged("altop@gmail.com", "alt")).thenReturn(isValid)
    
    val actualResult = mockLoginViewModel.loginDataChanged("altop@gmail.com", "alt").getOrAwaitValue()
    Assert.assertNotNull(actualResult)
    Assert.assertEquals(false, actualResult)
  }
  
}
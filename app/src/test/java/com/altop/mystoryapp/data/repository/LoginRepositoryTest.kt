package com.altop.mystoryapp.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.altop.mystoryapp.DataDummy
import com.altop.mystoryapp.MainCoroutineRule
import com.altop.mystoryapp.data.Result
import com.altop.mystoryapp.data.local.IUserPreference
import com.altop.mystoryapp.data.remote.response.User
import com.altop.mystoryapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.json.JSONObject
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class LoginRepositoryTest {
  
  @get:Rule
  var instantExecutorRule = InstantTaskExecutorRule()
  
  @get:Rule
  var mainCoroutineRule = MainCoroutineRule()
  
  @Mock
  private lateinit var mUserPref: IUserPreference
  private lateinit var loginRepository: LoginRepository
  
  @Mock
  private lateinit var mockRepo: LoginRepository
  private val dummyUser = DataDummy.generateDummyUserEntity()
  
  @Mock
  private lateinit var mock: JSONObject
  
  private val name = "altop"
  private val id = "idUser"
  private val token = "tokenuser"
  
  @Before
  fun setUp() {
    Mockito.doReturn(User(name, id, token)).`when`(mUserPref).getUser()
    
    loginRepository = LoginRepository(mUserPref)
    LoginRepository.getInstance(mUserPref)
    
  }

  @Test
  fun `when login Should return Success`() = mainCoroutineRule.runBlockingTest {
    val email = "altop@gmail.com"
    val password = "altopaltop"
    val expectedUser = MutableLiveData<Result<User>>()
    expectedUser.value = Result.Success(dummyUser)

    `when`(mockRepo.login(email, password)).thenReturn(expectedUser)

    val actualUser = mockRepo.login(email, password).getOrAwaitValue()
    Assert.assertNotNull(actualUser)
    Assert.assertTrue(actualUser is Result.Success)
    Assert.assertEquals(dummyUser.name, (actualUser as Result.Success).data.name)
    Assert.assertEquals(dummyUser.token, actualUser.data.token)
    Assert.assertEquals(dummyUser.userId, actualUser.data.userId)
  }
  
  @Test
  fun `when setUser called Should mUserPref_setUser called too`() {
    
    loginRepository.setUser(User(name, id, token))
    
    verify(mUserPref).setUser(User(name, id, token))
  }

  @Test
  fun `when isLoggedIn called Should return true`() {
    val expectedValue = true
  
    `when`(loginRepository.isLoggedIn()).thenReturn(expectedValue)
  
    val actualNews = loginRepository.isLoggedIn()
    Assert.assertNotNull(actualNews)
    Assert.assertEquals(true, actualNews)
  }
}
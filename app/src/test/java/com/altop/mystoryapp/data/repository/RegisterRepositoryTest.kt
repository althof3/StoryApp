package com.altop.mystoryapp.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.altop.mystoryapp.MainCoroutineRule
import com.altop.mystoryapp.data.Result
import com.altop.mystoryapp.data.local.IUserPreference
import com.altop.mystoryapp.data.remote.response.User
import com.altop.mystoryapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doReturn
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class RegisterRepositoryTest {
  
  @get:Rule
  var instantExecutorRule = InstantTaskExecutorRule()
  
  @get:Rule
  var mainCoroutineRule = MainCoroutineRule()

  
  @Mock
  private lateinit var mUserPref: IUserPreference
  private lateinit var registerRepository: RegisterRepository
  
  @Mock
  private lateinit var mockRepo: RegisterRepository
  
  
  private val name = "altop"
  private val id = "idUser"
  private val token = "tokenuser"
  private val email = "altop@gmail.com"
  private val password = "altopaltop"
  
  @Before
  fun setUp() {
    doReturn(User(name, id, token)).`when`(mUserPref).getUser()
    registerRepository = RegisterRepository(mUserPref)
    RegisterRepository.getInstance(mUserPref)
  }
  
  @Test
  fun `when register Should return true`() {
    val expectedUser = MutableLiveData<Result<Boolean>>()
    expectedUser.value = Result.Success(true)
    
    `when`(mockRepo.register(name, email, password)).thenReturn(expectedUser)
    
    val actualUser = mockRepo.register(name, email, password).getOrAwaitValue()
    Assert.assertNotNull(actualUser)
    Assert.assertTrue(actualUser is Result.Success)
    Assert.assertEquals(true, (actualUser as Result.Success).data)
    
  }
  
}
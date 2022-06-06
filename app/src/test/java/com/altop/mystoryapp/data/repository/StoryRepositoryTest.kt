package com.altop.mystoryapp.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import com.altop.mystoryapp.DataDummy
import com.altop.mystoryapp.MainCoroutineRule
import com.altop.mystoryapp.data.entity.StoryEntity
import com.altop.mystoryapp.data.local.IUserPreference
import com.altop.mystoryapp.data.remote.response.User
import com.altop.mystoryapp.database.StoryDao
import com.altop.mystoryapp.database.StoryDatabase
import com.altop.mystoryapp.getOrAwaitValue
import com.altop.mystoryapp.ui.listStory.PagedTestDataSources
import com.altop.mystoryapp.ui.listStory.listStoryCardView.ListStoryAdapter
import com.altop.mystoryapp.ui.listStory.noopListUpdateCallback
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.io.File


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class StoryRepositoryTest {
  
  @get:Rule
  var instantExecutorRule = InstantTaskExecutorRule()
  
  @get:Rule
  var mainCoroutineRule = MainCoroutineRule()
  
  @Mock
  private lateinit var storyDB: StoryDatabase
  
  @Mock
  private lateinit var mUserPref: IUserPreference
  private lateinit var storyRepository: StoryRepository
  
  @Mock
  private lateinit var mockStoryRepository: StoryRepository
  
  private val dummyStories = DataDummy.generateDummyListStoryEntity()
  
  @Mock
  private lateinit var dao: StoryDao
  
  @Before
  fun setUp() {
    `when`(mUserPref.getUser()).thenReturn(User("name", "userId", "token"))
    `when`(storyDB.storyDao()).thenReturn(dao)
    
    storyRepository = StoryRepository(storyDB, mUserPref)
    StoryRepository.getInstance(storyDB, mUserPref)
    
  }
  
  @Test
  fun `when getStoriesFromDB Should Not Null`() {
    val expectedStory = MutableLiveData<List<StoryEntity>>()
    expectedStory.value = dummyStories
    
    `when`(dao.getStories()).thenReturn(expectedStory)
    
    val actualStories = storyRepository.getStoriesFromDB().getOrAwaitValue()
    
    Assert.assertNotNull(actualStories)
    Assert.assertEquals(dummyStories.size, actualStories.size)
  }
  
  @Test
  fun `when getUserName Should same as FakeUser username`() {
    doReturn(User("altop", "id", "token")).`when`(mUserPref).getUser()
    
    val actualUsername = storyRepository.getUserName()
    Assert.assertNotNull(actualUsername)
    Assert.assertEquals("altop", actualUsername)
  }
  
  @Test
  fun `when logout Should delete current User`() {
    
    storyRepository.logout()
    
    verify(mUserPref).clearSession()
  }
  
  @Test
  fun `when isLoggedIn called Should return true`() {
    
    `when`(mUserPref.isLoggedIn()).thenReturn(true)
    
    val actualIsLogin = storyRepository.isLoggedIn()
    
    Assert.assertNotNull(actualIsLogin)
    Assert.assertEquals(true, actualIsLogin)
  }
  
  @Test
  fun `when getStories called Should Not Null and return pagingData of StoryEntity`() =
    mainCoroutineRule.runBlockingTest {
      val dummyStory = DataDummy.generateDummyListStoryEntity()
      val data = PagedTestDataSources.snapshot(dummyStory)
      val story = MutableLiveData<PagingData<StoryEntity>>()
      story.value = data

      `when`(mockStoryRepository.getStories()).thenReturn(story)

      val actualStories = mockStoryRepository.getStories().getOrAwaitValue()

      val differ = AsyncPagingDataDiffer(
        diffCallback = ListStoryAdapter.DIFF_CALLBACK,
        updateCallback = noopListUpdateCallback,
        mainDispatcher = mainCoroutineRule.dispatcher,
        workerDispatcher = mainCoroutineRule.dispatcher,
      )
      differ.submitData(actualStories)

      advanceUntilIdle()

      Assert.assertNotNull(differ.snapshot())
      Assert.assertEquals(dummyStory.size, differ.snapshot().size)
      Assert.assertEquals(dummyStory[0].name, differ.snapshot()[0]?.name)
    }
  
  @Test
  fun `when postStory called, storyRepositoryPostStory should called too`() {
    val description = "editDesc".toRequestBody("text/plain".toMediaType())
    val lat = "lat".toRequestBody("text/plain".toMediaType())
    val lon = "lon".toRequestBody("text/plain".toMediaType())
    val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
      "photo", "file.name", File("aasdsa").asRequestBody("image/jpeg".toMediaTypeOrNull())
    )
    storyRepository.postStory(imageMultipart, description, lat, lon)
    
    verify(mUserPref).getUser()
  }
}
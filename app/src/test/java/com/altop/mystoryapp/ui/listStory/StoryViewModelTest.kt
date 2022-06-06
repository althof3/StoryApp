package com.altop.mystoryapp.ui.listStory

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.altop.mystoryapp.DataDummy
import com.altop.mystoryapp.MainCoroutineRule
import com.altop.mystoryapp.data.entity.StoryEntity
import com.altop.mystoryapp.data.repository.StoryRepository
import com.altop.mystoryapp.getOrAwaitValue
import com.altop.mystoryapp.ui.listStory.listStoryCardView.ListStoryAdapter
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
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryViewModelTest {
  @get:Rule
  var instantExecutorRule = InstantTaskExecutorRule()
  
  @get:Rule
  var mainCoroutineRules = MainCoroutineRule()
  
  private lateinit var storyViewModel: StoryViewModel
  
  @Mock
  private lateinit var storyRepository: StoryRepository
  private val dummyStories = DataDummy.generateDummyListStoryEntity()
  
  @Before
  fun setUp() {
    storyViewModel = StoryViewModel(storyRepository)
  }
  
  @Test
  fun `when getStories called Should Not Null and return pagingData of StoryEntity`() = mainCoroutineRules.runBlockingTest {
    val dummyStory = DataDummy.generateDummyListStoryEntity()
    val data = PagedTestDataSources.snapshot(dummyStory)
    val story = MutableLiveData<PagingData<StoryEntity>>()
    story.value = data
    
    `when`(storyRepository.getStories()).thenReturn(story)
    
    val actualStories = storyViewModel.getStories().getOrAwaitValue()
    
    val differ = AsyncPagingDataDiffer(
      diffCallback = ListStoryAdapter.DIFF_CALLBACK,
      updateCallback = noopListUpdateCallback,
      mainDispatcher = mainCoroutineRules.dispatcher,
      workerDispatcher = mainCoroutineRules.dispatcher,
    )
    differ.submitData(actualStories)
    
    advanceUntilIdle()
    
    verify(storyRepository).getStories()
    Assert.assertNotNull(differ.snapshot())
    Assert.assertEquals(dummyStory.size, differ.snapshot().size)
    Assert.assertEquals(dummyStory[0].name, differ.snapshot()[0]?.name)
  }
  
  @Test
  fun `when allStories called should return list of StoryEntity`() {
    val expectedStory = MutableLiveData<List<StoryEntity>>()
    expectedStory.value = dummyStories
    
    `when`(storyRepository.getStoriesFromDB()).thenReturn(expectedStory)
    
    val actualStory = storyViewModel.allStories().getOrAwaitValue()
    
    Assert.assertNotNull(actualStory)
    Assert.assertEquals(dummyStories.size, actualStory.size)
  }
  
  @Test
  fun `when getUserName called should return username`() {
    val expectedUsername = "altop"
    
    `when`(storyRepository.getUserName()).thenReturn(expectedUsername)
    
    val actualUsername = storyViewModel.getUserName()
    
    Assert.assertNotNull(actualUsername)
    Assert.assertEquals(expectedUsername, actualUsername)
  }
  
  @Test
  fun `when isLoggedIn called should return true`() {
    val expectedIsLogin = true
    
    `when`(storyRepository.isLoggedIn()).thenReturn(expectedIsLogin)
    
    val actualIsLogin = storyViewModel.isLoggedIn()
    
    Assert.assertNotNull(actualIsLogin)
    Assert.assertEquals(expectedIsLogin, actualIsLogin)
  }
  
  @Test
  fun `when logout called, storyRepositoryLogout should called too`() {
    storyViewModel.logout()
    
    verify(storyRepository).logout()
  }
  
  @Test
  fun `when postStory called, storyRepositoryPostStory should called too`() {
    val description = "editDesc".toRequestBody("text/plain".toMediaType())
    val lat = "lat".toRequestBody("text/plain".toMediaType())
    val lon = "lon".toRequestBody("text/plain".toMediaType())
    val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
      "photo", "file.name", File("aasdsa").asRequestBody("image/jpeg".toMediaTypeOrNull())
    )
    storyViewModel.postStory(imageMultipart, description, lat, lon)
    
    verify(storyRepository).postStory(imageMultipart, description, lat, lon)
  }
  
}

class PagedTestDataSources private constructor(private val items: List<StoryEntity>) :
  PagingSource<Int, LiveData<List<StoryEntity>>>() {
  companion object {
    fun snapshot(items: List<StoryEntity>): PagingData<StoryEntity> {
      return PagingData.from(items)
    }
  }
  
  override fun getRefreshKey(state: PagingState<Int, LiveData<List<StoryEntity>>>): Int {
    return 0
  }
  
  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<StoryEntity>>> {
    return LoadResult.Page(emptyList(), 0, 1)
  }
}

val noopListUpdateCallback = object : ListUpdateCallback {
  override fun onInserted(position: Int, count: Int) {}
  override fun onRemoved(position: Int, count: Int) {}
  override fun onMoved(fromPosition: Int, toPosition: Int) {}
  override fun onChanged(position: Int, count: Int, payload: Any?) {}
}
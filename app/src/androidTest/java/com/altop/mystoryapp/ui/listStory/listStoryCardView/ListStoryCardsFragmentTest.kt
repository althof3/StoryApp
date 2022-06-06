package com.altop.mystoryapp.ui.listStory.listStoryCardView

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.altop.mystoryapp.EspressoIdlingResource
import com.altop.mystoryapp.JsonConverter
import com.altop.mystoryapp.R
import com.altop.mystoryapp.data.remote.retrofit.ApiConfig
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class ListStoryCardsFragmentTest {
  
  private val mockWebServer = MockWebServer()
  
  @Before
  fun setUp() {
    mockWebServer.start(8080)
    ApiConfig.BASE_URL = "http://127.0.0.1:8080/"
    IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    
  }
  
  @After
  fun tearDown() {
    mockWebServer.shutdown()
    IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
  
  }
  
  @Test
  fun getStories_Success() {
    
    
    launchFragmentInContainer<ListStoryCardsFragment>(themeResId = R.style.Theme_MyStoryApp)
    
    val mockResponse = MockResponse().setResponseCode(200)
      .setBody(JsonConverter.readStringFromFile("success_list_story_response.json"))
    mockWebServer.enqueue(mockResponse)
    
    onView(withId(R.id.list)).check(matches(isDisplayed()))
    onView(withText("Naufal Nibtos")).check(matches(isDisplayed()))
    
  }
}
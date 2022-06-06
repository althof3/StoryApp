package com.altop.mystoryapp

import com.altop.mystoryapp.data.entity.StoryEntity
import com.altop.mystoryapp.data.remote.response.Story
import com.altop.mystoryapp.data.remote.response.User

object DataDummy {
  fun generateDummyUserEntity(): User {
    return User("altop", "id-altop", "daundeqwndon")
  }
  
  fun generateDummyListStoryEntity(): List<StoryEntity> {
    val storyList = ArrayList<StoryEntity>()
    for (i in 0..10) {
      val news = StoryEntity(
        "story $i",
        "2022-02-22T22:22:22Z",
        "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
        "https://www.dicoding.com/",
        "2020-03-03T04:30:03Z",
        10.0,
        10.0
      )
      storyList.add(news)
    }
    return storyList
  }
  fun generateDummyListStory(): List<Story> {
    val storyList = ArrayList<Story>()
    for (i in 0..10) {
      val news = Story(
        "story $i",
        "2022-02-22T22:22:22Z",
        "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
        "https://www.dicoding.com/",
        "2020-03-03T04:30:03Z",
        10.0,
        10.0
      )
      storyList.add(news)
    }
    return storyList
  }
}
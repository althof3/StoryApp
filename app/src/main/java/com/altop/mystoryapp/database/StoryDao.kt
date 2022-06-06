package com.altop.mystoryapp.database

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.altop.mystoryapp.data.entity.StoryEntity

@Dao
interface StoryDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertStories(stories: List<StoryEntity>)
  
  @Query("SELECT * FROM story")
  fun getAllStory(): PagingSource<Int, StoryEntity>
  
  @Query("SELECT * FROM story")
  fun getStories(): LiveData<List<StoryEntity>>
  
  @Query("DELETE FROM story")
  suspend fun deleteAll()
}
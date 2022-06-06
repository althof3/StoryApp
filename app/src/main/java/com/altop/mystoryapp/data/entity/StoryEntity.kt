package com.altop.mystoryapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "story")
data class StoryEntity(
  @PrimaryKey @field:SerializedName("id") val id: String,
  @field:SerializedName("name") val name: String,
  @field:SerializedName("description") val description: String,
  @field:SerializedName("photoUrl") val photoUrl: String,
  @field:SerializedName("createdAt") val createdAt: String,
  @field:SerializedName("lat") val lat: Double,
  @field:SerializedName("lon") val lon: Double,
)

package com.altop.mystoryapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class StoryListResponse(
  @field:SerializedName("listStory") val listStory: List<Story>,
  @field:SerializedName("error") val error: Boolean,
  @field:SerializedName("message") val message: String
)

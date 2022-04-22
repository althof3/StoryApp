package com.altop.mystoryapp.data.remote.response

import com.google.gson.annotations.SerializedName


/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class User(
  @field:SerializedName("name") var name: String? = null,
  @field:SerializedName("userId") var userId: String? = null,
  @field:SerializedName("token") var token: String? = null
)

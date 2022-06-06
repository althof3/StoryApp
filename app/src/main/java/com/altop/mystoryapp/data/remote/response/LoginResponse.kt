package com.altop.mystoryapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(
  @field:SerializedName("loginResult") val loginResult: User,
  @field:SerializedName("error") val error: Boolean,
  @field:SerializedName("message") val message: String
)

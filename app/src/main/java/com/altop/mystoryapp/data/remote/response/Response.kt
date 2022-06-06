package com.altop.mystoryapp.data.remote.response

import com.google.gson.annotations.SerializedName

open class Response(
  @field:SerializedName("error") val error: Boolean = false,
  @field:SerializedName("message") val message: String = ""
)

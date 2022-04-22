package com.altop.mystoryapp.data

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class Result<out T> private constructor() {
  
  data class Success<out T>(val data: T) : Result<T>()
  data class Error(val error: String) : Result<Nothing>()
  object Loading : Result<Nothing>()
  
}
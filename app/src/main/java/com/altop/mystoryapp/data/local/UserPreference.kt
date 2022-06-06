package com.altop.mystoryapp.data.local

import android.content.Context
import com.altop.mystoryapp.data.remote.response.User


interface IUserPreference {
  fun setUser(value: User)
  fun getUser(): User
  fun isLoggedIn(): Boolean
  fun clearSession()
}

class UserPreference {
  
  class UserPreference(context: Context) : IUserPreference {
    companion object {
      private const val PREFS_NAME = "user_pref"
      private const val NAME = "name"
      private const val USERID = "userId"
      private const val TOKEN = "token"
    }
    
    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    override fun setUser(value: User) {
      val editor = preferences.edit()
      editor.putString(NAME, value.name)
      editor.putString(USERID, value.userId)
      editor.putString(TOKEN, value.token)
      editor.apply()
    }
    
    override fun getUser(): User {
      val model = User()
      model.name = preferences.getString(NAME, null)
      model.userId = preferences.getString(USERID, null)
      model.token = preferences.getString(TOKEN, null)
      
      return model
    }
    
    override fun isLoggedIn(): Boolean {
      return getUser().token != null
    }
    
    override fun clearSession() {
      val editor = preferences.edit()
      editor.clear().apply()
    }
  }
}
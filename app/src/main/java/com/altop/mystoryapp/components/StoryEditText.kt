package com.altop.mystoryapp.components

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.altop.mystoryapp.R
import com.altop.mystoryapp.isEmailValid
import com.altop.mystoryapp.isPasswordValid

class StoryEditText : AppCompatEditText {
  
  private lateinit var mClearButtonImage: Drawable
  
  
  private fun init() {
    mClearButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_close) as Drawable
    
    addTextChangedListener(object : TextWatcher {
      override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
      override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
      override fun afterTextChanged(s: Editable) {
        if (s.isNotEmpty()) {
          checkTextInput(s.toString())
        }
      }
    })
    
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      textCursorDrawable = null
    }
    setPadding(40)
    setBackgroundResource(R.drawable.edit_text_background)
    setTextColor(Color.Black.toArgb())
  }
  
  constructor(context: Context) : super(context) {
    init()
  }
  
  constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    init()
  }
  
  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
    context, attrs, defStyleAttr
  ) {
    init()
  }
  
  fun checkTextInput(input: String) {
    when (inputType - 1) {
      InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS -> {
        error = if (!isEmailValid(text.toString())) {
          context.getString(R.string.invalid_email)
        } else {
          null
        }
        
      }
      InputType.TYPE_TEXT_VARIATION_PASSWORD -> {
        error = if (!isPasswordValid(input)) {
          context.getString(R.string.invalid_password)
        } else {
          null
        }
      }
      else -> {}
    }
  }
  
}
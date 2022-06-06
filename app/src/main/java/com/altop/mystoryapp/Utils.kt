package com.altop.mystoryapp

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.test.espresso.idling.CountingIdlingResource
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

private const val FILENAME_FORMAT = "dd-MMM-yyyy"

fun isEmailValid(email: String): Boolean {
  return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun isPasswordValid(password: String): Boolean {
  return password.length > 5
}

fun Fragment.hideKeyboard() {
  view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
  hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
  val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
  inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun createJsonRequestBody(vararg params: Pair<String, String>) =
  JSONObject(mapOf(*params)).toString()
    .toRequestBody("application/json; charset=utf-8".toMediaType())

@RequiresApi(Build.VERSION_CODES.O)
fun convertDate(text: String): String {
  val instant = Instant.parse(text)
  val formatter =
    DateTimeFormatter.ofPattern("dd MMM yyyy | HH:mm").withZone(ZoneId.of("Asia/Jakarta"))
  return formatter.format(instant)
}

fun getErrorMessage(jsonString: String?): String {
  val jsonObj = JSONObject(jsonString ?: "{\"message\":\"Something Error\"}")
  return jsonObj.getString("message")
}

val timeStamp: String = SimpleDateFormat(
  FILENAME_FORMAT, Locale.getDefault()
).format(System.currentTimeMillis())

fun createCustomTempFile(context: Context): File {
  val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
  return File.createTempFile(timeStamp, ".jpg", storageDir)
}

fun uriToFile(selectedImg: Uri, context: Context): File {
  val contentResolver: ContentResolver = context.contentResolver
  val myFile = createCustomTempFile(context)
  
  val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
  val outputStream: OutputStream = FileOutputStream(myFile)
  val buf = ByteArray(1024)
  var len: Int
  while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
  outputStream.close()
  inputStream.close()
  
  return myFile
}

object EspressoIdlingResource {
  
  private const val RESOURCE = "GLOBAL"
  
  @JvmField
  val countingIdlingResource = CountingIdlingResource(RESOURCE)
  
  fun increment() {
    countingIdlingResource.increment()
  }
  
  fun decrement() {
    if (!countingIdlingResource.isIdleNow) {
      countingIdlingResource.decrement()
    }
  }
}

inline fun <T> wrapEspressoIdlingResource(function: () -> T): T {
  EspressoIdlingResource.increment() // Set app as busy.
  return try {
    function()
  } finally {
    EspressoIdlingResource.decrement() // Set app as idle.
  }
}

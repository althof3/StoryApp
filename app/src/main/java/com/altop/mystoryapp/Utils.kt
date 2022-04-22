package com.altop.mystoryapp

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
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

fun convertDate(text: String): String {
  val stringFormat = SimpleDateFormat("dd MMMM yyyy 'at' HH:mm", Locale.US)
  val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
  val resultDate = formatter.parse(text)
  return stringFormat.format(resultDate).toString()
}

fun getErrorMessage(jsonString: String?): String {
  val jsonObj = JSONObject(jsonString ?: "{\"message\":\"Something Error\"}")
  return jsonObj.getString("message")
}

val timeStamp: String = SimpleDateFormat(
  FILENAME_FORMAT,
  Locale.getDefault()
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
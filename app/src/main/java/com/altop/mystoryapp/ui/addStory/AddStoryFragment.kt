package com.altop.mystoryapp.ui.addStory

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.altop.mystoryapp.R
import com.altop.mystoryapp.createCustomTempFile
import com.altop.mystoryapp.data.Result
import com.altop.mystoryapp.databinding.FragmentAddStoryBinding
import com.altop.mystoryapp.hideKeyboard
import com.altop.mystoryapp.ui.listStory.StoryViewModel
import com.altop.mystoryapp.ui.listStory.StoryViewModelFactory
import com.altop.mystoryapp.uriToFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryFragment : Fragment() {
  
  private lateinit var progressDialog: ProgressDialog
  
  private var getFile: File? = null
  
  private var _binding: FragmentAddStoryBinding? = null
  private val binding get() = _binding!!
  
  private var currentPhotoPath: String = ""
  
  private lateinit var storyFactory: StoryViewModelFactory
  private val storyViewModel: StoryViewModel by viewModels {
    storyFactory
  }
  
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    _binding = FragmentAddStoryBinding.inflate(inflater, container, false)
    progressDialog = ProgressDialog(context)
    
    if (!allPermissionsGranted()) {
      ActivityCompat.requestPermissions(
        requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
      )
    }
    return binding.root
  }
  
  private fun showProgressDialog(message: String) {
    progressDialog.apply {
      setMessage(message)
      setCancelable(false)
    }
    progressDialog.show()
  }
  
  private fun hideProgressDialog() {
    progressDialog.dismiss()
  }
  
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    storyFactory = StoryViewModelFactory.getInstance(requireActivity())
    
    binding.bCamera.setOnClickListener { startTakePhoto() }
    binding.bGallery.setOnClickListener { startGallery() }
    binding.bUpload.setOnClickListener { uploadImage() }
    
  }
  
  private fun startGallery() {
    val intent = Intent()
    intent.action = ACTION_GET_CONTENT
    intent.type = "image/*"
    val chooser = Intent.createChooser(intent, "Choose a Picture")
    launcherIntentGallery.launch(chooser)
  }
  
  private val launcherIntentGallery = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
  ) { result ->
    if (result.resultCode == RESULT_OK) {
      val selectedImg: Uri = result.data?.data as Uri
      
      val myFile = uriToFile(selectedImg, requireActivity())
      
      getFile = myFile
      
      binding.ivImagePreview.setImageURI(selectedImg)
    }
  }
  
  private fun startTakePhoto() {
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    intent.resolveActivity(requireContext().packageManager)
    
    val path = createCustomTempFile(requireContext())
    val photoURI: Uri = FileProvider.getUriForFile(
      requireContext(), "com.altop.mystoryapp", path
    )
    currentPhotoPath = path.absolutePath
    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
    launcherIntentCamera.launch(intent)
  }
  
  
  private val launcherIntentCamera = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
  ) {
    if (it.resultCode == RESULT_OK) {
      val myFile = File(currentPhotoPath)
      getFile = myFile
      val result = BitmapFactory.decodeFile(getFile?.path)
      binding.ivImagePreview.setImageBitmap(result)
    }
  }
  
  private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
    ContextCompat.checkSelfPermission(
      requireActivity().baseContext, it
    ) == PackageManager.PERMISSION_GRANTED
  }
  
  private fun uploadImage() {
    if (getFile != null) {
      val file = getFile as File
      val editDesc = binding.editDesc.text.toString()
      val description = editDesc.toRequestBody("text/plain".toMediaType())
      val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
      val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
        "photo", file.name, requestImageFile
      )
      hideKeyboard()
      
      storyViewModel.postStory(imageMultipart, description)
      storyViewModel.uploadStatus.observe(viewLifecycleOwner) { uploadStatus ->
        uploadStatus ?: return@observe
        when (uploadStatus) {
          is Result.Loading -> {
            showProgressDialog(getString(R.string.upload_loading))
          }
          is Result.Success -> {
            hideProgressDialog()
            showToast(getString(R.string.upload_success))
            findNavController().navigate(R.id.action_addStoryFragment_to_listStoryFragment)
          }
          is Result.Error -> {
            hideProgressDialog()
            showToast(getString(R.string.upload_failed))
          }
        }
      }
      
    }
  }
  
  private fun showToast(message: String) {
    val appContext = context?.applicationContext ?: return
    Toast.makeText(appContext, message, Toast.LENGTH_LONG).show()
  }
  
  companion object {
    
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    private const val REQUEST_CODE_PERMISSIONS = 10
  }
  
  override fun onRequestPermissionsResult(
    requestCode: Int, permissions: Array<String>, grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == REQUEST_CODE_PERMISSIONS) {
      if (!allPermissionsGranted()) {
        Toast.makeText(
          requireContext(), "Tidak mendapatkan permission.", Toast.LENGTH_SHORT
        ).show()
        activity?.finish()
      }
    }
  }
  
}
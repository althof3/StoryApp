package com.altop.mystoryapp.ui.addStory

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
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
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.altop.mystoryapp.R
import com.altop.mystoryapp.createCustomTempFile
import com.altop.mystoryapp.data.Result
import com.altop.mystoryapp.databinding.FragmentAddStoryBinding
import com.altop.mystoryapp.hideKeyboard
import com.altop.mystoryapp.ui.listStory.StoryViewModel
import com.altop.mystoryapp.ui.listStory.StoryViewModelFactory
import com.altop.mystoryapp.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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
  private val storyViewModel: StoryViewModel by activityViewModels {
    storyFactory
  }
  
  private lateinit var fusedLocationClient: FusedLocationProviderClient
  
  private var lat: Double = 0.0
  private var lon: Double = 0.0
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
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
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    storyFactory = StoryViewModelFactory.getInstance(requireActivity())
    
    binding.bCamera.setOnClickListener { startTakePhoto() }
    binding.bGallery.setOnClickListener { startGallery() }
    binding.bUpload.setOnClickListener { uploadImage() }
    
    getMyLastLocation()
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
      val latText = lat.toString()
      val lonText = lon.toString()
      
      val description = editDesc.toRequestBody("text/plain".toMediaType())
      val latBody = latText.toRequestBody("text/plain".toMediaType())
      val lonBody = lonText.toRequestBody("text/plain".toMediaType())
      
      val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
      val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
        "photo", file.name, requestImageFile
      )
      hideKeyboard()
      
      storyViewModel.postStory(imageMultipart, description, latBody, lonBody)
      storyViewModel.uploadStatus.observe(viewLifecycleOwner) { uploadStatus ->
        uploadStatus ?: return@observe
        when (uploadStatus) {
          is Result.Loading -> {
            showProgressDialog(getString(R.string.upload_loading))
          }
          is Result.Success -> {
            hideProgressDialog()
            showToast(getString(R.string.upload_success))
            if (findNavController().currentDestination?.label.toString() == "fragment_add_story") {
              findNavController().navigate(R.id.action_addStoryFragment_to_listStoryFragment)
            }
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
          requireContext(), getString(R.string.permission_not_given), Toast.LENGTH_SHORT
        ).show()
        activity?.finish()
      }
    }
  }
  
  private val requestPermissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestMultiplePermissions()
  ) { permissions ->
    when {
      permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
        getMyLastLocation()
      }
      permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
        getMyLastLocation()
      }
      else -> {}
    }
  }
  
  private fun checkPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(
      requireContext(), permission
    ) == PackageManager.PERMISSION_GRANTED
  }
  
  private fun getMyLastLocation() {
    if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
      fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
        if (location != null) {
          lat = location.latitude
          lon = location.longitude
        } else {
          Toast.makeText(
            requireContext(), getString(R.string.location_not_found), Toast.LENGTH_SHORT
          ).show()
        }
      }
    } else {
      requestPermissionLauncher.launch(
        arrayOf(
          Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
        )
      )
    }
  }
  
}
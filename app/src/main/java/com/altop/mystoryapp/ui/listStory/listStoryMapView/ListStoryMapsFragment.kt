package com.altop.mystoryapp.ui.listStory.listStoryMapView

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.altop.mystoryapp.R
import com.altop.mystoryapp.ui.listStory.StoryViewModel
import com.altop.mystoryapp.ui.listStory.StoryViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class ListStoryMapsFragment : Fragment() {
  
  private lateinit var storyFactory: StoryViewModelFactory
  private val storyViewModel: StoryViewModel by activityViewModels {
    storyFactory
  }
  
  private lateinit var fusedLocationClient: FusedLocationProviderClient
  
  private val callback = OnMapReadyCallback { googleMap ->
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * In this case, we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to
     * install it inside the SupportMapFragment. This method will only be triggered once the
     * user has installed Google Play services and returned to the app.
     */
    storyViewModel.allStories().observe(viewLifecycleOwner) { stories ->
      stories.forEach {
        
        val location = LatLng(it.lat, it.lon)
        
        googleMap.addMarker(
          MarkerOptions().position(location).title(it.name).snippet(it.description)
            .icon(vectorToBitmap(R.drawable.ic_emoji_people, Color.parseColor("#3DDC84")))
        )?.showInfoWindow()
      }
      
      
    }
    
    setMapStyle(googleMap)
    getMyLocation(googleMap)
  }
  
  private val requestPermissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestPermission()
  ) { isGranted: Boolean ->
    if (isGranted) {
      getMyLocation()
    }
  }
  
  private fun getMyLocation(mMap: GoogleMap? = null) {
    if (ContextCompat.checkSelfPermission(
        requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
      ) == PackageManager.PERMISSION_GRANTED
    ) {
      if (mMap != null) {
        mMap.isMyLocationEnabled = true
  
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
          if (location != null) {
            val currentLocation = LatLng(location.latitude,location.longitude)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
          }
        }
      }
    } else {
      requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
  }
  
  private fun setMapStyle(mMap: GoogleMap) {
    try {
      val success =
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))
      if (!success) {
        Log.e("TAG", "Style parsing failed.")
      }
    } catch (exception: Resources.NotFoundException) {
      Log.e("TAG", "Can't find style. Error: ", exception)
    }
  }
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
  }
  
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_list_story_maps, container, false)
  }
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    storyFactory = StoryViewModelFactory.getInstance(requireActivity())
    
    val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
    mapFragment?.getMapAsync(callback)
  }
  
  private fun vectorToBitmap(@DrawableRes id: Int, @ColorInt color: Int): BitmapDescriptor {
    val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
    if (vectorDrawable == null) {
      Log.e("BitmapHelper", "Resource not found")
      return BitmapDescriptorFactory.defaultMarker()
    }
    val bitmap = Bitmap.createBitmap(
      vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
    DrawableCompat.setTint(vectorDrawable, color)
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
  }
}
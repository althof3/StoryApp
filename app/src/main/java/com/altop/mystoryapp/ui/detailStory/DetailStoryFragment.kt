package com.altop.mystoryapp.ui.detailStory

import android.os.Bundle
import android.transition.ChangeBounds
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.altop.mystoryapp.convertDate
import com.altop.mystoryapp.databinding.FragmentDetailStoryBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class DetailStoryFragment : Fragment() {
  
  private var _binding: FragmentDetailStoryBinding? = null
  private val binding get() = _binding!!
  
  
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    _binding = FragmentDetailStoryBinding.inflate(inflater, container, false)
    sharedElementEnterTransition = ChangeBounds()
    return binding.root
  }
  
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    
    binding.detailUsername.text = arguments?.getString("userName", "")
    binding.detailDesc.text = arguments?.getString("userDesc", "")
    val dateString = arguments?.getString("createdAt", "") ?: ""
    binding.detailCreartedAt.text = convertDate(dateString)
    
    Glide.with(requireContext()).load(arguments?.getString("proflePic")).centerCrop()
      .transition(DrawableTransitionOptions.withCrossFade()).into(binding.profilePic)
    
  }
  
}
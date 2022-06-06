package com.altop.mystoryapp.ui.listStory

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.altop.mystoryapp.R
import com.altop.mystoryapp.databinding.FragmentStoryListBinding
import com.google.android.material.tabs.TabLayoutMediator

class ListStoryFragment : Fragment() {
  
  
  private var _binding: FragmentStoryListBinding? = null
  private val binding get() = _binding!!
  
  private lateinit var storyFactory: StoryViewModelFactory
  private val storyViewModel: StoryViewModel by activityViewModels {
    storyFactory
  }
  
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    _binding = FragmentStoryListBinding.inflate(inflater, container, false)
    
    return binding.root
  }
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    storyFactory = StoryViewModelFactory.getInstance(requireActivity())
    
    if (!storyViewModel.isLoggedIn()) {
      findNavController().navigate(R.id.action_listStoryFragment_to_loginFragment)
    }
    
    binding.username.text = getString(R.string.greet_user, storyViewModel.getUserName())
    
    binding.settingImageView.setOnClickListener {
      startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
    }
    binding.logoutButton.setOnClickListener {
      storyViewModel.logout()
      findNavController().navigate(R.id.action_listStoryFragment_to_loginFragment)
    }
    
    showViewPager()
  }
  
  private fun showViewPager() {
    binding.apply {
      pager.isUserInputEnabled = false
      pager.adapter = SectionsPagerAdapter(this@ListStoryFragment)
      TabLayoutMediator(tabLayout, pager) { tab, position ->
        tab.text = resources.getString(TAB_TITLES[position])
      }.attach()
    }
    
  }
  
  
  companion object {
    @StringRes
    val TAB_TITLES = intArrayOf(
      R.string.cardView, R.string.mapView
    )
  }
  
}
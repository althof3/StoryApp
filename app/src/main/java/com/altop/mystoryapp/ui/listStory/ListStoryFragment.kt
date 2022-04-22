package com.altop.mystoryapp.ui.listStory

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.altop.mystoryapp.R
import com.altop.mystoryapp.data.Result
import com.altop.mystoryapp.data.remote.response.Story
import com.altop.mystoryapp.databinding.FragmentStoryListBinding

class ListStoryFragment : Fragment() {
  
  
  private var _binding: FragmentStoryListBinding? = null
  private val binding get() = _binding!!
  
  private lateinit var storyFactory: StoryViewModelFactory
  private val storyViewModel: StoryViewModel by viewModels {
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
    binding.floatingActionButton.setOnClickListener {
      findNavController().navigate(R.id.action_listStoryFragment_to_addStoryFragment)
    }
    binding.settingImageView.setOnClickListener {
      startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
    }
    
    storyViewModel.getStories().observe(viewLifecycleOwner, Observer { stories ->
      stories ?: return@Observer
      when (stories) {
        is Result.Loading -> setLoading(View.VISIBLE)
        is Result.Success -> {
          setLoading(View.GONE)
          showRecyclerList(stories.data)
        }
        is Result.Error -> {
          setLoading(View.GONE)
          showToast(stories.error)
        }
      }
    })
  }
  
  private fun showRecyclerList(stories: List<Story>) {
    binding.list.layoutManager =
      if (requireContext().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        GridLayoutManager(requireContext(), 2)
      } else {
        LinearLayoutManager(requireContext())
      }
    val listStoryAdapter = ListStoryAdapter(stories)
    binding.list.adapter = listStoryAdapter
    
    binding.logoutButton.setOnClickListener {
      storyViewModel.logout()
      findNavController().navigate(R.id.action_listStoryFragment_to_loginFragment)
    }
    
    listStoryAdapter.setOnItemClickCallback(object : ListStoryAdapter.OnItemClickCallback {
      override fun onItemClicked(data: Story) {
        val bundle = bundleOf(
          "userName" to data.name,
          "proflePic" to data.photoUrl,
          "userDesc" to data.description,
          "createdAt" to data.createdAt
        )
        
        findNavController().navigate(
          R.id.action_listStoryFragment_to_detailStoryFragment, bundle
        )
      }
      
    })
  }
  
  private fun setLoading(visibility: Int) {
    binding.progressBar.visibility = visibility
    
  }
  
  private fun showToast(message: String) {
    val appContext = context?.applicationContext ?: return
    Toast.makeText(appContext, message, Toast.LENGTH_LONG).show()
  }
  
}
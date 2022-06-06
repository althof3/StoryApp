package com.altop.mystoryapp.ui.listStory.listStoryCardView

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.altop.mystoryapp.R
import com.altop.mystoryapp.data.entity.StoryEntity
import com.altop.mystoryapp.databinding.FragmentListStoryCardBinding
import com.altop.mystoryapp.ui.listStory.StoryViewModel
import com.altop.mystoryapp.ui.listStory.StoryViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class ListStoryCardsFragment : Fragment() {
  
  
  private var _binding: FragmentListStoryCardBinding? = null
  private val binding get() = _binding!!
  
  private lateinit var storyFactory: StoryViewModelFactory
  private val storyViewModel: StoryViewModel by activityViewModels {
    storyFactory
  }
  
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    _binding = FragmentListStoryCardBinding.inflate(inflater, container, false)
    
    return binding.root
  }
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    storyFactory = StoryViewModelFactory.getInstance(requireActivity())
    
    binding.floatingActionButton.setOnClickListener {
      findNavController().navigate(R.id.action_listStoryFragment_to_addStoryFragment)
    }
    showRecyclerList()
  }
  
  private fun showRecyclerList() {
    binding.list.layoutManager =
      if (requireContext().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        GridLayoutManager(requireContext(), 2)
      } else {
        LinearLayoutManager(requireContext())
      }
    
    val listStoryAdapter = ListStoryAdapter()
    binding.list.adapter = listStoryAdapter.withLoadStateFooter(footer = LoadingStateAdapter {
      listStoryAdapter.retry()
    })
    
    listStoryAdapter.setOnItemClickCallback(object : ListStoryAdapter.OnItemClickCallback {
      override fun onItemClicked(data: StoryEntity) {
        val bundle = bundleOf(
          "userName" to data.name,
          "profilePic" to data.photoUrl,
          "userDesc" to data.description,
          "createdAt" to data.createdAt
        )
        
        findNavController().navigate(
          R.id.action_listStoryFragment_to_detailStoryFragment, bundle
        )
      }
    })
    
    lifecycleScope.launch {
      listStoryAdapter.loadStateFlow.collectLatest { loadState ->
        binding.progressBar.isVisible = loadState.refresh is LoadState.Loading
      }
    }
    
    storyViewModel.getStories().observe(viewLifecycleOwner) { stories ->
      listStoryAdapter.submitData(lifecycle, stories)
    }
    
  }
}
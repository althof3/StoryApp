package com.altop.mystoryapp.ui.listStory


import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.altop.mystoryapp.ui.listStory.ListStoryFragment.Companion.TAB_TITLES
import com.altop.mystoryapp.ui.listStory.listStoryCardView.ListStoryCardsFragment
import com.altop.mystoryapp.ui.listStory.listStoryMapView.ListStoryMapsFragment


class SectionsPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
  
  override fun getItemCount() = TAB_TITLES.size
  
  override fun createFragment(position: Int): Fragment {
    var fragment: Fragment? = null
    when (position) {
      0 -> fragment = ListStoryCardsFragment()
      1 -> fragment = ListStoryMapsFragment()
    }
    return fragment as Fragment
  }
  
}
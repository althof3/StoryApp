package com.altop.mystoryapp.ui.listStory.listStoryCardView

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.altop.mystoryapp.convertDate
import com.altop.mystoryapp.data.entity.StoryEntity
import com.altop.mystoryapp.databinding.ItemStoryBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class ListStoryAdapter : PagingDataAdapter<StoryEntity, ListStoryAdapter.MyViewHolder>(DIFF_CALLBACK) {
  
  
  private lateinit var onItemClickCallback: OnItemClickCallback
  
  interface OnItemClickCallback {
    fun onItemClicked(data: StoryEntity)
  }
  
  fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
    this.onItemClickCallback = onItemClickCallback
  }
  
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
    
    val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return MyViewHolder(binding)
    
  }
  
  @RequiresApi(Build.VERSION_CODES.O)
  override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
    val data = getItem(position)
    if (data != null) {
      holder.bind(data)
      holder.itemView.setOnClickListener {
        onItemClickCallback.onItemClicked(data)
      }
    }
  }
  
  class MyViewHolder(private val binding: ItemStoryBinding) :
    RecyclerView.ViewHolder(binding.root) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(data: StoryEntity) {
      Glide.with(binding.root.context).load(data.photoUrl).centerCrop()
        .transition(DrawableTransitionOptions.withCrossFade()).into(binding.photoUrl)
      binding.name.text = data.name
      binding.description.text = data.description
      binding.createdAt.text = convertDate(data.createdAt)
      
    }
  }
  
  companion object {
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryEntity>() {
      override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
        return oldItem == newItem
      }
      
      override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
        return oldItem.id == newItem.id
      }
    }
  }
}
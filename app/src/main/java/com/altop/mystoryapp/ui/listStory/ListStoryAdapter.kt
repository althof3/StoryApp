package com.altop.mystoryapp.ui.listStory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.altop.mystoryapp.convertDate
import com.altop.mystoryapp.data.remote.response.Story
import com.altop.mystoryapp.databinding.ItemStoryBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class ListStoryAdapter(
  private val listStory: List<Story>
) : RecyclerView.Adapter<ListStoryAdapter.ListViewHolder>() {
  
  
  private lateinit var onItemClickCallback: OnItemClickCallback
  
  interface OnItemClickCallback {
    fun onItemClicked(data: Story)
  }
  
  fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
    this.onItemClickCallback = onItemClickCallback
  }
  
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
    
    val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return ListViewHolder(binding)
    
  }
  
  override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
    val item = listStory[position]
    Glide.with(holder.itemView.context).load(item.photoUrl).centerCrop()
      .transition(DrawableTransitionOptions.withCrossFade()).into(holder.binding.photoUrl)
    holder.binding.name.text = item.name
    holder.binding.description.text = item.description
    holder.binding.createdAt.text = convertDate(item.createdAt)
    
    holder.itemView.setOnClickListener {
      onItemClickCallback.onItemClicked(listStory[holder.bindingAdapterPosition])
    }
  }
  
  override fun getItemCount(): Int = listStory.size
  
  class ListViewHolder(var binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root)
  
}
package com.mena97villalobos.ltvblog.ui.blogs.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mena97villalobos.ltvblog.data.model.Blog
import com.mena97villalobos.ltvblog.data.network.BlogsNetwork
import com.mena97villalobos.ltvblog.databinding.BlogItemBinding

class BlogAdapter(private val onClickListener: (Blog) -> Unit) :
    ListAdapter<Blog, BlogAdapter.BlogViewHolder>(BlogDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder =
        BlogViewHolder.from(parent)

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) =
        holder.bind(getItem(position)!!, onClickListener)

    class BlogViewHolder private constructor(private val binding: BlogItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): BlogViewHolder =
                BlogViewHolder(
                    BlogItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        fun bind(item: Blog, onClickListener: (Blog) -> Unit) {

            fun resetImageViews() {
                binding.blogImage.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }

            binding.mainContents.setOnClickListener { onClickListener(item) }
            binding.blogTitle.text = item.title
            binding.blogDate.text = item.getFormattedDate()
            binding.blogDescription.text = item.description
            binding.blogAuthor.text = item.author

            if (item.imageBitmap != null) {
                binding.blogImage.setImageBitmap(item.imageBitmap)
                resetImageViews()
            } else {
                Thread {
                        item.imageBitmap = BlogsNetwork.downloadImage(item.getResizedImageURL())

                        binding.blogImage.post {
                            binding.blogImage.setImageBitmap(item.imageBitmap)
                            resetImageViews()
                        }
                    }
                    .start()
            }
        }
    }
}

class BlogDiffCallback : DiffUtil.ItemCallback<Blog>() {
    override fun areItemsTheSame(oldItem: Blog, newItem: Blog): Boolean =
        oldItem.uuid == newItem.uuid

    override fun areContentsTheSame(oldItem: Blog, newItem: Blog): Boolean = oldItem == newItem
}

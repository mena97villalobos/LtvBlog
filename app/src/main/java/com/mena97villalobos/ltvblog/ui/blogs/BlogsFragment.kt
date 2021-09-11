package com.mena97villalobos.ltvblog.ui.blogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.mena97villalobos.ltvblog.databinding.FragmentBlogsBinding
import com.mena97villalobos.ltvblog.model.Blog
import com.mena97villalobos.ltvblog.ui.blogs.adapter.BlogAdapter

class BlogsFragment : Fragment() {

    private val viewModel: BlogsViewModel by viewModels()
    private lateinit var binding: FragmentBlogsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBlogsBinding.inflate(inflater, container, false)

        setupObservers()
        initializeData()

        return binding.root
    }

    private fun initializeData() =
        viewModel.getAllBlogs()

    private fun setupObservers() {
        viewModel.blogs.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.loading.visibility = View.GONE
                setupAdapter(it)
                viewModel.clearBlogs()
            }
        }
    }

    private fun setupAdapter(blogs: List<Blog>) {
        val adapter = BlogAdapter {
            findNavController().navigate(BlogsFragmentDirections.actionNavigationHomeToBlogDetailsFragment(it.articleLink))
        }
        adapter.submitList(blogs)
        binding.blogList.adapter = adapter
        binding.blogList.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
    }
}
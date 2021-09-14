package com.mena97villalobos.ltvblog.data.repository

import com.mena97villalobos.ltvblog.data.model.Blog
import com.mena97villalobos.ltvblog.data.network.BlogsNetwork

class BlogsRepositoryImpl : BlogsRepository {

    override fun getAllBlogs(): List<Blog> = BlogsNetwork.getAllBlogs()
}

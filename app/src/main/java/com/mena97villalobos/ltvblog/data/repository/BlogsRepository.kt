package com.mena97villalobos.ltvblog.data.repository

import com.mena97villalobos.ltvblog.data.model.Blog

interface BlogsRepository {

    fun getAllBlogs(): List<Blog>
}

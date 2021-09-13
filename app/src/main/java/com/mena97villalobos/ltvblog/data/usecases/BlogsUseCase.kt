package com.mena97villalobos.ltvblog.data.usecases

import com.mena97villalobos.ltvblog.data.model.Blog
import com.mena97villalobos.ltvblog.data.repository.BlogsRepository

class BlogsUseCase(
    private val blogsRepo: BlogsRepository
) : SingleUseCase<List<Blog>> {

    override fun execute(): List<Blog> = blogsRepo.getAllBlogs()

}
package com.mena97villalobos.ltvblog.data.repository

import com.mena97villalobos.ltvblog.data.model.Blog
import com.mena97villalobos.ltvblog.data.model.BlogRetrofit
import com.mena97villalobos.ltvblog.data.network.BlogsNetworkApi

class BlogsRepositoryRetrofitImpl : BlogsRepository {

    object Mapper {
        fun fromRetrofitToBlog(model: BlogRetrofit) =
            Blog(
                title = model.title,
                description = model.description,
                author = model.author,
                date = model.date,
                uuid = model.uuid,
                articleLink = model.articleLink,
                imageUrl = model.imageUrl)
    }

    override suspend fun getAllBlogs(): List<Blog> =
        BlogsNetworkApi.retrofitServices.getAllBlogs().articles.map {
            Mapper.fromRetrofitToBlog(it)
        }
}

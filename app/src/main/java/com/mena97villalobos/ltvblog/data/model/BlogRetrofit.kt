package com.mena97villalobos.ltvblog.data.model

import com.google.gson.annotations.SerializedName

data class BlogsResponse(
    @SerializedName("last_rev") val lastRevision: String,
    val articles: List<BlogRetrofit>
)

data class BlogRetrofit(
    val title: String,
    val description: String?,
    val author: String,
    @SerializedName("article_date") val date: String,
    val uuid: String,
    @SerializedName("link") val articleLink: String,
    @SerializedName("image") val imageUrl: String,
)

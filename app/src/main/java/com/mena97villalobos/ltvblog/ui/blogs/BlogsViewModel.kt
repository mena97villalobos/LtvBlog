package com.mena97villalobos.ltvblog.ui.blogs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mena97villalobos.ltvblog.data.model.Blog
import com.mena97villalobos.ltvblog.data.repository.BlogsRepositoryImpl
import com.mena97villalobos.ltvblog.data.repository.BlogsRepositoryRetrofitImpl
import com.mena97villalobos.ltvblog.data.usecases.BlogsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BlogsViewModel : ViewModel() {

    private val blogsUseCase: BlogsUseCase = BlogsUseCase(BlogsRepositoryImpl())
    private val blogsUseCaseRetrofit = BlogsUseCase(BlogsRepositoryRetrofitImpl())

    private val _blogs = MutableLiveData<List<Blog>?>()
    val blogs: LiveData<List<Blog>?>
        get() = _blogs

    fun clearBlogs() {
        _blogs.value = null
    }

    fun getAllBlogs() =
        viewModelScope.launch {
            withContext(Dispatchers.IO) { _blogs.postValue(blogsUseCase.execute()) }
        }

    fun getAllBlogsRetrofit() =
        viewModelScope.launch {
            withContext(Dispatchers.IO) { _blogs.postValue(blogsUseCaseRetrofit.execute()) }
        }
}

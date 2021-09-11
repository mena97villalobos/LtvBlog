package com.mena97villalobos.ltvblog.ui.blogs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mena97villalobos.ltvblog.model.Blog
import com.mena97villalobos.ltvblog.network.BlogsNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class BlogsViewModel : ViewModel() {

    private val _blogs = MutableLiveData<List<Blog>?>()
    val blogs: LiveData<List<Blog>?>
        get() = _blogs

    fun clearBlogs() {
        _blogs.value = null
    }

    fun getAllBlogs() =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _blogs.postValue(BlogsNetwork.getAllBlogs())
            }
        }
}
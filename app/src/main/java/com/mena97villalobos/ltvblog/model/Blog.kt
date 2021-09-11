package com.mena97villalobos.ltvblog.model

import android.graphics.Bitmap
import java.text.SimpleDateFormat
import java.util.*

data class Blog(
    val title: String,
    val description: String,
    val author: String,
    val date: String,
    val uuid: String,
    val articleLink: String,
    val imageUrl: String,
    var imageBitmap: Bitmap? = null
) {

    fun getFormattedDate(): String {
        val inputFormatter = SimpleDateFormat("E, d MMM y hh:mm:ss Z", Locale.getDefault())
        inputFormatter.parse(date)?.let {
            val outputFormatter = SimpleDateFormat("E, d MMM y hh:mm", Locale.getDefault())
            return outputFormatter.format(it)
        }
        return date
    }

    fun getResizedImageURL(): String {
        val lastIndex = imageUrl.lastIndexOf('/')
        val imageName = imageUrl.substring(lastIndex, imageUrl.length)
        val baseUrl = "https://bv-content.beenverified.com"//imageUrl.substring(0, lastIndex)
        return "$baseUrl/60x0/filters:autojpg()$imageName"
    }
}
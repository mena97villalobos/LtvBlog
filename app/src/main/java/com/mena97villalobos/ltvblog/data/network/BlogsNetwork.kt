package com.mena97villalobos.ltvblog.data.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.mena97villalobos.ltvblog.data.model.Blog
import org.json.JSONObject
import org.json.JSONTokener
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

object BlogsNetwork {

    fun getAllBlogs(): List<Blog> {
        val dataset = arrayListOf<Blog>()
        val connection: HttpURLConnection =
            URL("https://www.beenverified.com/articles/index.mobile-android.json").openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()

        val scanner = Scanner(connection.inputStream).useDelimiter("\\A")
        if (scanner.hasNext()) {
            val jsonObject = JSONTokener(scanner.next()).nextValue() as JSONObject
            val articlesArray = jsonObject.getJSONArray("articles")
            (0 until articlesArray.length()).forEach {
                val currentArticle = articlesArray.getJSONObject(it)
                dataset.add(Blog(
                    title = currentArticle.getString("title"),
                    description = currentArticle.getString("description"),
                    author = currentArticle.getString("author"),
                    date = currentArticle.getString("article_date"),
                    uuid = currentArticle.getString("uuid"),
                    articleLink = currentArticle.getString("link"),
                    imageUrl = currentArticle.getString("image")
                ))
            }
        }
        connection.disconnect()
        return dataset
    }

    fun downloadImage(imageUrl: String): Bitmap {
        val connection: HttpURLConnection = URL(imageUrl).openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()
        return BitmapFactory.decodeStream(connection.inputStream).also {
            connection.disconnect()
        }
    }

}
package com.mena97villalobos.ltvblog.ui.blogdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.mena97villalobos.ltvblog.databinding.FragmentBlogDetailsBinding

class BlogDetailsFragment : Fragment() {

    private val args by navArgs<BlogDetailsFragmentArgs>()
    private lateinit var binding: FragmentBlogDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBlogDetailsBinding.inflate(inflater, container, false)

        initializeViews()

        return binding.root
    }

    private fun initializeViews() {
        /*
         * Avoid opening a new browser tab and going out of the app
         * https://stackoverflow.com/questions/13321510/prevent-webview-from-opening-the-browser/13321585
         */
        binding.webView.webViewClient =
            object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    return false
                }
            }

        binding.webView.loadUrl(args.url)
    }
}

package com.example.footballapp

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment


class WebViewFragment : Fragment() {

    private val TAG = "WebView"

    lateinit var webview: WebView
    private var linkURL: String = "https://www.theguardian.com/us/sport"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_web_view, container, false)

        webview = root.findViewById(R.id.webView)

        val urlString = arguments?.getString("URL")
        Log.d(TAG, " URL: $urlString")


        webViewSetup(linkURL)


        return root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun webViewSetup(param: String) {
        webview.webViewClient = WebViewClient()
        webview.apply {
            loadUrl(param)
            settings.javaScriptEnabled = true
            settings.builtInZoomControls = true
            settings.safeBrowsingEnabled = true

        }

    }


}

/**

fun shouldInterceptBackPress() = true

activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
override fun handleOnBackPressed() {
if (shouldInterceptBackPress()) {
Toast.makeText(
requireContext(),
"Back press intercepted in:${this@WebViewFragment}",
Toast.LENGTH_SHORT
).show()
if (webview.canGoBack()) {
webview.goBack()
} else {
//super.onBackPressed()
}
} else {
isEnabled = false
activity?.onBackPressed()
}
}
})


 */


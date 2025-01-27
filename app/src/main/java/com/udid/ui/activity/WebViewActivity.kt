package com.udid.ui.activity

import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.udid.R
import com.udid.databinding.ActivityWebViewBinding
import com.udid.utilities.AppConstants
import com.udid.utilities.BaseActivity

class WebViewActivity : BaseActivity<ActivityWebViewBinding>() {

    private var mBinding: ActivityWebViewBinding? = null
    private var url :String ?= null

    override val layoutId: Int
        get() = R.layout.activity_web_view

    override fun initView() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
        url = intent.extras?.getString(AppConstants.WEB_URL)
        if(url == "privacy_policy") {
            mBinding?.tvHeading?.text = "Privacy Policy"
            mBinding?.webView?.let { setupWebView(it, "https://www.swavlambancard.gov.in/privacy-policy?view-type=mobile") }
        }
        else{
            mBinding?.tvHeading?.text = "Terms And Conditions"
            mBinding?.webView?.let { setupWebView(it, "https://www.swavlambancard.gov.in/terms-and-conditions?view-type=mobile") }
        }
    }

    override fun setVariables() {
    }

    override fun setObservers() {
    }

    inner class ClickActions {
        fun backPress(view: View) {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupWebView(webView: WebView, urlLink: String) {
        showLoader(this)

        // Configure WebView settings
        webView.settings.apply {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            useWideViewPort = true
            loadWithOverviewMode = true
            setSupportMultipleWindows(true)
        }

        // Set WebViewClient to handle URL loading within the WebView
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                Log.d("WebView", "Loading URL: $url")
                return false
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                dismissLoader()
                Log.d("WebView", "Finished loading URL: $url")
            }
        }

        // Load the URL
        webView.loadUrl(urlLink)
    }

}
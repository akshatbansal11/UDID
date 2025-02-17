package com.swavlambancard.udid.ui.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.swavlambancard.udid.R
import com.swavlambancard.udid.databinding.ActivityApplyForUdidBinding
import com.swavlambancard.udid.utilities.BaseActivity
import com.swavlambancard.udid.utilities.Utility.showSnackbar
import com.swavlambancard.udid.utilities.hideView
import com.swavlambancard.udid.utilities.showView
import java.io.BufferedReader
import java.io.InputStreamReader

class ApplyForUdidActivity : BaseActivity<ActivityApplyForUdidBinding>() {

    private var mBinding: ActivityApplyForUdidBinding? = null
    override val layoutId: Int
        get() = R.layout.activity_apply_for_udid

    override fun initView() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
        mBinding?.webView?.let { setupWebView(it, "https://swavlambancard.gov.in/Applyforudid?view-type=mobile") }
    }

    override fun setVariables() {
        mBinding?.tvApplyForUDID?.text = readRawTextFile(R.raw.udid_string)
    }

    override fun setObservers() {
    }

    override fun onResume() {
        super.onResume()
        mBinding?.clParent?.showView()
    }

    inner class ClickActions {
        fun backPress(view: View) {
            onBackPressedDispatcher.onBackPressed()
        }

        fun submit(view: View) {
            if (mBinding?.cbUdidForm?.isChecked == true) {
                mBinding?.clParent?.hideView()
                showUdidDialog()
            }
            else
                mBinding?.clParent?.let {
                    showSnackbar(
                        it,
                        getString(R.string.you_must_check_the_box_to_confirm_that_you_have_read_and_understood_the_process)
                    )
                }
        }
    }

    private fun readRawTextFile(resId: Int): String {
        val inputStream = resources.openRawResource(resId)
        val reader = BufferedReader(InputStreamReader(inputStream))
        return reader.readText().also { reader.close() }
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

    private fun showUdidDialog() {
        val dialog = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setGravity(Gravity.CENTER)
        val lp: WindowManager.LayoutParams = dialog.window!!.attributes
        lp.dimAmount = 0.5f
        dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window
        dialog.window?.attributes = lp
        dialog.setContentView(R.layout.apply_for_udid_dialog)

        val radioGroup: RadioGroup = dialog.findViewById(R.id.radioGroup)
        val btnConfirm: TextView = dialog.findViewById(R.id.tvSubmit)

        btnConfirm.setOnClickListener {
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            if (selectedRadioButtonId == -1) {
                mBinding?.clParent?.let {
                    showSnackbar(
                        it,
                        getString(R.string.please_select_an_option)
                    )
                }
            } else {
                val selectedRadioButton: RadioButton = dialog.findViewById(selectedRadioButtonId)
                startActivity(Intent(this,PersonalProfileActivity::class.java))
                dialog.dismiss()
            }
        }

        dialog.show()
    }
}
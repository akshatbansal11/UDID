package com.swavlambancard.udid.ui.activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
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
import androidx.appcompat.view.ContextThemeWrapper
import androidx.databinding.DataBindingUtil
import com.swavlambancard.udid.R
import com.swavlambancard.udid.databinding.ActivityApplyForUdidBinding
import com.swavlambancard.udid.databinding.ApplyForUdidDialogBinding
import com.swavlambancard.udid.utilities.BaseActivity
import com.swavlambancard.udid.utilities.Utility.showSnackbar
import com.swavlambancard.udid.utilities.hideView
import com.swavlambancard.udid.utilities.showView
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Calendar

class ApplyForUdidActivity : BaseActivity<ActivityApplyForUdidBinding>() {

    private var mBinding: ActivityApplyForUdidBinding? = null
    var date: String? = null
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
//                mBinding?.clParent?.hideView()
//                showUdidDialog()
                startActivity(Intent(this@ApplyForUdidActivity, ApplyForUdidFormActivity::class.java))
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
        val bindingDialog: ApplyForUdidDialogBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.apply_for_udid_dialog,
            null,
            false
        )
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(bindingDialog.root)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        val window = dialog.window
        if (window != null) {
            window.setLayout(
                (resources.displayMetrics.widthPixels * 0.9).toInt(),
                LinearLayout.LayoutParams.WRAP_CONTENT
            ) // Set width to 90% of the screen width
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // Remove background gray effect
            window.setGravity(Gravity.CENTER)
            val lp: WindowManager.LayoutParams = window.attributes
            lp.dimAmount = 0.5f
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            window.attributes = lp
        }
        val radioGroup: RadioGroup = dialog.findViewById(R.id.radioGroup)
        val btnConfirm: TextView = dialog.findViewById(R.id.tvSubmit)
        bindingDialog.etDob.setOnClickListener{
            calenderOpen(this@ApplyForUdidActivity,bindingDialog.etDob)
        }
        bindingDialog.etDobPending.setOnClickListener{
            calenderOpen(this@ApplyForUdidActivity,bindingDialog.etDobPending)
        }
         bindingDialog.radioGroup.setOnCheckedChangeListener { radioGroup, checkedButton ->
             when(checkedButton){
                 R.id.radio_option1 -> {
                     bindingDialog.llUdidCardReject.hideView()
                     bindingDialog.llUdidCardPending.hideView()
                     bindingDialog.llUdidCard.hideView()
                 }
                 R.id.radio_option2 -> {
                     bindingDialog.llUdidCardReject.hideView()
                     bindingDialog.llUdidCardPending.hideView()
                     bindingDialog.llUdidCard.hideView()
                 }
                 R.id.radio_option3 -> {
                     bindingDialog.llUdidCardReject.showView()
                     bindingDialog.llUdidCardPending.hideView()
                     bindingDialog.llUdidCard.hideView()
                     date=""
                     bindingDialog.etDobPending.text=""
                     bindingDialog.etDob.text=""
                 }
                 R.id.radio_option4 -> {
                     bindingDialog.llUdidCardReject.hideView()
                     bindingDialog.llUdidCardPending.hideView()
                     bindingDialog.llUdidCard.showView()
                 }
                 R.id.radio_option5 -> {
                     bindingDialog.llUdidCardReject.hideView()
                     bindingDialog.llUdidCardPending.showView()
                     bindingDialog.llUdidCard.hideView()
                     date=""
                     bindingDialog.etDobPending.text=""
                     bindingDialog.etDob.text=""

                 }
             }
         }


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
                startActivity(Intent(this, PersonalProfileActivity::class.java))
                dialog.dismiss()
            }
        }
        dialog.show()
    }
    @SuppressLint("SetTextI18n")
    private fun calenderOpen(context: Context, editText: TextView) {
        val cal: Calendar = Calendar.getInstance()
        Log.d("Calendar", "tvDob clicked")
        // Parse the date from editText if it contains a valid date
        val currentText = editText.text.toString()
        if (currentText.isNotEmpty()) {
            try {
                val parts = currentText.split("/")
                if (parts.size == 3) {
                    val day = parts[0].toInt()
                    val month = parts[1].toInt() - 1 // Months are 0-based in Calendar
                    val year = parts[2].toInt()
                    cal.set(year, month, day)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val year: Int = cal.get(Calendar.YEAR)
        val month: Int = cal.get(Calendar.MONTH)
        val day: Int = cal.get(Calendar.DAY_OF_MONTH)

        val dialog = DatePickerDialog(
            context,
            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedMonth =
                    String.format("%02d", selectedMonth + 1) // Add 1 to month and format
                val formattedDay =
                    String.format("%02d", selectedDay) // Format day as two digits if needed
                Log.d(
                    "Date",
                    "onDateSet: MM/dd/yyyy: $formattedMonth/$formattedDay/$selectedYear"
                )
                date = "$selectedYear-$formattedMonth-$formattedDay"
                editText.text = "$formattedDay/$formattedMonth/$selectedYear"
            },
            year, month, day
        )
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }
}
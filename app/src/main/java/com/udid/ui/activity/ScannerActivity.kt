package com.udid.ui.activity

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.udid.R
import com.udid.databinding.ActivityScannerBinding
import com.udid.utilities.BaseActivity
import com.udid.utilities.Utility
import com.udid.utilities.toast

class ScannerActivity : BaseActivity<ActivityScannerBinding>() {

    private var mBinding: ActivityScannerBinding? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                showCamera()
            } else {
                toast("Permission is required")
            }
        }

    private val scanLauncher =
        registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
            run {
                if (result.contents == null) {
                    toast("Canceled")
                } else {
                    setResult(result.contents)
                }
            }
        }

    private fun setResult(string: String){
        Log.d("content" , string)

        val pairs = parseKeyValuePairs(string)

        mBinding?.etUdidNo?.text =pairs["UDID No"]
        mBinding?.etName?.text =pairs["Name"]
        mBinding?.etYearOfBirth?.text =pairs["Year of Birth"]
        mBinding?.etDisabilityType?.text =pairs["Disability Type"]
        mBinding?.etPercentageOfDisability?.text =pairs["Percentage of Disability"]
        mBinding?.etDateOfIssue?.text =pairs["Date of Issue"]
        mBinding?.etValidUpto?.text =pairs["Valid Upto"]
        mBinding?.etAadhaarNo?.text = pairs["Aadhaar No"]?.let { Utility.maskAadharNumber(it) }
    }

    private fun parseKeyValuePairs(data: String): Map<String, String> {
        val pairs = mutableMapOf<String, String>()
        val lines = data.split("\n")
        for (line in lines) {
            val parts = line.split(": ")
            if (parts.size == 2) {
                val key = parts[0].trim()
                val value = parts[1].trim()
                pairs[key] = value
            }
        }
        return pairs
    }

    override val layoutId: Int
        get() = R.layout.activity_scanner


    override fun initView() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
        checkPermissionCamera(this)
    }

    inner class ClickActions{
        fun backPress(view: View) {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun showCamera() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Scan QR code")
        options.setCameraId(0)
        options.setBeepEnabled(false)
        options.setBarcodeImageEnabled(true)
        options.setOrientationLocked(false)

        scanLauncher.launch(options)
    }

    private fun checkPermissionCamera(context: Context) {
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            showCamera()
        } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
            toast("CAMERA permission required")
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    override fun setVariables() {
    }

    override fun setObservers() {
    }
}
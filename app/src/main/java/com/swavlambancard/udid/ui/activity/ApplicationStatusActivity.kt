package com.swavlambancard.udid.ui.activity

import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.swavlambancard.udid.R
import com.swavlambancard.udid.databinding.ActivityApplicationStatusBinding
import com.swavlambancard.udid.model.UserData
import com.swavlambancard.udid.utilities.AppConstants
import com.swavlambancard.udid.utilities.BaseActivity
import com.swavlambancard.udid.utilities.JSEncryptService
import com.swavlambancard.udid.utilities.Preferences.getPreferenceOfLogin
import com.swavlambancard.udid.utilities.Utility.showSnackbar
import com.swavlambancard.udid.utilities.hideView
import com.swavlambancard.udid.utilities.showView
import com.swavlambancard.udid.utilities.toast
import com.swavlambancard.udid.viewModel.ViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File

class ApplicationStatusActivity : BaseActivity<ActivityApplicationStatusBinding>() {

    private var mBinding: ActivityApplicationStatusBinding? = null
    private var viewModel = ViewModel()

    override val layoutId: Int
        get() = R.layout.activity_application_status

    override fun initView() {
        mBinding = viewDataBinding
        viewModel.init()
        mBinding?.clickAction = ClickActions()
        if (getPreferenceOfLogin(
                this,
                AppConstants.LOGIN_DATA,
                UserData::class.java
            ).application_status == 4 || getPreferenceOfLogin(
                this,
                AppConstants.LOGIN_DATA,
                UserData::class.java
            ).application_status == 20
        ) {
            mBinding?.etRejectionLetter?.showView()
        } else {
            mBinding?.etRejectionLetter?.hideView()
        }
    }

    override fun setVariables() {
        mBinding?.etApplicationStatus?.text =
            getPreferenceOfLogin(
                this,
                AppConstants.LOGIN_DATA,
                UserData::class.java
            ).pwdapplicationstatus.status_name

        mBinding?.ivProfile?.let {
            Glide.with(this)
                .load(
                    getPreferenceOfLogin(
                        this,
                        AppConstants.LOGIN_DATA,
                        UserData::class.java
                    ).photo_path
                )
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(it)
        }
    }

    override fun setObservers() {
        viewModel.downloadResult.observe(this) { result ->
            result.onSuccess { file ->
                openPDF(file)
            }.onFailure { error ->
                toast("Download failed: ${error.message}")
            }
        }

        viewModel.errors.observe(this) {
            mBinding?.llParent?.let { it1 -> showSnackbar(it1, it) }
        }
    }

    inner class ClickActions {
        fun backPress(view: View) {
            onBackPressedDispatcher.onBackPressed()
        }

        fun downloadRejectionLetter(view: View) {
            val requestBody = JSONObject().apply {
                put(
                    "application_number", JSEncryptService.encrypt(
                        getPreferenceOfLogin(
                            context,
                            AppConstants.LOGIN_DATA,
                            UserData::class.java
                        ).application_number.toString()
                    )
                )
                put("type", "mobile")
            }.toString()

            viewModel.downloadRejectionLetter(
                this@ApplicationStatusActivity, requestBody.toRequestBody("application/json".toMediaType())
            )
        }
    }

    private fun openPDF(pdfFile: File) {
        try {
            // Create a URI for the PDF file using FileProvider for secure file access
            val pdfUri: Uri = FileProvider.getUriForFile(
                this,
                "${this.packageName}.fileprovider", // Ensure your app's FileProvider is correctly configured
                pdfFile
            )

            // Create an Intent to open the PDF file
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(pdfUri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Grant permission for the PDF viewer
            }

            // Start the activity to view the PDF
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle the exception (e.g., show a toast if no PDF viewer is available)
        }
    }
}
package com.swavlambancard.udid.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.swavlambancard.udid.R
import com.swavlambancard.udid.databinding.ActivityFeedbackAndQueryBinding
import com.swavlambancard.udid.model.UserData
import com.swavlambancard.udid.utilities.AppConstants
import com.swavlambancard.udid.utilities.BaseActivity
import com.swavlambancard.udid.utilities.EncryptionModel
import com.swavlambancard.udid.utilities.Preferences.getPreferenceOfLogin
import com.swavlambancard.udid.utilities.URIPathHelper
import com.swavlambancard.udid.utilities.Utility
import com.swavlambancard.udid.utilities.Utility.showSnackbar
import com.swavlambancard.udid.utilities.toast
import com.swavlambancard.udid.viewModel.ViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class FeedbackAndQueryActivity : BaseActivity<ActivityFeedbackAndQueryBinding>() {

    private var mBinding: ActivityFeedbackAndQueryBinding? = null
    private var viewModel= ViewModel()
    var body: MultipartBody.Part? = null

    override val layoutId: Int
        get() = R.layout.activity_feedback_and_query

    override fun initView() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
        viewModel.init()
    }

    override fun setVariables() {
        mBinding?.ivProfile?.let {
            Glide.with(this)
                .load(getPreferenceOfLogin(
                    this,
                    AppConstants.LOGIN_DATA,
                    UserData::class.java
                ).photo_path)
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(it)
        }
    }

    override fun setObservers() {
        viewModel.feedbackAndQueryResult.observe(this) {
            val userResponseModel = it
            if (userResponseModel?._resultflag != 0) {
                toast(userResponseModel.message)
                onBackPressedDispatcher.onBackPressed()
            } else {
                mBinding?.clParent?.let { it1 -> showSnackbar(it1, userResponseModel.message) }
            }
        }
        viewModel.errors.observe(this) {
            mBinding?.let { it1 -> showSnackbar(it1.clParent, it) }
        }
    }

    inner class ClickActions {
        fun backPress(view: View){
            onBackPressedDispatcher.onBackPressed()
        }

        fun submit(view: View){
            if(valid()) {
                feedbackAndQueryApi()
            }
        }
        fun uploadFile(view: View) {
            checkStoragePermission(this@FeedbackAndQueryActivity)
        }
    }

    private fun feedbackAndQueryApi() {
        viewModel.getFeedBack(
            context = this,
            fullName = EncryptionModel.aesEncrypt(mBinding?.etName?.text.toString().trim())
                .toRequestBody(MultipartBody.FORM),
            email = EncryptionModel.aesEncrypt(mBinding?.etEmail?.text.toString().trim())
                .toRequestBody(MultipartBody.FORM),
            mobile = EncryptionModel.aesEncrypt(
                mBinding?.etMobile?.text.toString().trim()
            ).toRequestBody(MultipartBody.FORM),
            subject = EncryptionModel.aesEncrypt(mBinding?.etSubject?.text.toString().trim())
                .toRequestBody(MultipartBody.FORM),
            message = EncryptionModel.aesEncrypt(mBinding?.etMobile?.text.toString().trim())
                .toRequestBody(MultipartBody.FORM),
            type = "mobile".toRequestBody(MultipartBody.FORM),
            document = body
        )
    }

    private fun valid(): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        if (mBinding?.etName?.text.toString().trim().isEmpty()) {
            mBinding?.clParent?.let { showSnackbar(it, getString(R.string.please_enter_name)) }
            return false
        }
        else if (mBinding?.etEmail?.text.toString().trim().isEmpty()) {
            mBinding?.clParent?.let { showSnackbar(it,
                getString(R.string.please_enter_an_email_address)) }
            return false
        }
        else if (!mBinding?.etEmail?.text.toString().trim().matches(emailRegex)) {
            mBinding?.clParent?.let { showSnackbar(it,
                getString(R.string.please_enter_a_valid_email_address)) }
            return false
        }
        else if (mBinding?.etMobile?.text.toString().trim().isEmpty()) {
            mBinding?.clParent?.let {
                showSnackbar(it, getString(R.string.please_enter_the_mobile_number))
            }
            return false
        }
        else if (mBinding?.etMobile?.text.toString().trim().length != 10) {
            mBinding?.clParent?.let {
                showSnackbar(it, getString(R.string.mobile_number_must_be_exactly_10_digits))
            }
            return false
        }
        else if (mBinding?.etSubject?.text.toString().trim().isEmpty()) {
            mBinding?.clParent?.let { showSnackbar(it, getString(R.string.please_enter_subject)) }
            return false
        }
        else if (mBinding?.etMessage?.text.toString().trim().isEmpty()) {
            mBinding?.clParent?.let { showSnackbar(it, getString(R.string.please_enter_your_message)) }
            return false
        }
        else if (mBinding?.etFileName?.text.toString().trim().isEmpty()) {
            mBinding?.clParent?.let { showSnackbar(it,getString(R.string.please_upload_document)) }
            return false
        }

        return true
    }

    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAPTURE_IMAGE_REQUEST -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    val imageFile = saveImageToFile(imageBitmap)
                    photoFile = imageFile

                    val fileSizeInBytes = photoFile?.length() ?: 0
                    if (isFileSizeWithinLimit(fileSizeInBytes, 500.0)) { // 500 KB limit
                        mBinding?.etFileName?.text = photoFile?.name
                        uploadImage(photoFile!!)
                    } else {
                        compressFile(photoFile!!) // Compress if size exceeds limit
                        mBinding?.etFileName?.text = photoFile?.name
                        uploadImage(photoFile!!)
                    }
                }

                PICK_IMAGE -> {
                    val selectedImageUri = data?.data
                    if (selectedImageUri != null) {
                        val uriPathHelper = URIPathHelper()
                        val filePath = uriPathHelper.getPath(this, selectedImageUri)

                        val fileExtension =
                            filePath?.substringAfterLast('.', "").orEmpty().lowercase()
                        if (fileExtension in listOf("png", "jpg", "jpeg")) {
                            val file = filePath?.let { File(it) }
                            val fileSizeInBytes = file?.length() ?: 0
                            if (isFileSizeWithinLimit(fileSizeInBytes, 500.0)) { // 500 KB limit
                                mBinding?.etFileName?.text = file?.name
                                uploadImage(file!!)
                            } else {
                                compressFile(file!!) // Compress if size exceeds limit
                                mBinding?.etFileName?.text = file.name
                                uploadImage(file)
                            }
                        } else {
                            mBinding?.clParent?.let {
                                showSnackbar(
                                    it,
                                    getString(R.string.format_not_supported)
                                )
                            }
                        }
                    }
                }

                REQUEST_iMAGE_PDF -> {
                    data?.data?.let { uri ->
                        val projection = arrayOf(
                            MediaStore.MediaColumns.DISPLAY_NAME,
                            MediaStore.MediaColumns.SIZE
                        )

                        val cursor = contentResolver.query(uri, projection, null, null, null)
                        cursor?.use {
                            if (it.moveToFirst()) {
                                val documentName =
                                    it.getString(it.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME))
                                val fileSizeInBytes =
                                    it.getLong(it.getColumnIndex(MediaStore.MediaColumns.SIZE))
                                println(fileSizeInBytes)
                                if (isFileSizeWithinLimit(fileSizeInBytes, 500.0)) { // 500 KB limit
                                    uploadDocument(documentName, uri)
                                    mBinding?.etFileName?.text = documentName
                                } else {
                                    mBinding?.clParent?.let {
                                        showSnackbar(
                                            it,
                                            getString(R.string.file_size_exceeds_5_mb)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun uploadImage(file: File) {
        lifecycleScope.launch {
            val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            body =
                MultipartBody.Part.createFormData(
                    "document",
                    file.name, reqFile
                )
        }
    }

    private fun uploadDocument(documentName: String?, uri: Uri) {
        val requestBody = convertToRequestBody(this, uri)
        body = MultipartBody.Part.createFormData(
            "document",
            documentName,
            requestBody
        )
    }
}
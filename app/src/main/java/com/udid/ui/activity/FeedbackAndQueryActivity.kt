package com.udid.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.RotateDrawable
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.udid.R
import com.udid.databinding.ActivityFeedbackAndQueryBinding
import com.udid.model.DropDownResult
import com.udid.ui.adapter.BottomSheetAdapter
import com.udid.utilities.BaseActivity
import com.udid.utilities.URIPathHelper
import com.udid.utilities.Utility
import com.udid.utilities.Utility.showSnackbar
import com.udid.utilities.hideView
import com.udid.utilities.showView
import com.udid.viewModel.ViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException

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

    }

    override fun setObservers() {
    }

    inner class ClickActions {
        fun backPress(view: View){
            onBackPressedDispatcher.onBackPressed()
        }

        fun submit(view: View){
            onBackPressedDispatcher.onBackPressed()
        }
        fun generateOtp(view: View){
            if(valid()){
                mBinding?.clParent?.let { Utility.showSnackbar(it,"Done OTP") }
            }
        }
        fun uploadFile(view: View) {
            checkStoragePermission(this@FeedbackAndQueryActivity)
        }
    }

    private fun valid(): Boolean {
        val name = mBinding?.etName?.text?.toString()
        val email = mBinding?.etEmail?.text?.toString()
        val mobile = mBinding?.etMobile?.text?.toString()
        val subject = mBinding?.etSubject?.text?.toString()
        val message = mBinding?.etMessage?.text?.toString()
        val fileName = mBinding?.etFileName?.text?.toString()

//        if (fileName==getString(R.string.no_file_chosen)) {
//            mBinding?.clParent?.let { Utility.showSnackbar(it,"Please Upload Supporting Document.") }
//            return false
//        }
        if (name.isNullOrEmpty()) {
            mBinding?.clParent?.let { Utility.showSnackbar(it, "Please enter name.") }
            return false
        }
        if (subject.isNullOrEmpty()) {
            mBinding?.clParent?.let { Utility.showSnackbar(it, "Please enter subject.") }
            return false
        }
        if (message.isNullOrEmpty()) {
            mBinding?.clParent?.let { Utility.showSnackbar(it, "Please enter your message.") }
            return false
        }
        if (email.isNullOrEmpty()) {
            mBinding?.clParent?.let { Utility.showSnackbar(it, "Please enter an email address.") }
            return false
        }

        // Regular expression to validate email format
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

        // Check if the email matches the regex
        if (!email.matches(emailRegex)) {
            mBinding?.clParent?.let { Utility.showSnackbar(it, "Please enter a valid email address.") }
            return false
        }

        if (mobile.isNullOrEmpty()) {
            mBinding?.clParent?.let {
                Utility.showSnackbar(it, "Please enter the mobile number.")
            }
            return false
        }

        // Check if the mobile number is exactly 10 digits long
        if (mobile.length != 10) {
            mBinding?.clParent?.let {
                Utility.showSnackbar(it, "Mobile number must be exactly 10 digits.")
            }
            return false
        }

        return true
    }



    private fun uploadImage(file: File) {
        lifecycleScope.launch {
            val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            body =
                MultipartBody.Part.createFormData(
                    "document_name",
                    file.name, reqFile
                )
//            viewModel.getProfileUploadFile(
//                context = this@AddAscadStateActivity,
//                document_name = body,
//                user_id = getPreferenceOfScheme(
//                    this@AddAscadStateActivity,
//                    AppConstants.SCHEME,
//                    Result::class.java
//                )?.user_id,
//                table_name = getString(R.string.ascad_state).toRequestBody(
//                    MultipartBody.FORM
//                ),
//            )
        }
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
                    mBinding?.etFileName?.text = photoFile?.name
                }

                PICK_IMAGE -> {
                    val selectedImageUri = data?.data
                    if (selectedImageUri != null) {
                        val uriPathHelper = URIPathHelper()
                        val filePath = uriPathHelper.getPath(this, selectedImageUri)

                        val fileExtension =
                            filePath?.substringAfterLast('.', "").orEmpty().lowercase()
                        // Validate file extension
                        if (fileExtension in listOf("png", "jpg", "jpeg")) {
                            val file = filePath?.let { File(it) }

                            // Check file size (5 MB = 5 * 1024 * 1024 bytes)
                            file?.let {
                                val fileSizeInMB = it.length() / (1024 * 1024.0) // Convert to MB
                                if (fileSizeInMB <= 5) {
                                    mBinding?.etFileName?.text = file.name
//                                    mBinding?.llUploadOne?.showView()
//                                    mBinding?.ivPicOne?.setImageURI(selectedImageUri)
                                    uploadImage(it)
                                } else {
                                    mBinding?.let {
                                        showSnackbar(
                                            it.clParent,
                                            "File size exceeds 5 MB"
                                        )
                                    }
                                }
                            }
                        } else {
                            mBinding?.let { showSnackbar(it.clParent, "Format not supported") }
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
                                val fileSizeInMB =
                                    fileSizeInBytes / (1024 * 1024.0) // Convert to MB

                                // Validate file size (5 MB = 5 * 1024 * 1024 bytes)
                                if (fileSizeInMB <= 5) {
                                    uploadDocument(documentName, uri)
                                    mBinding?.etFileName?.text = documentName
//                                    mBinding?.ivPicOne?.setImageResource(R.drawable.ic_pdf)
                                } else {
                                    mBinding?.let {
                                        showSnackbar(
                                            it.clParent,
                                            "File size exceeds 5 MB"
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

    private fun uploadDocument(documentName: String?, uri: Uri) {
        val requestBody = convertToRequestBody(this, uri)
        body = MultipartBody.Part.createFormData(
            "document_name",
            documentName,
            requestBody
        )
//        viewModel.getProfileUploadFile(
//            context = this,
//            document_name = body,
//            user_id = getPreferenceOfScheme(this, AppConstants.SCHEME, Result::class.java)?.user_id,
//            table_name = getString(R.string.ascad_state).toRequestBody(MultipartBody.FORM),
//        )
    }

    fun convertToRequestBody(context: Context, uri: Uri): RequestBody {
        val contentResolver: ContentResolver = context.contentResolver
        val tempFileName = "temp_${System.currentTimeMillis()}.pdf"
        val file = File(context.cacheDir, tempFileName)

        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            // Handle the error appropriately
        }

        return file.asRequestBody("application/pdf".toMediaType())
    }
}
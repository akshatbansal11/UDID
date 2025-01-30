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
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.udid.R
import com.udid.databinding.ActivityUpdateNameBinding
import com.udid.model.DropDownRequest
import com.udid.model.DropDownResult
import com.udid.model.Fields
import com.udid.model.Filters
import com.udid.model.GenerateOtpRequest
import com.udid.model.UserData
import com.udid.ui.adapter.BottomSheetAdapter
import com.udid.utilities.AppConstants
import com.udid.utilities.BaseActivity
import com.udid.utilities.EncryptionModel
import com.udid.utilities.JSEncryptService
import com.udid.utilities.Preferences.getPreferenceOfLogin
import com.udid.utilities.URIPathHelper
import com.udid.utilities.Utility.rotateDrawable
import com.udid.utilities.Utility.showSnackbar
import com.udid.utilities.hideView
import com.udid.utilities.showView
import com.udid.utilities.toast
import com.udid.viewModel.ViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException

class UpdateNameActivity : BaseActivity<ActivityUpdateNameBinding>() {

    private var mBinding: ActivityUpdateNameBinding? = null
    private var viewModel = ViewModel()
    private var bottomSheetDialog: BottomSheetDialog? = null
    private var bottomSheetAdapter: BottomSheetAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    private var reasonToUpdateNameList = ArrayList<DropDownResult>()
    private var reasonToUpdateNameId: String? = null
    private var identityProofList = ArrayList<DropDownResult>()
    private var identityProofId: String? = null
    var body: MultipartBody.Part? = null

    override val layoutId: Int
        get() = R.layout.activity_update_name

    override fun initView() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
        viewModel.init()
    }

    override fun setVariables() {
        mBinding?.etCurrentName?.text =
            getPreferenceOfLogin(
                this,
                AppConstants.LOGIN_DATA,
                UserData::class.java
            ).full_name
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

        viewModel.dropDownResult.observe(this) {
            val userResponseModel = it
            if (userResponseModel?._result != null && userResponseModel._result.isNotEmpty()) {
                if (userResponseModel.model == "Identityproofs") {
                    identityProofList.clear()
                    identityProofList.add(DropDownResult("0", getString(R.string.select_identity_proof)))
                    identityProofList.addAll(userResponseModel._result)
                    bottomSheetAdapter?.notifyDataSetChanged()
                } else if (userResponseModel.model == "Updationreason") {
                    reasonToUpdateNameList.clear()
                    reasonToUpdateNameList.add(DropDownResult("0", getString(R.string.spelling_correction_in_name)))
                    reasonToUpdateNameList.addAll(userResponseModel._result)
                    bottomSheetAdapter?.notifyDataSetChanged()
                }
            }
        }

        viewModel.generateOtpLoginResult.observe(this) {
            val userResponseModel = it
            if (userResponseModel?._resultflag != 0) {
                toast(userResponseModel.message)
                mBinding?.llOtp?.showView()
                mBinding?.scrollView?.post {
                    mBinding?.scrollView?.fullScroll(View.FOCUS_DOWN)
                }
            } else {
                mBinding?.clParent?.let { it1 -> showSnackbar(it1, userResponseModel.message) }
            }
        }

        viewModel.updateNameResult.observe(this) {
            val userResponseModel = it
            if (userResponseModel?._resultflag != 0) {
                toast(userResponseModel.message)
                startActivity(Intent(this, UpdateRequestActivity::class.java)
                    .putExtra(AppConstants.UPDATE_REQUEST, getString(R.string.submit_update_name)))
            } else {
                mBinding?.clParent?.let { it1 -> showSnackbar(it1, userResponseModel.message) }
            }
        }
        viewModel.errors.observe(this) {
            mBinding?.let { it1 -> showSnackbar(it1.clParent, it) }
        }
    }

    inner class ClickActions {
        fun backPress(view: View) {
            onBackPressedDispatcher.onBackPressed()
        }

        fun uploadFile(view: View) {
            checkStoragePermission(this@UpdateNameActivity)
        }

        fun identityProof(view: View) {
            showBottomSheetDialog("identity_proof")
        }

        fun reasonToUpdateName(view: View) {
            showBottomSheetDialog("reason_to_update_name")
        }

        fun generateOtp(view: View) {
            if (valid()) {
                generateOtpApi()
            }
        }

        fun submit(view: View) {
            if (valid()) {
                if (mBinding?.etEnterOtp?.text.toString().trim().isNotEmpty()) {
                    updateNameApi()
                } else {
                    showSnackbar(mBinding?.clParent!!, getString(R.string.please_enter_the_otp))
                }
            }
        }
    }

    private fun identityProofApi() {
        viewModel.getDropDown(
            this, DropDownRequest(
                Fields(id = "identity_name"),
                model = "Identityproofs",
                type = "mobile"
            )
        )
    }

    private fun reasonToUpdateNameListApi() {
        viewModel.getDropDown(
            this, DropDownRequest(
                Fields(reason = "reason"),
                model = "Updationreason",
                filters = Filters(
                    status = 1,
                    request_code = "NA"
                ),
                type = "mobile",
                order = "DESC"
            )
        )
    }

    private fun generateOtpApi() {
        viewModel.getGenerateOtpLoginApi(
            this,
            GenerateOtpRequest(
                application_number = JSEncryptService.encrypt(
                    getPreferenceOfLogin(
                        this,
                        AppConstants.LOGIN_DATA,
                        UserData::class.java
                    ).application_number.toString()
                )
            )
        )
    }

    private fun updateNameApi() {
        viewModel.getUpdateName(
            context = this,
            applicationNumber = EncryptionModel.aesEncrypt(
                getPreferenceOfLogin(
                    this,
                    AppConstants.LOGIN_DATA,
                    UserData::class.java
                ).application_number.toString()
            )
                .toRequestBody(MultipartBody.FORM),
            name = EncryptionModel.aesEncrypt(mBinding?.etUpdatedName?.text.toString().trim())
                .toRequestBody(MultipartBody.FORM),
            nameRegionalLanguage = EncryptionModel.aesEncrypt(
                mBinding?.etUpdatedNameRegionalLanguage?.text.toString().trim()
            ).toRequestBody(MultipartBody.FORM),
            addressProofId = EncryptionModel.aesEncrypt(identityProofId.toString())
                .toRequestBody(MultipartBody.FORM),
            reason = EncryptionModel.aesEncrypt(reasonToUpdateNameId.toString())
                .toRequestBody(MultipartBody.FORM),
            otherReason = EncryptionModel.aesEncrypt(
                mBinding?.etAnyOtherReason?.text.toString().trim()
            )
                .toRequestBody(MultipartBody.FORM),
            otp = EncryptionModel.aesEncrypt(mBinding?.etEnterOtp?.text.toString().trim()).toRequestBody(MultipartBody.FORM),
            type = "mobile".toRequestBody(MultipartBody.FORM),
            document = body
        )
    }

    private fun showBottomSheetDialog(type: String) {
        bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_state, null)
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val rvBottomSheet = view.findViewById<RecyclerView>(R.id.rvBottomSheet)
        val close = view.findViewById<TextView>(R.id.tvClose)

        close.setOnClickListener {
            bottomSheetDialog?.dismiss()
        }

        // Define a variable for the selected list and TextView
        val selectedList: List<DropDownResult>
        val selectedTextView: TextView?
        // Initialize based on type
        when (type) {
            "identity_proof" -> {
                if (identityProofList.isEmpty()) {
                    identityProofApi()
                }
                selectedList = identityProofList
                selectedTextView = mBinding?.etIdentityProof
            }

            "reason_to_update_name" -> {
                if (reasonToUpdateNameList.isEmpty()) {
                    reasonToUpdateNameListApi()
                }
                selectedList = reasonToUpdateNameList
                selectedTextView = mBinding?.etReasonToUpdateName
            }

            else -> return
        }

        // Set up the adapter
        bottomSheetAdapter = BottomSheetAdapter(this, selectedList) { selectedItem, id ->
            // Handle state item click
            selectedTextView?.text = selectedItem
            when (type) {
                "identity_proof" -> {
                    if (selectedItem == "Select Identity Proof") {
                        selectedTextView?.text = ""
                    } else {
                        identityProofId = id
                    }
                }

                "reason_to_update_name" -> {
                    when (selectedItem) {
                        "Any other " -> {
                            mBinding?.tvAnyOtherReason?.showView()
                            mBinding?.etAnyOtherReason?.showView()
                            reasonToUpdateNameId = id
                        }

                        "Spelling Correction in Name" -> {
                            mBinding?.tvAnyOtherReason?.hideView()
                            mBinding?.etAnyOtherReason?.hideView()
                            selectedTextView?.text = ""
                            mBinding?.etAnyOtherReason?.setText("")
                        }

                        else -> {
                            mBinding?.tvAnyOtherReason?.hideView()
                            mBinding?.etAnyOtherReason?.hideView()
                            mBinding?.etAnyOtherReason?.setText("")
                            reasonToUpdateNameId = id
                        }
                    }
                }
            }
            selectedTextView?.setTextColor(ContextCompat.getColor(this, R.color.black))
            bottomSheetDialog?.dismiss()
        }

        layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvBottomSheet.layoutManager = layoutManager
        rvBottomSheet.adapter = bottomSheetAdapter
        bottomSheetDialog?.setContentView(view)


        // Rotate drawable
        val drawable = ContextCompat.getDrawable(this, R.drawable.ic_arrow_down)
        var rotatedDrawable = rotateDrawable(drawable, 180f)
        selectedTextView?.setCompoundDrawablesWithIntrinsicBounds(null, null, rotatedDrawable, null)

        // Set a dismiss listener to reset the view visibility
        bottomSheetDialog?.setOnDismissListener {
            rotatedDrawable = rotateDrawable(drawable, 0f)
            selectedTextView?.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                rotatedDrawable,
                null
            )
        }

        // Show the bottom sheet
        bottomSheetDialog?.show()
    }

    private fun valid(): Boolean {
        if (mBinding?.etCurrentName?.text.toString().isEmpty()) {
            mBinding?.clParent?.let {
                showSnackbar(it, getString(R.string.please_enter_the_updated_name))
            }
            return false
        } else if (mBinding?.etUpdatedNameRegionalLanguage?.text.toString().isEmpty()) {
            mBinding?.clParent?.let {
                showSnackbar(it, getString(R.string.please_enter_the_updated_regional_name))

            }
            return false
        } else if (mBinding?.etIdentityProof?.text.toString().isEmpty()) {
            mBinding?.clParent?.let {
                showSnackbar(it, getString(R.string.please_select_the_identity_proof))

            }
            return false
        } else if (mBinding?.etFileName?.text.toString().isEmpty()) {
            mBinding?.clParent?.let {
                showSnackbar(it, getString(R.string.please_upload_the_document))
            }
            return false
        } else if (mBinding?.etReasonToUpdateName?.text.toString().isEmpty()) {
            mBinding?.clParent?.let {
                showSnackbar(it, getString(R.string.please_enter_the_reason))
            }
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
                                            getString(R.string.file_size_exceeds_5_mb)
                                        )
                                    }
                                }
                            }
                        } else {
                            mBinding?.let { showSnackbar(it.clParent, getString(R.string.format_not_supported)) }
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

    private fun uploadDocument(documentName: String?, uri: Uri) {
        val requestBody = convertToRequestBody(this, uri)
        body = MultipartBody.Part.createFormData(
            "document",
            documentName,
            requestBody
        )
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
}
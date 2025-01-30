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
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.udid.R
import com.udid.databinding.ActivityUpdateAadharNumberBinding
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
import com.udid.utilities.Utility
import com.udid.utilities.Utility.maskAadharNumber
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

class UpdateAadharNumberActivity : BaseActivity<ActivityUpdateAadharNumberBinding>() {

    private var mBinding: ActivityUpdateAadharNumberBinding? = null
    private var viewModel = ViewModel()
    private var isPasswordVisible: Boolean = false
    private var bottomSheetDialog: BottomSheetDialog? = null
    private var bottomSheetAdapter: BottomSheetAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    private var reasonToUpdateAadhaarList = ArrayList<DropDownResult>()
    private var reasonToUpdateAadhaarId: String? = null
    private var identityProofList = ArrayList<DropDownResult>()
    private var identityProofId: String? = null
    var body: MultipartBody.Part? = null

    override val layoutId: Int
        get() = R.layout.activity_update_aadhar_number

    override fun initView() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
        viewModel.init()
    }

    override fun setVariables() {
        mBinding?.etCurrentAadhaarNumber?.text =
            maskAadharNumber(
                getPreferenceOfLogin(
                    this,
                    AppConstants.LOGIN_DATA,
                    UserData::class.java
                ).aadhaar_no.toString()
            )

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
                    identityProofList.add(DropDownResult("0",getString(R.string.select_identity_proof)))
                    identityProofList.addAll(userResponseModel._result)
                    bottomSheetAdapter?.notifyDataSetChanged()
                } else if (userResponseModel.model == "Updationreason") {
                    reasonToUpdateAadhaarList.clear()
                    reasonToUpdateAadhaarList.add(DropDownResult("0",
                        getString(R.string.reason_to_update_aadhaar)))
                    reasonToUpdateAadhaarList.addAll(userResponseModel._result)
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

        viewModel.updateAadhaarResult.observe(this) {
            val userResponseModel = it
            if (userResponseModel?._resultflag != 0) {
                toast(userResponseModel.message)
                startActivity(
                    Intent(this, UpdateRequestActivity::class.java)
                        .putExtra(AppConstants.UPDATE_REQUEST,
                            getString(R.string.submit_update_aadhaar_number)))
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

        fun toggleEye(view: View) {
            togglePasswordVisibility()
        }

        fun uploadFile(view: View) {
            checkStoragePermission(this@UpdateAadharNumberActivity)
        }

        fun identityProof(view: View) {
            showBottomSheetDialog("identity_proof")
        }

        fun reasonToUpdateAadhaar(view: View) {
            showBottomSheetDialog("reason_to_update_aadhaar")
        }

        fun submit(view: View) {
            if (valid()) {
                if (mBinding?.etEnterOtp?.text.toString().trim().isNotEmpty()) {
                    updateAadhaarApi()
                } else {
                    showSnackbar(mBinding?.clParent!!, getString(R.string.please_enter_the_otp))
                }
            }
        }

        fun generateOtp(view: View) {
            if (valid()) {
                generateOtpApi()
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

    private fun reasonToUpdateAadhaarListApi() {
        viewModel.getDropDown(
            this, DropDownRequest(
                Fields(reason = "reason"),
                model = "Updationreason",
                filters = Filters(
                    status = 1,
                    request_code = "AD"
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

    private fun updateAadhaarApi() {
        viewModel.getUpdateAadhaar(
            context = this,
            applicationNumber = EncryptionModel.aesEncrypt(
                getPreferenceOfLogin(
                    this,
                    AppConstants.LOGIN_DATA,
                    UserData::class.java
                ).application_number.toString()
            )
                .toRequestBody(MultipartBody.FORM),
            aadhaarNo = EncryptionModel.aesEncrypt(mBinding?.etUpdatedAadhaarNumber?.text.toString().trim())
                .toRequestBody(MultipartBody.FORM),
            addressProofId = EncryptionModel.aesEncrypt(identityProofId.toString())
                .toRequestBody(MultipartBody.FORM),
            reason = EncryptionModel.aesEncrypt(reasonToUpdateAadhaarId.toString())
                .toRequestBody(MultipartBody.FORM),
            otherReason = EncryptionModel.aesEncrypt(mBinding?.etAnyOtherReason?.text.toString().trim().toString())
                .toRequestBody(MultipartBody.FORM),
            otp = EncryptionModel.aesEncrypt(mBinding?.etEnterOtp?.text.toString().trim()).toRequestBody(MultipartBody.FORM),
            type = "mobile".toRequestBody(MultipartBody.FORM),
            document = body
        )
    }

    private fun valid(): Boolean {

        // Check if Aadhaar number is null or empty
        if (mBinding?.etUpdatedAadhaarNumber?.text.toString().trim().isEmpty()) {
            mBinding?.clParent?.let { showSnackbar(it, getString(R.string.please_enter_aadhaar_no)) }
            return false
        }
        if (mBinding?.etIdentityProof?.text.toString().trim().isEmpty()) {
            mBinding?.clParent?.let { showSnackbar(it,
                getString(R.string.please_select_identity_proof)) }
            return false
        }
        if (mBinding?.etFileName?.text.toString().trim().isEmpty()) {
            mBinding?.clParent?.let {
                showSnackbar(it, getString(R.string.please_upload_supporting_document))
            }
            return false
        }
        if (mBinding?.etReasonToUpdateAadhar?.text.toString().trim().isEmpty()) {
            mBinding?.clParent?.let {
                showSnackbar(
                    it,
                    getString(R.string.please_select_reason_to_update_aadhaar_number)
                )
            }
            return false
        }

        // If all checks pass
        return true
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

            "reason_to_update_aadhaar" -> {
                if (reasonToUpdateAadhaarList.isEmpty()) {
                    reasonToUpdateAadhaarListApi()
                }
                reasonToUpdateAadhaarListApi()
                selectedList = reasonToUpdateAadhaarList
                selectedTextView = mBinding?.etReasonToUpdateAadhar
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

                "reason_to_update_aadhaar" -> {
                    when (selectedItem) {
                        "Any other " -> {
                            mBinding?.tvAnyOtherReason?.showView()
                            mBinding?.etAnyOtherReason?.showView()
                            reasonToUpdateAadhaarId = id
                        }
                        "Reason to update Aadhaar" -> {
                            mBinding?.tvAnyOtherReason?.hideView()
                            mBinding?.etAnyOtherReason?.hideView()
                            selectedTextView?.text = ""
                            mBinding?.etAnyOtherReason?.setText("")
                        }
                        else -> {
                            mBinding?.tvAnyOtherReason?.hideView()
                            mBinding?.etAnyOtherReason?.hideView()
                            mBinding?.etAnyOtherReason?.setText("")
                            reasonToUpdateAadhaarId = id
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

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            mBinding?.etUpdatedAadhaarNumber?.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            mBinding?.ivTogglePassword?.setImageResource(R.drawable.ic_eye_off) // change to eye icon
        } else {
            mBinding?.etUpdatedAadhaarNumber?.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            mBinding?.ivTogglePassword?.setImageResource(R.drawable.ic_eye) // change to eye off icon
        }
        mBinding?.etUpdatedAadhaarNumber?.setSelection(
            mBinding?.etUpdatedAadhaarNumber?.text?.length ?: 0
        )  // Move cursor to end of text
        isPasswordVisible = !isPasswordVisible
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
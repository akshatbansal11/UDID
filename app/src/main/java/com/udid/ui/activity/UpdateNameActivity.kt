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
import com.udid.databinding.ActivityUpdateNameBinding
import com.udid.model.DropDownRequest
import com.udid.model.DropDownResult
import com.udid.model.Fields
import com.udid.model.Filters
import com.udid.model.GenerateOtpRequest
import com.udid.ui.adapter.BottomSheetAdapter
import com.udid.utilities.AppConstants
import com.udid.utilities.BaseActivity
import com.udid.utilities.JSEncryptService
import com.udid.utilities.URIPathHelper
import com.udid.utilities.Utility
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
    private var otp: String? = null
    var body: MultipartBody.Part? = null

    override val layoutId: Int
        get() = R.layout.activity_update_name

    override fun initView() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
        viewModel.init()
    }

    inner class ClickActions {
        fun backPress(view: View) {
            onBackPressedDispatcher.onBackPressed()
        }

        fun uploadFile(view: View) {
            openOnlyPdfAccordingToPosition()
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
                mBinding?.llOtp?.showView()
            }
        }

        fun submit(view: View) {
            if(mBinding?.etAnyOtherReason?.text.toString().trim().isNotEmpty()){
                updateNameApi(mBinding?.etAnyOtherReason?.text.toString().trim())
            }
            else {
                updateNameApi(null)
            }
//            onBackPressedDispatcher.onBackPressed()
        }
    }


    override fun setVariables() {
        mBinding?.etCurrentName?.text = Utility.getPreferenceString(this, AppConstants.FULL_NAME)
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
                type = "mobile"
            )
        )
    }

    private fun generateOtpApi() {
        viewModel.getGenerateOtpLoginApi(
            this,
            GenerateOtpRequest(
                application_number = JSEncryptService.encrypt(
                    Utility.getPreferenceString(
                        this@UpdateNameActivity,
                        AppConstants.APPLICATION_NUMBER
                    )
                )
            )
        )
    }

    private fun updateNameApi(anyOtherReason : String?){
        viewModel.getUpdateName(
            context = this,
            applicationNumber = Utility.getPreferenceString(this, AppConstants.APPLICATION_NUMBER)
                .toRequestBody(MultipartBody.FORM),
            name = mBinding?.etUpdatedName?.text.toString().trim().toRequestBody(MultipartBody.FORM),
            nameRegionalLanguage = mBinding?.etUpdatedNameRegionalLanguage?.text.toString().trim().toRequestBody(MultipartBody.FORM),
            addressProofId = identityProofId?.toRequestBody(MultipartBody.FORM),
            reason = reasonToUpdateNameId?.toRequestBody(MultipartBody.FORM),
            otherReason = anyOtherReason?.toRequestBody(MultipartBody.FORM),
            otp = otp?.toRequestBody(MultipartBody.FORM),
            document = body
        )
    }

    override fun setObservers() {

        viewModel.dropDownResult.observe(this) {
            val userResponseModel = it
            if (userResponseModel?._result != null && userResponseModel._result.isNotEmpty()) {
                if (userResponseModel.model == "Identityproofs") {
                    identityProofList.clear()
                    identityProofList.addAll(userResponseModel._result)
                    bottomSheetAdapter?.notifyDataSetChanged()
                } else if (userResponseModel.model == "Updationreason") {
                    reasonToUpdateNameList.clear()
                    reasonToUpdateNameList.addAll(userResponseModel._result)
                    bottomSheetAdapter?.notifyDataSetChanged()
                }
            }
        }

        viewModel.generateOtpLoginResult.observe(this) {
            val userResponseModel = it
            if (userResponseModel?._resultflag != 0) {
                mBinding?.clParent?.let { it1 -> showSnackbar(it1, userResponseModel.message) }
                otp = JSEncryptService.decrypt(userResponseModel.otp)
                toast(otp.toString())
            } else {
                mBinding?.clParent?.let { it1 -> showSnackbar(it1, userResponseModel.message) }
            }
        }
        viewModel.errors.observe(this) {
            mBinding?.let { it1 -> showSnackbar(it1.clParent, it) }
        }
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
                identityProofApi()
                selectedList = identityProofList
                selectedTextView = mBinding?.etIdentityProof
            }

            "reason_to_update_name" -> {
                reasonToUpdateNameListApi()
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
                    identityProofId = id
                }

                "reason_to_update_name" -> {
                    if (selectedItem == "Any other ") {
                        mBinding?.tvAnyOtherReason?.showView()
                        mBinding?.etAnyOtherReason?.showView()
                    } else {
                        mBinding?.tvAnyOtherReason?.hideView()
                        mBinding?.etAnyOtherReason?.hideView()
                        mBinding?.etAnyOtherReason?.setText("")
                    }
                    reasonToUpdateNameId = id
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

    private fun rotateDrawable(drawable: Drawable?, angle: Float): Drawable? {
        drawable?.mutate() // Mutate the drawable to avoid affecting other instances

        val rotateDrawable = RotateDrawable()
        rotateDrawable.drawable = drawable
        rotateDrawable.fromDegrees = 0f
        rotateDrawable.toDegrees = angle
        rotateDrawable.level = 10000 // Needed to apply the rotation

        return rotateDrawable
    }

    private fun valid(): Boolean {
        if (mBinding?.etCurrentName?.text.toString().isEmpty()) {
            mBinding?.clParent?.let {
                showSnackbar(it, "Please Enter the Updated Name")
            }
            return false
        } else if (mBinding?.etUpdatedNameRegionalLanguage?.text.toString().isEmpty()) {
            mBinding?.clParent?.let {
                showSnackbar(it, "Please Enter the Updated Regional Name")

            }
            return false
        } else if (mBinding?.etIdentityProof?.text.toString().isEmpty()) {
            mBinding?.clParent?.let {
                showSnackbar(it, "Please Select the identity proof")

            }
            return false
        } else if (mBinding?.etFileName?.text.toString().isEmpty()) {
            mBinding?.clParent?.let {
                showSnackbar(it, "Please upload the document")
            }
            return false
        } else if (mBinding?.etReasonToUpdateName?.text.toString().isEmpty()) {
            mBinding?.clParent?.let {
                showSnackbar(it, "Please Enter the Reason")
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
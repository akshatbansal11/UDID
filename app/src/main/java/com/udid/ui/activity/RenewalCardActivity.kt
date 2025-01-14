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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.udid.R
import com.udid.databinding.ActivityRenewalCardBinding
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

class RenewalCardActivity : BaseActivity<ActivityRenewalCardBinding>() {

    private var mBinding: ActivityRenewalCardBinding? = null
    private var viewModel = ViewModel()
    private var isPasswordVisible: Boolean = false
    private var uploadData: ImageView? = null
    private var bottomSheetDialog: BottomSheetDialog? = null
    private var bottomSheetAdapter: BottomSheetAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    private var reasonToUpdateAadhaarList = ArrayList<DropDownResult>()
    private var reasonToUpdateAadhaarId: String? = null
    private var identityProofList = ArrayList<DropDownResult>()
    private var identityProofId: String? = null
    private var radioTag: Int = 0
    var body: MultipartBody.Part? = null

    override val layoutId: Int
        get() = R.layout.activity_renewal_card

    override fun initView() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
        viewModel.init()

        mBinding?.rgRenewalCard?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbHome -> {
                    mBinding?.llPresent?.hideView()
                    mBinding?.llTreating?.hideView()
                    radioTag = 1
                }

                R.id.rbPresent -> {
                    mBinding?.llPresent?.showView()
                    mBinding?.llTreating?.hideView()
                    radioTag = 2
                }

                R.id.rbTreating -> {
                    mBinding?.llPresent?.hideView()
                    mBinding?.llTreating?.showView()
                    radioTag = 3
                }

                else -> {
                    radioTag = 0
                }
            }
        }

    }

    inner class ClickActions {
        fun backPress(view: View) {
            onBackPressedDispatcher.onBackPressed()
        }

        fun uploadFile(view: View) {
            checkStoragePermission(this@RenewalCardActivity)
        }

        fun hospitalName(view: View) {
            showBottomSheetDialog("tvPresentDistrictName")
        }

        fun subDistrictName(view: View) {
            showBottomSheetDialog("tvPresentDistrictName")
        }

        fun districtName(view: View) {
            showBottomSheetDialog("tvPresentDistrictName")
        }

        fun stateName(view: View) {
            showBottomSheetDialog("tvPresentStateName")
        }

        fun submit(view: View) {
            onBackPressedDispatcher.onBackPressed()
        }

        fun generateOtp(view: View) {
            if (valid()) {
                mBinding?.clParent?.let { Utility.showSnackbar(it, "Done OTP") }

            }
        }
    }

    private fun valid(): Boolean {
        val etPermanentAddress = mBinding?.etPermanentAddress?.text?.toString()
        val fileName = mBinding?.etFileName?.text?.toString()
        val tvPresentStateName = mBinding?.tvPresentStateName?.hint?.toString()
        val tvPresentDistrictName = mBinding?.tvPresentDistrictName?.hint?.toString()
        val tvSubDistrictName = mBinding?.tvSubDistrictName?.hint?.toString()
        val etPincode = mBinding?.etPincode?.text?.toString()
        val tvHospitalName = mBinding?.tvHospitalName?.hint?.toString()

        if (radioTag == 0) {
            mBinding?.clParent?.let { showSnackbar(it, "Please Select One of the Options") }
            return false
        }

        if (radioTag == 2) {
            if (etPermanentAddress.isNullOrEmpty()) {
                mBinding?.clParent?.let { showSnackbar(it, "Please enter Address") }
                return false
            }

            if (tvPresentStateName == "Select State Name") {
                mBinding?.clParent?.let {
                    showSnackbar(
                        it,
                        "Please select Hospital Treating State / UTs."
                    )
                }
                return false
            }

            if (tvPresentDistrictName == "Select District Name") {
                mBinding?.clParent?.let {
                    showSnackbar(
                        it,
                        "Please select Hospital Treating District"
                    )
                }
                return false
            }

            if (tvSubDistrictName == "Select Sub-District Name") {
                mBinding?.clParent?.let {
                    showSnackbar(
                        it,
                        "Please select Hospital Treating Sub-District"
                    )
                }
                return false
            }

            if (etPincode.isNullOrEmpty()) {
                mBinding?.clParent?.let { showSnackbar(it, "Please enter Pincode") }
                return false
            }

            if (tvHospitalName == "Select Sub-District Name") {
                mBinding?.clParent?.let { showSnackbar(it, "Please select Hospital Name") }
                return false
            }
            return true
        } else if (radioTag == 3) {

            if (mBinding?.tvTreatingStateName?.hint?.toString() == "Select State Name") {
                mBinding?.clParent?.let { showSnackbar(it, "Please Select State Name") }
                return false
            }

            if (mBinding?.tvTreatingDistrictName?.hint?.toString() == "Select District Name") {
                mBinding?.clParent?.let { showSnackbar(it, "Please Select District Name") }
                return false
            }

            if (mBinding?.tvTreatingHospitalName?.hint?.toString() == "Select Hospital Name") {
                mBinding?.clParent?.let { showSnackbar(it, "Please select Hospital Name") }
                return false
            }

            return true
        }
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
            "tvPresentDistrictName" -> {
//                identityProofApi()
                selectedList = identityProofList
                selectedTextView = mBinding?.tvPresentDistrictName
            }

            "tvPresentStateName" -> {
//                reasonToUpdateNameListApi()
                selectedList = reasonToUpdateAadhaarList
                selectedTextView = mBinding?.tvPresentStateName
            }

            else -> return
        }

        // Set up the adapter
        bottomSheetAdapter = BottomSheetAdapter(this, selectedList) { selectedItem, id ->
            // Handle state item click
            selectedTextView?.text = selectedItem
            selectedTextView?.hint = ""
            when (type) {
                "tvPresentDistrictName" -> {
                    identityProofId = id
                }

                "tvPresentStateName" -> {
                    if (selectedItem == "Any other ") {
                        mBinding?.tvAnyOtherReason?.showView()
                        mBinding?.etAnyOtherReason?.showView()
                    } else {
                        mBinding?.tvAnyOtherReason?.hideView()
                        mBinding?.etAnyOtherReason?.hideView()
                    }
                    reasonToUpdateAadhaarId = id
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


    override fun setVariables() {
    }

    override fun setObservers() {
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
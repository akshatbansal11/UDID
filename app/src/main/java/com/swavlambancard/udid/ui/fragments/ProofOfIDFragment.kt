package com.swavlambancard.udid.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.swavlambancard.udid.R
import com.swavlambancard.udid.databinding.FragmentProofOfIDBinding
import com.swavlambancard.udid.model.DropDownRequest
import com.swavlambancard.udid.model.DropDownResult
import com.swavlambancard.udid.model.Fields
import com.swavlambancard.udid.ui.PdfViewerActivity
import com.swavlambancard.udid.ui.activity.PersonalProfileActivity
import com.swavlambancard.udid.ui.adapter.BottomSheetAdapter
import com.swavlambancard.udid.utilities.BaseFragment
import com.swavlambancard.udid.utilities.EncryptionModel
import com.swavlambancard.udid.utilities.URIPathHelper
import com.swavlambancard.udid.utilities.Utility.getNameById
import com.swavlambancard.udid.utilities.Utility.openFile
import com.swavlambancard.udid.utilities.Utility.rotateDrawable
import com.swavlambancard.udid.utilities.Utility.setBlueUnderlinedText
import com.swavlambancard.udid.utilities.Utility.showSnackbar
import com.swavlambancard.udid.utilities.hideView
import com.swavlambancard.udid.utilities.showView
import com.swavlambancard.udid.utilities.toast
import com.swavlambancard.udid.viewModel.SharedDataViewModel
import com.swavlambancard.udid.viewModel.ViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


class ProofOfIDFragment : BaseFragment<FragmentProofOfIDBinding>() {
    private lateinit var sharedViewModel: SharedDataViewModel
    private var viewModel = ViewModel()
    private var mBinding: FragmentProofOfIDBinding? = null
    private var bottomSheetDialog: BottomSheetDialog? = null
    private var bottomSheetAdapter: BottomSheetAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    private var identityProofList = ArrayList<DropDownResult>()
    private var identityProofListYes = ArrayList<DropDownResult>()
    private var identityProofId: String? = null
    var body: MultipartBody.Part? = null
    private var identityProofName: String? = null
    private var enrollmentSlipName: String? = null
    private var document = 0
    private var aadhaarTag: Int = 2
    private var imageUri: Uri? = null
    private var cameraUri: Uri? = null
    private var pdfUri: Uri? = null

    override val layoutId: Int
        get() = R.layout.fragment_proof_of_i_d


    override fun init() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
        viewModel.init()
        sharedViewModel = ViewModelProvider(requireActivity())[SharedDataViewModel::class.java]
        identityProofApi()
        sharedViewModel.userData.observe(viewLifecycleOwner) { userData ->
            when (userData.aadhaarTag) {
                0 -> {
                    mBinding?.rbNo?.isChecked = true
                    mBinding?.llYesAadhaarCard?.hideView()
                    mBinding?.llNoAadhaarCard?.showView()
                }

                1 -> {
                    mBinding?.rbYes?.isChecked = true
                    mBinding?.llYesAadhaarCard?.showView()
                    mBinding?.llNoAadhaarCard?.hideView()
                }

                else -> {
                    mBinding?.rbNo?.isChecked = false
                    mBinding?.rbYes?.isChecked = false
                    mBinding?.llYesAadhaarCard?.hideView()
                    mBinding?.llNoAadhaarCard?.hideView()
                }
            }
            when (userData.aadhaarCheckBox) {
                0 -> {
                    mBinding?.checkboxConfirm?.isChecked = false
                }

                1 -> {
                    mBinding?.checkboxConfirm?.isChecked = true
                }
            }
            mBinding?.etAadhaarNo?.setText(userData.aadhaarNo)
            mBinding?.etAadhaarEnrollment?.setText(userData.aadhaarEnrollmentNo)
            mBinding?.etIdentityProof?.text = userData.identityProofName
            identityProofId = userData.identityProofId
            if(userData.identityProofUploadPath!=null){
                mBinding?.etFileNameIdentityProof?.text="VIEW"
                mBinding?.etFileNameIdentityProof?.let {
                    setBlueUnderlinedText(
                        it,
                        "VIEW"
                    )
                }
            }
            if(userData.aadhaarEnrollmentUploadSlipPath!=null){
                mBinding?.etFileNameEnrollmentSlip?.text="VIEW"
                mBinding?.etFileNameEnrollmentSlip?.let {
                    setBlueUnderlinedText(
                        it,
                        "VIEW"
                    )
                }
            }
            if (sharedViewModel.userData.value?.isFrom != "login") {
                if (userData.identityProofUpload != null) {
                    mBinding?.etFileNameIdentityProof?.let {
                        setBlueUnderlinedText(
                            it,
                            "VIEW"
                        )
                    }
                    sharedViewModel.userData.value?.identityProofUpload=null
                }
                if(userData.aadhaarEnrollmentUploadSlip!=null){
                    mBinding?.etFileNameEnrollmentSlip?.let {
                        setBlueUnderlinedText(
                            it,
                            "VIEW"
                        )
                    }
                    sharedViewModel.userData.value?.aadhaarEnrollmentUploadSlip=null
                }
            }
//                mBinding?.etFileNameIdentityProof?.let {
//                    setBlueUnderlinedText(
//                        it,
//                        userData.identityProofUpload.toString()
//                    )
//                }
//                mBinding?.etFileNameIdentityProof?.setOnClickListener {
//                    openFile(userData.identityProofUpload.toString(), requireContext())
//                }
//                mBinding?.etFileNameEnrollmentSlip?.let {
//                    setBlueUnderlinedText(
//                        it,
//                        userData.aadhaarEnrollmentUploadSlip.toString()
//                    )
//                }
//                mBinding?.etFileNameEnrollmentSlip?.setOnClickListener {
//                    openFile(userData.aadhaarEnrollmentUploadSlip.toString(), requireContext())
//                }
//            } else {
//                mBinding?.etFileNameIdentityProof?.let {
//                    setBlueUnderlinedText(
//                        it,
//                        userData.identityProofUpload.toString()
//                    )
//                }
//                mBinding?.etFileNameEnrollmentSlip?.let {
//                    setBlueUnderlinedText(
//                        it,
//                        userData.aadhaarEnrollmentUploadSlip.toString()
//                    )
//                }
//            }


        }

        mBinding?.rgAadhaar?.setOnCheckedChangeListener { _, checkedId ->
            aadhaarTag = when (checkedId) {
                R.id.rbNo -> {
                    0

                }

                R.id.rbYes -> {
                    1
                }

                else -> {
                    2
                }
            }
            sharedViewModel.userData.value?.aadhaarTag = aadhaarTag
            sharedViewModel.userData.value?.aadhaarInfo = aadhaarTag
        }
        mBinding?.checkboxConfirm?.setOnCheckedChangeListener { _, isChecked ->
            sharedViewModel.userData.value?.aadhaarCheckBox = if (isChecked) 1 else 0
        }
        mBinding?.etAadhaarNo?.addTextChangedListener {
            sharedViewModel.userData.value?.aadhaarNo = it.toString()
        }
        mBinding?.etAadhaarEnrollment?.addTextChangedListener {
            sharedViewModel.userData.value?.aadhaarEnrollmentNo = it.toString()
        }
//        mBinding?.etFileNameEnrollmentSlip?.addTextChangedListener {
//            sharedViewModel.userData.value?.aadhaarEnrollmentUploadSlip = it.toString()
//        }
        mBinding?.etIdentityProof?.addTextChangedListener {
            sharedViewModel.userData.value?.identityProofName = it.toString()
        }
//        mBinding?.etFileNameIdentityProof?.addTextChangedListener {
//            sharedViewModel.userData.value?.identityProofUpload = it.toString()
//        }
    }

    override fun setVariables() {
    }

    override fun setObservers() {
        viewModel.dropDownResult.observe(this) {
            val userResponseModel = it
            if (userResponseModel?._result != null && userResponseModel._result.isNotEmpty()) {
                if (userResponseModel.model == "Identityproofs") {
                    identityProofList.clear()
                    identityProofList.add(
                        DropDownResult(
                            "0",
                            "Select Identity Proof"
                        )
                    )
                    identityProofList.addAll(userResponseModel._result)
                    identityProofListYes.addAll(userResponseModel._result)
                    if (sharedViewModel.userData.value?.isFrom != "login") {
                        mBinding?.etIdentityProof?.text = getNameById(
                            sharedViewModel.userData.value?.identityProofId.toString(),
                            identityProofList
                        )
                    }
                    bottomSheetAdapter?.notifyDataSetChanged()
                }
            }
        }

        viewModel.uploadFile.observe(this) {
            val userResponseModel = it
            if (userResponseModel?._result != null) {
                if (userResponseModel._resultflag == 0) {
                    mBinding?.llParent?.let { it1 ->
                        showSnackbar(
                            it1,
                            userResponseModel.message
                        )
                    }
                } else {
                    if (document == 1) {
                        identityProofName = userResponseModel._result.file_name
                        mBinding?.etFileNameIdentityProof?.text =
                            "VIEW"
                        mBinding?.etFileNameIdentityProof?.let {
                            setBlueUnderlinedText(
                                it,
                                "VIEW"
                            )
                        }
                        sharedViewModel.userData.value?.identityProofUpload=userResponseModel._result.file_name
                        when {
                            pdfUri != null -> sharedViewModel.userData.value?.identityProofUploadPath =
                                pdfUri.toString()

                            cameraUri != null -> sharedViewModel.userData.value?.identityProofUploadPath =
                                cameraUri.toString()


                            imageUri != null -> sharedViewModel.userData.value?.identityProofUploadPath =
                                imageUri.toString()

                        }

                    } else if (document == 2) {
                        enrollmentSlipName = userResponseModel._result.file_name
                        mBinding?.etFileNameEnrollmentSlip?.text =
                            "VIEW"

                        mBinding?.etFileNameEnrollmentSlip?.let {
                            setBlueUnderlinedText(
                                it,
                                "VIEW"
                            )
                        }
                        sharedViewModel.userData.value?.aadhaarEnrollmentUploadSlip=userResponseModel._result.file_name
                        when {
                            pdfUri != null -> sharedViewModel.userData.value?.aadhaarEnrollmentUploadSlipPath =
                                pdfUri.toString()

                            cameraUri != null -> sharedViewModel.userData.value?.aadhaarEnrollmentUploadSlipPath =
                                cameraUri.toString()

                            imageUri != null -> sharedViewModel.userData.value?.aadhaarEnrollmentUploadSlipPath =
                                imageUri.toString()
                        }
                    }

                }
            }
        }
        viewModel.errors.observe(this) {
            mBinding?.let { it1 -> showSnackbar(it1.llParent, it) }
        }
    }

    inner class ClickActions {
        fun next(view: View) {
            if (valid()) {

            (requireActivity() as PersonalProfileActivity).replaceFragment(
                ProofOfAddressFragment()
            )
            }
        }

        fun back(view: View) {
            (requireActivity() as PersonalProfileActivity).replaceFragment(PersonalDetailFragment())
        }

        fun rbYes(view: View) {
            aadhaarTag = 1
            identityProofList.clear()
            identityProofList.add(DropDownResult("0", getString(R.string.select_identity_proof)))
            identityProofList.add(DropDownResult("8", "Aadhaar Card"))
            mBinding?.llYesAadhaarCard?.showView()
            mBinding?.llNoAadhaarCard?.hideView()
            mBinding?.etAadhaarEnrollment?.setText("")
            mBinding?.etFileNameEnrollmentSlip?.text = ""
            enrollmentSlipName = ""
        }

        fun rbNo(view: View) {
            aadhaarTag = 0
            identityProofListYes.remove(DropDownResult("8", "Aadhaar Card"))
            mBinding?.llYesAadhaarCard?.hideView()
            mBinding?.llNoAadhaarCard?.showView()
            mBinding?.etAadhaarNo?.setText("")
            mBinding?.checkboxConfirm?.isChecked = false
            sharedViewModel.userData.value?.aadhaarEnrollmentUploadSlipPath=null
            sharedViewModel.userData.value?.aadhaarEnrollmentUploadSlip=null
            cameraUri=null
            imageUri=null
            pdfUri=null

        }

        fun identityProof(view: View) {
            showBottomSheetDialog("identity_proof")
        }

        fun uploadFileIdentityProof(view: View) {
            document = 1
            checkStoragePermission(requireContext())
        }

        fun fileViewerIdentity(view: View) {
            if (sharedViewModel.userData.value?.identityProofUploadPath == null) {
                return
            }
            Log.d("path",sharedViewModel.userData.value?.identityProofUploadPath.toString())
//            if (sharedViewModel.userData.value?.isFrom != "login") {
//                val documentPath = sharedViewModel.userData.value?.identityProofUploadPath
//                if (documentPath.isNullOrEmpty()) {
//                    Toast.makeText(requireContext(), "No document found", Toast.LENGTH_SHORT).show()
//                    return
//                }
//
//                val uri = Uri.parse(documentPath)
//
//                if (documentPath.endsWith(".pdf", ignoreCase = true)) {
//                    // Open PDF in Chrome using Google Docs Viewer
//                    val pdfUrl = "https://docs.google.com/viewer?url=$uri"
//                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl))
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    intent.setPackage("com.android.chrome") // Forces it to open in Chrome if available
//
//                    try {
//                        startActivity(intent)
//                    } catch (e: Exception) {
//                        intent.setPackage(null) // Open in any available browser
//                        startActivity(intent)
//                    }
//                } else {
//                    // Open Image in Chrome by using "file://" or "content://"
//                    val intent = Intent(requireContext(), PdfViewerActivity::class.java)
//                    intent.putExtra("fileUri", uri.toString())
//                    startActivity(intent)                }
//            }
//            else {
                val intent = Intent(requireContext(), PdfViewerActivity::class.java)
                intent.putExtra("fileUri", sharedViewModel.userData.value?.identityProofUploadPath)
                startActivity(intent)
//            }

        }

        fun fileViewerAadhaar(view: View) {
            if (sharedViewModel.userData.value?.aadhaarEnrollmentUploadSlipPath == null) {
                return
            }
//            if (sharedViewModel.userData.value?.isFrom != "login") {
//                val documentPath = sharedViewModel.userData.value?.aadhaarEnrollmentUploadSlipPath
//                if (documentPath.isNullOrEmpty()) {
//                    Toast.makeText(requireContext(), "No document found", Toast.LENGTH_SHORT).show()
//                    return
//                }
//
//                val uri = Uri.parse(documentPath)
//
//                if (documentPath.endsWith(".pdf", ignoreCase = true)) {
//                    // Open PDF in Chrome using Google Docs Viewer
//                    val pdfUrl = "https://docs.google.com/viewer?url=$uri"
//                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl))
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    intent.setPackage("com.android.chrome") // Forces it to open in Chrome if available
//
//                    try {
//                        startActivity(intent)
//                    } catch (e: Exception) {
//                        intent.setPackage(null) // Open in any available browser
//                        startActivity(intent)
//                    }
//                }
//                else {
//                    // Open Image in Chrome by using "file://" or "content://"
//                    val intent = Intent(requireContext(), PdfViewerActivity::class.java)
//                    intent.putExtra(
//                        "fileUri",
//                        uri.toString()
//                    )
//                    startActivity(intent)
//                }// update case
//            }
//            else {
                val intent = Intent(requireContext(), PdfViewerActivity::class.java)
                intent.putExtra(
                    "fileUri",
                    sharedViewModel.userData.value?.aadhaarEnrollmentUploadSlipPath
                )
                startActivity(intent)
//            }// registration case


        }

        fun uploadFileAadhaarEnrollmentSlip(view: View) {
            document = 2
            checkStoragePermission(requireContext())
        }
    }

    private fun identityProofApi() {
        viewModel.getDropDown(
            requireContext(), DropDownRequest(
                Fields(id = "identity_name"),
                model = "Identityproofs",
                type = "mobile"
            )
        )
    }

    private fun showBottomSheetDialog(type: String) {
        bottomSheetDialog = BottomSheetDialog(requireContext())
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
                if (identityProofListYes.isEmpty()) {
                    identityProofApi()
                }
                val list: ArrayList<DropDownResult> = when (aadhaarTag) {
                    1 -> {
                        identityProofList
                    }

                    0 -> {
                        identityProofListYes
                    }

                    else -> {
                        identityProofListYes
                    }
                }
                selectedList = list
                selectedTextView = mBinding?.etIdentityProof
            }

            else -> return
        }

        // Set up the adapter
        bottomSheetAdapter =
            BottomSheetAdapter(requireContext(), selectedList) { selectedItem, id ->
                // Handle state item click
                selectedTextView?.text = selectedItem
                when (type) {
                    "identity_proof" -> {
                        if (selectedItem == "Select Identity Proof") {
                            selectedTextView?.text = ""
                        } else {
                            identityProofId = id
                            sharedViewModel.userData.value?.identityProofId = identityProofId
                        }
                    }
                }
                selectedTextView?.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
                bottomSheetDialog?.dismiss()
            }

        layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rvBottomSheet.layoutManager = layoutManager
        rvBottomSheet.adapter = bottomSheetAdapter
        bottomSheetDialog?.setContentView(view)


        // Rotate drawable
        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_down)
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
        if (aadhaarTag == 2) {
            mBinding?.llParent?.let {
                showSnackbar(
                    it,
                    getString(R.string.do_you_have_aadhaar_card_)
                )
            }
            return false
        } else if (aadhaarTag == 1) {
            if (mBinding?.etAadhaarNo?.text.toString().isEmpty()) {
                mBinding?.llParent?.let {
                    showSnackbar(
                        it,
                        getString(R.string.enter_aadhaar_number)
                    )
                }
                return false
            } else if (mBinding?.checkboxConfirm?.isChecked != true) {
                mBinding?.llParent?.let {
                    showSnackbar(
                        it,
                        getString(R.string.please_select_checkbox)
                    )
                }
                return false
            }
        } else if (aadhaarTag == 0) {
            if (mBinding?.etAadhaarEnrollment?.text.toString().trim().isEmpty()) {
                mBinding?.llParent?.let {
                    showSnackbar(
                        it,
                        getString(R.string.enter_aadhaar_enrollment_number)
                    )
                }
                return false
            } else if (mBinding?.etAadhaarEnrollment?.text.toString().trim().length !in 12..16) {
                mBinding?.llParent?.let {
                    showSnackbar(
                        it,
                        getString(R.string.aadhaar_enrollment_number_must_be_between_12_and_16_digits)
                    )
                }
                return false
            } else if (mBinding?.etFileNameEnrollmentSlip?.text.toString().trim().isEmpty()) {
                mBinding?.llParent?.let {
                    showSnackbar(
                        it,
                        getString(R.string.upload_aadhaar_enrollment_slip_)
                    )
                }
                return false
            }
        }
        if (mBinding?.etIdentityProof?.text.toString().trim().isEmpty()) {
            mBinding?.llParent?.let {
                showSnackbar(
                    it,
                    getString(R.string.please_select_identity_proof)
                )
            }
            return false
        }
        if (mBinding?.etFileNameIdentityProof?.text.toString().trim().isEmpty()) {
            mBinding?.llParent?.let {
                showSnackbar(it, getString(R.string.please_upload_supporting_document))
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
                    cameraUri = Uri.fromFile(imageFile) // Get URI from file
                    imageUri = null
                    pdfUri = null
                    photoFile = imageFile
                    val fileSizeInBytes = photoFile?.length() ?: 0
                    if (isFileSizeWithinLimit(fileSizeInBytes, 500.0)) { // 500 KB limit
                    } else {
                        compressFile(photoFile!!) // Compress if size exceeds limit
                    }
                    uploadImage(photoFile!!)
                }

                PICK_IMAGE -> {
                    imageUri = data?.data
                    cameraUri = null
                    pdfUri = null
                    val selectedImageUri = data?.data
                    if (selectedImageUri != null) {
                        val uriPathHelper = URIPathHelper()
                        val filePath = uriPathHelper.getPath(requireContext(), selectedImageUri)

                        val fileExtension =
                            filePath?.substringAfterLast('.', "").orEmpty().lowercase()
                        if (fileExtension in listOf("png", "jpg", "jpeg")) {
                            val file = filePath?.let { File(it) }
                            val fileSizeInBytes = file?.length() ?: 0
                            if (isFileSizeWithinLimit(fileSizeInBytes, 500.0)) { // 500 KB limit
                            } else {
                                compressFile(file!!) // Compress if size exceeds limit
                            }
                            uploadImage(file!!)
                        } else {
                            mBinding?.llParent?.let {
                                showSnackbar(
                                    it,
                                    getString(R.string.format_not_supported)
                                )
                            }
                        }
                    }
                }

                REQUEST_iMAGE_PDF -> {
                    pdfUri = data?.data
                    cameraUri = null
                    imageUri = null
                    data?.data?.let { uri ->
                        val projection = arrayOf(
                            MediaStore.MediaColumns.DISPLAY_NAME,
                            MediaStore.MediaColumns.SIZE
                        )

                        val cursor = requireContext().contentResolver.query(
                            uri,
                            projection,
                            null,
                            null,
                            null
                        )
                        cursor?.use {
                            if (it.moveToFirst()) {
                                val documentName =
                                    it.getString(it.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME))
                                val fileSizeInBytes =
                                    it.getLong(it.getColumnIndex(MediaStore.MediaColumns.SIZE))
                                if (isFileSizeWithinLimit(fileSizeInBytes, 500.0)) { // 500 KB limit
                                    uploadDocument(documentName, uri)
                                } else {
                                    mBinding?.llParent?.let {
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
        uploadFileApi()
    }

    private fun uploadDocument(documentName: String?, uri: Uri) {
        val requestBody = convertToRequestBody(requireContext(), uri)
        body = MultipartBody.Part.createFormData(
            "document",
            documentName,
            requestBody
        )
        uploadFileApi()
    }

    private fun uploadFileApi() {
        if (document == 1) {
            viewModel.uploadFile(
                requireContext(),
                EncryptionModel.aesEncrypt("identitity_proof_file")
                    .toRequestBody(MultipartBody.FORM),
                EncryptionModel.aesEncrypt("mobile").toRequestBody(MultipartBody.FORM),
                body
            )
        } else if (document == 2) {
            viewModel.uploadFile(
                requireContext(),
                EncryptionModel.aesEncrypt("aadhar_enrollment_slip")
                    .toRequestBody(MultipartBody.FORM),
                EncryptionModel.aesEncrypt("mobile").toRequestBody(MultipartBody.FORM),
                body
            )
        }
    }
}
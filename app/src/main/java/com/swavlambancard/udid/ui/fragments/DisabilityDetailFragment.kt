package com.swavlambancard.udid.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.swavlambancard.udid.R
import com.swavlambancard.udid.databinding.FragmentDisabilityDetailsBinding
import com.swavlambancard.udid.model.DropDownResult
import com.swavlambancard.udid.ui.adapter.BottomSheetAdapter
import com.swavlambancard.udid.utilities.BaseFragment
import com.swavlambancard.udid.utilities.URIPathHelper
import com.swavlambancard.udid.utilities.Utility.rotateDrawable
import com.swavlambancard.udid.utilities.Utility.showSnackbar
import com.swavlambancard.udid.viewModel.SharedDataViewModel
import com.swavlambancard.udid.viewModel.ViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.Calendar


class DisabilityDetailFragment : BaseFragment<FragmentDisabilityDetailsBinding>() {
    private lateinit var sharedViewModel: SharedDataViewModel
    private var viewModel = ViewModel()
    private var mBinding: FragmentDisabilityDetailsBinding? = null
    private var bottomSheetDialog: BottomSheetDialog? = null
    private var bottomSheetAdapter: BottomSheetAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    var body: MultipartBody.Part? = null
    var date: String? = null
    private var disabilityByBirthTag: Int = 0
    private var disabilityCertificateTag : Int = 0
    private var disabilityTypeList = ArrayList<DropDownResult>()
    private var disabilityTypeId: String? = null
    private var disabilitySinceList = ArrayList<DropDownResult>()
    private var disabilitySinceId: String? = null
    private val disabilityDueToList = listOf(
        DropDownResult(id = "0", name = "Select Disability Due To"),
        DropDownResult(id = "1", name = "Accident"),
        DropDownResult(id = "2", name = "Congenital"),
        DropDownResult(id = "3", name = "Diseases"),
        DropDownResult(id = "4", name = "Hereditary"),
        DropDownResult(id = "5", name = "Infection"),
        DropDownResult(id = "6", name = "Medicine")
    )
    private var disabilityDueToId: String? = null
    private val detailsOfIssuingAuthorityList = listOf(
        DropDownResult(id = "0", name = "Select Issuing Authority"),
        DropDownResult(id = "1", name = "Chief Medical Office"),
        DropDownResult(id = "2", name = "Medical Authority"),
    )
    private var detailsOfIssuingAuthorityId: String? = null
    override val layoutId: Int
        get() = R.layout.fragment_disability_details

    override fun init() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
        viewModel.init()
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedDataViewModel::class.java)
//        sharedViewModel.userData.observe(viewLifecycleOwner) { userData ->
//            mBinding?.tvDisabilityType?.setText(userData.disabilityType)
//            if (!userData.disabilityType.isNullOrEmpty()) {
//                mBinding?.tvDisabilityType?.setTextColor(Color.parseColor("#000000"))
//            }
//            mBinding?.tvDueTo?.setText(userData.disabilityDue)
//            if (!userData.disabilityDue.isNullOrEmpty()) {
//                mBinding?.tvDueTo?.setTextColor(Color.parseColor("#000000"))
//            }
//            if (userData.disabilityBirth == 1) {
//                mBinding?.rbYes?.isChecked = true
//                mBinding?.tvApplicantMotherName?.hideView()
//                mBinding?.tvDisabilitySince?.hideView()
//            } else if (userData.disabilityBirth == 2) {
//                mBinding?.rbNo?.isChecked = true
//                mBinding?.tvApplicantMotherName?.showView()
//                mBinding?.tvDisabilitySince?.showView()
//            } else {
//                mBinding?.rbYes?.isChecked = false
//                mBinding?.rbNo?.isChecked = false
//                mBinding?.tvApplicantMotherName?.hideView()
//                mBinding?.tvDisabilitySince?.hideView()
//            }
//            mBinding?.tvDisabilitySince?.setText(userData.disabilitySince)
//            if (!userData.disabilitySince.isNullOrEmpty()) {
//                mBinding?.tvDisabilitySince?.setTextColor(Color.parseColor("#000000"))
//            }
//        }
//        if (mBinding?.rbNo?.isChecked == true) {
//            mBinding?.tvApplicantMotherName?.showView()
//            mBinding?.tvDisabilitySince?.showView()
//        } else {
//            mBinding?.tvApplicantMotherName?.hideView()
//            mBinding?.tvDisabilitySince?.hideView()
//        }
//        mBinding?.rgBirth?.setOnCheckedChangeListener { group, checkedId ->
//            when (checkedId) {
//                R.id.rbYes -> {
//                    mBinding?.tvApplicantMotherName?.hideView()
//                    mBinding?.tvDisabilitySince?.hideView()
//                    gender = 1
//                }
//                R.id.rbNo -> {
//                    mBinding?.tvApplicantMotherName?.showView()
//                    mBinding?.tvDisabilitySince?.showView()
//                    gender = 2
//                }
//
//                else -> {
//                    gender = 0
//                }
//            }
//            sharedViewModel.userData.value?.disabilityBirth = gender
//        }
//        mBinding?.tvDisabilityType?.addTextChangedListener {
//            sharedViewModel.userData.value?.disabilityType = it.toString()
//        }
//        mBinding?.tvDueTo?.addTextChangedListener {
//            sharedViewModel.userData.value?.disabilityDue = it.toString()
//        }
//        mBinding?.tvDisabilitySince?.addTextChangedListener {
//            sharedViewModel.userData.value?.disabilitySince = it.toString()
//        }
//        mBinding?.tvDisabilityType?.setOnClickListener {
//            showBottomSheetDialog("Type")
//        }
//        mBinding?.tvDueTo?.setOnClickListener {
//            showBottomSheetDialog("Due")
//        }
//        mBinding?.tvDisabilitySince?.setOnClickListener {
//            showBottomSheetDialog("Since")
//        }
    }

    override fun setVariables() {
        mBinding?.rgDisabilityByBirth?.setOnCheckedChangeListener { _, checkedId ->
            disabilityByBirthTag = when (checkedId) {
                R.id.rbYes -> {
                    1
                }

                R.id.rbNo -> {
                    2
                }

                else -> {
                    0
                }
            }
        }
        mBinding?.rgDisabilityCertificate?.setOnCheckedChangeListener { _, checkedId ->
            disabilityCertificateTag = when (checkedId) {
                R.id.rbDisabilityCertificateYes -> {
                    1
                }

                R.id.rbDisabilityCertificateNo -> {
                    2
                }

                else -> {
                    0
                }
            }
        }
    }

    override fun setObservers() {
//        viewModel.dropDownResult.observe(this) {
//            val userResponseModel = it
//            if (userResponseModel?._result != null && userResponseModel._result.isNotEmpty()) {
//                when (userResponseModel.model) {
//                    "States" -> {
//                        disabilityTypeList.clear()
//                        disabilityTypeList.add(DropDownResult("0", getString(R.string.select_state_name)))
//                        disabilityTypeList.addAll(userResponseModel._result)
//                    }
//                }
//                bottomSheetAdapter?.notifyDataSetChanged()
//            }
//        }

        viewModel.errors.observe(this) {
            mBinding?.let { it1 -> showSnackbar(it1.llParent, it) }
        }
    }

    inner class ClickActions {
        fun next(view: View) {
            if (valid()) {
                mBinding?.llParent?.let { showSnackbar(it, "Done OTP") }
            }
        }
        fun uploadFile(view: View) {
            checkStoragePermission(requireContext())
        }
        fun dateOfIssuanceOfCertificate(view: View) {
            mBinding?.etDateOfIssuanceOfCertificate?.let { calenderOpen(requireContext(), it) }
        }
        fun disabilityType(view: View) {
            showBottomSheetDialog("disabilityType")
        }

        fun disabilityDueTo(view: View) {
            showBottomSheetDialog("disabilityDueTo")
        }

        fun disabilitySince(view: View) {
            showBottomSheetDialog("disabilitySince")
        }

        fun detailsOfIssuingAuthority(view: View) {
            showBottomSheetDialog("detailsOfIssuingAuthority")
        }
    }

    private fun calenderOpen(
        context: Context,
        editText: TextView,
    ) {
        val cal: Calendar = Calendar.getInstance()

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

        val selectedList: List<DropDownResult>
        val selectedTextView: TextView?
        when (type) {
            "disabilityType" -> {
//                if (disabilityTypeList.isEmpty()) {
//                    stateListApi()
//                }
                selectedList = disabilityTypeList
                selectedTextView = mBinding?.etDisabilityType
            }
            "disabilityDueTo" -> {
                selectedList = disabilityDueToList
                selectedTextView = mBinding?.etDisabilityDueTo
            }

            "disabilitySince" -> {
                selectedList = disabilitySinceList
                selectedTextView = mBinding?.etDisabilitySince
            }
            "detailsOfIssuingAuthority" -> {
                selectedList = detailsOfIssuingAuthorityList
                selectedTextView = mBinding?.etSelectIssuingAuthority
            }

            else -> return
        }

        bottomSheetAdapter =
            BottomSheetAdapter(requireContext(), selectedList) { selectedItem, id ->
                selectedTextView?.text = selectedItem
                when (type) {
                    "disabilityType" -> {
                        if (selectedItem == "Select State Name") {
                            selectedTextView?.text = ""
                        } else {
                            disabilityTypeId = id
                        }
                    }
                    "disabilityDueTo" -> {
                        if (selectedItem == "Select Disability Due To") {
                            selectedTextView?.text = ""
                        } else {
                            disabilityDueToId = id
                        }
                    }

                    "disabilitySince" -> {
                        if (selectedItem == "Select Disability Since") {
                            selectedTextView?.text = ""
                        } else {
                            disabilitySinceId = id
                        }
                    }
                    "detailsOfIssuingAuthority" -> {
                        if (selectedItem == "Select Issuing Authority") {
                            selectedTextView?.text = ""
                        } else {
                            detailsOfIssuingAuthorityId = id
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
        if (mBinding?.etDisabilityType?.text?.toString().isNullOrEmpty()) {
            mBinding?.llParent?.let { showSnackbar(it, "Please select Disability type.") }
            return false
        }
        else if (disabilityByBirthTag == 0) {
            mBinding?.llParent?.let { showSnackbar(it, "Please Check Disability by Birth yes/no.") }
            return false
        }
        else if(disabilityByBirthTag == 2) {
            if (mBinding?.etDisabilityDueTo?.text?.toString().isNullOrEmpty()) {
                mBinding?.llParent?.let { showSnackbar(it, "Please select Disability Since.") }
                return false
            }
        }
        else if (disabilityCertificateTag == 0) {
            mBinding?.llParent?.let { showSnackbar(it, "Please select Do you have disability Certificate yes/no.") }
            return false
        }
        else if(disabilityCertificateTag == 1) {
            if (mBinding?.etFileName?.text.toString().isEmpty()) {
                mBinding?.llParent?.let {
                    showSnackbar(it, "Please Upload Photo")
                }
                return false
            }
            else if (mBinding?.etRegistrationNoOfCertificate?.text?.toString().isNullOrEmpty()) {
                mBinding?.llParent?.let { showSnackbar(it, "Please enter sr. no. / registration no. of certificate.") }
                return false
            }
            else if (mBinding?.etDateOfIssuanceOfCertificate?.text?.toString().isNullOrEmpty()) {
                mBinding?.llParent?.let { showSnackbar(it, "Please select date of issuance of certificate.") }
                return false
            }
            else if (mBinding?.etSelectIssuingAuthority?.text?.toString().isNullOrEmpty()) {
                mBinding?.llParent?.let { showSnackbar(it, "Please select details of issuing authority.") }
                return false
            }
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
//                            uploadImage(photoFile!!)
                    } else {
                        compressFile(photoFile!!) // Compress if size exceeds limit
                        mBinding?.etFileName?.text = photoFile?.name
//                            uploadImage(photoFile!!)
                    }
                }

                PICK_IMAGE -> {
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
                                mBinding?.etFileName?.text = file?.name
//                                    uploadImage(file!!)
                            } else {
                                compressFile(file!!) // Compress if size exceeds limit
                                mBinding?.etFileName?.text = file.name
//                                    uploadImage(file)
                            }
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
//                                    uploadDocument(documentName, uri)
                                    mBinding?.etFileName?.text = documentName
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
                    "address_proof_file",
                    file.name, reqFile
                )
        }
    }

    private fun uploadDocument(documentName: String?, uri: Uri) {
        val requestBody = convertToRequestBody(requireContext(), uri)
        body = MultipartBody.Part.createFormData(
            "address_proof_file",
            documentName,
            requestBody
        )
    }
}
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
import com.swavlambancard.udid.model.CodeDropDownRequest
import com.swavlambancard.udid.model.DropDownRequest
import com.swavlambancard.udid.model.DropDownResult
import com.swavlambancard.udid.model.Fields
import com.swavlambancard.udid.ui.activity.PersonalProfileActivity
import com.swavlambancard.udid.ui.adapter.BottomSheetAdapter
import com.swavlambancard.udid.ui.adapter.MultipleSelectionBottomSheetAdapter
import com.swavlambancard.udid.utilities.BaseFragment
import com.swavlambancard.udid.utilities.EncryptionModel
import com.swavlambancard.udid.utilities.URIPathHelper
import com.swavlambancard.udid.utilities.Utility.rotateDrawable
import com.swavlambancard.udid.utilities.Utility.showSnackbar
import com.swavlambancard.udid.utilities.hideView
import com.swavlambancard.udid.utilities.showView
import com.swavlambancard.udid.viewModel.SharedDataViewModel
import com.swavlambancard.udid.viewModel.ViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.Calendar

class DisabilityDetailFragment : BaseFragment<FragmentDisabilityDetailsBinding>() {
    private lateinit var sharedViewModel: SharedDataViewModel
    private var viewModel = ViewModel()
    private var mBinding: FragmentDisabilityDetailsBinding? = null
    private var bottomSheetDialog: BottomSheetDialog? = null
    private var bottomSheetAdapter: BottomSheetAdapter? = null
    private var multipleSelectionBottomSheetAdapter: MultipleSelectionBottomSheetAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    var body: MultipartBody.Part? = null
    private var disabilityCertificateName: String? = null
    private var listName: String? = null
    var date: String? = null
    private var disabilityByBirthTag: Int = 0
    private var disabilityCertificateTag: Int = 0
    private var disabilityTypeList = ArrayList<DropDownResult>()
    private var matchItemDisabilityTypeList = arrayListOf<DropDownResult>()
    private var disabilityTypeId = arrayListOf<String>()
    private var disabilitySinceList = ArrayList<DropDownResult>()
    private var disabilitySinceId: String? = null
    private val disabilityDueToList = ArrayList<DropDownResult>()
    private var disabilityDueToId: String? = null
    private val detailsOfIssuingAuthorityList = ArrayList<DropDownResult>()
    private var detailsOfIssuingAuthorityId: String? = null
    override val layoutId: Int
        get() = R.layout.fragment_disability_details

    override fun init() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
        viewModel.init()
        sharedViewModel = ViewModelProvider(requireActivity())[SharedDataViewModel::class.java]
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
        viewModel.dropDownResult.observe(this) {
            val userResponseModel = it
            if (userResponseModel?._result != null && userResponseModel._result.isNotEmpty()) {
                when (userResponseModel.model) {
                    "Disabilitytypes" -> {
                        disabilityTypeList.clear()
                        disabilityTypeList.addAll(userResponseModel._result)
                    }
                }
                multipleSelectionBottomSheetAdapter?.notifyDataSetChanged()
            }
        }

        viewModel.codeDropDownResult.observe(this) {
            val userResponseModel = it
            if (userResponseModel?._result != null && userResponseModel._result.isNotEmpty()) {
                if (listName == "disabilityDueTo") {
                    disabilityDueToList.clear()
                    disabilityDueToList.add(
                        DropDownResult(
                            "0",
                            getString(R.string.select_disability_due_to)
                        )
                    )
                    disabilityDueToList.addAll(userResponseModel._result)

                } else if (listName == "disabilitySince") {
                    disabilitySinceList.clear()
                    disabilitySinceList.add(
                        DropDownResult(
                            "0",
                            getString(R.string.select_disability_since)
                        )
                    )
                    disabilitySinceList.addAll(userResponseModel._result)

                } else if (listName == "detailsOfIssuingAuthority") {
                    detailsOfIssuingAuthorityList.clear()
                    detailsOfIssuingAuthorityList.add(
                        DropDownResult(
                            "0",
                            getString(R.string.select_issuing_authority)
                        )
                    )
                    detailsOfIssuingAuthorityList.addAll(userResponseModel._result)

                }
                bottomSheetAdapter?.notifyDataSetChanged()
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
                    disabilityCertificateName = userResponseModel._result.file_name
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
                    HospitalAssessmentFragment()
                )
            }
        }

        fun back(view: View) {
            (requireActivity() as PersonalProfileActivity).replaceFragment(ProofOfIDFragment())
        }

        fun uploadFile(view: View) {
            checkStoragePermission(requireContext())
        }

        fun rbYes(view: View) {
            mBinding?.tvDisabilitySince?.hideView()
            mBinding?.etDisabilitySince?.hideView()
        }

        fun rbNo(view: View) {
            mBinding?.tvDisabilitySince?.showView()
            mBinding?.etDisabilitySince?.showView()
        }

        fun rbDisabilityCertificateYes(view: View) {
            mBinding?.llDisabilityCertificateYes?.showView()
        }

        fun rbDisabilityCertificateNo(view: View) {
            mBinding?.llDisabilityCertificateYes?.hideView()
        }

        fun dateOfIssuanceOfCertificate(view: View) {
            mBinding?.etDateOfIssuanceOfCertificate?.let { calenderOpen(requireContext(), it) }
        }

        fun disabilityType(view: View) {
            selectBottomDialog()
        }

        fun disabilityDueTo(view: View) {
            listName = "disabilityDueTo"
            showBottomSheetDialog("disabilityDueTo")
        }

        fun disabilitySince(view: View) {
            listName = "disabilitySince"
            showBottomSheetDialog("disabilitySince")
        }

        fun detailsOfIssuingAuthority(view: View) {
            listName = "detailsOfIssuingAuthority"
            showBottomSheetDialog("detailsOfIssuingAuthority")
        }
    }

    private fun disabilityTypeApi() {
        viewModel.getDropDown(
            requireContext(), DropDownRequest(
                model = "Disabilitytypes",
                fields = Fields(id = "disability_type"),
                type = "mobile"
            )
        )
    }

    private fun disabilityDueToApi() {
        viewModel.getCodeDropDown(
            requireContext(), CodeDropDownRequest(
                listname = "getDisabilityDueto",
                type = "mobile"
            )
        )
    }

    private fun issuingAuthorityApi() {
        viewModel.getCodeDropDown(
            requireContext(), CodeDropDownRequest(
                listname = "getAuthority",
                type = "mobile"
            )
        )
    }

    private fun disabilitySinceApi() {
        viewModel.getCodeDropDown(
            requireContext(), CodeDropDownRequest(
                listname = "getYearlist",
                type = "mobile"
            )
        )
    }

    private fun selectBottomDialog() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_state, null)

        val tvClose = view.findViewById<TextView>(R.id.tvClose)
        if (disabilityTypeList.isEmpty()) {
            disabilityTypeApi()
        }
        setAdapter(view, disabilityTypeList)
        tvClose.setOnClickListener {
            dialog.dismiss()
            matchItemDisabilityTypeList = multipleSelectionBottomSheetAdapter?.selectedItems ?: matchItemDisabilityTypeList
            if (matchItemDisabilityTypeList.size < 0)
                mBinding?.etDisabilityType?.text = matchItemDisabilityTypeList.joinToString(", ") { it.name }
             else {
                mBinding?.etDisabilityType?.hint = getString(R.string.choose_disability_types)
            }
        }
        dialog.setCancelable(false)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun setAdapter(
        view: View,
        list: ArrayList<DropDownResult>,

        ) {
        val rvBottomSheet = view.findViewById<RecyclerView>(R.id.rvBottomSheet)
        layoutManager = LinearLayoutManager(requireContext())
        rvBottomSheet.layoutManager = layoutManager
        multipleSelectionBottomSheetAdapter = MultipleSelectionBottomSheetAdapter(
            requireContext(),
            list,
            matchItemDisabilityTypeList,
        ) {
            if (disabilityTypeId.contains(it)) {
                disabilityTypeId.remove(it)
            } else {
                disabilityTypeId.add(it)
            }
        }
        rvBottomSheet.adapter = multipleSelectionBottomSheetAdapter
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
            "disabilityDueTo" -> {
                if (disabilityDueToList.isEmpty()) {
                    disabilityDueToApi()
                }
                selectedList = disabilityDueToList
                selectedTextView = mBinding?.etDisabilityDueTo
            }

            "disabilitySince" -> {
                if (disabilitySinceList.isEmpty()) {
                    disabilitySinceApi()
                }
                selectedList = disabilitySinceList
                selectedTextView = mBinding?.etDisabilitySince
            }

            "detailsOfIssuingAuthority" -> {
                if (detailsOfIssuingAuthorityList.isEmpty()) {
                    issuingAuthorityApi()
                }
                selectedList = detailsOfIssuingAuthorityList
                selectedTextView = mBinding?.etSelectIssuingAuthority
            }

            else -> return
        }

        bottomSheetAdapter =
            BottomSheetAdapter(requireContext(), selectedList) { selectedItem, id ->
                selectedTextView?.text = selectedItem
                when (type) {
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
            mBinding?.llParent?.let {
                showSnackbar(
                    it,
                    getString(R.string.please_select_disability_type)
                )
            }
            return false
        } else if (disabilityByBirthTag == 0) {
            mBinding?.llParent?.let {
                showSnackbar(
                    it,
                    getString(R.string.please_check_disability_by_birth_yes_no)
                )
            }
            return false
        } else if (disabilityByBirthTag == 2) {
            if (mBinding?.etDisabilityDueTo?.text?.toString().isNullOrEmpty()) {
                mBinding?.llParent?.let {
                    showSnackbar(
                        it,
                        getString(R.string.please_select_disability_since)
                    )
                }
                return false
            }
        } else if (disabilityCertificateTag == 0) {
            mBinding?.llParent?.let {
                showSnackbar(
                    it,
                    getString(R.string.please_select_do_you_have_disability_certificate_yes_no)
                )
            }
            return false
        } else if (disabilityCertificateTag == 1) {
            if (mBinding?.etFileName?.text.toString().isEmpty()) {
                mBinding?.llParent?.let {
                    showSnackbar(it, "Please Upload Photo")
                }
                return false
            } else if (mBinding?.etRegistrationNoOfCertificate?.text?.toString().isNullOrEmpty()) {
                mBinding?.llParent?.let {
                    showSnackbar(
                        it,
                        getString(R.string.please_enter_sr_no_registration_no_of_certificate)
                    )
                }
                return false
            } else if (mBinding?.etDateOfIssuanceOfCertificate?.text?.toString().isNullOrEmpty()) {
                mBinding?.llParent?.let {
                    showSnackbar(
                        it,
                        getString(R.string.please_select_date_of_issuance_of_certificate)
                    )
                }
                return false
            } else if (mBinding?.etSelectIssuingAuthority?.text?.toString().isNullOrEmpty()) {
                mBinding?.llParent?.let {
                    showSnackbar(
                        it,
                        getString(R.string.please_select_details_of_issuing_authority)
                    )
                }
                return false
            } else if (mBinding?.etDisabilityPercentage?.text.toString().trim().isNotEmpty() &&
                (mBinding?.etDisabilityPercentage?.text.toString() > 0.toString() || mBinding?.etDisabilityPercentage?.text.toString() < 100.toString())
            )
                mBinding?.llParent?.let {
                    showSnackbar(
                        it,
                        getString(R.string.enter_a_number_between_1_and_100)
                    )
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
                    val fileSizeInBytes = photoFile?.length() ?: 0
                    if (isFileSizeWithinLimit(fileSizeInBytes, 500.0)) { // 500 KB limit
                        mBinding?.etFileName?.text = photoFile?.name
                    } else {
                        compressFile(photoFile!!) // Compress if size exceeds limit
                        mBinding?.etFileName?.text = photoFile?.name
                    }
                    uploadImage(photoFile!!)
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
                            } else {
                                compressFile(file!!) // Compress if size exceeds limit
                                mBinding?.etFileName?.text = file.name
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
                    "document",
                    file.name, reqFile
                )
        }
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
        viewModel.uploadFile(
            requireContext(),
            EncryptionModel.aesEncrypt("disability_cert_doc").toRequestBody(MultipartBody.FORM),
            body
        )
    }
}
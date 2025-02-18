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
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.swavlambancard.udid.R
import com.swavlambancard.udid.databinding.FragmentPersonalDetailsBinding
import com.swavlambancard.udid.model.DropDownRequest
import com.swavlambancard.udid.model.DropDownResult
import com.swavlambancard.udid.model.Fields
import com.swavlambancard.udid.ui.activity.PersonalProfileActivity
import com.swavlambancard.udid.ui.adapter.BottomSheetAdapter
import com.swavlambancard.udid.utilities.BaseFragment
import com.swavlambancard.udid.utilities.URIPathHelper
import com.swavlambancard.udid.utilities.Utility.rotateDrawable
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
import java.io.File
import java.util.Calendar

class PersonalDetailFragment : BaseFragment<FragmentPersonalDetailsBinding>() {

    private lateinit var sharedViewModel: SharedDataViewModel
    private var viewModel = ViewModel()
    private var mBinding: FragmentPersonalDetailsBinding? = null
    private var bottomSheetDialog: BottomSheetDialog? = null
    private var bottomSheetAdapter: BottomSheetAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    var body: MultipartBody.Part? = null
    var isEditable = false
    private var document = 0
    private var genderTag: Int = 0
    private var stateList = ArrayList<DropDownResult>()
    private var stateId: String? = null
    var date: String? = null
    private var guardianId: String? = null
    private val guardianList = listOf(
        DropDownResult(id = "0", name = "Select Relation with PWD"),
        DropDownResult(id = "1", name = "Father"),
        DropDownResult(id = "2", name = "Mother"),
        DropDownResult(id = "3", name = "Guardian")
    )
    private var relationWithPersonId: String? = null
    private val relationWithPersonList = listOf(
        DropDownResult(id = "0", name = "Select Relation with PWD"),
        DropDownResult(id = "1", name = "Husband"),
        DropDownResult(id = "2", name = "Wife"),
        DropDownResult(id = "3", name = "Brother"),
        DropDownResult(id = "4", name = "Sister"),
        DropDownResult(id = "5", name = "Daughter"),
        DropDownResult(id = "6", name = "Uncle"),
        DropDownResult(id = "7", name = "Aunty"),
        DropDownResult(id = "8", name = "Other")
    )

    override val layoutId: Int
        get() = R.layout.fragment_personal_details

    override fun init() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
        viewModel.init()
        sharedViewModel = ViewModelProvider(requireActivity())[SharedDataViewModel::class.java]
//        sharedViewModel.userData.value?.applicantFullName = mBinding?.etApplicantFullName?.text.toString().trim()
        sharedViewModel.userData.observe(viewLifecycleOwner) { userData ->
            mBinding?.etApplicantFullName?.setText(userData.applicantFullName)
        }
//            mBinding?.etApplicantFullName?.setText(userData.applicantFullName)
//            mBinding?.etApplicantMobileNo?.setText(userData.applicantMobileNo)
//            mBinding?.etApplicantEmailId?.setText(userData.applicantEmail)
//            Log.d("DOB",userData.applicantDob)
//            mBinding?.etApplicantDateOfBirth?.text = userData.applicantDob
////                convertDate(userData.applicantDob)
//            mBinding?.etApplicantDateOfBirth?.text = userData.applicantDob
//            mBinding?.tvGuardian?.setText(userData.guardian)
//            if(!userData.guardian.isNullOrEmpty()){
//                mBinding?.tvGuardian?.setTextColor(Color.parseColor("#000000"))
//            }
//            when (userData.gender) {
//                1 -> {
//                    mBinding?.rbMale?.isChecked=true
//                }
//                2 -> {
//                    mBinding?.rbFemale?.isChecked=true
//                }
//                3 -> {
//                    mBinding?.rbTransgender?.isChecked=true
//                }
//                else -> {
//                    mBinding?.rbMale?.isChecked=false
//                    mBinding?.rbFemale?.isChecked=false
//                    mBinding?.rbTransgender?.isChecked=false
//                }
//            }
//
//        }
//
//        // Save data when fields change
          mBinding?.etApplicantFullName?.addTextChangedListener {
                sharedViewModel.userData.value?.applicantFullName = it.toString()
          }
//        mBinding?.etApplicantMobileNo?.addTextChangedListener {
//            sharedViewModel.userData.value?.applicantMobileNo = it.toString()
//        }
//
//        mBinding?.etApplicantEmailId?.addTextChangedListener {
//            sharedViewModel.userData.value?.applicantEmail = it.toString()
//        }
//
////        mBinding?.etApplicantDateOfBirth?.addTextChangedListener {
////            sharedViewModel.userData.value?.applicantDob = etApplicantDateOfBirth.toString()
////        }
//
//        mBinding?.tvGuardian?.addTextChangedListener {
//            sharedViewModel.userData.value?.guardian = it.toString()
//        }
//
//        mBinding?.rgGender?.setOnCheckedChangeListener { group, checkedId ->
//            gender =when (checkedId) {
//                R.id.rbMale -> 1
//                R.id.rbFemale -> 2
//                R.id.rbTransgender -> 3
//                else->0
//            }
//            sharedViewModel.userData.value?.gender =gender
//        }
//
//        mBinding?.etApplicantDateOfBirth?.setOnClickListener {
//            openCalendar("etApplicantDateOfBirth", mBinding?.etApplicantDateOfBirth!!)
//        }
//        mBinding?.tvGuardian?.setOnClickListener {
//            showBottomSheetDialog("Guardian")
//        }
//        mBinding?.tvChooseFile?.setOnClickListener {
//            sign=0
//            checkStoragePermission(requireContext())
//        }
//        mBinding?.tvChooseFileSign?.setOnClickListener {
//            sign=1
//            checkStoragePermission(requireContext())
//        }

    }

    override fun setVariables() {
        mBinding?.rgGender?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbMale -> {
                    genderTag = 1
                }

                R.id.rbFemale -> {
                    genderTag = 2
                }

                R.id.rbTransgender -> {
                    genderTag = 3
                }

                else -> {
                    genderTag = 0
                }
            }
        }
    }

    override fun setObservers() {
        viewModel.dropDownResult.observe(this) {
            val userResponseModel = it
            if (userResponseModel?._result != null && userResponseModel._result.isNotEmpty()) {
                when (userResponseModel.model) {
                    "States" -> {
                        stateList.clear()
                        stateList.add(DropDownResult("0", getString(R.string.select_state_name)))
                        stateList.addAll(userResponseModel._result)
                    }
                }
                bottomSheetAdapter?.notifyDataSetChanged()
            }
        }

        viewModel.errors.observe(this) {
            mBinding?.let { it1 -> showSnackbar(it1.llParent, it) }
        }
    }

    inner class ClickActions {
        fun next(view: View) {
            (requireActivity() as PersonalProfileActivity).replaceFragment(ProofOfIDFragment())
//            if (valid()) {
//                mBinding?.llParent?.let { showSnackbar(it, "Done OTP") }
//            }
        }

        fun edit(view: View){
            isEditable = !isEditable  // Toggle state

            mBinding?.etApplicantNameInRegionalLanguage?.isClickable = isEditable
            mBinding?.etApplicantNameInRegionalLanguage?.isFocusable = isEditable
            mBinding?.etApplicantNameInRegionalLanguage?.isFocusableInTouchMode = isEditable  // Required for text input

            if (isEditable) {
                mBinding?.etApplicantNameInRegionalLanguage?.requestFocus()  // Focus if enabled
            } else {
                mBinding?.etApplicantNameInRegionalLanguage?.clearFocus()  // Remove focus if disabled
            }
        }

        fun uploadFilePhoto(view: View) {
            document = 1
            isFrom = 1
            checkStoragePermission(requireContext())
        }

        fun uploadFileSignature(view: View) {
            document = 2
            isFrom = 1
            checkStoragePermission(requireContext())
        }

        fun state(view: View) {
            showBottomSheetDialog("state")
        }

        fun guardian(view: View) {
            showBottomSheetDialog("guardian")
        }

        fun relationWithPerson(view: View) {
            showBottomSheetDialog("relationWithPerson")
        }

        fun dateOfBirth(view: View) {
            mBinding?.etApplicantDateOfBirth?.let { calenderOpen(requireContext(), it) }
        }
    }

    private fun stateListApi() {
        viewModel.getDropDown(
            requireContext(), DropDownRequest(
                model = "States",
                fields = Fields(state_code = "name"),
                type = "mobile"
            )
        )
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
            "state" -> {
                if (stateList.isEmpty()) {
                    stateListApi()
                }
                selectedList = stateList
                selectedTextView = mBinding?.etStateName
            }
            "guardian" -> {
                selectedList = guardianList
                selectedTextView = mBinding?.etApplicantsFMGName
            }

            "relationWithPerson" -> {
                selectedList = relationWithPersonList
                selectedTextView = mBinding?.etRelationWithPerson
            }

            else -> return
        }

        bottomSheetAdapter =
            BottomSheetAdapter(requireContext(), selectedList) { selectedItem, id ->
                selectedTextView?.text = selectedItem
                when (type) {
                    "state" -> {
                        if (selectedItem == "Select State Name") {
                            selectedTextView?.text = ""
                        } else {
                            stateId = id
                            mBinding?.tvApplicantNameInRegionalLanguage?.showView()
                            mBinding?.etApplicantNameInRegionalLanguage?.showView()
                        }
                    }
                    "guardian" -> {
                        when (selectedItem) {
                            "Select Relation with PWD" -> {
                                selectedTextView?.text = ""
                                mBinding?.llParentInfo?.hideView()
                            }

                            "Father" -> {
                                guardianId = id
                                mBinding?.llParentInfo?.showView()
                                mBinding?.tvRelationWithPerson?.hideView()
                                mBinding?.etRelationWithPerson?.hideView()
                                mBinding?.tvApplicantNameFMG?.text =
                                    getString(R.string.applicant_father_s_name)
                                mBinding?.etApplicantNameFMG?.hint =
                                    getString(R.string.applicant_father_s_name_)
                            }

                            "Mother" -> {
                                guardianId = id
                                mBinding?.llParentInfo?.showView()
                                mBinding?.tvRelationWithPerson?.hideView()
                                mBinding?.etRelationWithPerson?.hideView()
                                mBinding?.tvApplicantNameFMG?.text =
                                    getString(R.string.applicant_mother_s_name)
                                mBinding?.etApplicantNameFMG?.hint =
                                    getString(R.string.applicant_mother_s_name_)
                            }

                            "Guardian" -> {
                                guardianId = id
                                mBinding?.llParentInfo?.showView()
                                mBinding?.tvRelationWithPerson?.showView()
                                mBinding?.etRelationWithPerson?.showView()
                                mBinding?.tvApplicantNameFMG?.text =
                                    getString(R.string.name_of_guardian_caretaker)
                                mBinding?.etApplicantNameFMG?.hint =
                                    getString(R.string.name_of_guardian_caretaker_)
                            }
                        }
//                        context?.toast(guardianId.toString())
                    }

                    "relationWithPerson" -> {
                        if (selectedItem == "Select Relation with PWD") {
                            selectedTextView?.text = ""
                        } else {
                            relationWithPersonId = id
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
        if (mBinding?.etApplicantFullName?.text?.toString().isNullOrEmpty()) {
            mBinding?.llParent?.let { showSnackbar(it,
                getString(R.string.please_enter_applicant_s_full_name)) }
            return false
        }
        else if (mBinding?.etStateName?.text.toString().trim().isEmpty()) {
            mBinding?.llParent?.let {
                showSnackbar(
                    it,
                    getString(R.string.please_select_state)
                )
            }
            return false
        }
        else if (mBinding?.etApplicantMobileNo?.text?.toString().isNullOrEmpty()) {
            mBinding?.llParent?.let {
                showSnackbar(it, getString(R.string.mobile_number_is_required))
            }
            return false
        } else if (mBinding?.etApplicantMobileNo?.text?.toString()?.length != 10) {
            mBinding?.llParent?.let {
                showSnackbar(it, getString(R.string.mobile_number_must_be_exactly_10_digits))
            }
            return false
        } else if (mBinding?.etApplicantDateOfBirth?.text?.toString().isNullOrEmpty()) {
            mBinding?.llParent?.let { showSnackbar(it, getString(R.string.please_select_date_of_birth)) }
            return false
        } else if (guardianId == "0") {
            mBinding?.llParent?.let { showSnackbar(it, getString(R.string.please_select_gender)) }
            return false
        } else if (mBinding?.etApplicantsFMGName?.text.toString().trim().isEmpty()) {
            mBinding?.llParent?.let {
                showSnackbar(
                    it,
                    getString(R.string.please_select_guardian_relation)
                )
            }
            return false
        } else if (guardianId == "1") {
            if (mBinding?.etApplicantNameFMG?.text.toString().isEmpty()) {
                mBinding?.llParent?.let { showSnackbar(it,
                    getString(R.string.please_enter_father_name)) }
                return false
            } else if (mBinding?.etContactNoOfGuardian?.text?.toString().isNullOrEmpty()) {
                mBinding?.llParent?.let { showSnackbar(it,
                    getString(R.string.please_enter_contact_number)) }
                return false
            }
            return false
        } else if (guardianId == "2") {
            if (mBinding?.etApplicantNameFMG?.text.toString().isEmpty()) {
                mBinding?.llParent?.let { showSnackbar(it,
                    getString(R.string.please_enter_mother_name)) }
                return false
            } else if (mBinding?.etContactNoOfGuardian?.text?.toString().isNullOrEmpty()) {
                mBinding?.llParent?.let { showSnackbar(it, getString(R.string.please_enter_contact_number)) }
                return false
            }
        } else if (guardianId == "3") {
            if (mBinding?.etRelationWithPerson?.text.toString().isEmpty()) {
                mBinding?.llParent?.let { showSnackbar(it,
                    getString(R.string.please_select_relation_with_person)) }
                return false
            } else if (mBinding?.etApplicantNameFMG?.text.toString().isEmpty()) {
                mBinding?.llParent?.let { showSnackbar(it,
                    getString(R.string.please_enter_guardian_name)) }
                return false
            } else if (mBinding?.etContactNoOfGuardian?.text?.toString().isNullOrEmpty()) {
                mBinding?.llParent?.let { showSnackbar(it, getString(R.string.please_enter_contact_number)) }
                return false
            }
        } else if (mBinding?.etFileNamePhoto?.text.toString().isEmpty()) {
            mBinding?.llParent?.let {
                showSnackbar(it, getString(R.string.please_upload_photo))
            }
            return false
        } else if (mBinding?.etFileNameSignature?.text.toString().isEmpty()) {
            mBinding?.llParent?.let {
                showSnackbar(it, getString(R.string.please_upload_signature))
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
                    if (document == 1) {
                        val fileSizeInBytes = photoFile?.length() ?: 0
                        if (isFileSizeWithinLimit(fileSizeInBytes, 500.0)) { // 500 KB limit
                            mBinding?.etFileNamePhoto?.text = photoFile?.name
//                            uploadImage(photoFile!!)
                        } else {
                            compressFile(photoFile!!) // Compress if size exceeds limit
                            mBinding?.etFileNamePhoto?.text = photoFile?.name
//                            uploadImage(photoFile!!)
                        }
                    } else if (document == 2) {
                        val fileSizeInBytes = photoFile?.length() ?: 0
                        if (isFileSizeWithinLimit(fileSizeInBytes, 500.0)) { // 500 KB limit
                            mBinding?.etFileNameSignature?.text = photoFile?.name
//                            uploadImage(photoFile!!)
                        } else {
                            compressFile(photoFile!!) // Compress if size exceeds limit
                            mBinding?.etFileNameSignature?.text = photoFile?.name
//                            uploadImage(photoFile!!)
                        }
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
                            if (document == 1) {
                                if (isFileSizeWithinLimit(fileSizeInBytes, 500.0)) { // 500 KB limit
                                    mBinding?.etFileNamePhoto?.text = file?.name
//                                    uploadImage(file!!)
                                } else {
                                    compressFile(file!!) // Compress if size exceeds limit
                                    mBinding?.etFileNamePhoto?.text = file.name
//                                    uploadImage(file)
                                }
                            } else if (document == 2) {
                                if (isFileSizeWithinLimit(fileSizeInBytes, 500.0)) { // 500 KB limit
                                    mBinding?.etFileNameSignature?.text = file?.name
//                                    uploadImage(file!!)
                                } else {
                                    compressFile(file!!) // Compress if size exceeds limit
                                    mBinding?.etFileNameSignature?.text = file.name
//                                    uploadImage(file)
                                }
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
                                    if (document == 1)
                                        mBinding?.etFileNamePhoto?.text = documentName
                                    else if (document == 2)
                                        mBinding?.etFileNameSignature?.text = documentName
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
package com.swavlambancard.udid.ui.fragments

import Translator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.swavlambancard.udid.R
import com.swavlambancard.udid.databinding.FragmentPersonalDetailsBinding
import com.swavlambancard.udid.model.CodeDropDownRequest
import com.swavlambancard.udid.model.DropDownRequest
import com.swavlambancard.udid.model.DropDownResult
import com.swavlambancard.udid.model.Fields
import com.swavlambancard.udid.model.LanguageLocalize
import com.swavlambancard.udid.ui.activity.PersonalProfileActivity
import com.swavlambancard.udid.ui.adapter.BottomSheetAdapter
import com.swavlambancard.udid.utilities.BaseFragment
import com.swavlambancard.udid.utilities.EncryptionModel
import com.swavlambancard.udid.utilities.URIPathHelper
import com.swavlambancard.udid.utilities.Utility.rotateDrawable
import com.swavlambancard.udid.utilities.Utility.showSnackbar
import com.swavlambancard.udid.utilities.hideView
import com.swavlambancard.udid.utilities.showView
import com.swavlambancard.udid.utilities.toast
import com.swavlambancard.udid.viewModel.SharedDataViewModel
import com.swavlambancard.udid.viewModel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
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
    private var photoName: String? = null
    private var signatureName: String? = null
    var isEditable = false
    private var document = 0
    private var genderTag: String = "O"
    private var stateList = ArrayList<DropDownResult>()
    private var stateId: String? = null
    private var relationWithPersonList = ArrayList<DropDownResult>()
    private var relationWithPersonId: String? = null
    var date: String? = null
    private var guardianId: String? = null
    private val guardianList = listOf(
        DropDownResult(id = "0", name = "Select Relation with PWD"),
        DropDownResult(id = "Father", name = "Father"),
        DropDownResult(id = "Mother", name = "Mother"),
        DropDownResult(id = "Guardian", name = "Guardian")
    )

    private val languageList = listOf(
        LanguageLocalize("35", "en", "English"),   // Andaman & Nicobar
        LanguageLocalize("28", "te", "Telugu"),    // Andhra Pradesh
        LanguageLocalize("12", "en", "English"),   // Arunachal Pradesh
        LanguageLocalize("18", "as", "Assamese"),  // Assam
        LanguageLocalize("10", "hi", "Hindi"),     // Bihar
        LanguageLocalize("4", "en", "English"),    // Chandigarh
        LanguageLocalize("22", "hi", "Hindi"),     // Chhattisgarh
        LanguageLocalize("7", "hi", "Hindi"),      // Delhi
        LanguageLocalize("30", "kok", "Konkani"),  // Goa
        LanguageLocalize("24", "gu", "Gujarati"),  // Gujarat
        LanguageLocalize("6", "hi", "Hindi"),      // Haryana
        LanguageLocalize("2", "hi", "Hindi"),      // Himachal Pradesh
        LanguageLocalize("1", "hi", "Hindi"),      // Jammu & Kashmir
        LanguageLocalize("20", "hi", "Hindi"),     // Jharkhand
        LanguageLocalize("29", "kn", "Kannada"),   // Karnataka
        LanguageLocalize("32", "ml", "Malayalam"), // Kerala
        LanguageLocalize("37", "en", "English"),   // Ladakh
        LanguageLocalize("31", "hi", "Hindi"),     // Lakshadweep
        LanguageLocalize("23", "hi", "Hindi"),     // Madhya Pradesh
        LanguageLocalize("27", "mr", "Marathi"),   // Maharashtra
        LanguageLocalize("14", "ma", "Manipuri"),  // Manipur
        LanguageLocalize("17", "en", "English"),   // Meghalaya
        LanguageLocalize("15", "mi", "Mizo"),      // Mizoram
        LanguageLocalize("13", "en", "English"),   // Nagaland
        LanguageLocalize("21", "or", "Oriya"),     // Odisha
        LanguageLocalize("34", "en", "English"),   // Puducherry
        LanguageLocalize("3", "pa", "Punjabi"),    // Punjab
        LanguageLocalize("8", "hi", "Hindi"),      // Rajasthan
        LanguageLocalize("11", "ne", "Nepali"),    // Sikkim
        LanguageLocalize("33", "ta", "Tamil"),     // Tamil Nadu
        LanguageLocalize("36", "te", "Telugu"),    // Telangana
        LanguageLocalize("38", "gu", "English"),   // Dadra & Nagar Haveli and Daman & Diu
        LanguageLocalize("16", "en", "English"),   // Tripura
        LanguageLocalize("5", "hi", "Hindi"),      // Uttarakhand
        LanguageLocalize("9", "hi", "Hindi"),      // Uttar Pradesh
        LanguageLocalize("19", "bn", "Bengali")    // West Bengal
    )

        private var selectedLanguageCode: String? = null
        private val handler = android.os.Handler()
        private var translationRunnable: Runnable? = null

    override val layoutId: Int
        get() = R.layout.fragment_personal_details

    override fun init() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
        viewModel.init()
        sharedViewModel = ViewModelProvider(requireActivity())[SharedDataViewModel::class.java]
//        mBinding?.etApplicantFullName?.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable?) {
//                translationRunnable?.let { handler.removeCallbacks(it) } // Cancel previous task
//
//                translationRunnable = Runnable {
//                    translateText(s.toString().trim())
//                }
//
//                handler.postDelayed(translationRunnable!!, 1500) // Delay translation by 500ms
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//        }) // old implementation
        mBinding?.etApplicantFullName?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                translationRunnable?.let { handler.removeCallbacks(it) } // Cancel previous task

                translationRunnable = Runnable {
                    lifecycleScope.launch {
                        translateText(s.toString().trim())
                    }
                }

                handler.postDelayed(translationRunnable!!, 1500) // Delay translation by 500ms
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        sharedViewModel.userData.observe(viewLifecycleOwner) { userData ->
            mBinding?.etApplicantFullName?.setText(userData.applicantFullName)
            mBinding?.etApplicantNameInRegionalLanguage?.setText(userData.full_name_i18n)
            selectedLanguageCode = userData.regionalLanguageCode
            requireContext().toast(userData.regionalLanguageCode.toString())
            mBinding?.etApplicantMobileNo?.setText(userData.applicantMobileNo)
            mBinding?.etApplicantEmailId?.setText(userData.applicantEmail)
            mBinding?.etApplicantDateOfBirth?.text = userData.applicantDob
            stateId = userData.stateCode
            mBinding?.etStateName?.text = userData.stateName
            when (userData.gender) {
                "M" -> {
                    mBinding?.rbMale?.isChecked = true
                }

                "F" -> {
                    mBinding?.rbFemale?.isChecked = true
                }

                "T" -> {
                    mBinding?.rbTransgender?.isChecked = true
                }

                else -> {
                    mBinding?.rbMale?.isChecked = false
                    mBinding?.rbFemale?.isChecked = false
                    mBinding?.rbTransgender?.isChecked = false
                }
            }
            mBinding?.etApplicantsFMGName?.text = userData.applicantsFMGName
            guardianId = userData.applicantsFMGCode
            when (userData.applicantsFMGCode) {
                "0" -> {
                    mBinding?.etApplicantsFMGName?.text = ""
                    mBinding?.llParentInfo?.hideView()
                }

                "Father" -> {
                    mBinding?.llParentInfo?.showView()
                    mBinding?.tvRelationWithPerson?.hideView()
                    mBinding?.etRelationWithPerson?.hideView()
                    mBinding?.tvApplicantNameFMG?.text =
                        getString(R.string.applicant_father_s_name)
                    mBinding?.etApplicantRelativeName?.hint =
                        getString(R.string.applicant_father_s_name_)
                    mBinding?.etApplicantRelativeName?.setText(userData.fatherName)
                }

                "Mother" -> {
                    mBinding?.llParentInfo?.showView()
                    mBinding?.tvRelationWithPerson?.hideView()
                    mBinding?.etRelationWithPerson?.hideView()
                    mBinding?.tvApplicantNameFMG?.text =
                     getString(R.string.applicant_mother_s_name)
                    mBinding?.etApplicantRelativeName?.hint =
                        getString(R.string.applicant_mother_s_name_)
                    mBinding?.etApplicantRelativeName?.setText(userData.motherName)
                }

                "Guardian" -> {
                    mBinding?.llParentInfo?.showView()
                    mBinding?.tvRelationWithPerson?.showView()
                    mBinding?.etRelationWithPerson?.showView()
                    mBinding?.tvApplicantNameFMG?.text =
                        getString(R.string.name_of_guardian_caretaker)
                    mBinding?.etApplicantRelativeName?.hint =
                        getString(R.string.name_of_guardian_caretaker_)
                    mBinding?.etApplicantRelativeName?.setText(userData.guardianName)
                }
            }
            mBinding?.etContactNoOfGuardian?.setText(userData.guardianContact)

            if (userData.relationWithPersonCode != "Self") {
                mBinding?.llSelf?.showView()
            } else {
                mBinding?.llSelf?.hideView()
            }
            mBinding?.etRelationWithPerson?.text = userData.relationWithPersonName
            relationWithPersonId = userData.relationWithPersonCode

            mBinding?.etFileNamePhoto?.text = userData.photo
            mBinding?.etFileNameSignature?.text = userData.sign
        }

        mBinding?.etApplicantFullName?.addTextChangedListener {
            sharedViewModel.userData.value?.applicantFullName = it.toString()
        }
        mBinding?.etStateName?.addTextChangedListener {
            sharedViewModel.userData.value?.stateName = it.toString()
        }
        mBinding?.etApplicantNameInRegionalLanguage?.addTextChangedListener {
            sharedViewModel.userData.value?.full_name_i18n = it.toString()
        }
        mBinding?.etApplicantMobileNo?.addTextChangedListener {
            sharedViewModel.userData.value?.applicantMobileNo = it.toString()
        }
        mBinding?.etApplicantEmailId?.addTextChangedListener {
            sharedViewModel.userData.value?.applicantEmail = it.toString()
        }
        Log.d("EMAILFrag1", sharedViewModel.userData.value?.applicantEmail.toString())
        mBinding?.etApplicantDateOfBirth?.addTextChangedListener {
            sharedViewModel.userData.value?.applicantDob = it.toString()
        }
        mBinding?.rgGender?.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbMale -> {
                    genderTag = "M"
                }

                R.id.rbFemale -> {
                    genderTag = "F"
                }

                R.id.rbTransgender -> {
                    genderTag = "T"
                }

                else -> {
                    genderTag = "O"
                }
            }
            sharedViewModel.userData.value?.gender = genderTag
        }
        mBinding?.etApplicantsFMGName?.addTextChangedListener {
            sharedViewModel.userData.value?.applicantsFMGName = it.toString()
        }
        mBinding?.etContactNoOfGuardian?.addTextChangedListener {
            sharedViewModel.userData.value?.guardianContact = it.toString()
        }
        mBinding?.etApplicantRelativeName?.addTextChangedListener {
            when (guardianId) {
                "Father" -> {

                        sharedViewModel.userData.value?.fatherName = it.toString()

                }
                "Mother" -> {

                        sharedViewModel.userData.value?.motherName = it.toString()

                }
                "Guardian" -> {

                        sharedViewModel.userData.value?.guardianName = it.toString()
                }
            }
        }


        mBinding?.etRelationWithPerson?.addTextChangedListener {
            sharedViewModel.userData.value?.relationWithPersonName = it.toString()
        }
        mBinding?.etFileNamePhoto?.addTextChangedListener {
            sharedViewModel.userData.value?.photo = it.toString()
        }
        mBinding?.etFileNameSignature?.addTextChangedListener {
            sharedViewModel.userData.value?.sign = it.toString()
        }
//        mBinding?.tvSaveDraft?.setOnClickListener {
//
//            Log.d("FragmentData",valid().toString())
//            Log.d("FragmentData",mBinding?.etFileNamePhoto?.text.toString().trim())
//            if (valid()) {
//                (requireActivity() as PersonalProfileActivity).replaceFragment(ProofOfIDFragment())
//                sharedViewModel.userData.observe(viewLifecycleOwner){
//
//                    Log.d("FragmentData1",it.toString())
//                }
//
//            }
//        }
    }

    override fun setVariables() {
    }

    override fun setObservers() {
        viewModel.dropDownResult.observe(this) {
            val userResponseModel = it
            if (userResponseModel?._result != null && userResponseModel._result.isNotEmpty()) {
                when (userResponseModel.model) {
                    "States" -> {
                        stateList.clear()
                        stateList.add(
                            DropDownResult(
                                "0",
                                getString(R.string.select_state_name)
                            )
                        )
                        stateList.addAll(userResponseModel._result)
                    }
                }
                bottomSheetAdapter?.notifyDataSetChanged()
            }
        }
        viewModel.codeDropDownResult.observe(this) {
            val userResponseModel = it
            if (userResponseModel?._result != null && userResponseModel._result.isNotEmpty()) {
                relationWithPersonList.clear()
                relationWithPersonList.add(
                    DropDownResult(
                        "Select Relation with PWD",
                        getString(R.string.select_relation_with_pwd)
                    )
                )
                relationWithPersonList.addAll(userResponseModel._result)
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
                    if (document == 1) {
                        photoName = userResponseModel._result.file_name
                        mBinding?.etFileNamePhoto?.text = userResponseModel._result.file_name
                    } else if (document == 2) {
                        signatureName = userResponseModel._result.file_name
                        mBinding?.etFileNameSignature?.text = userResponseModel._result.file_name
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
                (requireActivity() as PersonalProfileActivity).replaceFragment(ProofOfIDFragment())
                sharedViewModel.userData.observe(viewLifecycleOwner){
                    Log.d("FragmentData1",it.toString())
                }

            }

        }

        fun edit(view: View) {
            isEditable = !isEditable  // Toggle state

            mBinding?.etApplicantNameInRegionalLanguage?.isClickable = isEditable
            mBinding?.etApplicantNameInRegionalLanguage?.isFocusable = isEditable
            mBinding?.etApplicantNameInRegionalLanguage?.isFocusableInTouchMode =
                isEditable  // Required for text input

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

    private fun relationWithPersonApi() {
        viewModel.getCodeDropDown(
            requireContext(), CodeDropDownRequest(
                listname = "getRelationpwd",
                type = "mobile"
            )
        )
    }
//    private fun translateText(inputText: String) {
//        if (inputText.isEmpty() || selectedLanguageCode.isNullOrEmpty()) {
//            mBinding?.etApplicantNameInRegionalLanguage?.setText("")
//            return
//        }
//
//        CoroutineScope(Dispatchers.Main).launch {
//            Translator.getTranslation("en",selectedLanguageCode!!,inputText) { translatedText ->
//                Log.d("Translation", "Translated: $translatedText")
//                mBinding?.etApplicantNameInRegionalLanguage?.setText(translatedText)
//            }
//        }
//    }// old implementation
    private suspend fun translateText(inputText: String) {
        if (inputText.isEmpty() || selectedLanguageCode.isNullOrEmpty()) {
            withContext(Dispatchers.Main) {// all the ui updations are taking place in ui thread
                mBinding?.etApplicantNameInRegionalLanguage?.setText("")
            }
            return
        }

        val translatedText = withContext(Dispatchers.IO) {
            Translator.getTranslation("en", selectedLanguageCode!!, inputText)// this is the main function responsible to give the translation so earlier on we were using a callback function inside main thread now we are using suspendcancelable coroutine which solves the blocking issue
        }

        withContext(Dispatchers.Main) {
            Log.d("Translation", "Translated: $translatedText")
            mBinding?.etApplicantNameInRegionalLanguage?.setText(translatedText)
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
//                Log.d(
//                    "Date",
//                    "onDateSet: MM/dd/yyyy: $formattedMonth/$formattedDay/$selectedYear"
//                )
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
                if (relationWithPersonList.isEmpty()) {
                    relationWithPersonApi()
                }
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
                            sharedViewModel.userData.value?.stateCode = stateId.toString()
                            mBinding?.tvApplicantNameInRegionalLanguage?.showView()
                            mBinding?.etApplicantNameInRegionalLanguage?.showView()
                            val languageInfo = languageList.firstOrNull { it.stateId == stateId }
                            if (languageInfo != null) {
                                selectedLanguageCode = languageInfo.langCode
                                sharedViewModel.userData.value?.regionalLanguageCode = languageInfo.langCode
                                    mBinding?.tvApplicantNameInRegionalLanguage?.text =
                                    getString(R.string.applicant_name_in, languageInfo.languageName)
                                mBinding?.etApplicantNameInRegionalLanguage?.hint = getString(R.string.applicant_name_in, languageInfo.languageName)

                                // Show loading state before translation
                                mBinding?.etApplicantNameInRegionalLanguage?.setText("Translating...")

                                // Cancel any pending translation and introduce delay
                                translationRunnable?.let { handler.removeCallbacks(it) }
                                translationRunnable = Runnable {
                                    lifecycleScope.launch {
                                        translateText(mBinding!!.etApplicantFullName.text.toString().trim())
                                    }
                                }
                                handler.postDelayed(translationRunnable!!, 500) // Delay translation by 500ms
                            }
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
                                sharedViewModel.userData.value?.applicantsFMGCode =
                                    guardianId.toString()
                                mBinding?.llParentInfo?.showView()
                                mBinding?.tvRelationWithPerson?.hideView()
                                mBinding?.etRelationWithPerson?.hideView()
                                mBinding?.tvApplicantNameFMG?.text =
                                    getString(R.string.applicant_father_s_name)
                                mBinding?.etApplicantRelativeName?.hint =
                                    getString(R.string.applicant_father_s_name_)
                            }

                            "Mother" -> {
                                guardianId = id
                                sharedViewModel.userData.value?.applicantsFMGCode =
                                    guardianId.toString()
                                mBinding?.llParentInfo?.showView()
                                mBinding?.tvRelationWithPerson?.hideView()
                                mBinding?.etRelationWithPerson?.hideView()
                                mBinding?.tvApplicantNameFMG?.text =
                                    getString(R.string.applicant_mother_s_name)
                                mBinding?.etApplicantRelativeName?.hint =
                                    getString(R.string.applicant_mother_s_name_)
                            }

                            "Guardian" -> {
                                guardianId = id
                                sharedViewModel.userData.value?.applicantsFMGCode =
                                    guardianId.toString()
                                mBinding?.llParentInfo?.showView()
                                mBinding?.tvRelationWithPerson?.showView()
                                mBinding?.etRelationWithPerson?.showView()
                                mBinding?.tvApplicantNameFMG?.text =
                                    getString(R.string.name_of_guardian_caretaker)
                                mBinding?.etApplicantRelativeName?.hint =
                                    getString(R.string.name_of_guardian_caretaker_)
                            }
                        }
                    }

                    "relationWithPerson" -> {
                        when (selectedItem) {
                            "Select Relation with PWD" -> {
                                mBinding?.llSelf?.showView()
                                selectedTextView?.text = ""
                            }

                            "Self" -> {
                                mBinding?.llSelf?.hideView()
                                relationWithPersonId = id
                                sharedViewModel.userData.value?.relationWithPersonCode =
                                    relationWithPersonId.toString()
                            }

                            else -> {
                                mBinding?.llSelf?.showView()
                                relationWithPersonId = id
                                sharedViewModel.userData.value?.relationWithPersonCode =
                                    relationWithPersonId.toString()
                            }
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
        selectedTextView?.setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            rotatedDrawable,
            null
        )

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
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        if (mBinding?.etApplicantFullName?.text?.toString().isNullOrEmpty()) {
            mBinding?.llParent?.let {
                showSnackbar(
                    it,
                    getString(R.string.please_enter_applicant_s_full_name)
                )
            }
            return false
        } else if (mBinding?.etStateName?.text.toString().trim().isEmpty()) {
            mBinding?.llParent?.let {
                showSnackbar(
                    it,
                    getString(R.string.please_select_state)
                )
            }
            return false
        } else if (mBinding?.etApplicantMobileNo?.text?.toString().isNullOrEmpty()) {
            mBinding?.llParent?.let {
                showSnackbar(it, getString(R.string.mobile_number_is_required))
            }
            return false
        } else if (mBinding?.etApplicantMobileNo?.text?.toString()?.length != 10) {
            mBinding?.llParent?.let {
                showSnackbar(it, getString(R.string.mobile_number_must_be_exactly_10_digits))
            }
            return false
        }  else if (!mBinding?.etApplicantEmailId?.text.toString().trim().matches(emailRegex)) {
            mBinding?.llParent?.let {
                showSnackbar(
                    it,
                    getString(R.string.please_enter_a_valid_email_address)
                )
            }
            return false
        } else if (mBinding?.etApplicantDateOfBirth?.text?.toString().isNullOrEmpty()) {
            mBinding?.llParent?.let {
                showSnackbar(
                    it,
                    getString(R.string.please_select_date_of_birth)
                )
            }
            return false
        } else if (genderTag == "O") {
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
        } else if (guardianId == "Father") {
            if (mBinding?.etApplicantRelativeName?.text.toString().isEmpty()) {
                mBinding?.llParent?.let {
                    showSnackbar(
                        it,
                        getString(R.string.please_enter_father_name)
                    )
                }
                return false
            } else if (mBinding?.etContactNoOfGuardian?.text?.toString().isNullOrEmpty()) {
                mBinding?.llParent?.let {
                    showSnackbar(
                        it,
                        getString(R.string.please_enter_contact_number)
                    )
                }
                return false
            }
        }
        else if (guardianId == "Mother") {
            if (mBinding?.etApplicantRelativeName?.text.toString().isEmpty()) {
                mBinding?.llParent?.let {
                    showSnackbar(
                        it,
                        getString(R.string.please_enter_mother_name)
                    )
                }
                return false
            } else if (mBinding?.etContactNoOfGuardian?.text?.toString().isNullOrEmpty()) {
                mBinding?.llParent?.let {
                    showSnackbar(
                        it,
                        getString(R.string.please_enter_contact_number)
                    )
                }
                return false
            }
        }
        else if (guardianId == "Guardian") {
            if (mBinding?.etRelationWithPerson?.text.toString().isEmpty()) {
                mBinding?.llParent?.let {
                    showSnackbar(
                        it,
                        getString(R.string.please_select_relation_with_person)
                    )
                }
                return false
            } else if (relationWithPersonId != "Self") {
                if (mBinding?.etApplicantRelativeName?.text.toString().isEmpty()) {
                    mBinding?.llParent?.let {
                        showSnackbar(
                            it,
                            getString(R.string.please_enter_guardian_name)
                        )
                    }
                    return false
                } else if (mBinding?.etContactNoOfGuardian?.text?.toString().isNullOrEmpty()) {
                    mBinding?.llParent?.let {
                        showSnackbar(
                            it,
                            getString(R.string.please_enter_contact_number)
                        )
                    }
                    return false
                }
            }
        }

        else if (mBinding?.etFileNamePhoto?.text.toString().trim().isEmpty()) {
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
                    photoFile = imageFile
                    val fileSizeInBytes = photoFile?.length() ?: 0
                    if (isFileSizeWithinLimit(fileSizeInBytes, 500.0)) { // 500 KB limit
                    } else {
                        compressFile(photoFile!!) // Compress if size exceeds limit
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
                            if (isFileSizeWithinLimit(
                                    fileSizeInBytes,
                                    500.0
                                )
                            ) { // 500 KB limit
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

    private fun uploadFileApi() {
        if (document == 1) {
            viewModel.uploadFile(
                requireContext(),
                EncryptionModel.aesEncrypt("photo").toRequestBody(MultipartBody.FORM),
                body
            )
        } else if (document == 2) {
            viewModel.uploadFile(
                requireContext(),
                EncryptionModel.aesEncrypt("signature_thumb_print")
                    .toRequestBody(MultipartBody.FORM),
                body
            )
        }
    }


}
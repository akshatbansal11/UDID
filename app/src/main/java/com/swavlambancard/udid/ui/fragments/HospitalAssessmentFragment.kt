package com.swavlambancard.udid.ui.fragments

import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.swavlambancard.udid.R
import com.swavlambancard.udid.callBack.DialogCallback
import com.swavlambancard.udid.databinding.FragmentHospitalAssesmentBinding
import com.swavlambancard.udid.model.DropDownRequest
import com.swavlambancard.udid.model.DropDownResult
import com.swavlambancard.udid.model.Fields
import com.swavlambancard.udid.model.Filters
import com.swavlambancard.udid.model.UserData
import com.swavlambancard.udid.ui.activity.DashboardActivity
import com.swavlambancard.udid.ui.activity.LoginActivity
import com.swavlambancard.udid.ui.activity.PersonalProfileActivity
import com.swavlambancard.udid.ui.activity.PwdLoginActivity
import com.swavlambancard.udid.ui.adapter.BottomSheetAdapter
import com.swavlambancard.udid.utilities.AppConstants
import com.swavlambancard.udid.utilities.BaseActivity
import com.swavlambancard.udid.utilities.BaseActivity.Companion
import com.swavlambancard.udid.utilities.BaseFragment
import com.swavlambancard.udid.utilities.EncryptionModel
import com.swavlambancard.udid.utilities.Preferences.getPreferenceOfLogin
import com.swavlambancard.udid.utilities.Utility.getNameById
import com.swavlambancard.udid.utilities.Utility.rotateDrawable
import com.swavlambancard.udid.utilities.Utility.showConfirmationAlertDialog
import com.swavlambancard.udid.utilities.Utility.showSnackbar
import com.swavlambancard.udid.utilities.hideView
import com.swavlambancard.udid.utilities.showView
import com.swavlambancard.udid.utilities.toast
import com.swavlambancard.udid.viewModel.SharedDataViewModel
import com.swavlambancard.udid.viewModel.ViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class HospitalAssessmentFragment : BaseFragment<FragmentHospitalAssesmentBinding>() {
    private lateinit var sharedViewModel: SharedDataViewModel
    private var viewModel = ViewModel()
    private var mBinding: FragmentHospitalAssesmentBinding? = null
    private var bottomSheetDialog: BottomSheetDialog? = null
    private var bottomSheetAdapter: BottomSheetAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    private var treatingHospitalTag: String = "0"
    var body: MultipartBody.Part? = null
    private var stateList = ArrayList<DropDownResult>()
    private var stateId: String? = null
    private var districtList = ArrayList<DropDownResult>()
    private var districtId: String? = null
    private var hospitalDistrictId: String? = null
    private var hospitalList = ArrayList<DropDownResult>()
    private var hospitalId: String? = null
    private var applicationNumber: String? = null
    override val layoutId: Int
        get() = R.layout.fragment_hospital_assesment

    override fun init() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
        viewModel.init()
        sharedViewModel = ViewModelProvider(requireActivity())[SharedDataViewModel::class.java]
        if (sharedViewModel.userData.value?.isFrom != "login") {
            mBinding?.llCbConfirm?.hideView()
            hospitalListApi(sharedViewModel.userData.value?.districtCode.toString())
        }

        sharedViewModel.userData.observe(viewLifecycleOwner) { userData ->
            when (userData.treatingHospitalTag) {
                "1" -> {
                    mBinding?.rbYes?.isChecked = true
                    mBinding?.llTreatingHospital?.showView()
                }

                "0" -> {
                    mBinding?.rbNo?.isChecked = true
                    mBinding?.llTreatingHospital?.hideView()
                }

                else -> {
                    mBinding?.rbYes?.isChecked = false
                    mBinding?.rbNo?.isChecked = false
                    mBinding?.llTreatingHospital?.hideView()
                }
            }
            if (userData.treatingHospitalTag == "1") {
                hospitalDistrictId = userData.hospitalDistrictId
            } else {
                districtId = userData.districtCode
            }
            mBinding?.etHospitalTreatingState?.text = userData.hospitalStateName
            stateId = userData.hospitalStateId
            mBinding?.etHospitalTreatingDistrict?.text = userData.hospitalDistrictName
            mBinding?.etHospitalName?.text = userData.hospitalNameName
            hospitalId = userData.hospitalNameId
            when (userData.hospitalCheckBox) {
                "0" -> {
                    mBinding?.cbConfirm?.isChecked = false
                }

                "1" -> {
                    mBinding?.cbConfirm?.isChecked = true
                }
            }
        }

        mBinding?.rgTreatingState?.setOnCheckedChangeListener { _, checkedId ->
            treatingHospitalTag = when (checkedId) {
                R.id.rbYes -> {
                    "1"
                }

                R.id.rbNo -> {
                    "0"
                }

                else -> {
                    "2"
                }
            }
            sharedViewModel.userData.value?.treatingHospitalTag = treatingHospitalTag
        }
        mBinding?.etHospitalTreatingState?.addTextChangedListener {
            sharedViewModel.userData.value?.hospitalStateName = it.toString()
        }
        mBinding?.etHospitalTreatingDistrict?.addTextChangedListener {
            sharedViewModel.userData.value?.hospitalDistrictName = it.toString()
        }
        mBinding?.etHospitalName?.addTextChangedListener {
            sharedViewModel.userData.value?.hospitalNameName = it.toString()
        }
        mBinding?.cbConfirm?.setOnCheckedChangeListener { _, isChecked ->
            sharedViewModel.userData.value?.hospitalCheckBox = if (isChecked) "1" else "0"
        }

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
                                "Please select Hospital Treating State / UTs."
                            )
                        )
                        stateList.addAll(userResponseModel._result)
                    }

                    "Districts" -> {
                        districtList.clear()
                        districtList.add(
                            DropDownResult(
                                "0",
                               "Choose Hospital Treating District"
                            )
                        )
                        districtList.addAll(userResponseModel._result)
                    }

                    "Hospitals" -> {
                        hospitalList.clear()
                        hospitalList.add(
                            DropDownResult(
                                "0",
                                "Select Hospital Name"
                            )
                        )
                        hospitalList.addAll(userResponseModel._result)
                        if (sharedViewModel.userData.value?.isFrom != "login") {
                            mBinding?.etHospitalName?.text = getNameById(
                                sharedViewModel.userData.value?.hospitalNameId.toString(),
                                hospitalList
                            )
                        }
                    }
                }
                bottomSheetAdapter?.notifyDataSetChanged()
            }
        }

        sharedViewModel.savePWDFormResult.observe(this) {
            val userResponseModel = it
            if (userResponseModel?._result != null) {
                if (userResponseModel._resultflag == 0) {
                    requireContext().toast(userResponseModel.message)
                    return@observe
                } else {
                    applicationNumber = userResponseModel._result.application_number
                    requireContext().toast(userResponseModel.message)
                    val dialog = ThankYouDialog(applicationNumber!!)
                    dialog.show(
                        (context as AppCompatActivity).supportFragmentManager,
                        "ThankYouDialog"
                    )
                }
            }
        }

        sharedViewModel.updatePWDFormResult.observe(this) {
            val userResponseModel = it
            if (userResponseModel?._result != null) {
                if (userResponseModel._resultflag == 0) {
                    requireContext().toast(userResponseModel.message)
                    return@observe
                } else {
                    requireContext().toast(userResponseModel.message)
                    context?.startActivity(Intent(requireContext(),DashboardActivity::class.java))
                }
            }
        }

        viewModel.errors.observe(this) {
            mBinding?.let { it1 -> showSnackbar(it1.llParent, it) }
        }
        sharedViewModel.errors_api.observe(requireActivity()) {
            mBinding?.let { it1 -> showSnackbar(it1.llParent, it) }
        }

    }

    inner class ClickActions {
        fun submit(view: View) {
            if(valid())
            {
            if (sharedViewModel.userData.value?.isFrom != "login") {
                showConfirmationAlertDialog(requireContext(), object : DialogCallback {
                    override fun onYes() {
//                        Log.d("Pwd Form data", sharedViewModel.userData.value.toString())
                        updatePwsFormApi()
                        Log.d("Pwd Form data", sharedViewModel.userData.value?.sign.toString())
                        Log.d("Pwd Form data", sharedViewModel.userData.value?.photo.toString())
                        Log.d("Pwd Form data", sharedViewModel.userData.value?.aadhaarEnrollmentUploadSlip.toString())
                        Log.d("Pwd Form data", sharedViewModel.userData.value?.identityProofUpload.toString())
                        Log.d("Pwd Form data", sharedViewModel.userData.value?.documentAddressProofPhoto.toString())
                        Log.d("Pwd Form data", sharedViewModel.userData.value?.uploadDisabilityCertificate.toString())
                        Log.d("Pwd Form data", sharedViewModel.userData.value?.serialNumber.toString())

                    }
                },getString(R.string.please_check_again_all_your_details_again_before_confirming_your_application))
            } else {
                showConfirmationAlertDialog(requireContext(), object : DialogCallback {
                    override fun onYes() {
//                        Log.d("Pwd Form data", sharedViewModel.userData.value.toString())
                        savePwdFormApi()
                        Log.d("Pwd Form data", sharedViewModel.userData.value?.sign.toString())
                        Log.d("Pwd Form data", sharedViewModel.userData.value?.photo.toString())
                        Log.d("Pwd Form data", sharedViewModel.userData.value?.aadhaarEnrollmentUploadSlip.toString())
                        Log.d("Pwd Form data", sharedViewModel.userData.value?.identityProofUpload.toString())
                        Log.d("Pwd Form data", sharedViewModel.userData.value?.documentAddressProofPhoto.toString())
                        Log.d("Pwd Form data", sharedViewModel.userData.value?.uploadDisabilityCertificate.toString())
                        Log.d("Pwd Form data", sharedViewModel.userData.value?.serialNumber.toString())

                    }
                },
                    getString(R.string.please_check_again_all_your_details_again_before_confirming_your_application))
            }
        }}

        fun cancel(view: View) {
            if(sharedViewModel.userData.value?.isFrom != "login") {

                val intent = Intent(requireContext(), DashboardActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
            else{
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }
        fun back(view: View) {
            (requireActivity() as PersonalProfileActivity).replaceFragment(DisabilityDetailFragment())
        }

        fun yes(view: View) {
            mBinding?.llTreatingHospital?.showView()
            mBinding?.etHospitalName?.text = ""
            districtId = ""
        }

        fun no(view: View) {
            mBinding?.llTreatingHospital?.hideView()
            districtId = sharedViewModel.userData.value?.districtCode
            mBinding?.etHospitalTreatingState?.text = ""
            mBinding?.etHospitalTreatingDistrict?.text = ""
            mBinding?.etHospitalName?.text = ""
            sharedViewModel.userData.value?.hospitalNameId = "0"
            stateId = ""
            hospitalDistrictId = ""
        }

        fun hospitalTreatingState(view: View) {
            showBottomSheetDialog("state")
        }

        fun hospitalTreatingDistrict(view: View) {
            if (mBinding?.etHospitalTreatingState?.text.toString().trim().isNotEmpty()) {
                showBottomSheetDialog("district")
            } else {
                mBinding?.llParent?.let {
                    showSnackbar(
                        it,
                        getString(R.string.please_select_state_first)
                    )
                }
            }
        }


        fun hospitalName(view: View) {
            if (!districtId.isNullOrEmpty() || !hospitalDistrictId.isNullOrEmpty()) {
                showBottomSheetDialog("hospitalName")
            } else {
                mBinding?.llParent?.let {
                    showSnackbar(
                        it,
                        getString(R.string.please_select_district_first)
                    )
                }
            }
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

    private fun districtListApi() {
        viewModel.getDropDown(
            requireContext(), DropDownRequest(
                model = "Districts",
                filters = Filters(state_code = stateId),
                fields = Fields(district_code = "district_name"),
                type = "mobile"
            )
        )
    }

    private fun hospitalListApi(id: String) {
        viewModel.getDropDown(
            requireContext(), DropDownRequest(
                model = "Hospitals",
                filters = Filters(district_code = id),
                fields = Fields(id = "hospital_name"),
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

        val selectedList: List<DropDownResult>
        val selectedTextView: TextView?
        when (type) {
            "state" -> {
                if (stateList.isEmpty()) {
                    stateListApi()
                }
                selectedList = stateList
                selectedTextView = mBinding?.etHospitalTreatingState
            }

            "district" -> {
//                if (districtList.isEmpty()) {
                districtListApi()
//                }
                selectedList = districtList
                selectedTextView = mBinding?.etHospitalTreatingDistrict
            }

            "hospitalName" -> {
                if (treatingHospitalTag == "1") {
                    hospitalDistrictId?.let { hospitalListApi(it) }
                } else if (treatingHospitalTag == "0") {
                    districtId?.let { hospitalListApi(it) }
                }
                selectedList = hospitalList
                selectedTextView = mBinding?.etHospitalName
            }

            else -> return
        }

        bottomSheetAdapter =
            BottomSheetAdapter(requireContext(), selectedList) { selectedItem, id ->
                selectedTextView?.text = selectedItem
                when (type) {
                    "state" -> {
                        if (selectedItem == "Please select Hospital Treating State / UTs.") {
                            selectedTextView?.text = ""
                            mBinding?.etHospitalTreatingDistrict?.text = ""
                            districtList.clear()
                        } else {
                            stateId = id
                            sharedViewModel.userData.value?.hospitalStateId = id
                        }
                    }

                    "district" -> {
                        if (selectedItem == "Choose Hospital Treating District") {
                            selectedTextView?.text = ""
                            mBinding?.etHospitalName?.text = ""
                            hospitalList.clear()
                        } else {
                            if (treatingHospitalTag == "1") {
                                hospitalDistrictId = id
                            } else {
                                districtId = id
                            }
                            sharedViewModel.userData.value?.hospitalDistrictId = hospitalDistrictId
                        }
                    }

                    "hospitalName" -> {
                        if (selectedItem == "Select Hospital Name") {
                            selectedTextView?.text = ""
                            sharedViewModel.userData.value?.hospitalNameId = "0"
                        } else {
                            hospitalId = id
                            sharedViewModel.userData.value?.hospitalNameId = id
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

        if (treatingHospitalTag == "1") {
            if (mBinding?.etHospitalTreatingState?.text.toString().trim().isEmpty()) {
                mBinding?.llParent?.let {
                    showSnackbar(
                        it,
                        getString(R.string.please_select_state_uts)
                    )
                }
                return false
            } else if (mBinding?.etHospitalTreatingDistrict?.text.toString().trim().isEmpty()) {
                mBinding?.llParent?.let {
                    showSnackbar(
                        it,
                        getString(R.string.please_select_district)
                    )
                }
                return false
            }
        }

         if (mBinding?.etHospitalName?.text.toString().trim().isEmpty()) {
            mBinding?.llParent?.let {
                showSnackbar(
                    it,
                    getString(R.string.select_hospital_name)
                )
            }
            return false
        } else if (sharedViewModel.userData.value?.isFrom == "login") {
            if (mBinding?.cbConfirm?.isChecked == false) {
                mBinding?.llParent?.let {
                    showSnackbar(
                        it,
                        getString(R.string.you_must_check_the_box_to_confirm_that_you_have_read_and_understood_the_process)
                    )
                }
                return false
            }
        }
        return true
    }

    private fun savePwdFormApi() {


            sharedViewModel.savePWDForm(
                context = requireContext(),
                type = EncryptionModel.aesEncrypt("mobile").toRequestBody(MultipartBody.FORM),
                applicationNumber = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.application_number.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                fullName = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.applicantFullName.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                regionalFullName = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.full_name_i18n.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                regionalLanguage = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.regionalLanguageCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                mobile = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.applicantMobileNo.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                email = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.applicantEmail.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                dob = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.applicantDob.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                gender = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.gender.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                guardianRelation = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.applicantsFMGCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                relationPwd = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.relationWithPersonCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                fatherName = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.fatherName.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                motherName = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.motherName.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                guardianName = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.guardianName.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                guardianContact = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.guardianContact.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                photo = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.photo.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                sign = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.sign.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                aadhaarNo = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.aadhaarNo.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                shareAadhaarInfo = EncryptionModel.aesEncrypt(
                    sharedViewModel.userData.value?.aadhaarCheckBox?.toString().toString()
                ).toRequestBody(MultipartBody.FORM),
                aadhaarInfo = EncryptionModel.aesEncrypt(
                    sharedViewModel.userData.value?.aadhaarInfo?.toString().toString()
                ).toRequestBody(MultipartBody.FORM),
                aadhaarEnrollmentNo = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.aadhaarEnrollmentNo.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                aadhaarEnrollmentSlip = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.aadhaarEnrollmentUploadSlip.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                identityProofId = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.identityProofId.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                identityProofFile = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.identityProofUpload.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                addressProofId = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.natureDocumentAddressProofCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                addressProofFile = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.documentAddressProofPhoto.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                currentAddress = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.address.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                currentStateCode = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.stateCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                currentDistrictCode = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.districtCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                currentSubDistrictCode = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.subDistrictCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                currentVillageCode = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.villageCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                currentPincode = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.pincodeCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                disabilityTypeId = EncryptionModel.aesEncrypt(Gson().toJson(sharedViewModel.userData.value?.disabilityTypeCode)).toRequestBody(
                    MultipartBody.FORM
                ),
                disabilityDueTo = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.disabilityDueToCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                disabilitySinceBirth = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.disabilityBirth.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                disabilitySince = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.disabilitySinceCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                haveDisabilityCert = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.haveDisabilityCertificate.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                disabilityCertDoc = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.uploadDisabilityCertificate.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                serialNumber = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.serialNumber.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                dateOfCertificate = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.dateOfCertificate.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                detailOfAuthority = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.detailOfAuthorityCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                disabilityPer = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.disabilityPercentage.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                isHospitalTreatingOtherState = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.treatingHospitalTag.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                hospitalTreatingStateCode = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.hospitalStateId.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                hospitalTreatingDistrictCode = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.hospitalDistrictId.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                declaration = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.hospitalCheckBox.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                hospitalTreatingId = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.hospitalNameId.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                apiRequestType = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.apiRequestType.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
            )
    }

    private fun updatePwsFormApi() {

            sharedViewModel.updatePWDForm(
                context = requireContext(),
                type = EncryptionModel.aesEncrypt("mobile").toRequestBody(MultipartBody.FORM),
                applicationNumber = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.application_number.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                fullName = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.applicantFullName.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                regionalFullName = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.full_name_i18n.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                regionalLanguage = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.regionalLanguageCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                mobile = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.applicantMobileNo.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                email = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.applicantEmail.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                dob = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.applicantDob.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                gender = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.gender.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                guardianRelation = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.applicantsFMGCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                relationPwd = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.relationWithPersonCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                fatherName = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.fatherName.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                motherName = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.motherName.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                guardianName = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.guardianName.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                guardianContact = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.guardianContact.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                photo = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.photo.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                sign = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.sign.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                aadhaarNo = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.aadhaarNo.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                shareAadhaarInfo = EncryptionModel.aesEncrypt(
                    sharedViewModel.userData.value?.aadhaarCheckBox?.toString().toString()
                ).toRequestBody(MultipartBody.FORM),
                aadhaarInfo = EncryptionModel.aesEncrypt(
                    sharedViewModel.userData.value?.aadhaarInfo?.toString().toString()
                ).toRequestBody(MultipartBody.FORM),
                aadhaarEnrollmentNo = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.aadhaarEnrollmentNo.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                aadhaarEnrollmentSlip = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.aadhaarEnrollmentUploadSlip.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                identityProofId = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.identityProofId.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                identityProofFile = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.identityProofUpload.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                addressProofId = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.natureDocumentAddressProofCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                addressProofFile = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.documentAddressProofPhoto.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                currentAddress = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.address.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                currentStateCode = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.stateCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                currentDistrictCode = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.districtCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                currentSubDistrictCode = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.subDistrictCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                currentVillageCode = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.villageCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                currentPincode = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.pincodeCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                disabilityTypeId = EncryptionModel.aesEncrypt(Gson().toJson(sharedViewModel.userData.value?.disabilityTypeCode)).toRequestBody(
                    MultipartBody.FORM
                ),
                disabilityDueTo = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.disabilityDueToCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                disabilitySinceBirth = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.disabilityBirth.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                disabilitySince = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.disabilitySinceCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                haveDisabilityCert = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.haveDisabilityCertificate.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                disabilityCertDoc = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.uploadDisabilityCertificate.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                serialNumber = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.serialNumber.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                dateOfCertificate = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.dateOfCertificate.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                detailOfAuthority = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.detailOfAuthorityCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                disabilityPer = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.disabilityPercentage.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                isHospitalTreatingOtherState = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.treatingHospitalTag.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                hospitalTreatingStateCode = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.hospitalStateId.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                hospitalTreatingDistrictCode = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.hospitalDistrictId.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                hospitalTreatingId = EncryptionModel.aesEncrypt(sharedViewModel.userData.value?.hospitalNameId.toString()).toRequestBody(
                    MultipartBody.FORM
                )
            )

    }

    private fun updatePwsFormApiWithoutEncryption() {

            sharedViewModel.updatePWDForm(
                context = requireContext(),
                type = ("mobile").toRequestBody(MultipartBody.FORM),
                applicationNumber = (sharedViewModel.userData.value?.application_number.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                fullName = (sharedViewModel.userData.value?.applicantFullName.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                regionalFullName = (sharedViewModel.userData.value?.full_name_i18n.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                regionalLanguage = (sharedViewModel.userData.value?.regionalLanguageCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                mobile = (sharedViewModel.userData.value?.applicantMobileNo.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                email = (sharedViewModel.userData.value?.applicantEmail.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                dob = (sharedViewModel.userData.value?.applicantDob.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                gender = (sharedViewModel.userData.value?.gender.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                guardianRelation = (sharedViewModel.userData.value?.applicantsFMGCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                relationPwd = (sharedViewModel.userData.value?.relationWithPersonCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                fatherName = (sharedViewModel.userData.value?.fatherName.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                motherName = (sharedViewModel.userData.value?.motherName.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                guardianName = (sharedViewModel.userData.value?.guardianName.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                guardianContact = (sharedViewModel.userData.value?.guardianContact.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                photo = (sharedViewModel.userData.value?.photo.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                sign = (sharedViewModel.userData.value?.sign.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                aadhaarNo = (sharedViewModel.userData.value?.aadhaarNo.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                shareAadhaarInfo = (
                    sharedViewModel.userData.value?.aadhaarCheckBox?.toString().toString()
                ).toRequestBody(MultipartBody.FORM),
                aadhaarInfo = (
                    sharedViewModel.userData.value?.aadhaarInfo?.toString().toString()
                ).toRequestBody(MultipartBody.FORM),
                aadhaarEnrollmentNo = (sharedViewModel.userData.value?.aadhaarEnrollmentNo.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                aadhaarEnrollmentSlip = (sharedViewModel.userData.value?.aadhaarEnrollmentUploadSlip.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                identityProofId = (sharedViewModel.userData.value?.identityProofId.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                identityProofFile = (sharedViewModel.userData.value?.identityProofUpload.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                addressProofId = (sharedViewModel.userData.value?.natureDocumentAddressProofCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                addressProofFile = (sharedViewModel.userData.value?.documentAddressProofPhoto.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                currentAddress = (sharedViewModel.userData.value?.address.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                currentStateCode = (sharedViewModel.userData.value?.stateCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                currentDistrictCode = (sharedViewModel.userData.value?.districtCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                currentSubDistrictCode = (sharedViewModel.userData.value?.subDistrictCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                currentVillageCode = (sharedViewModel.userData.value?.villageCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                currentPincode = (sharedViewModel.userData.value?.pincodeCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                disabilityTypeId = (Gson().toJson(sharedViewModel.userData.value?.disabilityTypeCode)).toRequestBody(
                    MultipartBody.FORM
                ),
                disabilityDueTo = (sharedViewModel.userData.value?.disabilityDueToCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                disabilitySinceBirth = (sharedViewModel.userData.value?.disabilityBirth.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                disabilitySince = (sharedViewModel.userData.value?.disabilitySinceCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                haveDisabilityCert = (sharedViewModel.userData.value?.haveDisabilityCertificate.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                disabilityCertDoc = (sharedViewModel.userData.value?.uploadDisabilityCertificate.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                serialNumber = (sharedViewModel.userData.value?.serialNumber.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                dateOfCertificate = (sharedViewModel.userData.value?.dateOfCertificate.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                detailOfAuthority = (sharedViewModel.userData.value?.detailOfAuthorityCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                disabilityPer = (sharedViewModel.userData.value?.disabilityPercentage.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                isHospitalTreatingOtherState = (sharedViewModel.userData.value?.treatingHospitalTag.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                hospitalTreatingStateCode = (sharedViewModel.userData.value?.hospitalStateId.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                hospitalTreatingDistrictCode = (sharedViewModel.userData.value?.hospitalDistrictId.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                hospitalTreatingId = (sharedViewModel.userData.value?.hospitalNameId.toString()).toRequestBody(
                    MultipartBody.FORM
                )
            )

    }

    private fun savePwdFormApiWithoutEncryption() {



            sharedViewModel.savePWDForm(
                context = requireContext(),
                type = ("mobile").toRequestBody(MultipartBody.FORM),
                applicationNumber = (sharedViewModel.userData.value?.application_number.toString()
                        ).toRequestBody(
                        MultipartBody.FORM
                    ),
                fullName =
                (sharedViewModel.userData.value?.applicantFullName.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                regionalFullName =
                (sharedViewModel.userData.value?.full_name_i18n.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                regionalLanguage =
                (sharedViewModel.userData.value?.regionalLanguageCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                mobile = (sharedViewModel.userData.value?.applicantMobileNo.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                email = (sharedViewModel.userData.value?.applicantEmail.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                dob = (sharedViewModel.userData.value?.applicantDob.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                gender = (sharedViewModel.userData.value?.gender.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                guardianRelation =
                (sharedViewModel.userData.value?.applicantsFMGCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                relationPwd =
                (sharedViewModel.userData.value?.relationWithPersonCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                fatherName = (sharedViewModel.userData.value?.fatherName.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                motherName = (sharedViewModel.userData.value?.motherName.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                guardianName = (sharedViewModel.userData.value?.guardianName.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                guardianContact =
                (sharedViewModel.userData.value?.guardianContact.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                photo = (sharedViewModel.userData.value?.photo.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                sign = (sharedViewModel.userData.value?.sign.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                aadhaarNo = (sharedViewModel.userData.value?.aadhaarNo.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                shareAadhaarInfo = (
                        sharedViewModel.userData.value?.aadhaarCheckBox?.toString().toString()
                        ).toRequestBody(MultipartBody.FORM),
                aadhaarInfo = (
                        sharedViewModel.userData.value?.aadhaarInfo?.toString().toString()
                        ).toRequestBody(MultipartBody.FORM),
                aadhaarEnrollmentNo =
                (sharedViewModel.userData.value?.aadhaarEnrollmentNo.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                aadhaarEnrollmentSlip =
                (sharedViewModel.userData.value?.aadhaarEnrollmentUploadSlip.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                identityProofId =
                (sharedViewModel.userData.value?.identityProofId.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                identityProofFile =
                (sharedViewModel.userData.value?.identityProofUpload.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                addressProofId =
                (sharedViewModel.userData.value?.natureDocumentAddressProofCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                addressProofFile =
                (sharedViewModel.userData.value?.documentAddressProofPhoto.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                currentAddress = (sharedViewModel.userData.value?.address.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                currentStateCode =
                (sharedViewModel.userData.value?.stateCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                currentDistrictCode =
                (sharedViewModel.userData.value?.districtCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                currentSubDistrictCode =
                (sharedViewModel.userData.value?.subDistrictCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                currentVillageCode =
                (sharedViewModel.userData.value?.villageCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                currentPincode =
                (sharedViewModel.userData.value?.pincodeCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                disabilityTypeId =
                (Gson().toJson(sharedViewModel.userData.value?.disabilityTypeCode)).toRequestBody(
                    MultipartBody.FORM
                ),
                disabilityDueTo =
                (sharedViewModel.userData.value?.disabilityDueToCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                disabilitySinceBirth =
                (sharedViewModel.userData.value?.disabilityBirth.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                disabilitySince =
                (sharedViewModel.userData.value?.disabilitySinceCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                haveDisabilityCert =
                (sharedViewModel.userData.value?.haveDisabilityCertificate.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                disabilityCertDoc =
                (sharedViewModel.userData.value?.uploadDisabilityCertificate.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                serialNumber = (sharedViewModel.userData.value?.serialNumber.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                dateOfCertificate =
                (sharedViewModel.userData.value?.dateOfCertificate.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                detailOfAuthority =
                (sharedViewModel.userData.value?.detailOfAuthorityCode.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                disabilityPer =
                (sharedViewModel.userData.value?.disabilityPercentage.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                isHospitalTreatingOtherState =
                (sharedViewModel.userData.value?.treatingHospitalTag.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                hospitalTreatingStateCode =
                (sharedViewModel.userData.value?.hospitalStateId.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                hospitalTreatingDistrictCode =
                (sharedViewModel.userData.value?.hospitalDistrictId.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                declaration =
                (sharedViewModel.userData.value?.hospitalCheckBox.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                hospitalTreatingId =
                (sharedViewModel.userData.value?.hospitalNameId.toString()).toRequestBody(
                    MultipartBody.FORM
                ),
                apiRequestType =
                (sharedViewModel.userData.value?.apiRequestType.toString()).toRequestBody(
                    MultipartBody.FORM
                )
            )
    }
}
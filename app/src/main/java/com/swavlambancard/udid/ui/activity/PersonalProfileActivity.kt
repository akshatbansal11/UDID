package com.swavlambancard.udid.ui.activity

import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.swavlambancard.udid.R
import com.swavlambancard.udid.databinding.ActivityPersonalProfileBinding
import com.swavlambancard.udid.model.EditApplication
import com.swavlambancard.udid.model.EditProfileRequest
import com.swavlambancard.udid.model.UserData
import com.swavlambancard.udid.ui.fragments.DisabilityDetailFragment
import com.swavlambancard.udid.ui.fragments.HospitalAssessmentFragment
import com.swavlambancard.udid.ui.fragments.PersonalDetailFragment
import com.swavlambancard.udid.ui.fragments.ProofOfAddressFragment
import com.swavlambancard.udid.ui.fragments.ProofOfIDFragment
import com.swavlambancard.udid.utilities.AppConstants
import com.swavlambancard.udid.utilities.BaseActivity
import com.swavlambancard.udid.utilities.EncryptionModel
import com.swavlambancard.udid.utilities.JSEncryptService
import com.swavlambancard.udid.utilities.Preferences.getPreferenceOfLogin
import com.swavlambancard.udid.utilities.Utility
import com.swavlambancard.udid.utilities.Utility.dateConvertToFormat
import com.swavlambancard.udid.utilities.Utility.getFileNameFromUrl
import com.swavlambancard.udid.viewModel.SharedDataViewModel
import org.json.JSONObject

class PersonalProfileActivity : BaseActivity<ActivityPersonalProfileBinding>() {
    private var mBinding: ActivityPersonalProfileBinding? = null
    private var currentFragment: Fragment? = null
    private var sharedViewModel: SharedDataViewModel? = null
    private var isFrom: String? = null
    var check: Int? = null
    override val layoutId: Int
        get() = R.layout.activity_personal_profile

    override fun initView() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
        sharedViewModel = ViewModelProvider(this)[SharedDataViewModel::class.java]
        sharedViewModel?.init()
        isFrom = intent.extras?.getString(AppConstants.IS_FROM)
        check = intent.extras?.getInt(AppConstants.CHECK)
        sharedViewModel?.userData?.value?.isFrom = intent.extras?.getString(AppConstants.IS_FROM)

    }

    override fun onResume() {
        super.onResume()
        if (isFrom == "login") {
            replaceFragment(PersonalDetailFragment())
        } else {
            editApi()
        }
    }
    override fun setVariables() {
    }

    override fun setObservers() {
        sharedViewModel?.editProfileResult?.observe(this) {
            val userResponseModel = it
            if (userResponseModel != null) {
                if (userResponseModel._resultflag == 0) {
                    mBinding?.clParent?.let { it1 ->
                        Utility.showSnackbar(
                            it1,
                            userResponseModel.message
                        )
                    }
                } else {
                    val data =
                        JSONObject(EncryptionModel.aesDecrypt(userResponseModel._result))
                    val gson = Gson()
                    val userData = gson.fromJson(data.toString(), EditApplication::class.java)
                    Log.d("Decrypted Data EditProfile : ", data.toString())
                    //Personal Details
                    sharedViewModel?.userData?.value?.applicantFullName = userData.full_name
                    sharedViewModel?.userData?.value?.full_name_i18n = userData.full_name_i18n
                    sharedViewModel?.userData?.value?.regionalLanguageCode =
                        userData.regional_language
                    sharedViewModel?.userData?.value?.stateCode = userData.current_state_code
                    sharedViewModel?.userData?.value?.stateName = userData.state.name
                    sharedViewModel?.userData?.value?.applicantMobileNo = userData.mobile
                    sharedViewModel?.userData?.value?.gender = userData.gender
                    sharedViewModel?.userData?.value?.applicantDob =
                        dateConvertToFormat(userData.dob)
                    sharedViewModel?.userData?.value?.applicantEmail = userData.email
                    sharedViewModel?.userData?.value?.fatherName = userData.father_name
                    sharedViewModel?.userData?.value?.motherName = userData.mother_name
                    sharedViewModel?.userData?.value?.guardianName = userData.guardian_name
                    sharedViewModel?.userData?.value?.guardianContact = userData.guardian_contact
                    sharedViewModel?.userData?.value?.applicantsFMGCode = userData.guardian_relation
                    sharedViewModel?.userData?.value?.applicantsFMGName = userData.guardian_relation
                    sharedViewModel?.userData?.value?.relationWithPersonCode = userData.relation_pwd
                    sharedViewModel?.userData?.value?.relationWithPersonName = userData.relation_pwd
                    sharedViewModel?.userData?.value?.photo = getFileNameFromUrl(userData.photo)
                    sharedViewModel?.userData?.value?.sign =
                        getFileNameFromUrl(userData.signature_thumb_print)
                    //Proof of ID
                    sharedViewModel?.userData?.value?.aadhaarNo = userData.aadhaar_no
                    sharedViewModel?.userData?.value?.aadhaarCheckBox = userData.share_aadhar_info
                    sharedViewModel?.userData?.value?.aadhaarInfo = userData.aadhar_info
                    sharedViewModel?.userData?.value?.aadhaarTag = userData.aadhar_info
                    sharedViewModel?.userData?.value?.aadhaarEnrollmentNo =
                        userData.aadhar_enrollment_no
                    sharedViewModel?.userData?.value?.aadhaarEnrollmentUploadSlip =
                        userData.aadhar_enrollment_slip
                    sharedViewModel?.userData?.value?.identityProofId = userData.identitity_proof_id
                    sharedViewModel?.userData?.value?.identityProofUpload =
                        getFileNameFromUrl(userData.identitity_proof_file)
                    //Address Correspondence
                    sharedViewModel?.userData?.value?.natureDocumentAddressProofCode =
                        userData.address_proof_id
                    sharedViewModel?.userData?.value?.documentAddressProofPhoto =
                        getFileNameFromUrl(userData.address_proof_file)
                    sharedViewModel?.userData?.value?.address = userData.current_address
                    sharedViewModel?.userData?.value?.districtCode = userData.current_district_code
                    sharedViewModel?.userData?.value?.districtName = userData.district.district_name
                    sharedViewModel?.userData?.value?.subDistrictCode =
                        userData.current_subdistrict_code
                    sharedViewModel?.userData?.value?.subDistrictName =
                        userData.subdistrict.subdistrict_name
                    sharedViewModel?.userData?.value?.villageCode = userData.current_village_code
//                    sharedViewModel?.userData?.value?.villageName = userData.current_village_code
                    sharedViewModel?.userData?.value?.pincodeName = userData.current_pincode
                    sharedViewModel?.userData?.value?.pincodeCode = userData.current_pincode
                    //disability Details
                    sharedViewModel?.userData?.value?.disabilityTypeName = userData.disability_types
//                    sharedViewModel?.userData?.value?.disabilityTypeCode = userData.disability_type_id
                    sharedViewModel?.userData?.value?.disabilityDueToCode =
                        userData.disability_due_to
                    sharedViewModel?.userData?.value?.disabilityDueToName =
                        userData.disability_due_to
                    sharedViewModel?.userData?.value?.disabilityBirth =
                        userData.disability_since_birth
                    sharedViewModel?.userData?.value?.disabilitySinceCode =
                        userData.disability_since
                    sharedViewModel?.userData?.value?.disabilitySinceName =
                        userData.disability_since
                    if (userData.have_disability_cert)
                        sharedViewModel?.userData?.value?.haveDisabilityCertificate = 1
                    else
                        sharedViewModel?.userData?.value?.haveDisabilityCertificate = 0
                    sharedViewModel?.userData?.value?.uploadDisabilityCertificate =
                        getFileNameFromUrl(userData.disability_cert_doc)
                    sharedViewModel?.userData?.value?.detailOfAuthorityName = userData.detail_of_authority
                    sharedViewModel?.userData?.value?.detailOfAuthorityCode = userData.detail_of_authority
                    sharedViewModel?.userData?.value?.serialNumber = userData.serial_number
                    sharedViewModel?.userData?.value?.dateOfCertificate = dateConvertToFormat(userData.date_of_certificate)
                    sharedViewModel?.userData?.value?.disabilityPercentage = userData.disability_per
                    //Hospital Assessment
                    if (userData.is_hospital_treating_other_state)
                        sharedViewModel?.userData?.value?.treatingHospitalTag = "1"
                    else
                        sharedViewModel?.userData?.value?.treatingHospitalTag = "0"
                    sharedViewModel?.userData?.value?.hospitalNameId = userData.hospital_treating_id
                    sharedViewModel?.userData?.value?.hospitalStateId = userData.hospital_treating_state_code
                    sharedViewModel?.userData?.value?.hospitalStateName = userData.hospital_state.name
                    sharedViewModel?.userData?.value?.hospitalDistrictId = userData.hospital_treating_district_code
                    sharedViewModel?.userData?.value?.hospitalDistrictName = userData.hospital_district.district_name

                    replaceFragment(PersonalDetailFragment())
                }
            }
        }
    }

    inner class ClickActions {
        fun backPress(view: View) {
            finish()
            onBackPressedDispatcher.onBackPressed()
        }

        fun personalDetails(view: View) {
            replaceFragment(PersonalDetailFragment())
        }

        fun proofOfId(view: View) {
            replaceFragment(ProofOfIDFragment())
            mBinding?.horizontalScrollView?.smoothScrollTo(55, 0)
        }

        fun proofOfCorrespondId(view: View) {
            replaceFragment(ProofOfAddressFragment())
            mBinding?.horizontalScrollView?.smoothScrollTo(330, 0)
        }

        fun disabilityDetails(view: View) {
            replaceFragment(DisabilityDetailFragment())
            mBinding?.horizontalScrollView?.smoothScrollTo(750, 0)
        }

        fun hospitalAssessments(view: View) {
            replaceFragment(HospitalAssessmentFragment())
            mBinding?.horizontalScrollView?.smoothScrollTo(2050, 0)
        }
    }

    private fun editApi() {
        val encryptedString = JSEncryptService.encrypt(
            getPreferenceOfLogin(
                context,
                AppConstants.LOGIN_DATA,
                UserData::class.java
            ).application_number.toString()
        ).toString()
        Log.e("Encrypted Data", encryptedString)
        sharedViewModel?.editApplication(
            this, EditProfileRequest(
                encryptedString,
                JSEncryptService.encrypt("mobile").toString()
            )
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    fun replaceFragment(fragment: Fragment) {
        currentFragment = fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragFrame, fragment)
            .addToBackStack(null)
            .setReorderingAllowed(false)
            .commitAllowingStateLoss()
//            .commit()
        updateImagesForCurrentFragment()
    }

    private fun updateImagesForCurrentFragment() {
        when (currentFragment) {
            is PersonalDetailFragment -> {
                mBinding?.ivPD?.setImageResource(R.drawable.va_blue_ellipse)
                mBinding?.ivPOI?.setImageResource(R.drawable.va_orange_ellipse)
                mBinding?.ivPOCA?.setImageResource(R.drawable.va_ellipse_orange_down)
                mBinding?.ivDD?.setImageResource(R.drawable.va_orange_ellipse)
                mBinding?.ivHA?.setImageResource(R.drawable.va_ellipse_orange_down)
                mBinding?.tvPD?.setTextColor(ContextCompat.getColor(this, R.color.DarkBlue))
                mBinding?.tvPOI?.setTextColor(ContextCompat.getColor(this, R.color.orange))
                mBinding?.tvPOCA?.setTextColor(ContextCompat.getColor(this, R.color.orange))
                mBinding?.tvDD?.setTextColor(ContextCompat.getColor(this, R.color.orange))
                mBinding?.tvHA?.setTextColor(ContextCompat.getColor(this, R.color.orange))
            }

            is ProofOfIDFragment -> {
                mBinding?.ivPD?.setImageResource(R.drawable.va_ellipse_orange_down)
                mBinding?.ivPOI?.setImageResource(R.drawable.va_blue_ellipse_up)
                mBinding?.ivPOCA?.setImageResource(R.drawable.va_ellipse_orange_down)
                mBinding?.ivDD?.setImageResource(R.drawable.va_orange_ellipse)
                mBinding?.ivHA?.setImageResource(R.drawable.va_ellipse_orange_down)
                mBinding?.tvPD?.setTextColor(ContextCompat.getColor(this, R.color.orange))
                mBinding?.tvPOI?.setTextColor(ContextCompat.getColor(this, R.color.DarkBlue))
                mBinding?.tvPOCA?.setTextColor(ContextCompat.getColor(this, R.color.orange))
                mBinding?.tvDD?.setTextColor(ContextCompat.getColor(this, R.color.orange))
                mBinding?.tvHA?.setTextColor(ContextCompat.getColor(this, R.color.orange))
            }

            is ProofOfAddressFragment -> {
                mBinding?.ivPD?.setImageResource(R.drawable.va_ellipse_orange_down)
                mBinding?.ivPOI?.setImageResource(R.drawable.va_orange_ellipse)
                mBinding?.ivPOCA?.setImageResource(R.drawable.va_blue_ellipse)
                mBinding?.ivDD?.setImageResource(R.drawable.va_orange_ellipse)
                mBinding?.ivHA?.setImageResource(R.drawable.va_ellipse_orange_down)
                mBinding?.tvPD?.setTextColor(ContextCompat.getColor(this, R.color.orange))
                mBinding?.tvPOI?.setTextColor(ContextCompat.getColor(this, R.color.orange))
                mBinding?.tvPOCA?.setTextColor(ContextCompat.getColor(this, R.color.DarkBlue))
                mBinding?.tvDD?.setTextColor(ContextCompat.getColor(this, R.color.orange))
                mBinding?.tvHA?.setTextColor(ContextCompat.getColor(this, R.color.orange))
            }

            is DisabilityDetailFragment -> {
                mBinding?.ivPD?.setImageResource(R.drawable.va_ellipse_orange_down)
                mBinding?.ivPOI?.setImageResource(R.drawable.va_orange_ellipse)
                mBinding?.ivPOCA?.setImageResource(R.drawable.va_ellipse_orange_down)
                mBinding?.ivDD?.setImageResource(R.drawable.va_blue_ellipse_up)
                mBinding?.ivHA?.setImageResource(R.drawable.va_ellipse_orange_down)
                mBinding?.tvPD?.setTextColor(ContextCompat.getColor(this, R.color.orange))
                mBinding?.tvPOI?.setTextColor(ContextCompat.getColor(this, R.color.orange))
                mBinding?.tvPOCA?.setTextColor(ContextCompat.getColor(this, R.color.orange))
                mBinding?.tvDD?.setTextColor(ContextCompat.getColor(this, R.color.DarkBlue))
                mBinding?.tvHA?.setTextColor(ContextCompat.getColor(this, R.color.orange))
            }

            is HospitalAssessmentFragment -> {
                mBinding?.ivPD?.setImageResource(R.drawable.va_ellipse_orange_down)
                mBinding?.ivPOI?.setImageResource(R.drawable.va_orange_ellipse)
                mBinding?.ivPOCA?.setImageResource(R.drawable.va_ellipse_orange_down)
                mBinding?.ivDD?.setImageResource(R.drawable.va_orange_ellipse)
                mBinding?.ivHA?.setImageResource(R.drawable.va_blue_ellipse)
                mBinding?.tvPD?.setTextColor(ContextCompat.getColor(this, R.color.orange))
                mBinding?.tvPOI?.setTextColor(ContextCompat.getColor(this, R.color.orange))
                mBinding?.tvPOCA?.setTextColor(ContextCompat.getColor(this, R.color.orange))
                mBinding?.tvDD?.setTextColor(ContextCompat.getColor(this, R.color.orange))
                mBinding?.tvHA?.setTextColor(ContextCompat.getColor(this, R.color.DarkBlue))
            }

            else -> {}
        }
    }
}

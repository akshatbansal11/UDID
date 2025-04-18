package com.swavlambancard.udid.ui.activity

import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
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
import com.swavlambancard.udid.utilities.Utility.convertToArrayList
import com.swavlambancard.udid.utilities.Utility.dateConvertToFormat
import com.swavlambancard.udid.utilities.Utility.showSnackbar
import com.swavlambancard.udid.viewModel.SharedDataViewModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class PersonalProfileActivity : BaseActivity<ActivityPersonalProfileBinding>() {
    private var mBinding: ActivityPersonalProfileBinding? = null
    private var currentFragment: Fragment? = null
    private var sharedViewModel: SharedDataViewModel? = null
    private var isFrom: String? = null
    private lateinit var backPressedCallback: OnBackPressedCallback

    override val layoutId: Int
        get() = R.layout.activity_personal_profile

    override fun initView() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
        sharedViewModel = ViewModelProvider(this)[SharedDataViewModel::class.java]
        sharedViewModel?.init()
        isFrom = intent.extras?.getString(AppConstants.IS_FROM)
        sharedViewModel?.userData?.value?.isFrom = intent.extras?.getString(AppConstants.IS_FROM)
        sharedViewModel?.userData?.value?.check = intent.extras?.getString(AppConstants.CHECK)
        sharedViewModel?.userData?.value?.application_number = intent.extras?.getString(AppConstants.APPLICATION_NO)
        sharedViewModel?.userData?.value?.apiRequestType = intent.extras?.getString(AppConstants.REJECT_PENDING)
        if (isFrom == "login") {
            replaceFragment(PersonalDetailFragment())
        } else {
            editApi()
        }
        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Get the current fragment index
                val currentFragmentIndex = getCurrentFragmentIndex()

                if (currentFragmentIndex > 0) {
                    // Navigate to previous fragment
                    navigateToPreviousFragment(currentFragmentIndex)
                } else {
                    showExitConfirmationDialog()
                }
            }

        }

        // Register the callback
        onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }
    private fun getCurrentFragmentIndex(): Int {
        return when (currentFragment) {
            is PersonalDetailFragment -> 0
            is ProofOfIDFragment -> 1
            is ProofOfAddressFragment -> 2
            is DisabilityDetailFragment -> 3
            is HospitalAssessmentFragment -> 4
            else -> 0
        }
    }

    private fun navigateToPreviousFragment(currentIndex: Int) {
        when (currentIndex) {
            1 -> replaceFragment(PersonalDetailFragment())
            2 -> replaceFragment(ProofOfIDFragment())
            3 -> replaceFragment(ProofOfAddressFragment())
            4 -> replaceFragment(DisabilityDetailFragment())
            else -> showExitConfirmationDialog() // Fallback
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
                        showSnackbar(
                            it1,
                            userResponseModel.message
                        )
                    }
                } else {
                    val data =
                        JSONObject(EncryptionModel.aesDecrypt(userResponseModel._result))
                    val gson = Gson()
                    Log.d("Decrypted Data EditProfile : ", data.toString())
                    Log.d("Decrypted Data EditProfile : ", prettyPrintJson(data.toString()))
                    val userData = gson.fromJson(data.toString(), EditApplication::class.java)
                    Log.d("Decrypted Data EditProfile1 : ", userData.toString())
                    //Personal Details
                    sharedViewModel?.userData?.value?.applicantFullName = userData.full_name
                    sharedViewModel?.userData?.value?.full_name_i18n = userData.full_name_i18n
                    sharedViewModel?.userData?.value?.regionalLanguageCode =
                        userData.regional_language
                    sharedViewModel?.userData?.value?.stateCode = userData.current_state_code
                    sharedViewModel?.userData?.value?.stateName = userData.state?.name
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
                    sharedViewModel?.userData?.value?.photo = userData.photo
                    sharedViewModel?.userData?.value?.photoPath = userData.photo
                    sharedViewModel?.userData?.value?.sign = userData.signature_thumb_print
                    sharedViewModel?.userData?.value?.signaturePath = userData.signature_thumb_print
                    //Proof of ID
                    sharedViewModel?.userData?.value?.aadhaarNo = userData.aadhaar_no
                    sharedViewModel?.userData?.value?.aadhaarCheckBox = userData.share_aadhar_info
                    sharedViewModel?.userData?.value?.aadhaarInfo = userData.aadhar_info
                    sharedViewModel?.userData?.value?.aadhaarTag = userData.aadhar_info
                    sharedViewModel?.userData?.value?.aadhaarEnrollmentNo =
                        userData.aadhar_enrollment_no
                    sharedViewModel?.userData?.value?.aadhaarEnrollmentUploadSlip = userData.aadhar_enrollment_slip
                    sharedViewModel?.userData?.value?.aadhaarEnrollmentUploadSlipPath = userData.aadhar_enrollment_slip
                    sharedViewModel?.userData?.value?.identityProofId = userData.identitity_proof_id
                    sharedViewModel?.userData?.value?.identityProofUpload = userData.identitity_proof_file
                    sharedViewModel?.userData?.value?.identityProofUploadPath = userData.identitity_proof_file
                    sharedViewModel?.userData?.value?.isAadhaarAddressSame = userData.address_check
                    //Address Correspondence
                    sharedViewModel?.userData?.value?.natureDocumentAddressProofCode =
                        userData.address_proof_id
                    sharedViewModel?.userData?.value?.documentAddressProofPhoto = userData.address_proof_file
                    sharedViewModel?.userData?.value?.documentAddressProofPhotoPath = userData.address_proof_file
                    sharedViewModel?.userData?.value?.address = userData.current_address
                    sharedViewModel?.userData?.value?.districtCode = userData.current_district_code
                    sharedViewModel?.userData?.value?.districtName = userData.district?.district_name
                    sharedViewModel?.userData?.value?.subDistrictCode =
                        userData.current_subdistrict_code
                    sharedViewModel?.userData?.value?.subDistrictName =
                        userData.subdistrict?.subdistrict_name
                    sharedViewModel?.userData?.value?.villageCode = userData.current_village_code
                    sharedViewModel?.userData?.value?.villageName = userData.village?.village_name
                    sharedViewModel?.userData?.value?.pincodeName = userData.current_pincode
                    sharedViewModel?.userData?.value?.pincodeCode = userData.current_pincode
                    //disability Details
                    sharedViewModel?.userData?.value?.disabilityTypeName = userData.disability_types
                    sharedViewModel?.userData?.value?.disabilityTypeCode = convertToArrayList(userData.disability_type_id)
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
                    sharedViewModel?.userData?.value?.uploadDisabilityCertificate = userData.disability_cert_doc
                    sharedViewModel?.userData?.value?.uploadDisabilityCertificatePath = userData.disability_cert_doc
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
                    sharedViewModel?.userData?.value?.hospitalStateName = userData.hospital_state?.name
                    sharedViewModel?.userData?.value?.hospitalDistrictId = userData.hospital_treating_district_code
                    sharedViewModel?.userData?.value?.hospitalDistrictName = userData.hospital_district?.district_name

                    replaceFragment(PersonalDetailFragment())
                }
            }
        }
    }

    fun prettyPrintJson(json: String): String {
        return try {
            val jsonObject = JSONObject(json)
            jsonObject.toString(4) // Indent with 4 spaces
        } catch (e: JSONException) {
            val jsonArray = JSONArray(json)
            jsonArray.toString(4)
        }
    }


    inner class ClickActions {
        fun backPress(view: View) {
            // Get the current fragment index
            val currentFragmentIndex = getCurrentFragmentIndex()

            if (currentFragmentIndex > 0) {
                // Navigate to previous fragment
                navigateToPreviousFragment(currentFragmentIndex)
            } else {
                // Show exit dialog if we're on the first fragment
                showExitConfirmationDialog()
            }
        }
        fun personalDetails(view: View) {
//            if(sharedViewModel?.personalDetailValid(this@PersonalProfileActivity) == true) {
//                replaceFragment(PersonalDetailFragment())
//                error()
//            }
        }

        fun proofOfId(view: View) {
//            if(sharedViewModel?.proofOfIdValid(this@PersonalProfileActivity) == true) {
//                replaceFragment(ProofOfIDFragment())
//                mBinding?.horizontalScrollView?.smoothScrollTo(55, 0)
//                error()
//            }
        }

        fun proofOfCorrespondId(view: View) {
//            if(sharedViewModel?.proofOfAddressValid(this@PersonalProfileActivity) == true) {
//                replaceFragment(ProofOfAddressFragment())
//                mBinding?.horizontalScrollView?.smoothScrollTo(330, 0)
//                error()
//            }
        }

        fun disabilityDetails(view: View) {
//            if(sharedViewModel?.disabilityDetailsValid(this@PersonalProfileActivity) == true) {
//                replaceFragment(DisabilityDetailFragment())
//                mBinding?.horizontalScrollView?.smoothScrollTo(750, 0)
//                error()
//            }
        }

        fun hospitalAssessments(view: View) {
//            if(sharedViewModel?.hospitalAssessmentValid(this@PersonalProfileActivity) == true) {
//                replaceFragment(HospitalAssessmentFragment())
//                mBinding?.horizontalScrollView?.smoothScrollTo(2050, 0)
//                error()
//            }
        }
    }
    private fun showExitConfirmationDialog() {
//        val dialogBinding = LayoutInflater.from(this@PersonalProfileActivity).inflate(R.layout.custom_back_dialog, null)
//        val customDialog = Dialog(this@PersonalProfileActivity)
//
//        customDialog.setContentView(dialogBinding)
//        customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        customDialog.window!!.setLayout(
//            (resources.displayMetrics.widthPixels * 0.9).toInt(),
//            LinearLayout.LayoutParams.WRAP_CONTENT
//        )
//        customDialog.window!!.setGravity(Gravity.CENTER)
//        val lp: WindowManager.LayoutParams = customDialog.window!!.attributes
//        lp.dimAmount = 0.5f
//        customDialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
//        customDialog.setCancelable(false)
//
//        val btnStay = dialogBinding.findViewById<Button>(R.id.btnStay)
//        val btnDiscard = dialogBinding.findViewById<Button>(R.id.btnDiscard)
//
//        btnStay.setOnClickListener {
//            customDialog.dismiss()
//        }
//
//        btnDiscard.setOnClickListener {
//            customDialog.dismiss()
            backPressedCallback.remove() // Remove callback to prevent loop
            finish()
//        }

//        customDialog.show()
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


    fun replaceFragment(fragment: Fragment) {
        currentFragment = fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragFrame, fragment)
            // .addToBackStack(null) // Remove this line
            .commit()
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
                mBinding?.horizontalScrollView?.smoothScrollTo(55, 0)
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
                mBinding?.horizontalScrollView?.smoothScrollTo(330, 0)
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
                mBinding?.horizontalScrollView?.smoothScrollTo(750, 0)
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
                mBinding?.horizontalScrollView?.smoothScrollTo(2050, 0)
            }

            else -> {}
        }
    }
}

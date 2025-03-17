package com.swavlambancard.udid.viewModel


import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.swavlambancard.udid.R
import com.swavlambancard.udid.model.EditProfileRequest
import com.swavlambancard.udid.model.EditProfileResponse
import com.swavlambancard.udid.model.PwdApplication
import com.swavlambancard.udid.model.SavePWDFormResponse
import com.swavlambancard.udid.repository.Repository
import com.swavlambancard.udid.utilities.CommonUtils
import com.swavlambancard.udid.utilities.ProcessDialog
import com.swavlambancard.udid.utilities.UDID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import org.json.JSONObject
import java.net.SocketTimeoutException

class SharedDataViewModel : ViewModel() {
    private lateinit var repository: Repository
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var job: Job? = null
    var editProfileResult = MutableLiveData<EditProfileResponse>()
    var savePWDFormResult = MutableLiveData<SavePWDFormResponse>()
    var updatePWDFormResult = MutableLiveData<SavePWDFormResponse>()
    val userData = MutableLiveData(PwdApplication()) // Initialize with default fields
    val errors = MutableLiveData<String>()
    val errors_api = MutableLiveData<String>()

    init {
        userData.value = PwdApplication() // Initialize with default empty fields
    }

    fun init() {
        repository = Repository.instance
    }

    private fun showLoader(context: Context) {
        ProcessDialog.start(context)
    }

    private fun dismissLoader() {
        if (ProcessDialog.isShowing())
            ProcessDialog.dismiss()
    }

    private fun networkCheck(context: Context?, showLoader: Boolean) {
        if (CommonUtils.isNetworkAvailable(context) && showLoader) {
            context?.let { showLoader(context) }
        } else if (!CommonUtils.isNetworkAvailable(context)) {
            CommonUtils.displayNetworkAlert(context, false)
            return
        }
    }

    fun editApplication(
        context: Context,
        request: EditProfileRequest,
    ) {
        // can be launched in a separate asynchronous job
        networkCheck(context, true)
        job = scope.launch {
            try {
                val response = repository.editApplication(
                    request
                )

                Log.e("response", response.toString())
                when (response.isSuccessful) {
                    true -> {
                        when (response.code()) {
                            200, 201 -> {
                                editProfileResult.postValue(response.body())
                                dismissLoader()
                            }
                        }
                    }

                    false -> {
                        when (response.code()) {
                            400, 403, 404 -> {//Bad Request & Invalid Credentials
                                val errorBody = JSONObject(response.errorBody()!!.string())
                                errors_api.postValue(
                                    errorBody.getString("message") ?: "Bad Request"
                                )
                                dismissLoader()
                            }

                            401 -> {
                                val errorBody = JSONObject(response.errorBody()!!.string())
                                errors_api.postValue(
                                    errorBody.getString("message") ?: "Bad Request"
                                )
                                UDID.closeAndRestartApplication()
                                dismissLoader()
                            }

                            500 -> {//Internal Server error
                                errors_api.postValue("Internal Server error")
                                dismissLoader()
                            }

                            else -> {
                                dismissLoader()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                if (e is SocketTimeoutException) {
                    errors_api.postValue("Time out Please try again")
                }
                dismissLoader()
            }
        }
    }

    fun savePWDForm(
        context: Context,
        type: RequestBody?,
        applicationNumber: RequestBody?,
        fullName: RequestBody?,
        regionalFullName: RequestBody?,
        regionalLanguage: RequestBody?,
        mobile: RequestBody?,
        email: RequestBody?,
        dob: RequestBody?,
        gender: RequestBody?,//=>M/F/T
        guardianRelation: RequestBody?,//Mother/Father/Guardian
        relationPwd: RequestBody?,
        fatherName: RequestBody?,
        motherName: RequestBody?,
        guardianName: RequestBody?,
        guardianContact: RequestBody?,
        photo: RequestBody?,
        sign: RequestBody?,
        // Proof id Identity Card
        aadhaarNo: RequestBody?,
        shareAadhaarInfo: RequestBody?,//0/1
        aadhaarInfo: RequestBody?,//Yes(1)/No(0)
        aadhaarEnrollmentNo: RequestBody?,
        aadhaarEnrollmentSlip: RequestBody?,
        identityProofId: RequestBody?,
        identityProofFile: RequestBody?,
        //Address For Correspondence
        addressProofId: RequestBody?,
        addressProofFile: RequestBody?,
        currentAddress: RequestBody?,
        currentStateCode: RequestBody?,
        currentDistrictCode: RequestBody?,
        currentSubDistrictCode: RequestBody?,
        currentVillageCode: RequestBody?,
        currentPincode: RequestBody?,
        //Disability Details
        disabilityTypeId: RequestBody?,
        disabilityDueTo: RequestBody?,
        disabilitySinceBirth: RequestBody?,//Since(No)/Birth(Yes)
        disabilitySince: RequestBody?,
        haveDisabilityCert: RequestBody?,//1(yes)/0(no)
        disabilityCertDoc: RequestBody?,
        serialNumber: RequestBody?,
        dateOfCertificate: RequestBody?,
        detailOfAuthority: RequestBody?,
        disabilityPer: RequestBody?,
        //Hospital for assessment
        isHospitalTreatingOtherState: RequestBody?,//=> 0/1
        hospitalTreatingStateCode: RequestBody?,
        hospitalTreatingDistrictCode: RequestBody?,
        hospitalTreatingId: RequestBody?,
        declaration: RequestBody?,//=>0/1
        apiRequestType: RequestBody?
    ) {
        // can be launched in a separate asynchronous job
        networkCheck(context, true)
        job = scope.launch {
            try {
                val response = repository.savePwdForm(
                    type,
                    applicationNumber,
                    fullName,
                    regionalFullName,
                    regionalLanguage,
                    mobile,
                    email,
                    dob,
                    gender,
                    guardianRelation,
                    relationPwd,
                    fatherName,
                    motherName,
                    guardianName,
                    guardianContact,
                    photo,
                    sign,
                    aadhaarNo,
                    shareAadhaarInfo,
                    aadhaarInfo,
                    aadhaarEnrollmentNo,
                    aadhaarEnrollmentSlip,
                    identityProofId,
                    identityProofFile,
                    addressProofId,
                    addressProofFile,
                    currentAddress,
                    currentStateCode,
                    currentDistrictCode,
                    currentSubDistrictCode,
                    currentVillageCode,
                    currentPincode,
                    disabilityTypeId,
                    disabilityDueTo,
                    disabilitySinceBirth,
                    disabilitySince,
                    haveDisabilityCert,
                    disabilityCertDoc,
                    serialNumber,
                    dateOfCertificate,
                    detailOfAuthority,
                    disabilityPer,
                    isHospitalTreatingOtherState,
                    hospitalTreatingStateCode,
                    hospitalTreatingDistrictCode,
                    hospitalTreatingId,
                    declaration,
                    apiRequestType
                )

                Log.e("response", response.toString())
                when (response.isSuccessful) {
                    true -> {
                        when (response.code()) {
                            200, 201 -> {
                                savePWDFormResult.postValue(response.body())
                                dismissLoader()
                            }
                        }
                    }

                    false -> {
                        when (response.code()) {
                            400, 403, 404 -> {//Bad Request & Invalid Credentials
                                val errorBody = JSONObject(response.errorBody()!!.string())
                                errors_api.postValue(
                                    errorBody.getString("message") ?: "Bad Request"
                                )
                                dismissLoader()
                            }

                            401 -> {
                                val errorBody = JSONObject(response.errorBody()!!.string())
                                errors_api.postValue(
                                    errorBody.getString("message") ?: "Bad Request"
                                )
                                UDID.closeAndRestartApplication()
                                dismissLoader()
                            }

                            500 -> {//Internal Server error
                                errors_api.postValue("Internal Server error")
                                dismissLoader()
                            }

                            else -> {
                                dismissLoader()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                if (e is SocketTimeoutException) {
                    errors_api.postValue("Time out Please try again")
                }
                dismissLoader()
            }
        }
    }

    fun updatePWDForm(
        context: Context,
        type: RequestBody?,
        applicationNumber: RequestBody?,
        fullName: RequestBody?,
        regionalFullName: RequestBody?,
        regionalLanguage: RequestBody?,
        mobile: RequestBody?,
        email: RequestBody?,
        dob: RequestBody?,
        gender: RequestBody?,//=>M/F/T
        guardianRelation: RequestBody?,//Mother/Father/Guardian
        relationPwd: RequestBody?,
        fatherName: RequestBody?,
        motherName: RequestBody?,
        guardianName: RequestBody?,
        guardianContact: RequestBody?,
        photo: RequestBody?,
        sign: RequestBody?,
        // Proof id Identity Card
        aadhaarNo: RequestBody?,
        shareAadhaarInfo: RequestBody?,//0/1
        aadhaarInfo: RequestBody?,//Yes(1)/No(0)
        aadhaarEnrollmentNo: RequestBody?,
        aadhaarEnrollmentSlip: RequestBody?,
        identityProofId: RequestBody?,
        identityProofFile: RequestBody?,
        //Address For Correspondence
        addressProofId: RequestBody?,
        addressProofFile: RequestBody?,
        currentAddress: RequestBody?,
        currentStateCode: RequestBody?,
        currentDistrictCode: RequestBody?,
        currentSubDistrictCode: RequestBody?,
        currentVillageCode: RequestBody?,
        currentPincode: RequestBody?,
        //Disability Details
        disabilityTypeId: RequestBody?,
        disabilityDueTo: RequestBody?,
        disabilitySinceBirth: RequestBody?,//Since(No)/Birth(Yes)
        disabilitySince: RequestBody?,
        haveDisabilityCert: RequestBody?,//1(yes)/0(no)
        disabilityCertDoc: RequestBody?,
        serialNumber: RequestBody?,
        dateOfCertificate: RequestBody?,
        detailOfAuthority: RequestBody?,
        disabilityPer: RequestBody?,
        //Hospital for assessment
        isHospitalTreatingOtherState: RequestBody?,//=> 0/1
        hospitalTreatingStateCode: RequestBody?,
        hospitalTreatingDistrictCode: RequestBody?,
        hospitalTreatingId: RequestBody?,
    ) {
        // can be launched in a separate asynchronous job
        networkCheck(context, true)
        job = scope.launch {
            try {
                val response = repository.updatePwdForm(
                    type,
                    applicationNumber,
                    fullName,
                    regionalFullName,
                    regionalLanguage,
                    mobile,
                    email,
                    dob,
                    gender,
                    guardianRelation,
                    relationPwd,
                    fatherName,
                    motherName,
                    guardianName,
                    guardianContact,
                    photo,
                    sign,
                    aadhaarNo,
                    shareAadhaarInfo,
                    aadhaarInfo,
                    aadhaarEnrollmentNo,
                    aadhaarEnrollmentSlip,
                    identityProofId,
                    identityProofFile,
                    addressProofId,
                    addressProofFile,
                    currentAddress,
                    currentStateCode,
                    currentDistrictCode,
                    currentSubDistrictCode,
                    currentVillageCode,
                    currentPincode,
                    disabilityTypeId,
                    disabilityDueTo,
                    disabilitySinceBirth,
                    disabilitySince,
                    haveDisabilityCert,
                    disabilityCertDoc,
                    serialNumber,
                    dateOfCertificate,
                    detailOfAuthority,
                    disabilityPer,
                    isHospitalTreatingOtherState,
                    hospitalTreatingStateCode,
                    hospitalTreatingDistrictCode,
                    hospitalTreatingId
                )

                Log.e("response", response.toString())
                when (response.isSuccessful) {
                    true -> {
                        when (response.code()) {
                            200, 201 -> {
                                updatePWDFormResult.postValue(response.body())
                                dismissLoader()
                            }
                        }
                    }

                    false -> {
                        when (response.code()) {
                            400, 403, 404 -> {//Bad Request & Invalid Credentials
                                val errorBody = JSONObject(response.errorBody()!!.string())
                                errors_api.postValue(
                                    errorBody.getString("message") ?: "Bad Request"
                                )
                                dismissLoader()
                            }

                            401 -> {
                                val errorBody = JSONObject(response.errorBody()!!.string())
                                errors_api.postValue(
                                    errorBody.getString("message") ?: "Bad Request"
                                )
                                UDID.closeAndRestartApplication()
                                dismissLoader()
                            }

                            500 -> {//Internal Server error
                                errors_api.postValue("Internal Server error")
                                dismissLoader()
                            }

                            else -> {
                                dismissLoader()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                if (e is SocketTimeoutException) {
                    errors_api.postValue("Time out Please try again")
                }
                dismissLoader()
            }
        }
    }

    fun personalDetailValid(context: Context): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        if (userData.value?.applicantFullName.toString().isEmpty()) {
            errors.postValue(context.getString(R.string.please_enter_applicant_s_full_name))
            return false
        } else if (userData.value?.stateName.toString().isEmpty()) {
            errors.postValue(context.getString(R.string.please_select_state))
            return false
        } else if (userData.value?.applicantMobileNo.toString().isEmpty()) {
            errors.postValue(context.getString(R.string.mobile_number_is_required))
            return false
        } else if (userData.value?.applicantMobileNo.toString().length != 10) {
            errors.postValue(context.getString(R.string.mobile_number_must_be_exactly_10_digits))
            return false
        } else if (userData.value?.applicantEmail.toString().trim().isNotEmpty()) {
            if (!userData.value?.applicantEmail.toString().trim().matches(emailRegex)) {
                errors.postValue(context.getString(R.string.please_enter_a_valid_email_address))
                return false
            }
        } else if (userData.value?.applicantDob.toString().isEmpty()) {
            errors.postValue(context.getString(R.string.please_select_date_of_birth))
            return false
        } else if (userData.value?.gender == "O") {
            errors.postValue(context.getString(R.string.please_select_gender))
            return false
        } else if (userData.value?.applicantsFMGName.toString().isEmpty()) {
            errors.postValue(context.getString(R.string.please_select_guardian_relation))
            return false
        } else if (userData.value?.applicantsFMGName.toString().isEmpty()) {
            errors.postValue(context.getString(R.string.please_select_guardian_relation))
            return false
        } else if (userData.value?.applicantsFMGCode == "Father") {
            if (userData.value?.fatherName.toString().isEmpty()) {
                errors.postValue(context.getString(R.string.please_enter_father_name))
                return false
            } else if (userData.value?.guardianContact.toString().isEmpty()) {
                errors.postValue(context.getString(R.string.please_enter_contact_number))
                return false
            } else if (userData.value?.guardianContact.toString().length != 10) {
                errors.postValue(context.getString(R.string.mobile_number_must_be_exactly_10_digits))
                return false
            }
        } else if (userData.value?.applicantsFMGCode == "Mother") {
            if (userData.value?.motherName.toString().isEmpty()) {
                errors.postValue(context.getString(R.string.please_enter_father_name))
                return false
            } else if (userData.value?.guardianContact.toString().isEmpty()) {
                errors.postValue(context.getString(R.string.please_enter_contact_number))
                return false
            }
        } else if (userData.value?.applicantsFMGCode == "Guardian") {
            if (userData.value?.relationWithPersonName.toString().isEmpty()) {
                errors.postValue(context.getString(R.string.please_select_relation_with_person))
                return false
            } else if (userData.value?.relationWithPersonCode != "Self") {
                if (userData.value?.guardianName.toString().isEmpty()) {
                    errors.postValue(context.getString(R.string.please_enter_guardian_name))
                    return false
                } else if (userData.value?.guardianContact.toString().isEmpty()) {
                    errors.postValue(context.getString(R.string.please_enter_contact_number))
                    return false
                }
            }
        } else if (userData.value?.photo.toString().isEmpty()) {
            errors.postValue(context.getString(R.string.please_upload_photo))
            return false
        }
        return true
    }
    fun proofOfIdValid(context: Context): Boolean {
        if (userData.value?.aadhaarTag == 2) {

            errors.postValue(context.getString(R.string.do_you_have_aadhaar_card_))
            return false
        } else if (userData.value?.aadhaarTag == 1) {
            if (userData.value?.aadhaarNo.toString().isEmpty()) {

                errors.postValue(context.getString(R.string.enter_aadhaar_number))
                return false
            } else if (userData.value?.aadhaarCheckBox == 0) {
                errors.postValue(context.getString(R.string.please_select_checkbox))
                return false
            }
        } else if (userData.value?.aadhaarTag == 0) {
            if (userData.value?.aadhaarEnrollmentNo.toString().trim().isEmpty()) {

                errors.postValue(context.getString(R.string.enter_aadhaar_enrollment_number))
                return false
            } else if (userData.value?.aadhaarEnrollmentNo.toString().trim().length !in 12..16) {
                errors.postValue(context.getString(R.string.aadhaar_enrollment_number_must_be_between_12_and_16_digits))
                return false
            } else if (userData.value?.aadhaarEnrollmentUploadSlip.toString().trim().isEmpty()) {
                errors.postValue(context.getString(R.string.upload_aadhaar_enrollment_slip_))
                return false
            }
        }
        if (userData.value?.identityProofName.toString().trim().isEmpty()) {

            errors.postValue(context.getString(R.string.please_select_identity_proof))
            return false
        }
        if (userData.value?.identityProofUpload.toString().trim().isEmpty()) {

            errors.postValue(context.getString(R.string.please_upload_supporting_document))
            return false
        }
        return true
    }
    fun proofOfAddressValid(context: Context): Boolean {
        if (userData.value?.natureDocumentAddressProofName.toString().trim().isEmpty()) {
            errors.postValue(context.getString(R.string.please_upload_supporting_document))
            return false
        } else if (userData.value?.documentAddressProofPhoto.toString().isEmpty()) {
            errors.postValue(context.getString(R.string.please_select_address_proof))
            return false
        } else if (userData.value?.address.toString().isEmpty()) {

            errors.postValue(context.getString(R.string.please_enter_correspondence_address))
            return false
        } else if (userData.value?.stateName.toString().trim().isEmpty()) {

            errors.postValue(context.getString(R.string.please_select_state_uts))
            return false
        } else if (userData.value?.districtName.toString().trim().isEmpty()) {
            errors.postValue(context.getString(R.string.please_select_district))
            return false
        } else if (userData.value?.subDistrictName.toString().trim().isEmpty()) {
            errors.postValue(context.getString(R.string.please_select_city_sub_district_tehsil))
            return false
        } else if (userData.value?.pincodeName.toString().trim().isEmpty()) {
            errors.postValue(context.getString(R.string.please_enter_pincode))
            return false
        }
        return true
    }
    fun disabilityDetailsValid(context: Context): Boolean {
        if (userData.value?.disabilityTypeName.toString().isEmpty()) {

            errors.postValue(context.getString(R.string.please_select_disability_type))
            return false
        } else if (userData.value?.disabilityBirth == "0") {
            errors.postValue(context.getString(R.string.please_check_disability_by_birth_yes_no))
            return false
        } else if (userData.value?.disabilityBirth == "Since") {
            if (userData.value?.disabilitySinceName.toString().isEmpty()) {
                errors.postValue(context.getString(R.string.please_select_disability_since))
                return false
            }
        } else if (userData.value?.haveDisabilityCertificate == 2) {
            errors.postValue(context.getString(R.string.please_select_do_you_have_disability_certificate_yes_no))
            return false
        } else if (userData.value?.haveDisabilityCertificate == 1) {
            if (userData.value?.uploadDisabilityCertificate.toString().isEmpty()) {

                errors.postValue(context.getString(R.string.please_upload_disability_certificate))
                return false
            } else if (userData.value?.serialNumber.toString().isEmpty()) {

                errors.postValue(context.getString(R.string.please_enter_sr_no_registration_no_of_certificate))
                return false
            } else if (userData.value?.dateOfCertificate.toString().isEmpty()) {

                errors.postValue(context.getString(R.string.please_select_date_of_issuance_of_certificate))
                return false
            } else if (userData.value?.detailOfAuthorityName.toString().isEmpty()) {

                errors.postValue(context.getString(R.string.please_select_details_of_issuing_authority))
                return false
            } else if (userData.value?.disabilityPercentage.toString().trim().isNotEmpty() &&
                (userData.value?.disabilityPercentage.toString()
                    .toInt() < 0 || userData.value?.disabilityPercentage.toString().toInt() > 100)
            ) {
                errors.postValue(context.getString(R.string.enter_a_number_between_1_and_100))
                return false
            }
        }
        return true
    }
    fun hospitalAssessmentValid(context: Context): Boolean {
        if (userData.value?.treatingHospitalTag == "1") {
            if (userData.value?.hospitalStateName.toString().trim().isEmpty()) {

                errors.postValue(context.getString(R.string.please_select_state_uts))
                return false
            } else if (userData.value?.hospitalDistrictName.toString().trim().isEmpty()) {
                errors.postValue(context.getString(R.string.please_select_district))
                return false
            }
        } else if (userData.value?.hospitalNameName.toString().trim().isEmpty()) {

            errors.postValue(context.getString(R.string.select_hospital_name))
            return false
        } else if (userData.value?.hospitalCheckBox == "false") {
            errors.postValue(context.getString(R.string.you_must_check_the_box_to_confirm_that_you_have_read_and_understood_the_process))
            return false
        }
        return true
    }
}

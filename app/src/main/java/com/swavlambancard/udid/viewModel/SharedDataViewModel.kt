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
    val userData = MutableLiveData(PwdApplication()) // Initialize with default fields
    val errors = MutableLiveData<String>()

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
                                errors.postValue(
                                    errorBody.getString("message") ?: "Bad Request"
                                )
                                dismissLoader()
                            }

                            401 -> {
                                val errorBody = JSONObject(response.errorBody()!!.string())
                                errors.postValue(
                                    errorBody.getString("message") ?: "Bad Request"
                                )
                                UDID.closeAndRestartApplication()
                                dismissLoader()
                            }

                            500 -> {//Internal Server error
                                errors.postValue("Internal Server error")
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
                    errors.postValue("Time out Please try again")
                }
                dismissLoader()
            }
        }
    }

    fun savePWDForm(
        context: Context,
        fullName: RequestBody?,
        regionalFullName: RequestBody?,
        regionalLanguage: RequestBody?,
        mobile: RequestBody?,
        email: RequestBody?,
        dob: RequestBody?,
        gender: RequestBody?,//=>M/F/T
        guardianRelation: RequestBody?,//Mother/Father/Guardian
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
    ) {
        // can be launched in a separate asynchronous job
        networkCheck(context, true)
        job = scope.launch {
            try {
                val response = repository.savePwdForm(
                    fullName,
                    regionalFullName,
                    regionalLanguage,
                    mobile,
                    email,
                    dob,
                    gender,
                    guardianRelation,
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
                    declaration
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
                                errors.postValue(
                                    errorBody.getString("message") ?: "Bad Request"
                                )
                                dismissLoader()
                            }

                            401 -> {
                                val errorBody = JSONObject(response.errorBody()!!.string())
                                errors.postValue(
                                    errorBody.getString("message") ?: "Bad Request"
                                )
                                UDID.closeAndRestartApplication()
                                dismissLoader()
                            }

                            500 -> {//Internal Server error
                                errors.postValue("Internal Server error")
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
                    errors.postValue("Time out Please try again")
                }
                dismissLoader()
            }
        }
    }

    fun personalDetails(context: Context): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
          Log.d("EMAIL",userData.value?.applicantEmail.toString())
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
        } else if (userData.value?.applicantEmail.toString().isEmpty()) {
            errors.postValue(context.getString(R.string.please_enter_an_email_address))
            return false
        } else if (!userData.value?.applicantEmail.toString().trim().matches(emailRegex)) {
            errors.postValue(context.getString(R.string.please_enter_a_valid_email_address))
            return false
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
            }
            else if (userData.value?.guardianContact.toString().isEmpty()) {
                errors.postValue(context.getString(R.string.please_enter_contact_number))
                return false
            }
        }
        else if (userData.value?.applicantsFMGCode == "Mother") {
            if (userData.value?.motherName.toString().isEmpty()) {
                errors.postValue(context.getString(R.string.please_enter_father_name))
                return false
            }
            else if (userData.value?.guardianContact.toString().isEmpty()) {
                errors.postValue(context.getString(R.string.please_enter_contact_number))
                return false
            }
        }
        else if (userData.value?.applicantsFMGCode == "Guardian") {
            if (userData.value?.relationWithPersonName.toString().isEmpty()) {
                errors.postValue(context.getString(R.string.please_select_relation_with_person))
                return false
            }
            else if (userData.value?.relationWithPersonCode!= "Self") {
                if (userData.value?.guardianName.toString().isEmpty()) {
                    errors.postValue(context.getString(R.string.please_enter_guardian_name))
                    return false
                }
                else if (userData.value?.guardianContact.toString().isEmpty()) {
                    errors.postValue(context.getString(R.string.please_enter_contact_number))
                    return false
                }
            }
        }
        else if(userData.value?.photo.toString().isEmpty()){
            errors.postValue(context.getString(R.string.please_upload_photo))
            return false
        }
        return true
    }


}

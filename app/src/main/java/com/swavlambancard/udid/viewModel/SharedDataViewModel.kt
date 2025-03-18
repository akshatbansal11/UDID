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
}

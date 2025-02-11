package com.swavlambancard.udid.viewModel


import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.swavlambancard.udid.R
import com.swavlambancard.udid.model.ApplicationStatusRequest
import com.swavlambancard.udid.model.ApplicationStatusResponse
import com.swavlambancard.udid.model.CommonResponse
import com.swavlambancard.udid.model.DropDownRequest
import com.swavlambancard.udid.model.DropDownResponse
import com.swavlambancard.udid.model.GenerateOtpRequest
import com.swavlambancard.udid.model.LoginResponse
import com.swavlambancard.udid.model.MyAccountResponse
import com.swavlambancard.udid.model.OTPResponse
import com.swavlambancard.udid.repository.Repository
import com.swavlambancard.udid.utilities.CommonUtils
import com.swavlambancard.udid.utilities.ProcessDialog
import com.swavlambancard.udid.utilities.UDID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import java.io.File
import java.net.SocketTimeoutException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

open class ViewModel : ViewModel() {

    private lateinit var repository: Repository
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var job: Job? = null

    var loginResult = MutableLiveData<LoginResponse>()
    var generateOtpLoginResult = MutableLiveData<OTPResponse>()
    var myAccountResult = MutableLiveData<MyAccountResponse>()
    var appStatusResult = MutableLiveData<ApplicationStatusResponse>()
    var dropDownResult = MutableLiveData<DropDownResponse>()
    var updateNameResult = MutableLiveData<CommonResponse>()
    var updateMobileResult = MutableLiveData<CommonResponse>()
    var updateAadhaarResult = MutableLiveData<CommonResponse>()
    var updateDobResult = MutableLiveData<CommonResponse>()
    var updateEmailResult = MutableLiveData<CommonResponse>()
    var surrenderResult = MutableLiveData<CommonResponse>()
    var lostCardResult = MutableLiveData<CommonResponse>()
    var feedbackAndQueryResult = MutableLiveData<CommonResponse>()
    var appealResult = MutableLiveData<CommonResponse>()
    var renewCardResult = MutableLiveData<CommonResponse>()
    var logoutResult = MutableLiveData<CommonResponse>()
    private val _downloadResult = MutableLiveData<Result<File>>()
    val downloadResult: LiveData<Result<File>> get() = _downloadResult
    val errors = MutableLiveData<String>()

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

    fun getLoginApi(context: Context, request: RequestBody) {
        networkCheck(context, true)
        job = scope.launch {
            try {
                val response = repository.getLogin(request)

                Log.e("response", response.toString())
                when (response.isSuccessful) {
                    true -> {
                        when (response.code()) {
                            200, 201 -> {
                                loginResult.postValue(response.body())
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

    fun getGenerateOtpLoginApi(context: Context, request: GenerateOtpRequest) {
        networkCheck(context, true)
        job = scope.launch {
            try {
                val response = repository.getGenerateOtpLogin(request)

                Log.e("response", response.toString())
                when (response.isSuccessful) {
                    true -> {
                        when (response.code()) {
                            200, 201 -> {
                                generateOtpLoginResult.postValue(response.body())
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

    fun getMyAccount(context: Context, request: RequestBody) {
        // can be launched in a separate asynchronous job
        networkCheck(context, true)
        job = scope.launch {
            try {
                val response = repository.getMyAccount(request)
                Log.e("response", response.toString())
                when (response.isSuccessful) {
                    true -> {
                        when (response.code()) {
                            200, 201 -> {
                                myAccountResult.postValue(response.body())
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

    fun getAppStatus(context: Context, request: ApplicationStatusRequest) {
        // can be launched in a separate asynchronous job
        networkCheck(context, true)
        job = scope.launch {
            try {
                val response = repository.getAppStatus(request)

                Log.e("response", response.toString())
                when (response.isSuccessful) {
                    true -> {
                        when (response.code()) {
                            200, 201 -> {
                                appStatusResult.postValue(response.body())
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

    fun getDropDown(context: Context, request: DropDownRequest) {
        // can be launched in a separate asynchronous job
        networkCheck(context, true)
        job = scope.launch {
            try {
                val response = repository.getDropDown(request)

                Log.e("response", response.toString())
                when (response.isSuccessful) {
                    true -> {
                        when (response.code()) {
                            200, 201 -> {
                                dropDownResult.postValue(response.body())
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

    fun getUpdateName(
        context: Context,
        applicationNumber: RequestBody?,
        name: RequestBody?,
        nameRegionalLanguage: RequestBody?,
        reason: RequestBody?,
        addressProofId: RequestBody?,
        otherReason: RequestBody?,
        otp: RequestBody?,
        type: RequestBody?,
        document: MultipartBody.Part?,
    ) {
        // can be launched in a separate asynchronous job
        networkCheck(context, true)
        job = scope.launch {
            try {
                val response = repository.updateName(
                    applicationNumber,
                    name,
                    nameRegionalLanguage,
                    reason,
                    addressProofId,
                    otherReason,
                    otp,
                    type,
                    document
                )

                Log.e("response", response.toString())
                when (response.isSuccessful) {
                    true -> {
                        when (response.code()) {
                            200, 201 -> {
                                updateNameResult.postValue(response.body())
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

    fun getUpdateMobile(
        context: Context,
        applicationNumber: RequestBody?,
        mobile: RequestBody?,
        otp: RequestBody?,
        type: RequestBody?,
    ) {
        // can be launched in a separate asynchronous job
        networkCheck(context, true)
        job = scope.launch {
            try {
                val response = repository.updateMobile(
                    applicationNumber,
                    mobile,
                    otp,
                    type
                )

                Log.e("response", response.toString())
                when (response.isSuccessful) {
                    true -> {
                        when (response.code()) {
                            200, 201 -> {
                                updateMobileResult.postValue(response.body())
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

    fun getUpdateAadhaar(
        context: Context,
        applicationNumber: RequestBody?,
        aadhaarNo: RequestBody?,
        addressProofId: RequestBody?,
        reason: RequestBody?,
        otherReason: RequestBody?,
        otp: RequestBody?,
        type: RequestBody?,
        document: MultipartBody.Part?,
    ) {
        // can be launched in a separate asynchronous job
        networkCheck(context, true)
        job = scope.launch {
            try {
                val response = repository.updateAadhaar(
                    applicationNumber,
                    aadhaarNo,
                    addressProofId,
                    reason,
                    otherReason,
                    otp,
                    type,
                    document
                )

                Log.e("response", response.toString())
                when (response.isSuccessful) {
                    true -> {
                        when (response.code()) {
                            200, 201 -> {
                                updateAadhaarResult.postValue(response.body())
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

    fun getUpdateDob(
        context: Context,
        applicationNumber: RequestBody?,
        dob: RequestBody?,
        reason: RequestBody?,
        otherReason: RequestBody?,
        otp: RequestBody?,
        type: RequestBody?,
        document: MultipartBody.Part?,
    ) {
        // can be launched in a separate asynchronous job
        networkCheck(context, true)
        job = scope.launch {
            try {
                val response = repository.updateDob(
                    applicationNumber,
                    dob,
                    reason,
                    otherReason,
                    otp,
                    type,
                    document
                )

                Log.e("response", response.toString())
                when (response.isSuccessful) {
                    true -> {
                        when (response.code()) {
                            200, 201 -> {
                                updateDobResult.postValue(response.body())
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

    fun getUpdateEmail(
        context: Context,
        applicationNumber: RequestBody?,
        email: RequestBody?,
        otp: RequestBody?,
        type: RequestBody?
    ) {
        // can be launched in a separate asynchronous job
        networkCheck(context, true)
        job = scope.launch {
            try {
                val response = repository.updateEmail(
                    applicationNumber,
                    email,
                    otp,
                    type
                )

                Log.e("response", response.toString())
                when (response.isSuccessful) {
                    true -> {
                        when (response.code()) {
                            200, 201 -> {
                                updateEmailResult.postValue(response.body())
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

    fun getSurrenderCard(
        context: Context,
        applicationNumber: RequestBody?,
        reason: RequestBody?,
        otherReason: RequestBody?,
        otp: RequestBody?,
        type: RequestBody?
    ) {
        // can be launched in a separate asynchronous job
        networkCheck(context, true)
        job = scope.launch {
            try {
                val response = repository.surrenderCard(
                    applicationNumber,
                    reason,
                    otherReason,
                    otp,
                    type
                )

                Log.e("response", response.toString())
                when (response.isSuccessful) {
                    true -> {
                        when (response.code()) {
                            200, 201 -> {
                                surrenderResult.postValue(response.body())
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

    fun getLostCard(
        context: Context,
        applicationNumber: RequestBody?,
        reason: RequestBody?,
        otherReason: RequestBody?,
        otp: RequestBody?,
        type: RequestBody?,
        document: MultipartBody.Part?,
    ) {
        // can be launched in a separate asynchronous job
        networkCheck(context, true)
        job = scope.launch {
            try {
                val response = repository.lostCard(
                    applicationNumber,
                    reason,
                    otherReason,
                    otp,
                    type,
                    document
                )

                Log.e("response", response.toString())
                when (response.isSuccessful) {
                    true -> {
                        when (response.code()) {
                            200, 201 -> {
                                lostCardResult.postValue(response.body())
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

    fun getFeedBack(
        context: Context,
        fullName: RequestBody?,
        mobile: RequestBody?,
        subject: RequestBody?,
        email: RequestBody?,
        message: RequestBody?,
        type: RequestBody?,
        document: MultipartBody.Part?,
    ) {
        // can be launched in a separate asynchronous job
        networkCheck(context, true)
        job = scope.launch {
            try {
                val response = repository.feedBack(
                    fullName,
                    mobile,
                    subject,
                    email,
                    message,
                    type,
                    document
                )

                Log.e("response", response.toString())
                when (response.isSuccessful) {
                    true -> {
                        when (response.code()) {
                            200, 201 -> {
                                feedbackAndQueryResult.postValue(response.body())
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

    fun getAppeal(
        context: Context,
        applicationNumber: RequestBody?,
        reason: RequestBody?,
        type: RequestBody?,
        document: MultipartBody.Part?,
    ) {
        // can be launched in a separate asynchronous job
        networkCheck(context, true)
        job = scope.launch {
            try {
                val response = repository.appeal(
                    applicationNumber,
                    reason,
                    type,
                    document
                )

                Log.e("response", response.toString())
                when (response.isSuccessful) {
                    true -> {
                        when (response.code()) {
                            200, 201 -> {
                                appealResult.postValue(response.body())
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

    fun getRenewCard(
        context: Context,
        applicationNumber: RequestBody?,
        renewalType: RequestBody?,
        currentAddress: RequestBody?,
        hospitalTreatingStateCode: RequestBody?,
        hospitalTreatingDistrictCode: RequestBody?,
        hospitalTreatingSubDistrictCode: RequestBody?,
        currentPincode: RequestBody?,
        hospitalTreatingId: RequestBody?,
        type: RequestBody?,
        address_proof_file: MultipartBody.Part?,
    ) {
        // can be launched in a separate asynchronous job
        networkCheck(context, true)
        job = scope.launch {
            try {
                val response = repository.getRenewCard(
                    applicationNumber,
                    renewalType,
                    currentAddress,
                    hospitalTreatingStateCode,
                    hospitalTreatingDistrictCode,
                    hospitalTreatingSubDistrictCode,
                    currentPincode,
                    hospitalTreatingId,
                    type,
                    address_proof_file
                )

                Log.e("response", response.toString())
                when (response.isSuccessful) {
                    true -> {
                        when (response.code()) {
                            200, 201 -> {
                                renewCardResult.postValue(response.body())
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

    fun getLogout(
        context: Context,
        applicationNumber: RequestBody?,
        type: RequestBody?,
    ) {
        // can be launched in a separate asynchronous job
        networkCheck(context, true)
        job = scope.launch {
            try {
                val response = repository.logout(
                    applicationNumber, type
                )

                Log.e("response", response.toString())
                when (response.isSuccessful) {
                    true -> {
                        when (response.code()) {
                            200, 201 -> {
                                logoutResult.postValue(response.body())
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

    fun downloadApplication(context: Context, request: RequestBody) {
        networkCheck(context, true)
        scope.launch {
            try {
                val response = repository.downloadApplication(request)
                Log.e("response", response.toString())
                if (response.isSuccessful && response.code() in 200..201) {
                    response.body()?.bytes()?.let { data ->
                        convertToPDF(context,context.getString(R.string.application), data)
                    } ?: _downloadResult.postValue(Result.failure(Exception("No data received")))
                } else {
                    handleError(response)
                }
            } catch (e: Exception) {
                handleException(e)
            } finally {
                dismissLoader()
            }
        }
    }

    fun downloadReceipt(context: Context, request: RequestBody) {
        networkCheck(context, true)
        scope.launch {
            try {
                val response = repository.downloadReceipt(request)
                Log.e("response", response.toString())
                if (response.isSuccessful && response.code() in 200..201) {
                    response.body()?.bytes()?.let { data ->
                        convertToPDF(context,context.getString(R.string.receipt), data)
                    } ?: _downloadResult.postValue(Result.failure(Exception("No data received")))
                } else {
                    handleError(response)
                }
            } catch (e: Exception) {
                handleException(e)
            } finally {
                dismissLoader()
            }
        }
    }

    fun downloadEDisabilityCertificate(context: Context, request: RequestBody) {
        networkCheck(context, true)
        scope.launch {
            try {
                val response = repository.downloadEDisabilityCertificate(request)
                Log.e("response", response.toString())
                if (response.isSuccessful && response.code() in 200..201) {
                    response.body()?.bytes()?.let { data ->
                        convertToPDF(context,context.getString(R.string.your_e_disability_certificate), data)
                    } ?: _downloadResult.postValue(Result.failure(Exception("No data received")))
                } else {
                    handleError(response)
                }
            } catch (e: Exception) {
                handleException(e)
            } finally {
                dismissLoader()
            }
        }
    }
    fun downloadUdidCard(context: Context, request: RequestBody) {
        networkCheck(context, true)
        scope.launch {
            try {
                val response = repository.downloadUdidCard(request)
                Log.e("response", response.toString())
                if (response.isSuccessful && response.code() in 200..201) {
                    response.body()?.bytes()?.let { data ->
                        convertToPDF(context,context.getString(R.string.your_e_udid_card), data)
                    } ?: _downloadResult.postValue(Result.failure(Exception("No data received")))
                } else {
                    handleError(response)
                }
            } catch (e: Exception) {
                handleException(e)
            } finally {
                dismissLoader()
            }
        }
    }

    fun downloadDoctorDiagnosisSheet(context: Context, request: RequestBody) {
        networkCheck(context, true)
        scope.launch {
            try {
                val response = repository.downloadDoctorDiagnosisSheet(request)
                Log.e("response", response.toString())
                if (response.isSuccessful && response.code() in 200..201) {
                    response.body()?.bytes()?.let { data ->
                        convertToPDF(context,context.getString(R.string.doctor_diagnosis_sheet), data)
                    } ?: _downloadResult.postValue(Result.failure(Exception("No data received")))
                } else {
                    handleError(response)
                }
            } catch (e: Exception) {
                handleException(e)
            } finally {
                dismissLoader()
            }
        }
    }


    private fun convertToPDF(context: Context, fileName: String, data: ByteArray) {
        try {
            val temporaryDirectory = File(context.cacheDir, "pdfs").apply { if (!exists()) mkdirs() }
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val pdfFile = File(temporaryDirectory, "$fileName $timestamp.pdf").apply { writeBytes(data) }
            _downloadResult.postValue(Result.success(pdfFile))
        } catch (e: Exception) {
            _downloadResult.postValue(Result.failure(e))
        }
    }

    private fun handleError(response: Response<ResponseBody>) {
        try {
            val errorBody = response.errorBody()?.string()?.let { JSONObject(it) }
            val errorMessage = errorBody?.getString("message") ?: "Unknown Error"

            when (response.code()) {
                400, 403, 404 -> {
                    errors.postValue(errorMessage)
                }
                401 -> {
                    errors.postValue(errorMessage)
                    UDID.closeAndRestartApplication()
                }
                500 -> {
                    errors.postValue("Internal Server Error")
                }
                else -> {
                    errors.postValue("Unexpected Error")
                }
            }
        } catch (e: Exception) {
            errors.postValue("Error parsing error response: ${e.message}")
        }
    }

    private fun handleException(exception: Exception) {
        when (exception) {
            is SocketTimeoutException -> {
                errors.postValue("Timeout: Please try again")
            }
            else -> {
                errors.postValue("An error occurred: ${exception.message}")
            }
        }
    }
}
package com.udid.viewModel


import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udid.model.ApplicationStatusRequest
import com.udid.model.ApplicationStatusResponse
import com.udid.model.DropDownRequest
import com.udid.model.DropDownResponse
import com.udid.model.GenerateOtpRequest
import com.udid.model.LoginResponse
import com.udid.model.MyAccountResponse
import com.udid.repository.Repository
import com.udid.utilities.ProcessDialog
import com.udid.utilities.UDID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.SocketTimeoutException

class ViewModel : ViewModel() {

    private lateinit var repository: Repository
    private val scope = CoroutineScope(Dispatchers.IO)
    private var job: Job? = null

    var loginResult = MutableLiveData<LoginResponse>()
    var generateOtpLoginResult = MutableLiveData<LoginResponse>()
    var myAccountResult = MutableLiveData<MyAccountResponse>()
    var appStatusResult = MutableLiveData<ApplicationStatusResponse>()
    var dropDownResult = MutableLiveData<DropDownResponse>()

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
        if (com.udid.utilities.CommonUtils.isNetworkAvailable(context) && showLoader) {
            context?.let { showLoader(context) }
        } else if (!com.udid.utilities.CommonUtils.isNetworkAvailable(context)) {
            com.udid.utilities.CommonUtils.displayNetworkAlert(context, false)
            return
        }
    }

    fun getLoginApi(context: Context, request: String) {
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

    fun getMyAccount(context: Context, request: String) {
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
}
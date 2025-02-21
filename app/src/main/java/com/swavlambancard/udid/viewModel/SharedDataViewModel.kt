package com.swavlambancard.udid.viewModel


import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.swavlambancard.udid.model.EditProfileRequest
import com.swavlambancard.udid.model.EditProfileResponse
import com.swavlambancard.udid.model.PwdApplication
import com.swavlambancard.udid.repository.Repository
import com.swavlambancard.udid.utilities.CommonUtils
import com.swavlambancard.udid.utilities.ProcessDialog
import com.swavlambancard.udid.utilities.UDID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.SocketTimeoutException


class SharedDataViewModel : ViewModel() {
    private lateinit var repository: Repository
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var job: Job? = null
    var editProfileResult = MutableLiveData<EditProfileResponse>()
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


    fun valid(): Boolean{
        if(userData.value?.applicantFullName.toString().isEmpty()){
            errors.postValue("Please enter Applicant Full Name")
            return false
        }
        return true
    }
}

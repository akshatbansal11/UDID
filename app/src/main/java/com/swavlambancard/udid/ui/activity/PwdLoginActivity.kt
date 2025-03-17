package com.swavlambancard.udid.ui.activity

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.gson.Gson
import com.swavlambancard.udid.R
import com.swavlambancard.udid.databinding.ActivityPwdloginBinding
import com.swavlambancard.udid.model.UpdateRequest
import com.swavlambancard.udid.model.UserData
import com.swavlambancard.udid.model.UserDataPwdApplicationStatus
import com.swavlambancard.udid.utilities.AppConstants
import com.swavlambancard.udid.utilities.BaseActivity
import com.swavlambancard.udid.utilities.EncryptionModel
import com.swavlambancard.udid.utilities.Utility
import com.swavlambancard.udid.utilities.Utility.dateConvertToFormat
import com.swavlambancard.udid.utilities.Utility.getPreferenceString
import com.swavlambancard.udid.utilities.Utility.showSnackbar
import com.swavlambancard.udid.viewModel.ViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.Calendar

class PwdLoginActivity : BaseActivity<ActivityPwdloginBinding>() {

    private var mBinding: ActivityPwdloginBinding? = null
    private var viewModel = ViewModel()
    var date: String? = null


    override val layoutId: Int
        get() = R.layout.activity_pwdlogin

    override fun initView() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
        viewModel.init()
    }

    override fun setVariables() {
        val isRemembered = getPreferenceString(this, AppConstants.REMEMBER_MEE) == "isChecked"
        if (isRemembered) {
            mBinding?.etEnrollment?.setText(
                getPreferenceString(
                    this,
                    AppConstants.USERNAME
                ).let { EncryptionModel.aesDecrypt(it) }
            )
            mBinding?.etDob?.text = dateConvertToFormat(getPreferenceString(
                this,
                AppConstants.PASSWORD
            ).let { EncryptionModel.aesDecrypt(it) })
            date=getPreferenceString(
                this,
                AppConstants.PASSWORD
            ).let { EncryptionModel.aesDecrypt(it) }
        }
        mBinding?.checkBoxRememberMe?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Utility.savePreferencesString(this@PwdLoginActivity, AppConstants.REMEMBER_MEE, "isChecked")
            } else {
                Utility.savePreferencesString(this@PwdLoginActivity, AppConstants.REMEMBER_MEE, "unchecked")
            }
        }
    }

    override fun setObservers() {

        viewModel.loginResult.observe(this) {
            val userResponseModel = it
            if (userResponseModel != null) {
                when (userResponseModel._resultflag) {
                    0 -> {
                        mBinding?.rlParent?.let { it1 ->
                            showSnackbar(
                                it1,
                                userResponseModel.message
                            )
                        }
                    }
                    else -> {
                        Utility.savePreferencesString(
                            this,
                            AppConstants.USERNAME,
                            EncryptionModel.aesEncrypt(mBinding?.etEnrollment?.text.toString().trim())
                        )
                        Utility.savePreferencesString(
                            this,
                            AppConstants.PASSWORD,
                            EncryptionModel.aesEncrypt(date.toString())
                        )
                        val data =
                            JSONObject(EncryptionModel.aesDecrypt(userResponseModel._result.data))
                        Log.d("data",EncryptionModel.aesDecrypt(userResponseModel._result.data))
                        val gson = Gson()
                        val userData = gson.fromJson(data.toString(), UserData::class.java)
                        Log.d("data1",userData.toString())
                        com.swavlambancard.udid.utilities.Preferences.setPreference(
                            this, AppConstants.LOGIN_DATA, UserData(
                                userData.login_status,
                                userData.id,
                                userData.application_number,
                                userData.udid_number,
                                userData.regional_language,
                                userData.full_name_i18n,
                                userData.full_name,
                                userData.father_name,
                                userData.dob,
                                userData.gender,
                                userData.mobile,
                                userData.email,
                                userData.photo,
                                userData.current_address,
                                userData.current_state_code,
                                userData.current_district_code,
                                userData.current_subdistrict_code,
                                userData.current_village_code,
                                userData.current_pincode,
                                userData.disability_type_id,
                                userData.application_status,
                                userData.certificate_generate_date,
                                userData.rejected_date,
                                userData.disability_type_pt,
                                userData.pwd_card_expiry_date,
                                userData.aadhaar_no,
                                userData.transfer_date,
                                UserDataPwdApplicationStatus(userData.pwdapplicationstatus.status_name),
                                userData.pwddispatch,
                                userData.photo_path,
                                userData.appealrequest,
                                userData.renewalrequest,
                                userData.surrenderrequest,
                                userData.lostcardrequest,
                                UpdateRequest(
                                    userData.updaterequest?.Name,
                                    userData.updaterequest?.Email,
                                    userData.updaterequest?.Mobile,
                                    userData.updaterequest?.AadhaarNumber,
                                    userData.updaterequest?.DateOfBirth
                                )
                            )
                        )
                        userResponseModel._result.token.let { it1 ->
                            Utility.savePreferencesString(
                                this, com.swavlambancard.udid.utilities.PrefEntities.TOKEN,
                                it1
                            )
                        }
                        startActivity(Intent(this, DashboardActivity::class.java))
                        finishAffinity()
                    }
                }
            }
        }

        viewModel.errors.observe(this) {
            mBinding?.let { it1 -> showSnackbar(it1.rlParent, it) }
        }
    }

    inner class ClickActions {
        fun backPress(view: View) {
            onBackPressedDispatcher.onBackPressed()
        }

        fun dob(view: View) {
            mBinding?.etDob?.let { calenderOpen(this@PwdLoginActivity, it) }
        }

        fun login(view: View) {
            if (valid()) {
                val loginRequestJson = JSONObject().apply {
                    put("application_number", mBinding?.etEnrollment?.text.toString().trim())
                    put("dob", date)
                    put("type", "mobile")
                }
                Log.d("SerialDecrypt", EncryptionModel.aesDecrypt("MTdlNjM0Y2ZlZTAxNTgwYjpOVTNhOUg5Q3hGeWlLM2o3VXI5c0tnPT06NTg3YzJlZGZlMzMyZGZhZDQxN2U2NWVkMWYzNjYzODA="))
                val encryptedString = EncryptionModel.aesEncrypt(loginRequestJson.toString())
                val requestBody = encryptedString.toRequestBody("text/plain".toMediaTypeOrNull())
                viewModel.getLoginApi(
                    this@PwdLoginActivity,
                    requestBody
                )

                Log.e("Decrypted Data" ,EncryptionModel.aesDecrypt(encryptedString))
                Log.e("Encrypted Data" ,encryptedString)
            }
        }
    }

    private fun calenderOpen(context: Context, editText: TextView) {
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
                Log.d(
                    "Date",
                    "onDateSet: MM/dd/yyyy: $formattedMonth/$formattedDay/$selectedYear"
                )
                date = "$selectedYear-$formattedMonth-$formattedDay"
                mBinding?.etDob?.text = "$formattedDay/$formattedMonth/$selectedYear"
            },
            year, month, day
        )

        dialog.datePicker.maxDate = System.currentTimeMillis()

        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun valid(): Boolean {
        if (mBinding?.etEnrollment?.text.toString().trim().isEmpty()) {
            mBinding?.rlParent?.let {
                showSnackbar(it, getString(R.string.please_enrollment_number_udid_number))
            }
            return false
        } else if (mBinding?.etDob?.text?.toString()?.trim()?.isEmpty() == true) {
            mBinding?.rlParent?.let {
                showSnackbar(it, getString(R.string.please_date_of_birth))
            }
            return false
        }
        return true
    }
}
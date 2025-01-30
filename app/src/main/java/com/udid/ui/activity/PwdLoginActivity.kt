package com.udid.ui.activity

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.gson.Gson
import com.udid.R
import com.udid.databinding.ActivityPwdloginBinding
import com.udid.model.UpdateRequest
import com.udid.model.UserData
import com.udid.model.UserDataPwdApplicationStatus
import com.udid.utilities.AppConstants
import com.udid.utilities.BaseActivity
import com.udid.utilities.EncryptionModel
import com.udid.utilities.PrefEntities
import com.udid.utilities.Preferences
import com.udid.utilities.Utility
import com.udid.utilities.Utility.getPreferenceString
import com.udid.utilities.Utility.showSnackbar
import com.udid.utilities.crypt.EncryptionHelper
import com.udid.utilities.toast
import com.udid.viewModel.ViewModel
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
        val isRemembered = getPreferenceString(this, AppConstants.REMEMBER_MEE) == "isChecked"
        if (isRemembered) {
            mBinding?.etEnrollment?.setText(
                getPreferenceString(
                    this,
                    AppConstants.USERNAME
                ).let { EncryptionHelper.decrypt(it) }
            )
            mBinding?.etDob?.text = getPreferenceString(
                this,
                AppConstants.PASSWORD
            ).let { EncryptionHelper.decrypt(it) }
            date=getPreferenceString(
                this,
                AppConstants.PASSWORD
            ).let { EncryptionHelper.decrypt(it) }
        }
        mBinding?.checkBoxRememberMe?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Utility.savePreferencesString(this@PwdLoginActivity, AppConstants.REMEMBER_MEE, "isChecked")
            } else {
                Utility.savePreferencesString(this@PwdLoginActivity, AppConstants.REMEMBER_MEE, "unchecked")
            }
        }
    }

    override fun setVariables() {
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
                        val userName = mBinding?.etEnrollment?.text.toString().trim()
                        val password = date

                        if (password != null) {
                            if (userName.isNotEmpty() && password.isNotEmpty()) {
                                val encryptedUserName = EncryptionHelper.encrypt(userName)
                                val encryptedPassword = password.let { it1 ->
                                    EncryptionHelper.encrypt(
                                        it1
                                    )
                                }

                                // Save encrypted data in SharedPreferences
                                Utility.savePreferencesString(
                                    this,
                                    AppConstants.USERNAME,
                                    encryptedUserName
                                )
                                if (encryptedPassword != null) {
                                    Utility.savePreferencesString(
                                        this,
                                        AppConstants.PASSWORD,
                                        encryptedPassword
                                    )
                                }
                            }
                        }
                        val data =
                            JSONObject(EncryptionModel.aesDecrypt(userResponseModel._result.data))
                        Log.d("data",EncryptionModel.aesDecrypt(userResponseModel._result.data))
                        val gson = Gson()
                        val userData = gson.fromJson(data.toString(), UserData::class.java)
                        Log.d("data1",userData.toString())
                        Preferences.setPreference(
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
                                    userData.updaterequest.Name,
                                    userData.updaterequest.Email,
                                    userData.updaterequest.Mobile,
                                    userData.updaterequest.AadhaarNumber,
                                    userData.updaterequest.DateOfBirth
                                )
                            )
                        )
                        userResponseModel._result.token.let { it1 ->
                            Utility.savePreferencesString(
                                this, PrefEntities.TOKEN,
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
//                    put("type","mobile")
                    put("dob", date)
                }
                viewModel.getLoginApi(
                    this@PwdLoginActivity,
                    EncryptionModel.aesEncrypt(loginRequestJson.toString())
                )
                Log.e("Decrypted Data" ,EncryptionModel.aesDecrypt(EncryptionModel.aesEncrypt(loginRequestJson.toString())))
            }
        }
    }

    private fun calenderOpen(
        context: Context,
        editText: TextView,
    ) {
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
                val adjustedMonth = selectedMonth + 1 // Months are 0-based
                Log.d("Date", "onDateSet: MM/dd/yyyy: $adjustedMonth/$selectedDay/$selectedYear")
                date = "$selectedYear-$adjustedMonth-$selectedDay"
                editText.text = "$selectedDay/$adjustedMonth/$selectedYear"
            },
            year, month, day
        )
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
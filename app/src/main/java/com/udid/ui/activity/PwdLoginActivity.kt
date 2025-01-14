package com.udid.ui.activity

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import android.widget.TextView
import com.udid.R
import com.udid.databinding.ActivityPwdloginBinding
import com.udid.utilities.AppConstants
import com.udid.utilities.BaseActivity
import com.udid.utilities.EncryptionModel
import com.udid.utilities.PrefEntities
import com.udid.utilities.Utility
import com.udid.utilities.Utility.showSnackbar
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

                    2 -> {
                        toast("Need To update your mobile number from website")
                    }

                    else -> {
                        val data =
                            JSONObject(EncryptionModel.aesDecrypt(userResponseModel._result.data))
                        Log.d("data",EncryptionModel.aesDecrypt(userResponseModel._result.data))
                        Utility.savePreferencesString(
                            this, AppConstants.APPLICATION_NUMBER,
                            data.getString("application_number")
                        )
                        userResponseModel._result.token.let { it1 ->
                            Utility.savePreferencesString(
                                this, PrefEntities.TOKEN,
                                it1
                            )
                        }
                        val pwdApplicationStatus = data.getJSONObject("pwdapplicationstatus")
                        val statusName = pwdApplicationStatus.getString("status_name")
                        Utility.savePreferencesString(
                            this, AppConstants.STATUS_NAME,
                            statusName)
                        Utility.savePreferencesString(
                            this,
                            AppConstants.photo,
                            data.getString("photo_path")
                        )
                        Utility.savePreferencesString(
                            this,
                            AppConstants.FULL_NAME,
                            data.getString("full_name")
                        )
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

                val loginRequestJson = JSONObject()
                loginRequestJson.put(
                    "application_number",
                    mBinding?.etEnrollment?.text.toString().trim()
                )
                loginRequestJson.put("dob", date)
//              loginRequestJson.put("type", "mobile")

                viewModel.getLoginApi(
                    this@PwdLoginActivity,
                    EncryptionModel.aesEncrypt(loginRequestJson.toString())
                )
            }
        }
    }

    private fun calenderOpen(
        context: Context,
        editText: TextView,
    ) {
        val cal: Calendar = Calendar.getInstance()
        val year: Int = cal.get(Calendar.YEAR)
        val month: Int = cal.get(Calendar.MONTH)
        val day: Int = cal.get(Calendar.DAY_OF_MONTH)
        val dialog = DatePickerDialog(
            context,
            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
            { _, year, month, day ->
                var month = month
                month += 1
                Log.d("Date", "onDateSet: MM/dd/yyy: $month/$day/$year")
                date = "$year-$month-$day"
                editText.text = "$day/$month/$year"
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
package com.udid.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.udid.R
import com.udid.databinding.ActivityOtpBinding
//import com.udid.model.LoginRequest
//import com.udid.model.OtpRequest
import com.udid.utilities.AppConstants
import com.udid.utilities.BaseActivity
import com.udid.utilities.PrefEntities
import com.udid.utilities.Utility
import com.udid.utilities.Utility.getPreferenceString
import com.udid.utilities.hideView
import com.udid.utilities.showView
import com.udid.utilities.toast
import com.udid.viewModel.ViewModel
import java.util.concurrent.TimeUnit

class OTPActivity : BaseActivity<ActivityOtpBinding>() {
    private var mBinding: ActivityOtpBinding? = null
    private var viewModel= ViewModel()
    private var isFrom: String? = null
    private var otp:Int ?= 0
    private var dateOfBirth: String? = null
    private var timer: CountDownTimer? = null
    private var millisFinished: Long = 0
    private lateinit var editTexts: Array<EditText>

    override val layoutId: Int
        get() = R.layout.activity_otp

    override fun initView() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
        viewModel.init()
        editTexts = arrayOf(
            findViewById(R.id.etOtp1),
            findViewById(R.id.etOtp2),
            findViewById(R.id.etOtp3),
            findViewById(R.id.etOtp4),
            findViewById(R.id.etOtp5),
            findViewById(R.id.etOtp6),
        )
        setupOTPInput()

        isFrom = intent.getStringExtra(AppConstants.IS_FROM)
        dateOfBirth = intent.getStringExtra(AppConstants.DATE_OF_BIRTH)
        otp = intent.getIntExtra(AppConstants.OTP,0)
        val otps = otp.toString()
        Log.d("Otp --" , otp.toString())

        mBinding?.etOtp1?.setText(otps[0].toString())
        mBinding?.etOtp2?.setText(otps[1].toString())
        mBinding?.etOtp3?.setText(otps[2].toString())
        mBinding?.etOtp4?.setText(otps[3].toString())
        mBinding?.etOtp5?.setText(otps[4].toString())
        mBinding?.etOtp6?.setText(otps[5].toString())

    }

    override fun setVariables() {

    }

    override fun setObservers() {
//        viewModel.otpLoginResult.observe(this) {
//            val userResponseModel = it
//            if (userResponseModel != null) {
//                if (userResponseModel._resultflag == 0) {
//                    mBinding?.rlParent?.let { it1 ->
//                        Utility.showSnackbar(
//                            it1,
//                            userResponseModel.message
//                        )
//                    }
//                } else {
//                    userResponseModel._result.token.let { it1 ->
//                        Utility.savePreferencesString(
//                            this, PrefEntities.TOKEN,
//                            it1
//                        )
//                    }
////                    userResponseModel._result.data.pwdapplicationstatus.status_name.let { it1 ->
////                        Utility.savePreferencesString(
////                            this, AppConstants.STATUS_NAME,
////                            it1
////                        )
////                    }
////                    userResponseModel._result.data.application_number.let { it1 ->
////                        Utility.savePreferencesString(
////                            this, AppConstants.APPLICATION_NUMBER,
////                            it1
////                        )
////                    }
////                    userResponseModel._result.data.photo_path.let { it1 ->
////                        Utility.savePreferencesString(this,AppConstants.photo,it1)
////                    }
//
//                    if (isFrom == "Department") {
//                        startActivity(
//                            Intent(
//                                this@OTPActivity,
//                                ListForDepartmentUserActivity::class.java
//                            )
//                        )
//                    } else {
//                        startActivity(Intent(this@OTPActivity, DashboardActivity::class.java))
//                    }
//                    finishAffinity()
//
//                }
//            }
//        }
        viewModel.loginResult.observe(this) {
            val userResponseModel = it
            if (userResponseModel != null) {
                when (userResponseModel._resultflag) {
                    0 -> {
                        mBinding?.rlParent?.let { it1 ->
                            Utility.showSnackbar(
                                it1,
                                userResponseModel.message
                            )
                        }
                    }

                    2 -> {
                        toast("Need To update your mobile number from website")
                    }

                    else -> {
                        mBinding?.etOtp1?.setText("")
                        mBinding?.etOtp1?.requestFocus()
                        mBinding?.etOtp2?.setText("")
                        mBinding?.etOtp3?.setText("")
                        mBinding?.etOtp4?.setText("")
                        mBinding?.etOtp5?.setText("")
                        mBinding?.etOtp6?.setText("")

//                        val otp =  userResponseModel.result.oto.toString()
//
//                        mBinding?.etOtp1?.setText(otp[0].toString())
//                        mBinding?.etOtp2?.setText(otp[1].toString())
//                        mBinding?.etOtp3?.setText(otp[2].toString())
//                        mBinding?.etOtp4?.setText(otp[3].toString())
//                        mBinding?.etOtp5?.setText(otp[4].toString())
//                        mBinding?.etOtp6?.setText(otp[5].toString())

//                        toast(userResponseModel.result.oto.toString())

//                        userResponseModel.result.application_number.let { it1 ->
//                            Utility.savePreferencesString(
//                                this, AppConstants.APPLICATION_NUMBER,
//                                it1
//                            )
//                        }
                    }
                }
            }
        }

        viewModel.errors.observe(this) {
            mBinding?.let { it1 -> Utility.showSnackbar(it1.rlParent, it) }
        }
    }

    inner class ClickActions {
        fun confirm(view: View) {
            val typedOTP =
                (mBinding?.etOtp1?.text.toString() +
                        mBinding?.etOtp2?.text.toString() +
                        mBinding?.etOtp3?.text.toString() +
                        mBinding?.etOtp4?.text.toString() +
                        mBinding?.etOtp5?.text.toString() +
                        mBinding?.etOtp6?.text.toString())

            if (typedOTP.isNotEmpty()) {
                if (typedOTP.length == 6) {
                    otpVerify(typedOTP)
                } else {
                    toast(getString(R.string.please_enter_correct_otp))
                }
            } else {
                toast(getString(R.string.please_enter_otp))
            }
        }

        fun resendOtp(view: View) {
            showTimer(mBinding?.tvTimer, 60000, mBinding?.tvResentOtp)
//            otpResend()
        }

        fun backPress(view: View) {
            onBackPressedDispatcher.onBackPressed()
        }
    }

//    private fun otpResend() {
//        viewModel.getLoginApi(
//            this,
//            LoginRequest(
//                getPreferenceString(this, AppConstants.APPLICATION_NUMBER),
//                dateOfBirth
//            )
//        )
//    }

    private fun otpVerify(otp: String) {
//        viewModel.getOtpLoginApi(
//            this,
//            OtpRequest(
//                getPreferenceString(this, AppConstants.APPLICATION_NUMBER),
//                otp
//            )
//        )
    }

    private fun showTimer(tvTimer: TextView?, remainingTime: Long?, tvResendOtp: TextView?) {
        tvResendOtp?.isEnabled = false
        timer = object : CountDownTimer(remainingTime ?: 0, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                tvTimer?.showView()
                tvResendOtp?.isEnabled = false
                tvTimer?.text = "00:" + String.format(
                    "%02d",
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
                )
                millisFinished = millisUntilFinished
            }

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onFinish() {
                tvTimer?.hideView()
                tvResendOtp?.isEnabled = true
            }
        }
        timer!!.start()
    }

    private fun setupOTPInput() {
        for (i in editTexts.indices) {
            editTexts[i].addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1 && i < editTexts.size - 1) {
                        editTexts[i + 1].requestFocus()
                    } else if (s?.isEmpty() == true && i > 0) {
                        editTexts[i - 1].requestFocus()
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // Not needed
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // Not needed
                }
            })

            editTexts[i].setOnKeyListener { _, keyCode, _ ->
                if (keyCode == 67 && i > 0 && editTexts[i].text.isEmpty()) {
                    editTexts[i - 1].requestFocus()
                }
                false
            }
        }
    }
}
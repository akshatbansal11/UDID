package com.udid.ui.activity

import android.view.View
import com.udid.R
import com.udid.databinding.ActivityUpdateMobileNumberBinding
import com.udid.model.GenerateOtpRequest
import com.udid.model.UserData
import com.udid.utilities.AppConstants
import com.udid.utilities.BaseActivity
import com.udid.utilities.EncryptionModel
import com.udid.utilities.JSEncryptService
import com.udid.utilities.Preferences.getPreferenceOfLogin
import com.udid.utilities.Utility
import com.udid.utilities.Utility.showSnackbar
import com.udid.utilities.showView
import com.udid.utilities.toast
import com.udid.viewModel.ViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class UpdateMobileNumberActivity : BaseActivity<ActivityUpdateMobileNumberBinding>() {

    private var mBinding: ActivityUpdateMobileNumberBinding? = null
    private var viewModel = ViewModel()

    override val layoutId: Int
        get() = R.layout.activity_update_mobile_number

    override fun initView() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
        viewModel.init()
    }

    override fun setVariables() {
        mBinding?.etCurrentNumber?.text =
            getPreferenceOfLogin(
                this,
                AppConstants.LOGIN_DATA,
                UserData::class.java
            ).mobile.toString()
    }

    override fun setObservers() {
        viewModel.generateOtpLoginResult.observe(this) {
            val userResponseModel = it
            if (userResponseModel?._resultflag != 0) {
                toast(userResponseModel.message)
                mBinding?.llOtp?.showView()
                mBinding?.scrollView?.post {
                    mBinding?.scrollView?.fullScroll(View.FOCUS_DOWN)
                }
            } else {
                mBinding?.clParent?.let { it1 -> showSnackbar(it1, userResponseModel.message) }
            }
        }
        viewModel.updateMobileResult.observe(this) {
            val userResponseModel = it
            if (userResponseModel?._resultflag != 0) {
                toast(userResponseModel.message)
                onBackPressedDispatcher.onBackPressed()
            } else {
                mBinding?.clParent?.let { it1 -> showSnackbar(it1, userResponseModel.message) }
            }
        }
        viewModel.errors.observe(this) {
            mBinding?.let { it1 -> showSnackbar(it1.clParent, it) }
        }
    }

    inner class ClickActions {
        fun backPress(view: View) {
            onBackPressedDispatcher.onBackPressed()
        }

        fun generateOtp(view: View) {
            if (valid()) {
                generateOtpApi()
            }
        }

        fun submit(view: View) {
            if (valid()) {
                if (mBinding?.etEnterOtp?.text.toString().trim().isNotEmpty()) {
                    updateMobileNumberApi()
                } else {
                    showSnackbar(mBinding?.clParent!!, "Please enter the OTP")
                }
            }
        }
    }

    private fun generateOtpApi() {
        viewModel.getGenerateOtpLoginApi(
            this,
            GenerateOtpRequest(
                application_number = JSEncryptService.encrypt(
                    getPreferenceOfLogin(
                        this,
                        AppConstants.LOGIN_DATA,
                        UserData::class.java
                    ).application_number.toString()
                )
            )
        )
    }

    private fun updateMobileNumberApi() {
        viewModel.getUpdateMobile(
            context = this,
            applicationNumber = JSEncryptService.encrypt(
                getPreferenceOfLogin(
                    this,
                    AppConstants.LOGIN_DATA,
                    UserData::class.java
                ).application_number.toString()
            )
                ?.toRequestBody(MultipartBody.FORM),
            mobile = JSEncryptService.encrypt(mBinding?.etUpdatedNumber?.text.toString().trim())
                ?.toRequestBody(MultipartBody.FORM),
            otp = JSEncryptService.encrypt(mBinding?.etEnterOtp?.text.toString().trim())?.toRequestBody(MultipartBody.FORM),
        )
    }

    private fun valid(): Boolean {
        if (mBinding?.etUpdatedNumber?.text?.toString().isNullOrEmpty()) {
            mBinding?.clParent?.let {
                showSnackbar(it, "Please enter the updated mobile number.")
            }
            return false
        } else if (mBinding?.etUpdatedNumber?.text?.length != 10) {
            mBinding?.clParent?.let {
                showSnackbar(it, "Mobile number must be exactly 10 digits.")
            }
            return false
        }
        return true
    }
}
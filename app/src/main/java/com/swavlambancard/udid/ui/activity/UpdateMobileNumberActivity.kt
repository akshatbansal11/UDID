package com.swavlambancard.udid.ui.activity

import android.view.View
import com.bumptech.glide.Glide
import com.swavlambancard.udid.R
import com.swavlambancard.udid.databinding.ActivityUpdateMobileNumberBinding
import com.swavlambancard.udid.model.GenerateOtpRequest
import com.swavlambancard.udid.model.UserData
import com.swavlambancard.udid.utilities.AppConstants
import com.swavlambancard.udid.utilities.BaseActivity
import com.swavlambancard.udid.utilities.JSEncryptService
import com.swavlambancard.udid.utilities.Preferences
import com.swavlambancard.udid.utilities.Preferences.getPreference
import com.swavlambancard.udid.utilities.Preferences.getPreferenceOfLogin
import com.swavlambancard.udid.utilities.Utility.showSnackbar
import com.swavlambancard.udid.utilities.showView
import com.swavlambancard.udid.utilities.toast
import com.swavlambancard.udid.viewModel.ViewModel
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
        mBinding?.etCurrentNumber?.text =if(getPreferenceOfLogin(
                this,
                AppConstants.LOGIN_DATA,
                UserData::class.java
            ).mobile?.toString().isNullOrEmpty()) getString(R.string.na) else
            getPreferenceOfLogin(
                this,
                AppConstants.LOGIN_DATA,
                UserData::class.java
            ).mobile.toString()

        mBinding?.ivProfile?.let {
            Glide.with(this)
                .load(getPreferenceOfLogin(
                    this,
                    AppConstants.LOGIN_DATA,
                    UserData::class.java
                ).photo_path)
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(it)
        }
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
                com.swavlambancard.udid.utilities.Preferences.setPreference(this, AppConstants.LOGIN_DATA, getPreferenceOfLogin(
                    this,
                    AppConstants.LOGIN_DATA,
                    UserData::class.java
                ).copy(
                    mobile = mBinding?.etUpdatedNumber?.text.toString().trim().toLongOrNull()
                ))
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
                    showSnackbar(mBinding?.clParent!!, getString(R.string.please_enter_the_otp))
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
            type = "mobile".toRequestBody(MultipartBody.FORM)
        )
    }

    private fun valid(): Boolean {
        if (mBinding?.etUpdatedNumber?.text?.toString().isNullOrEmpty()) {
            mBinding?.clParent?.let {
                showSnackbar(it, getString(R.string.please_enter_the_updated_mobile_number))
            }
            return false
        } else if (mBinding?.etUpdatedNumber?.text?.length != 10) {
            mBinding?.clParent?.let {
                showSnackbar(it, getString(R.string.mobile_number_must_be_exactly_10_digits))
            }
            return false
        }
        return true
    }
}
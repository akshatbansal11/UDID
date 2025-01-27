package com.udid.ui.activity

import android.content.Intent
import android.view.View
import com.bumptech.glide.Glide
import com.udid.R
import com.udid.databinding.ActivityUpdateEmailIdactivityBinding
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

class UpdateEmailIDActivity : BaseActivity<ActivityUpdateEmailIdactivityBinding>() {

    private var mBinding: ActivityUpdateEmailIdactivityBinding? = null
    private var viewModel = ViewModel()


    override val layoutId: Int
        get() = R.layout.activity_update_email_idactivity

    override fun initView() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
        viewModel.init()
    }

    override fun setVariables() {
        mBinding?.etCurrentEmailId?.text =
            getPreferenceOfLogin(
                this,
                AppConstants.LOGIN_DATA,
                UserData::class.java
            ).email.toString()

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
        viewModel.updateEmailResult.observe(this) {
            val userResponseModel = it
            if (userResponseModel?._resultflag != 0) {
                toast(userResponseModel.message)
                startActivity(
                    Intent(this, UpdateRequestActivity::class.java)
                        .putExtra(AppConstants.UPDATE_REQUEST,
                            getString(R.string.submit_update_email_id)))
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
        fun submit(view: View){
            if (valid()) {
                if (mBinding?.etEnterOtp?.text.toString().trim().isNotEmpty()) {
                    updateEmailIdApi()
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

    private fun updateEmailIdApi() {
        viewModel.getUpdateEmail(
            context = this,
            applicationNumber = JSEncryptService.encrypt(
                getPreferenceOfLogin(
                    this,
                    AppConstants.LOGIN_DATA,
                    UserData::class.java
                ).application_number.toString()
            )
                ?.toRequestBody(MultipartBody.FORM),
            email = JSEncryptService.encrypt(mBinding?.etUpdatedEmail?.text.toString().trim())
                ?.toRequestBody(MultipartBody.FORM),
            otp = JSEncryptService.encrypt(mBinding?.etEnterOtp?.text.toString().trim())?.toRequestBody(MultipartBody.FORM),
            type = "mobile".toRequestBody(MultipartBody.FORM)
        )
    }

    private fun valid(): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        if (mBinding?.etUpdatedEmail?.text.toString().trim().isEmpty()) {
            mBinding?.clParent?.let { showSnackbar(it, "Please enter an email address.") }
            return false
        }
        else if (!mBinding?.etUpdatedEmail?.text.toString().trim().matches(emailRegex)) {
            mBinding?.clParent?.let { showSnackbar(it, "Please enter a valid email address.") }
            return false
        }

        return true
    }
}
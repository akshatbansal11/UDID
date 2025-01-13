package com.udid.ui.activity

import android.view.View
import com.udid.R
import com.udid.databinding.ActivityUpdateEmailIdactivityBinding
import com.udid.utilities.BaseActivity
import com.udid.utilities.Utility
import com.udid.viewModel.ViewModel

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

    inner class ClickActions {
        fun backPress(view: View) {
            onBackPressedDispatcher.onBackPressed()
        }

        fun generateOtp(view: View) {
            if (valid()) {
                mBinding?.clParent?.let { Utility.showSnackbar(it, "Done OTP") }
            }
        }
    }


    private fun valid(): Boolean {
        val updatedEmail = mBinding?.etUpdatedEmail?.text?.toString()

        // Check if email is null or empty
        if (updatedEmail.isNullOrEmpty()) {
            mBinding?.clParent?.let { Utility.showSnackbar(it, "Please enter an email address.") }
            return false
        }

        // Regular expression to validate email format
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

        // Check if the email matches the regex
        if (!updatedEmail.matches(emailRegex)) {
            mBinding?.clParent?.let { Utility.showSnackbar(it, "Please enter a valid email address.") }
            return false
        }

        return true
    }


    override fun setVariables() {

    }

    override fun setObservers() {
    }
}
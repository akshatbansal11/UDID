package com.udid.ui.activity

import android.view.View
import com.udid.R
import com.udid.databinding.ActivityFeedbackAndQueryBinding
import com.udid.utilities.BaseActivity
import com.udid.utilities.Utility
import com.udid.viewModel.ViewModel

class FeedbackAndQueryActivity : BaseActivity<ActivityFeedbackAndQueryBinding>() {

    private var mBinding: ActivityFeedbackAndQueryBinding? = null
    private var viewModel= ViewModel()


    override val layoutId: Int
        get() = R.layout.activity_feedback_and_query

    override fun initView() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
        viewModel.init()
    }

    override fun setVariables() {

    }

    override fun setObservers() {
    }

    inner class ClickActions {
        fun backPress(view: View){
            onBackPressedDispatcher.onBackPressed()
        }

        fun submit(view: View){
            onBackPressedDispatcher.onBackPressed()
        }
        fun generateOtp(view: View){
            if(valid()){
                mBinding?.clParent?.let { Utility.showSnackbar(it,"Done OTP") }
            }
        }

    }

    private fun valid(): Boolean {
        val name = mBinding?.etName?.text?.toString()
        val email = mBinding?.etEmail?.text?.toString()
        val mobile = mBinding?.etMobile?.text?.toString()
        val subject = mBinding?.etSubject?.text?.toString()
        val message = mBinding?.etMessage?.text?.toString()
        val fileName = mBinding?.etFileName?.text?.toString()

//        if (fileName==getString(R.string.no_file_chosen)) {
//            mBinding?.clParent?.let { Utility.showSnackbar(it,"Please Upload Supporting Document.") }
//            return false
//        }
        if (name.isNullOrEmpty()) {
            mBinding?.clParent?.let { Utility.showSnackbar(it, "Please enter name.") }
            return false
        }
        if (subject.isNullOrEmpty()) {
            mBinding?.clParent?.let { Utility.showSnackbar(it, "Please enter subject.") }
            return false
        }
        if (message.isNullOrEmpty()) {
            mBinding?.clParent?.let { Utility.showSnackbar(it, "Please enter your message.") }
            return false
        }
        if (email.isNullOrEmpty()) {
            mBinding?.clParent?.let { Utility.showSnackbar(it, "Please enter an email address.") }
            return false
        }

        // Regular expression to validate email format
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

        // Check if the email matches the regex
        if (!email.matches(emailRegex)) {
            mBinding?.clParent?.let { Utility.showSnackbar(it, "Please enter a valid email address.") }
            return false
        }

        if (mobile.isNullOrEmpty()) {
            mBinding?.clParent?.let {
                Utility.showSnackbar(it, "Please enter the mobile number.")
            }
            return false
        }

        // Check if the mobile number is exactly 10 digits long
        if (mobile.length != 10) {
            mBinding?.clParent?.let {
                Utility.showSnackbar(it, "Mobile number must be exactly 10 digits.")
            }
            return false
        }

        return true
    }
}
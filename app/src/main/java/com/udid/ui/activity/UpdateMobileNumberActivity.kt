package com.udid.ui.activity

import android.view.View
import com.udid.R
import com.udid.databinding.ActivityUpdateMobileNumberBinding
import com.udid.utilities.BaseActivity
import com.udid.utilities.Utility
import com.udid.viewModel.ViewModel

class UpdateMobileNumberActivity : BaseActivity<ActivityUpdateMobileNumberBinding>() {

    private var mBinding: ActivityUpdateMobileNumberBinding? = null
    private var viewModel= ViewModel()


    override val layoutId: Int
        get() = R.layout.activity_update_mobile_number

    override fun initView() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
        viewModel.init()
    }
    inner class ClickActions {
        fun backPress(view: View){
            onBackPressedDispatcher.onBackPressed()
        }
        fun generateOtp(view: View){
            if(valid()){
                mBinding?.clParent?.let { Utility.showSnackbar(it,"Done OTP") }
            }
        }



    }

    private fun valid(): Boolean {
        val updatedNumber = mBinding?.etUpdatedNumber?.text?.toString()

        // Check if mobile number is null or empty
        if (updatedNumber.isNullOrEmpty()) {
            mBinding?.clParent?.let {
                Utility.showSnackbar(it, "Please enter the revised mobile number.")
            }
            return false
        }

        // Check if the mobile number is exactly 10 digits long
        if (updatedNumber.length != 10) {
            mBinding?.clParent?.let {
                Utility.showSnackbar(it, "Mobile number must be exactly 10 digits.")
            }
            return false
        }

        return true
    }

    override fun setVariables() {

    }

    override fun setObservers() {
    }
}
package com.udid.ui.activity

import android.text.InputType
import android.view.View
import com.udid.R
import com.udid.databinding.ActivityUpdateAadharNumberBinding
import com.udid.utilities.BaseActivity
import com.udid.viewModel.ViewModel

class UpdateAadharNumberActivity : BaseActivity<ActivityUpdateAadharNumberBinding>() {

    private var mBinding: ActivityUpdateAadharNumberBinding? = null
    private var viewModel= ViewModel()
    private var isPasswordVisible: Boolean = false


    override val layoutId: Int
        get() = R.layout.activity_update_aadhar_number

    override fun initView() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
        viewModel.init()
    }
    inner class ClickActions {
        fun backPress(view: View){
            onBackPressedDispatcher.onBackPressed()
        }
        fun toggleEye(view: View){
            togglePasswordVisibility()
        }
        fun submit(view: View){
            onBackPressedDispatcher.onBackPressed()
        }
    }
    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            mBinding?.etUpdatedAadhaarNumber?.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            mBinding?.ivTogglePassword?.setImageResource(R.drawable.ic_eye_off) // change to eye icon
        } else {
            mBinding?.etUpdatedAadhaarNumber?.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            mBinding?.ivTogglePassword?.setImageResource(R.drawable.ic_eye) // change to eye off icon
        }
        mBinding?.etUpdatedAadhaarNumber?.setSelection(mBinding?.etUpdatedAadhaarNumber?.text?.length ?: 0)  // Move cursor to end of text
        isPasswordVisible = !isPasswordVisible
    }
    override fun setVariables() {
    }

    override fun setObservers() {
    }

}
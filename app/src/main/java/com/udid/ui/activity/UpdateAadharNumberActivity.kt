package com.udid.ui.activity

import android.text.InputType
import android.view.View
import com.udid.R
import com.udid.databinding.ActivityUpdateAadharNumberBinding
import com.udid.utilities.BaseActivity
import com.udid.utilities.Utility
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
        fun generateOtp(view: View){
            if(valid()){
                mBinding?.clParent?.let { Utility.showSnackbar(it,"Done OTP") }
            }
        }
    }

    private fun valid(): Boolean {
        val aadhaarNumber = mBinding?.etUpdatedAadhaarNumber?.text?.toString()
        val identityProof = mBinding?.etIdentityProof?.text?.toString()
        val fileName = mBinding?.etFileName?.text?.toString()
        val reasonToUpdateAadhaar = mBinding?.etReasonToUpdateAadhar?.text?.toString()

        // Check if Aadhaar number is null or empty
        if (aadhaarNumber.isNullOrEmpty()) {
            mBinding?.clParent?.let { Utility.showSnackbar(it,"Aadhaar number cannot be empty") }
            return false
        }
        if (identityProof=="Select Identity Proof") {
            mBinding?.clParent?.let { Utility.showSnackbar(it,"Please select Identity Proof.") }
            return false
        }
//        if (fileName==getString(R.string.no_file_chosen)) {
//            mBinding?.clParent?.let { Utility.showSnackbar(it,"Please Upload Supporting Document.") }
//            return false
//        }
        if (reasonToUpdateAadhaar=="Select Reason to update Aadhaar Number") {
            mBinding?.clParent?.let { Utility.showSnackbar(it,"Please Select Reason to update Aadhaar Number") }
            return false
        }


//        // Check if Aadhaar number is 12 digits long
//        if (aadhaarNumber.length != 12) {
//            mBinding?.etUpdatedAadhaarNumber?.error = "Aadhaar number must be 12 digits long"
//            return false
//        }
//
//        // Check if Aadhaar number contains only digits
//        if (!aadhaarNumber.all { it.isDigit() }) {
//            mBinding?.etUpdatedAadhaarNumber?.error = "Aadhaar number must contain only digits"
//            return false
//        }

//        // Optionally validate using Verhoeff Algorithm
//        if (!isValidAadhaar(aadhaarNumber)) {
//            mBinding?.etUpdatedAadhaarNumber?.error = "Invalid Aadhaar number"
//            return false
//        }

        // If all checks pass
        return true
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
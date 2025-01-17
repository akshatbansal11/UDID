package com.udid.ui.activity

import android.view.View
import com.bumptech.glide.Glide
import com.udid.R
import com.udid.databinding.ActivityMyAccountBinding
import com.udid.utilities.AppConstants
import com.udid.utilities.BaseActivity
import com.udid.utilities.EncryptionModel
import com.udid.utilities.Utility
import com.udid.viewModel.ViewModel
import org.json.JSONObject

class MyAccountActivity : BaseActivity<ActivityMyAccountBinding>() {
    private var mBinding: ActivityMyAccountBinding? = null
    private var viewModel= ViewModel()
    override val layoutId: Int
        get() = R.layout.activity_my_account

    override fun initView() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
        viewModel.init()
        val myAccountJson = JSONObject()
        myAccountJson.put(
            "application_number",
            Utility.getPreferenceString(this@MyAccountActivity,AppConstants.APPLICATION_NUMBER)
        )
//        myAccountJson.put(
//            "type",
//            "mobile"
//        )
        viewModel.getMyAccount(this@MyAccountActivity, EncryptionModel.aesEncrypt(myAccountJson.toString()))
    }

    inner class ClickActions {
        fun backPress(view: View){
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun setVariables() {

//        mBinding?.circularImageView?.let { it1 ->
//            Glide.with(this)
//                .load(Utility.getPreferenceString(this,AppConstants.photo))
//                .placeholder(R.drawable.ic_profile)
//                .into(it1)
//        }
    }

    override fun setObservers() {
        viewModel.myAccountResult.observe(this) {
            val userResponseModel = it
            if (userResponseModel != null) {
                if (userResponseModel._resultflag == 0) {
                    mBinding?.rlParent?.let { it1 ->
                        Utility.showSnackbar(
                            it1,
                            userResponseModel.message
                        )
                    }
                }
                else {
                    val data =
                        JSONObject(EncryptionModel.aesDecrypt(userResponseModel._result))
                    mBinding?.etUdidNo?.text=data.getString("udid_number")
                    mBinding?.etNameOfApplication?.text=data.getString("full_name")
                    mBinding?.etDOB?.text=data.getString("dob")
                    mBinding?.etUdidGenerationDate?.text=data.getString("certificate_generate_date")
                    mBinding?.etAadhaarNo?.text=Utility.maskAadharNumber(data.getString("aadhaar_no"))
                    mBinding?.etGender?.text=data.getString("gender")
                    mBinding?.tvBlindness?.text=data.getString("disability_types")
                    mBinding?.etDisabilityPercentage?.text= data.getString("final_disability_percentage")
                    mBinding?.etMobile?.text=data.getString("mobile")
                    mBinding?.etEmailID?.text=data.getString("email")
                    mBinding?.etAddress?.text=data.getString("current_address")
                }
            }
        }

        viewModel.errors.observe(this) {
            mBinding?.let { it1 -> Utility.showSnackbar(it1.rlParent, it) }
        }
    }
}
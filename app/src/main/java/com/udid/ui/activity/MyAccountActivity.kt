package com.udid.ui.activity

import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.udid.R
import com.udid.databinding.ActivityMyAccountBinding
import com.udid.model.UserData
import com.udid.utilities.AppConstants
import com.udid.utilities.BaseActivity
import com.udid.utilities.EncryptionModel
import com.udid.utilities.Preferences.getPreferenceOfLogin
import com.udid.utilities.Utility
import com.udid.utilities.Utility.convertDate
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
            getPreferenceOfLogin(
                context,
                AppConstants.LOGIN_DATA,
                UserData::class.java
            ).application_number.toString()
        )
        myAccountJson.put(
            "type",
            "mobile"
        )
        viewModel.getMyAccount(this@MyAccountActivity, EncryptionModel.aesEncrypt(myAccountJson.toString()))
    }

    inner class ClickActions {
        fun backPress(view: View){
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun setVariables() {

        mBinding?.ivProfile?.let {
            Glide.with(this)
                .load(
                    getPreferenceOfLogin(
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
                    Log.d("Decrypted Data : ", data.toString())
                    mBinding?.etUdidNo?.text= data.getString("udid_number").ifEmpty { getString(R.string.na) }
                    mBinding?.etNameOfApplication?.text=data.getString("full_name").ifEmpty { getString(R.string.na) }
                    mBinding?.etDOB?.text=data.getString("dob").ifEmpty { "NA" }
                    mBinding?.etUdidGenerationDate?.text=convertDate(data.getString("certificate_generate_date")).ifEmpty { getString(R.string.na) }
                    mBinding?.etAadhaarNo?.text=Utility.maskAadharNumber(data.getString("aadhaar_no")).ifEmpty { getString(R.string.na) }
                    mBinding?.etGender?.text=data.getString("gender").ifEmpty { getString(R.string.na) }
                    mBinding?.tvBlindness?.text=data.getString("disability_types").ifEmpty { getString(R.string.na) }
                    mBinding?.etDisabilityPercentage?.text= data.getString("final_disability_percentage").ifEmpty { getString(R.string.na) }
                    mBinding?.etMobile?.text=data.getString("mobile").ifEmpty { getString(R.string.na) }
                    mBinding?.etEmailID?.text=data.getString("email").ifEmpty { getString(R.string.na) }
                    mBinding?.etAddress?.text=data.getString("current_address").ifEmpty { getString(R.string.na) }
                }
            }
        }

        viewModel.errors.observe(this) {
            mBinding?.let { it1 -> Utility.showSnackbar(it1.rlParent, it) }
        }
    }
}
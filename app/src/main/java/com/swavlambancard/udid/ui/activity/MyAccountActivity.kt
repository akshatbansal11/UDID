package com.swavlambancard.udid.ui.activity

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.swavlambancard.udid.R
import com.swavlambancard.udid.databinding.ActivityMyAccountBinding
import com.swavlambancard.udid.model.MyAccountData
import com.swavlambancard.udid.model.UserData
import com.swavlambancard.udid.utilities.AppConstants
import com.swavlambancard.udid.utilities.BaseActivity
import com.swavlambancard.udid.utilities.EncryptionModel
import com.swavlambancard.udid.utilities.Preferences.getPreferenceOfLogin
import com.swavlambancard.udid.utilities.Utility
import com.swavlambancard.udid.utilities.Utility.convertDate
import com.swavlambancard.udid.utilities.Utility.dateConvertToFormat
import com.swavlambancard.udid.utilities.hideView
import com.swavlambancard.udid.utilities.showView
import com.swavlambancard.udid.viewModel.ViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class MyAccountActivity : BaseActivity<ActivityMyAccountBinding>() {
    private var mBinding: ActivityMyAccountBinding? = null
    private var viewModel = ViewModel()
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
            ).application_number.toString().trim()
        )
        myAccountJson.put(
            "type",
            "mobile"
        )
        viewModel.getMyAccount(
            this@MyAccountActivity,
            EncryptionModel.aesEncrypt(myAccountJson.toString()).toRequestBody("text/plain".toMediaTypeOrNull())
        )
    }

    inner class ClickActions {
        fun backPress(view: View) {
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
                    ).photo_path
                )
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(it)
        }
    }

    @SuppressLint("SetTextI18n")
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
                } else {
                    val data =
                        JSONObject(EncryptionModel.aesDecrypt(userResponseModel._result))
                    val gson = Gson()
                    val userData = gson.fromJson(data.toString(), MyAccountData::class.java)
                    Log.d("Decrypted Data : ", data.toString())
                    if(userData.udid_number.isNullOrEmpty()){
                        mBinding?.etUdidNo?.hideView()
                        mBinding?.tvUdidNo?.hideView()
                        mBinding?.tvUdidGenerationDate?.hideView()
                        mBinding?.etUdidGenerationDate?.hideView()
                        mBinding?.tvPermanentTemporary?.hideView()
                        mBinding?.etPermanentTemporary?.hideView()
                        mBinding?.tvDisabilityPercentage?.hideView()
                        mBinding?.etDisabilityPercentage?.hideView()
                        mBinding?.tvEnrollmentNo?.showView()
                        mBinding?.etEnrollmentNo?.showView()
                        mBinding?.tvHospitalName?.showView()
                        mBinding?.etHospitalName?.showView()
                    }
                    else{
                        mBinding?.etUdidNo?.showView()
                        mBinding?.tvUdidNo?.showView()
                        mBinding?.tvUdidGenerationDate?.showView()
                        mBinding?.etUdidGenerationDate?.showView()
                        mBinding?.tvPermanentTemporary?.showView()
                        mBinding?.etPermanentTemporary?.showView()
                        mBinding?.tvDisabilityPercentage?.showView()
                        mBinding?.etDisabilityPercentage?.showView()
                        mBinding?.tvEnrollmentNo?.hideView()
                        mBinding?.etEnrollmentNo?.hideView()
                        mBinding?.tvHospitalName?.hideView()
                        mBinding?.etHospitalName?.hideView()
                    }
                    mBinding?.etEnrollmentNo?.text = if(userData.application_number.isNullOrEmpty()) getString(R.string.na) else userData.application_number

                    mBinding?.etHospitalName?.text = if(userData.hospital?.hospital_name.isNullOrEmpty())getString(R.string.na) else userData.hospital?.hospital_name
                    mBinding?.etUdidNo?.text = if(userData.udid_number.isNullOrEmpty()) getString(R.string.na) else userData.udid_number
                    mBinding?.etNameOfApplication?.text = if(userData.full_name.isNullOrEmpty()) getString(R.string.na) else userData.full_name
                    mBinding?.etDOB?.text = if(userData.dob.isNullOrEmpty()) getString(R.string.na) else dateConvertToFormat(userData.dob)
                    mBinding?.etUdidGenerationDate?.text = if(userData.certificate_generate_date.isNullOrEmpty()) getString(R.string.na) else dateConvertToFormat(userData.certificate_generate_date.toString())
                    mBinding?.etAadhaarNo?.text =if(userData.aadhaar_no.isNullOrEmpty()) getString(R.string.na) else Utility.maskAadharNumber(userData.aadhaar_no.toString())
                    mBinding?.etGender?.text = if(userData.gender.isNullOrEmpty()) getString(R.string.na) else userData.gender
                    mBinding?.tvBlindness?.text = if(userData.disability_types.isNullOrEmpty()) getString(R.string.na) else userData.disability_types
                    mBinding?.etDisabilityPercentage?.text = if(userData.final_disability_percentage?.toString().isNullOrEmpty()) getString(R.string.na) else userData.final_disability_percentage.toString()
                    mBinding?.etPermanentTemporary?.text = if(userData.disability_condition_category.isNullOrEmpty()) getString(R.string.na) else userData.disability_condition_category
                    if(userData.disability_condition_category == "Temporary") {
                        mBinding?.etValidity?.text =
                            dateConvertToFormat(userData.pwd_card_expiry_date).ifEmpty { getString(R.string.na) }
                    }
                    else{
                        mBinding?.etValidity?.hideView()
                        mBinding?.tvValidity?.hideView()
                    }
                    mBinding?.etMobile?.text = if(userData.mobile?.toString().isNullOrEmpty()) getString(R.string.na) else userData.mobile.toString().ifEmpty { getString(R.string.na) }
                    mBinding?.etEmailID?.text = if(userData.email.isNullOrEmpty()) getString(R.string.na) else userData.email
                    mBinding?.etAddress?.text = userData.current_address.plus(", ").plus(userData.subdistrict?.subdistrict_name)
                        .plus(", ").plus(userData.district?.district_name).plus(", ").plus(userData.state?.name)
                        .plus(", ").plus(userData.current_pincode)
                }
            }
        }

        viewModel.errors.observe(this) {
            mBinding?.let { it1 -> Utility.showSnackbar(it1.rlParent, it) }
        }
    }
}
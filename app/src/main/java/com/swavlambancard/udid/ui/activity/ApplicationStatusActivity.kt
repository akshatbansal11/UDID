package com.swavlambancard.udid.ui.activity

import android.view.View
import com.bumptech.glide.Glide
import com.swavlambancard.udid.R
import com.swavlambancard.udid.databinding.ActivityApplicationStatusBinding
import com.swavlambancard.udid.model.UserData
import com.swavlambancard.udid.utilities.AppConstants
import com.swavlambancard.udid.utilities.BaseActivity
import com.swavlambancard.udid.utilities.Preferences.getPreferenceOfLogin

class ApplicationStatusActivity() : BaseActivity<ActivityApplicationStatusBinding>() {

    private var mBinding: ActivityApplicationStatusBinding? = null

    override val layoutId: Int
        get() = R.layout.activity_application_status

    override fun initView() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
    }

    inner class ClickActions {
        fun backPress(view: View){
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun setVariables() {
        mBinding?.etApplicationStatus?.text =
            getPreferenceOfLogin(
                this,
                AppConstants.LOGIN_DATA,
                UserData::class.java
            ).pwdapplicationstatus.status_name

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

    }
}
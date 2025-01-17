package com.udid.ui.activity

import android.util.Log
import android.view.View
import com.udid.R
import com.udid.databinding.ActivityApplicationStatusBinding
import com.udid.model.UserData
import com.udid.utilities.AppConstants
import com.udid.utilities.BaseActivity
import com.udid.utilities.JSEncryptService
import com.udid.utilities.Preferences.getPreferenceOfLogin
import com.udid.utilities.Utility

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
    }

    override fun setObservers() {

    }
}
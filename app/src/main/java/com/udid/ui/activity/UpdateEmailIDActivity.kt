package com.udid.ui.activity

import android.view.View
import com.udid.R
import com.udid.databinding.ActivityUpdateEmailIdactivityBinding
import com.udid.utilities.BaseActivity
import com.udid.viewModel.ViewModel

class UpdateEmailIDActivity :  BaseActivity<ActivityUpdateEmailIdactivityBinding>() {

    private var mBinding: ActivityUpdateEmailIdactivityBinding? = null
    private var viewModel= ViewModel()


    override val layoutId: Int
        get() = R.layout.activity_update_email_idactivity

    override fun initView() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
        viewModel.init()
    }
    inner class ClickActions {
        fun backPress(view: View){
            onBackPressedDispatcher.onBackPressed()
        }

    }
    override fun setVariables() {

    }

    override fun setObservers() {
    }
}
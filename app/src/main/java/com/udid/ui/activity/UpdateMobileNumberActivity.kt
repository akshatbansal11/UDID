package com.udid.ui.activity

import android.view.View
import com.udid.R
import com.udid.databinding.ActivityUpdateMobileNumberBinding
import com.udid.utilities.BaseActivity
import com.udid.viewModel.ViewModel

class UpdateMobileNumberActivity : BaseActivity<ActivityUpdateMobileNumberBinding>() {

    private var mBinding: ActivityUpdateMobileNumberBinding? = null
    private var viewModel= ViewModel()


    override val layoutId: Int
        get() = R.layout.activity_update_date_of_birth

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
package com.udid.ui.activity

import android.view.View
import com.udid.R
import com.udid.databinding.ActivityLostCardBinding
import com.udid.utilities.BaseActivity
import com.udid.viewModel.ViewModel

class LostCardActivity : BaseActivity<ActivityLostCardBinding>() {

    private var mBinding: ActivityLostCardBinding? = null
    private var viewModel= ViewModel()


    override val layoutId: Int
        get() = R.layout.activity_surrender_card

    override fun initView() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
        viewModel.init()
    }

    override fun setVariables() {
    }

    override fun setObservers() {
    }

    inner class ClickActions {
        fun backPress(view: View){
            onBackPressedDispatcher.onBackPressed()
        }

        fun submit(view: View){
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
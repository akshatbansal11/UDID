package com.udid.ui.activity

import android.view.View
import com.udid.R
import com.udid.databinding.ActivityLostCardBinding
import com.udid.utilities.BaseActivity
import com.udid.utilities.Utility
import com.udid.viewModel.ViewModel

class LostCardActivity : BaseActivity<ActivityLostCardBinding>() {

    private var mBinding: ActivityLostCardBinding? = null
    private var viewModel= ViewModel()


    override val layoutId: Int
        get() = R.layout.activity_lost_card

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
        fun generateOtp(view: View) {
            if (valid()) {
                mBinding?.clParent?.let { Utility.showSnackbar(it, "Done OTP") }
            }
        }
    }

    private fun valid(): Boolean {
        val lostCardReason = mBinding?.etLostCardReason?.text?.toString()
        val fileName = mBinding?.etFileName?.text?.toString()

//        if (fileName==getString(R.string.no_file_chosen)) {
//            mBinding?.clParent?.let { Utility.showSnackbar(it,"Please Upload Supporting Document.") }
//            return false
//        }
        if (lostCardReason == "Reason to Lost Card/Card Not Received") {
            mBinding?.clParent?.let {
                Utility.showSnackbar(
                    it,
                    "Please select reason"
                )
            }
            return false
        }

        return true
    }
}
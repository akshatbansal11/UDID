package com.udid.ui.activity

import android.content.Intent
import android.view.View
import com.udid.R
import com.udid.databinding.ActivityLoginBinding
import com.udid.utilities.BaseActivity

class LoginActivity : BaseActivity<ActivityLoginBinding>() {
    private var mBinding:ActivityLoginBinding?=null

    override val layoutId: Int
        get() = R.layout.activity_login

    override fun initView() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
    }

    inner class ClickActions {

        fun pwdLogin(view: View){
            startActivity(Intent(this@LoginActivity,PwdLoginActivity::class.java))
        }
        fun scanForDetails(view: View){
            startActivity(Intent(this@LoginActivity,ScannerActivity::class.java))
        }
        fun backPress(view: View){
            onBackPressedDispatcher.onBackPressed()
        }

    }

    override fun setVariables() {

    }

    override fun setObservers() {

    }
}
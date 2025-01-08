package com.udid.ui.activity

import android.content.Intent
import android.view.View
import com.udid.R
import com.udid.databinding.ActivityDepartmentLoginBinding
import com.udid.utilities.AppConstants
import com.udid.utilities.BaseActivity

class DepartmentLoginActivity() : BaseActivity<ActivityDepartmentLoginBinding>() {
    private var mBinding: ActivityDepartmentLoginBinding?=null
    override val layoutId: Int
        get() = R.layout.activity_department_login
    private var captchaSum: Int = 0

    override fun initView() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
//        generateCaptcha()
    }
    inner class ClickActions {
        fun sendOtp(view: View){
//            if (validateCaptcha())
//            {
                startActivity(Intent(this@DepartmentLoginActivity,ListForDepartmentUserActivity::class.java).putExtra(AppConstants.IS_FROM,"Department"))
//            }

        }
        fun backPress(view: View){
            onBackPressedDispatcher.onBackPressed()
        }
        fun recaptcha(view: View){
//            generateCaptcha()
        }

    }

    override fun setVariables() {

    }

    override fun setObservers() {

    }
//    private fun generateCaptcha() {
//        val random = Random()
//        val digit1 = random.nextInt(100) // Random digit between 0 and 9
//        val digit2 = random.nextInt(100)
//
//        captchaSum = digit1 + digit2
//
//        // Display the two digits in the TextView
//        mBinding?.etCaptcha?.setText(String.format("%d + %d", digit1, digit2))
//    }

    // Method to validate the user's input
//    fun validateCaptcha(): Boolean {
//        val userInput = mBinding?.etEnterCaptcha?.text.toString().trim()
//
//        if (userInput.isNotEmpty()) {
//            val userAnswer = userInput.toInt()
//
//            if (userAnswer == captchaSum) {
//                // Captcha is correct, handle accordingly
//                Toast.makeText(this, "captcha correct", Toast.LENGTH_SHORT).show()
//                return true
//                // ...
//            } else {
//                // Captcha is incorrect, handle accordingly
//                // .
//
//                Toast.makeText(this, "captcha incorrect", Toast.LENGTH_SHORT).show()
//                return false
//            }
//
//            // Generate a new captcha for the next attempt
//            generateCaptcha()
//        }
//        return false
//    }
}
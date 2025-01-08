package com.udid.ui.activity

import android.content.Intent
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import com.udid.R
import com.udid.databinding.ActivityOnboardingScreenBinding
import com.udid.ui.fragments.OnboardingOneFragment
import com.udid.ui.fragments.OnboardingTwoFragment
import com.udid.utilities.AppConstants
import com.udid.utilities.BaseActivity
import com.udid.utilities.Utility
import com.udid.utilities.hideView
import com.udid.utilities.showView


class OnboardingScreenActivity : BaseActivity<ActivityOnboardingScreenBinding>(){
    private var mBinding: ActivityOnboardingScreenBinding? = null
    private var currentFragment: Fragment? = null

    override val layoutId: Int
        get() = R.layout.activity_onboarding_screen

    override fun initView() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()

        // Handle clicks on Applicant Details
        Utility.savePreferencesString(this@OnboardingScreenActivity,AppConstants.WALK_THROUGH,"true")
        replaceFragment(OnboardingOneFragment())
    }
    override fun setVariables() {
    }

    override fun setObservers() {
    }

    inner class ClickActions {

        fun next(view: View){
            replaceFragment(OnboardingTwoFragment())
            mBinding?.tvGetStarted?.showView()
            mBinding?.tvBack?.showView()
            mBinding?.tvNext?.hideView()
            setTint(mBinding?.dotTwo, R.color.orangeDot)
            setTint(mBinding?.dotOne, R.color.grayDot)
        }
        fun back(view: View){
            replaceFragment(OnboardingOneFragment())
            mBinding?.tvGetStarted?.hideView()
            mBinding?.tvBack?.hideView()
            mBinding?.tvNext?.showView()
            setTint(mBinding?.dotTwo, R.color.grayDot)
            setTint(mBinding?.dotOne, R.color.orangeDot)
        }
        fun getStarted(view: View){
            startActivity(Intent(this@OnboardingScreenActivity, LoginActivity::class.java))
            finishAffinity()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        currentFragment = fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    private fun setTint(imageView: ImageView?, colorResource: Int) {
        val color = ContextCompat.getColor(this, colorResource)
        val drawable = imageView?.drawable?.mutate()
        drawable?.let {
            val wrappedDrawable = DrawableCompat.wrap(it)
            DrawableCompat.setTint(wrappedDrawable, color)
            imageView.setImageDrawable(wrappedDrawable)
        }
    }

}
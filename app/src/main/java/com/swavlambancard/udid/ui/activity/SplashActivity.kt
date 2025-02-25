package com.swavlambancard.udid.ui.activity

import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import com.swavlambancard.udid.R
import com.swavlambancard.udid.databinding.ActivitySplashBinding
import com.swavlambancard.udid.utilities.AppConstants
import com.swavlambancard.udid.utilities.BaseActivity
import com.swavlambancard.udid.utilities.Utility


class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    private var mBinding: ActivitySplashBinding? = null
    private var mDelay = 3000
    override val layoutId: Int
        get() = R.layout.activity_splash

    override fun initView() {
        window.apply {
            // Set the status bar color to transparent
            statusBarColor = Color.TRANSPARENT

            // Enable the layout to extend to the status bar
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        mBinding = viewDataBinding

        Handler(Looper.getMainLooper()).postDelayed({
            Log.d("token---", Utility.getPreferenceString(this, AppConstants.WALK_THROUGH))

            if (Utility.getPreferenceString(this@SplashActivity, AppConstants.WALK_THROUGH) == "true"
            ) {
                if (Utility.getPreferenceString(this, com.swavlambancard.udid.utilities.PrefEntities.TOKEN).isNotEmpty()) {
                    startActivity(Intent(this, DashboardActivity::class.java))
                } else {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
            } else {
                startActivity(Intent(this, OnboardingScreenActivity::class.java))
            }
            finish()
        }, mDelay.toLong())

    }

    override fun setVariables() {
    }

    override fun setObservers() {
    }
}
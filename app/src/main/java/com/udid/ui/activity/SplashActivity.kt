package com.udid.ui.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.udid.databinding.ActivitySplashBinding
import com.udid.utilities.AppConstants
import com.udid.utilities.PrefEntities
import com.udid.utilities.Utility


class SplashActivity : AppCompatActivity() {
    private var mBinding: ActivitySplashBinding? = null
    private var mDelay = 3000
    override fun onCreate(savedInstanceState: Bundle?) {
        window.apply {
            // Set the status bar color to transparent
            statusBarColor = Color.TRANSPARENT

            // Enable the layout to extend to the status bar
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        super.onCreate(savedInstanceState)
        mBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(mBinding?.root)
        Handler(Looper.getMainLooper()).postDelayed({
//            Log.d("token---", Utility.getPreferenceString(this, AppConstants.WALK_THROUGH))

            if (Utility.getPreferenceString(this@SplashActivity, AppConstants.WALK_THROUGH) == "true"
            ) {
                if (Utility.getPreferenceString(this, PrefEntities.TOKEN).isNotEmpty()) {
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
}
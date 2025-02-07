package com.udid.ui.activity

import android.content.Intent
import android.view.View
import com.udid.R
import com.udid.databinding.ActivityLanguageBinding
import com.udid.utilities.AppConstants
import com.udid.utilities.BaseActivity
import com.udid.utilities.Utility
import com.udid.utilities.Utility.setLocale

class LanguageActivity : BaseActivity<ActivityLanguageBinding>() {

    private var mBinding: ActivityLanguageBinding? = null

    override val layoutId: Int
        get() = R.layout.activity_language

    override fun initView() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
        if (Utility.getPreferenceString(this, AppConstants.LANGUAGE) == "hi") {
            mBinding?.tvHindi?.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.radio_button_checked,
                0
            );
            mBinding?.tvHindi?.setTextColor(resources.getColor(R.color.darkBlue))
            mBinding?.tvEnglish?.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.radio_button_unchecked,
                0
            );
            mBinding?.tvEnglish?.setTextColor(resources.getColor(R.color.black))
            Utility.savePreferencesString(
                this@LanguageActivity,
                AppConstants.LANGUAGE,
                "hi"
            )
        } else {
            mBinding?.tvEnglish?.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.radio_button_checked,
                0
            );
            mBinding?.tvEnglish?.setTextColor(resources.getColor(R.color.darkBlue))
            mBinding?.tvHindi?.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.radio_button_unchecked,
                0
            );
            mBinding?.tvHindi?.setTextColor(resources.getColor(R.color.black))
            Utility.savePreferencesString(
                this@LanguageActivity,
                AppConstants.LANGUAGE,
                "en"
            )
        }
    }

    override fun setVariables() {
    }

    override fun setObservers() {
    }

    inner class ClickActions {
        fun backPress(view: View) {
            onBackPressed()
        }

        fun submit(view: View) {
            if (Utility.getPreferenceString(
                    this@LanguageActivity,
                    AppConstants.LANGUAGE
                ) != "hi"
            ) {
                Utility.savePreferencesString(
                    this@LanguageActivity,
                    AppConstants.LANGUAGE,
                    "en"
                )
            } else {
                Utility.savePreferencesString(
                    this@LanguageActivity,
                    AppConstants.LANGUAGE,
                    "hi"
                )
            }
            startActivity(Intent(this@LanguageActivity, DashboardActivity::class.java))
        }

        fun english(view: View) {
            Utility.savePreferencesString(
                this@LanguageActivity,
                AppConstants.LANGUAGE,
                "en"
            )
            setLocale(this@LanguageActivity, AppConstants.LANGUAGE_CODE_ENGLISH)
            mBinding?.tvEnglish?.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.radio_button_checked,
                0
            );
            mBinding?.tvEnglish?.setTextColor(resources.getColor(R.color.darkBlue))
            mBinding?.tvHindi?.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.radio_button_unchecked,
                0
            );
            mBinding?.tvHindi?.setTextColor(resources.getColor(R.color.black))
            mBinding?.tvSubmit?.text = getString(R.string.submit)
            mBinding?.tvHeading?.text = getString(R.string.choose_language)
        }

        fun hindi(view: View) {
            Utility.savePreferencesString(
                this@LanguageActivity,
                AppConstants.LANGUAGE,
                "hi"
            )
            setLocale(this@LanguageActivity, AppConstants.LANGUAGE_CODE_HINDI)
            mBinding?.tvHindi?.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.radio_button_checked,
                0
            );
            mBinding?.tvHindi?.setTextColor(resources.getColor(R.color.darkBlue))
            mBinding?.tvEnglish?.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.radio_button_unchecked,
                0
            );
            mBinding?.tvEnglish?.setTextColor(resources.getColor(R.color.black))
            mBinding?.tvSubmit?.text = getString(R.string.submit)
            mBinding?.tvHeading?.text = getString(R.string.choose_language)
        }
    }
}
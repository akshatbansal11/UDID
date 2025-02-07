package com.udid.utilities

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.util.Base64
import android.util.Log
import com.udid.services.LOGIN
import com.udid.ui.activity.LoginActivity
import com.udid.ui.activity.PwdLoginActivity
import com.udid.utilities.Utility.setLocale
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Locale

class UDID : Application() {

    override fun onCreate() {
        super.onCreate()
        instance=this
        mContext = this
    }

    companion object{

        @SuppressLint("StaticFieldLeak")
        lateinit var instance: UDID

        @SuppressLint("StaticFieldLeak")
        @JvmField
        var mContext: Context? = null

        @JvmStatic
        fun getToken(): String {
            return "Bearer ".plus(Utility.getPreferenceString(mContext!!, PrefEntities.TOKEN))
        }
        fun closeAndRestartApplication() {
            Preferences.removeAllPreference(instance)
            Utility.clearAllPreferencesExceptDeviceToken(instance)
            val intent = Intent(instance, LoginActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            instance.startActivity(intent)
        }
    }
}
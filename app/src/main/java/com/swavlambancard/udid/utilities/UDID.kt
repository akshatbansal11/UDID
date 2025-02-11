package com.swavlambancard.udid.utilities

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import com.swavlambancard.udid.ui.activity.LoginActivity

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
            return "Bearer ".plus(Utility.getPreferenceString(mContext!!, com.swavlambancard.udid.utilities.PrefEntities.TOKEN))
        }
        fun closeAndRestartApplication() {
            com.swavlambancard.udid.utilities.Preferences.removeAllPreference(instance)
            Utility.clearAllPreferencesExceptDeviceToken(instance)
            val intent = Intent(instance, LoginActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            instance.startActivity(intent)
        }
    }
}
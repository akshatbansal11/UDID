package com.udid.utilities

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import com.udid.services.LOGIN
import com.udid.ui.activity.LoginActivity
import com.udid.ui.activity.PwdLoginActivity
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class UDID : Application() {

    override fun onCreate() {
        super.onCreate()
        instance=this
        mContext = this
        printHashKey(this)
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

    fun printHashKey(pContext: Context) {
        try {
            val info = pContext.packageManager.getPackageInfo(
                pContext.packageName,
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey: String = String(Base64.encode(md.digest(), 0))
                Log.i(ContentValues.TAG, "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e(ContentValues.TAG, "printHashKey()", e)
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "printHashKey()", e)
        }
    }
}
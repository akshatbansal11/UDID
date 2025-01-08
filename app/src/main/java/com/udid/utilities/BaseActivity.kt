package com.udid.utilities

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Rect
import android.hardware.biometrics.BiometricPrompt
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.snackbar.Snackbar
import com.udid.R
import com.udid.repository.Repository
import java.io.File
import java.util.*
import java.util.concurrent.Executor
import javax.inject.Inject

abstract class BaseActivity<T : ViewDataBinding> : AppCompatActivity() {
    private val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    @Inject
    protected lateinit var mApplication: UDID

    @Inject
    protected lateinit var mRepository: Repository

    private val CAPTURE_IMAGE_REQUEST = 1

    private var STORAGE_STORAGE_REQUEST_CODE = 61
    val REQUEST_iMAGE_PDF = 20
    private val REQUEST_iMAGE_GALLERY = 3

    private var biometricPrompt: BiometricPrompt? = null
    private lateinit var executor: Executor
    private lateinit var callBack: BiometricPrompt.AuthenticationCallback
    private var keyguardManager: KeyguardManager? = null

    var uriTemp: Uri? = null
//    var currentImagePath: String? = null
//    var mCurrentPhotoPath: String? = null
    private var photoFile: File? = null
    var file: File? = null

    lateinit var viewDataBinding: T

    var isPDF: Boolean = false

    private var pdf = ArrayList<File>()

    @get:LayoutRes
    abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        window.statusBarColor = ContextCompat.getColor(this, R.color.darkBlue)
//        window.decorView.systemUiVisibility =
//            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        performDataBinding()
        initView()
        setVariables()
        setObservers()
    }

    fun isArabicLang(): Boolean {
        return Preferences.getPreference(context, PrefEntities.selected_language).equals("ar")
    }

    private fun performDataBinding() {
        viewDataBinding = DataBindingUtil.setContentView(this, layoutId)
        viewDataBinding.executePendingBindings()
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        var context: Context? = null
    }

    abstract fun initView()

    abstract fun setVariables()

    abstract fun setObservers()

    private fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    open fun isUserLogin(): Boolean {
        return Preferences.getPreference(this, PrefEntities.USER_DETAILS).isNotEmpty()
    }

    fun View.showStringSnackbar(message: String) {
        Snackbar.make(this, message, Snackbar.LENGTH_LONG).also { snackbar ->
            snackbar.view.setBackgroundColor(Color.parseColor("#F16622"))
            snackbar.setActionTextColor(Color.WHITE)
            snackbar.setAction("Ok") {
                snackbar.dismiss()
            }
        }.show()
    }

    protected open fun getLanguageLocalize(lang: String?, context: Context) {
        val config = context.resources.configuration
        val locale = Locale(lang)
        Locale.setDefault(locale)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
            context.resources.updateConfiguration(config, null)
        } else {
            config.locale = locale
            context.resources.updateConfiguration(config, null)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
            config.setLayoutDirection(locale)
            context.resources
                .updateConfiguration(config, context.resources.displayMetrics)
            context.createConfigurationContext(config)
        } else {
            context.resources
                .updateConfiguration(config, context.resources.displayMetrics)
        }
    }


    /*   //code for hiding keyboard
       override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
           if (currentFocus != null) {
               val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
               imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
           }
           return super.dispatchTouchEvent(ev)
       }
   */

    fun showLoader() {
        ProcessDialog.start(context)
    }

    fun dismissLoader() {
        if (ProcessDialog.isShowing())
            ProcessDialog.dismiss()
    }


    fun hideKeyboard() {
        try {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            if (imm.isAcceptingText) {
                imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            }
        } catch (e: Exception) {
        }
    }

    interface OnKeyboardVisibilityListener {
        fun onVisibilityChanged(visible: Boolean)
    }

    fun setKeyboardVisibilityListener(onKeyboardVisibilityListener: OnKeyboardVisibilityListener) {
        val parentView = (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0)
        parentView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            private var alreadyOpen = false
            private val defaultKeyboardHeightDP = 100
            private val EstimatedKeyboardDP =
                defaultKeyboardHeightDP + if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) 48 else 0
            private val rect: Rect = Rect()
            override fun onGlobalLayout() {
                val estimatedKeyboardHeight = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    EstimatedKeyboardDP.toFloat(),
                    parentView.resources.displayMetrics
                )
                    .toInt()
                parentView.getWindowVisibleDisplayFrame(rect)
                val heightDiff: Int = parentView.rootView.height - (rect.bottom - rect.top)
                val isShown = heightDiff >= estimatedKeyboardHeight
                if (isShown == alreadyOpen) {
                    Log.i("Keyboard state", "Ignoring global layout change...")
                    return
                }
                alreadyOpen = isShown
                onKeyboardVisibilityListener.onVisibilityChanged(isShown)
            }
        })
    }


    fun callAlertDialog(message: Int, context: Context?) {
        val builder = AlertDialog.Builder(
            context!!
        )
        builder.setMessage(message)
            .setTitle(resources.getString(R.string.app_name))
            .setCancelable(false)
        val alert = builder.create()
        alert.show()
    }

    fun callAlertDialog(message: String?, context: Context?) {
        val builder = AlertDialog.Builder(
            context!!
        )
        builder.setMessage(message)
            .setTitle(resources.getString(R.string.app_name))
            .setCancelable(false).setPositiveButton("ok") { _, _ -> finish() }
        val alert = builder.create()
        alert.show()
    }


    @TargetApi(Build.VERSION_CODES.M)
    fun requestPermissionsSafely(permissions: Array<String?>?, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions!!, requestCode)
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun hasPermission(permission: String?): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                checkSelfPermission(permission!!) == PackageManager.PERMISSION_GRANTED
    }

//    override fun attachBaseContext(newBase: Context?) {
//        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase!!))
//    }

    fun openCalculator() {
        val items = ArrayList<HashMap<String, Any>>()
        // PackageManager pm;
        val p = packageManager
        val packs = p.getInstalledPackages(0)
        for (pi in packs) {
            if (pi.packageName.lowercase(Locale.getDefault()).contains("calcul")) {
                val map = HashMap<String, Any>()
                map["appName"] = pi.applicationInfo.loadLabel(p)
                map["packageName"] = pi.packageName
                items.add(map)
            }
        }
        if (items.size >= 1) {
            val packageName = items[0]["packageName"] as String?
            val i = p.getLaunchIntentForPackage(packageName!!)
            i?.let { startActivity(it) }
        } else {
            showToast("Application not found")
        }
    }
    //this method only work whose api level is greater than or equal to Jelly_Bean (16)

    //You can also use keyguardManager.isDeviceSecure(); but it requires API Level 23
    val isDeviceSecure: Boolean
        get() {
            val keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager

            //this method only work whose api level is greater than or equal to Jelly_Bean (16)
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && keyguardManager.isKeyguardSecure

            //You can also use keyguardManager.isDeviceSecure(); but it requires API Level 23
        }

    fun shareOptions() {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name)
            var shareMessage = "\nLet me recommend you this application\n\n"
            shareMessage += "https://play.google.com/store/apps/details?id=" /* + BuildConfig.APPLICATION_ID + "\n\n"*/
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, "choose one"))
        } catch (e: Exception) {
            //e.toString();
        }
    }
}


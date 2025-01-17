package com.udid.utilities

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Dialog
import android.app.KeyguardManager
import android.content.ContentResolver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.hardware.biometrics.BiometricPrompt
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.google.android.material.snackbar.Snackbar
import com.udid.R
import com.udid.repository.Repository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import javax.inject.Inject

abstract class BaseActivity<T : ViewDataBinding> : AppCompatActivity() {
    private val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    var allAccepted :Boolean= false

    @Inject
    protected lateinit var mApplication: UDID

    @Inject
    protected lateinit var mRepository: Repository

    val CAPTURE_IMAGE_REQUEST = 1

    private var STORAGE_STORAGE_REQUEST_CODE = 61
    val REQUEST_iMAGE_PDF = 20
    val PICK_IMAGE = 2
    private val REQUEST_iMAGE_GALLERY = 3

    private var biometricPrompt: BiometricPrompt? = null
    private lateinit var executor: Executor
    private lateinit var callBack: BiometricPrompt.AuthenticationCallback
    private var keyguardManager: KeyguardManager? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    var uriTemp: Uri? = null
//    var currentImagePath: String? = null
//    var mCurrentPhotoPath: String? = null
    var photoFile: File? = null
    var file: File? = null

    lateinit var viewDataBinding: T

    var isPDF: Boolean = false

    val cameraImageUri: Uri by lazy {
        val file = File(applicationContext.filesDir, "captured_image.jpg")
        FileProvider.getUriForFile(
            applicationContext,
            "com.udid.provider",
            file
        )
    }

    private var pdf = ArrayList<File>()

        val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            startCrop(cameraImageUri)
        } else {
            Toast.makeText(this, "Camera capture failed", Toast.LENGTH_SHORT).show()
        }
    }

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val croppedImageUri = result.uriContent
            croppedImageUri?.let { displayImageWithWatermark(it) }
        } else {
            result.error?.printStackTrace()
            Toast.makeText(this, "Error cropping image: ${result.error?.message}", Toast.LENGTH_SHORT).show()
        }

    }

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
        const val REQUEST_PERMISSION_CODE = 1001
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

    private fun displayImageWithWatermark(imageUri: Uri) {
        val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)

//        val imageBitmap = addWatermark(imageBitmap)
        val imageSizeInBytes = getImageSizeInBytes(imageBitmap)
        val imageSize = formatImageSize(imageSizeInBytes)
        if (imageSizeInBytes > 5 * 1024 * 1024) {
            Toast.makeText(this, "Upload an image size less than 5MB", Toast.LENGTH_SHORT).show()
        } else {
            showImage(imageBitmap)
            Toast.makeText(this, "Image size: $imageSize", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addWatermark(bitmap: Bitmap): Bitmap {
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = android.graphics.Canvas(mutableBitmap)

        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 100f
            isAntiAlias = true
            style = android.graphics.Paint.Style.FILL
        }

        val watermark = "Lat: $latitude, Long: $longitude"
        val x = 10f
        val y = mutableBitmap.height - 10f
        canvas.drawText(watermark, x, y, paint)

        return rotateImage(mutableBitmap, 0f)
    }

    private fun rotateImage(source: Bitmap, degree: Float): Bitmap {
        val matrix = android.graphics.Matrix()
        matrix.postRotate(degree)

        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }


    private fun getImageSizeInBytes(bitmap: Bitmap): Long {
        val stream = java.io.ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.size().toLong()
    }

    private fun formatImageSize(sizeInBytes: Long): String {
        val sizeInKB = sizeInBytes / 1024
        return if (sizeInKB < 1024) {
            "$sizeInKB KB"
        } else {
            val sizeInMB = sizeInKB / 1024
            "$sizeInMB MB"
        }
    }

    fun startCrop(uri: Uri) {
        cropImage.launch(
            CropImageContractOptions(
                uri = uri,
                cropImageOptions = CropImageOptions(
                    guidelines = CropImageView.Guidelines.ON,
                    outputCompressFormat = Bitmap.CompressFormat.PNG
                )
            )
        )

    }

    open fun showImage(bitmap: Bitmap) {
        // In your child activity, override this method to set the image view
        // For example: binding.imgViewer.setImageBitmap(bitmap)
    }

    fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (!hasCameraPermission()) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }
        if (!hasLocationPermission()) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSION_CODE
            )
        }
    }

    fun requestCameraPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (!hasCameraPermission()) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }


        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSION_CODE
            )
        }
    }

    fun checkStoragePermission(context:Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.CAMERA
                )
            )
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )
            )
        }
    }
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        allAccepted = true
        permissions.entries.forEach {
            Log.e("DEBUG", "${it.key} = ${it.value}")
            if (!it.value) {
                allAccepted = false
                context= context
                showDialogOK(getString(R.string.camera_permission)) { dialog, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> context?.let { it1 ->
                            checkStoragePermission(
                                it1
                            )
                        }
                        DialogInterface.BUTTON_NEGATIVE -> dialog.dismiss()
                    }
                }
            }
        }
        Log.d("ALLACCEPTED",allAccepted.toString())
        if (allAccepted) context?.let { openCamera(it) } else return@registerForActivityResult
    }
    fun saveImageToFile(bitmap: Bitmap): File {
        val filesDir = filesDir
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFile = File(filesDir, "IMG_$timestamp.jpg")

        val outputStream = FileOutputStream(imageFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        outputStream.flush()
        outputStream.close()

        return imageFile
    }
    fun openCamera(context:Context) {
        val dialog = Dialog(context, android.R.style.Theme_Translucent_NoTitleBar)

        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_camera)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setGravity(Gravity.CENTER)

        val cross: ImageView = dialog.findViewById(R.id.ivCross)
        val camera: TextView = dialog.findViewById(R.id.tvCamera)
        val gallery: TextView = dialog.findViewById(R.id.tvGallery)
        val pdf: TextView = dialog.findViewById(R.id.tvPdf)


        pdf.setOnClickListener {
            openOnlyPdfAccordingToPosition()
            dialog.dismiss()
        }

        camera.setOnClickListener {
            if (!hasCameraPermission()) {
                requestCameraPermissions()
            } else {
                dispatchTakePictureIntent()
            }
            dialog.dismiss()
        }

        gallery.setOnClickListener {
            openGallery()
            dialog.dismiss()
        }

        cross.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }
    private fun dispatchTakePictureIntent() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAPTURE_IMAGE_REQUEST)

    }
    fun openOnlyPdfAccordingToPosition() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
        }
        startActivityForResult(intent, REQUEST_iMAGE_PDF)
    }
    private fun openGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Use ACTION_OPEN_DOCUMENT for Android 13+
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            startActivityForResult(intent, PICK_IMAGE)
        } else {
            // Use ACTION_PICK for older versions
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE)
        }
    }
    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        val dialog = android.app.AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setMessage(message)
            .setPositiveButton(getString(R.string.ok), okListener)
            .setNegativeButton(getString(R.string.cancel), okListener)
            .create()
            .show()
    }
    fun convertToRequestBody(context: Context, uri: Uri): RequestBody {
        val contentResolver: ContentResolver = context.contentResolver
        val tempFileName = "temp_${System.currentTimeMillis()}.pdf"
        val file = File(context.cacheDir, tempFileName)

        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            // Handle the error appropriately
        }

        return file.asRequestBody("application/pdf".toMediaType())
    }
}


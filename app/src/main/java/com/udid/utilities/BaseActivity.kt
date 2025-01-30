package com.udid.utilities

import FileCompressor
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
import androidx.lifecycle.lifecycleScope
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.google.android.material.snackbar.Snackbar
import com.udid.R
import com.udid.repository.Repository
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executor
import javax.inject.Inject

abstract class BaseActivity<T : ViewDataBinding> : AppCompatActivity() {
    private val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    private val fileCompressor by lazy { FileCompressor(applicationContext) }
    var allAccepted: Boolean = false

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

    fun showLoader(context: Context) {
        ProcessDialog.start(context)
    }

    fun dismissLoader() {
        if (ProcessDialog.isShowing())
            ProcessDialog.dismiss()
    }

    private var pdf = ArrayList<File>()

    @get:LayoutRes
    abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Utility.getPreferenceString(this, AppConstants.LANGUAGE) == "en") {
            Utility.setLocale(this, AppConstants.LANGUAGE_CODE_ENGLISH)
        } else {
            Utility.setLocale(this, AppConstants.LANGUAGE_CODE_HINDI)
        }
//        if (Utility.isDarkMode(this)) {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//        } else {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//        }
        context = this
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        window.statusBarColor = ContextCompat.getColor(this, R.color.darkBlue)
        performDataBinding()
        initView()
        setVariables()
        setObservers()
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

    fun isFileSizeWithinLimit(fileSizeInBytes: Long, maxSizeInKB: Double): Boolean {
        val fileSizeInKB = fileSizeInBytes / 1024.0 // Convert bytes to KB
        return fileSizeInKB <= maxSizeInKB
    }

    fun compressFile(inputFile: File) {
        lifecycleScope.launch {
            println("size ${inputFile.length()}}")
            when (val result = fileCompressor.compressFile(inputFile)) {
                is FileCompressor.CompressionResult.Success -> {
                    val compressedFile = result.compressedFile
                    println("Original size: ${inputFile.length() / 1024}KB")
                    println("Compressed size: ${compressedFile.length() / 1024}KB")
                    // Use the compressed file
                    photoFile = compressedFile
                }

                is FileCompressor.CompressionResult.Error -> {
                    println("Compression failed: ${result.message}")
                }

                else -> {}
            }
        }
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermissions() {
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

    fun checkStoragePermission(context: Context) {
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
                context = context
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
        Log.d("ALLACCEPTED", allAccepted.toString())
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

    private fun openCamera(context: Context) {
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

    private fun openOnlyPdfAccordingToPosition() {
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


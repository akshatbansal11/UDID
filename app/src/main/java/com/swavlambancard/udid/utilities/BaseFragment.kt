package com.swavlambancard.udid.utilities

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.KeyguardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.biometrics.BiometricPrompt
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.google.android.material.snackbar.Snackbar
import com.swavlambancard.udid.R
import com.swavlambancard.udid.repository.Repository
import com.swavlambancard.udid.utilities.BaseActivity.Companion
import com.swavlambancard.udid.utilities.BaseActivity.Companion.REQUEST_PERMISSION_CODE
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executor
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject

abstract class BaseFragment<T : ViewDataBinding> : Fragment() {
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


    var isPDF: Boolean = false
    lateinit var viewDataBinding: T

    @get:LayoutRes
    abstract val layoutId: Int
    val cameraImageUri: Uri by lazy {
        val file = File(requireActivity().filesDir, "captured_image.jpg")
        FileProvider.getUriForFile(
            requireActivity(),
            "com.udid.provider",
            file
        )
    }
    val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            startCrop(cameraImageUri)
        } else {
            Toast.makeText(requireContext(), "Camera capture failed", Toast.LENGTH_SHORT).show()
        }
    }

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val croppedImageUri = result.uriContent
            croppedImageUri?.let { displayImageWithWatermark(it) }
        } else {
            result.error?.printStackTrace()
            Toast.makeText(requireContext(), "Error cropping image: ${result.error?.message}", Toast.LENGTH_SHORT).show()
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding= DataBindingUtil.inflate(inflater,layoutId,container,false)
        init()
        setObservers()
        return viewDataBinding.root
       /* setObservers()
        return init(inflater, container, savedInstanceState)*/
    }

    abstract fun init()

 /*   abstract fun init(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?*/
    abstract fun setVariables()

    abstract fun setObservers()


    fun View.showStringSnackbar(message: String) {
        Snackbar.make(this, message, Snackbar.LENGTH_LONG).also { snackbar ->
            snackbar.view.setBackgroundColor(Color.parseColor("#F16622"))
            snackbar.setActionTextColor(Color.WHITE)
            snackbar.setAction(R.string.Ok) {
                snackbar.dismiss()
            }
        }.show()
    }

    open fun hideKeyboard(activity: Activity) {
        try {
            val inputManager: InputMethodManager = activity
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val currentFocusedView = activity.currentFocus
            if (currentFocusedView != null) {
                inputManager.hideSoftInputFromWindow(
                    currentFocusedView.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
    fun showLoader() {
        com.swavlambancard.udid.utilities.ProcessDialog.start(BaseActivity.context)
    }

    fun dismissLoader() {
        if (com.swavlambancard.udid.utilities.ProcessDialog.isShowing())
            com.swavlambancard.udid.utilities.ProcessDialog.dismiss()
    }


    fun isArabicLang():Boolean{
        return  com.swavlambancard.udid.utilities.Preferences.getPreference(requireContext(), com.swavlambancard.udid.utilities.PrefEntities.selected_language).equals("ar")
    }

    open fun isUserLogin(): Boolean {
        return com.swavlambancard.udid.utilities.Preferences.getPreference(requireContext(), com.swavlambancard.udid.utilities.PrefEntities.USER_DETAILS).isNotEmpty()
    }

    open fun isValidPassword(password: String?): Boolean {
        val pattern: Pattern
        val PASSWORD_PATTERN = "(?=.*[A-Z])(?=.*[0-9])(?=.*[a-z]).{8,15}"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        val matcher: Matcher = pattern.matcher(password)
        return matcher.matches()
    }


    fun showToast(message: String?) {
        Toast.makeText(requireContext().applicationContext, message, Toast.LENGTH_SHORT).show()
    }
//    private fun displayImageWithWatermark(imageUri: Uri) {
//        val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
//
////        val imageBitmap = addWatermark(imageBitmap)
//        val imageSizeInBytes = getImageSizeInBytes(imageBitmap)
//        val imageSize = formatImageSize(imageSizeInBytes)
//        if (imageSizeInBytes > 5 * 1024 * 1024) {
//            Toast.makeText(requireContext(), "Upload an image size less than 5MB", Toast.LENGTH_SHORT).show()
//        } else {
//            showImage(imageBitmap)
//            Toast.makeText(requireContext(), "Image size: $imageSize", Toast.LENGTH_SHORT).show()
//        }
//    }

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
        // In your child activity, override requireContext() method to set the image view
        // For example: binding.imgViewer.setImageBitmap(bitmap)
    }

    fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
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
                requireActivity(),
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
                requireActivity(),
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
                BaseActivity.context = BaseActivity.context
                showDialogOK(getString(R.string.camera_permission)) { dialog, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> BaseActivity.context?.let { it1 ->
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
        if (allAccepted) BaseActivity.context?.let { openCamera(it) } else return@registerForActivityResult
    }
    fun saveImageToFile(bitmap: Bitmap): File {
        val filesDir = requireActivity().filesDir
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
    private fun displayImageWithWatermark(imageUri: Uri) {
        val imageBitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageUri)

//        val imageBitmap = addWatermark(imageBitmap)
        val imageSizeInBytes = getImageSizeInBytes(imageBitmap)
        val imageSize = formatImageSize(imageSizeInBytes)
        if (imageSizeInBytes > 5 * 1024 * 1024) {
            Toast.makeText(requireContext(), "Upload an image size less than 5MB", Toast.LENGTH_SHORT).show()
        } else {
            showImage(imageBitmap)
            Toast.makeText(requireContext(), "Image size: $imageSize", Toast.LENGTH_SHORT).show()
        }
    }
    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        val dialog = android.app.AlertDialog.Builder(context);
        dialog.setCancelable(false);
        dialog.setMessage(message)
            .setPositiveButton(getString(R.string.ok), okListener)
            .setNegativeButton(getString(R.string.cancel), okListener)
            .create()
            .show()
    }
}
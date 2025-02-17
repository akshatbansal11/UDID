package com.swavlambancard.udid.utilities

import FileCompressor
import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import androidx.lifecycle.lifecycleScope
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.google.android.material.snackbar.Snackbar
import com.swavlambancard.udid.R
import com.swavlambancard.udid.utilities.BaseActivity.Companion
import com.swavlambancard.udid.utilities.BaseActivity.Companion.REQUEST_PERMISSION_CODE
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
import java.util.regex.Matcher
import java.util.regex.Pattern

abstract class BaseFragment<T : ViewDataBinding> : Fragment() {
    private val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )
    private val fileCompressor by lazy { FileCompressor(requireContext()) }
    var allAccepted: Boolean = false

    val CAPTURE_IMAGE_REQUEST = 1

    private var STORAGE_STORAGE_REQUEST_CODE = 61
    val REQUEST_iMAGE_PDF = 20
    val PICK_IMAGE = 2
    var isFrom = 0
    private val REQUEST_iMAGE_GALLERY = 3

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    var uriTemp: Uri? = null

    var photoFile: File? = null
    var file: File? = null

    var isPDF: Boolean = false
    lateinit var viewDataBinding: T

    @get:LayoutRes
    abstract val layoutId: Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        if (Utility.getPreferenceString(requireContext(), AppConstants.LANGUAGE) == "hi") {
            Utility.setLocale(requireContext(), AppConstants.LANGUAGE_CODE_HINDI)
            Utility.savePreferencesString(
                requireContext(),
                AppConstants.LANGUAGE,
                "hi"
            )
        } else {
            Utility.setLocale(requireContext(), AppConstants.LANGUAGE_CODE_ENGLISH)
            Utility.savePreferencesString(
                requireContext(),
                AppConstants.LANGUAGE,
                "en"
            )
        }
        init()
        setObservers()
        return viewDataBinding.root
    }

    abstract fun init()

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
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (!hasCameraPermission()) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }


        if (permissionsToRequest.isNotEmpty()) {
            requestPermissions(
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
        Log.d("ALLACCEPTED", allAccepted.toString())
        if (allAccepted) BaseActivity.context?.let { openCamera(it, isFrom) } else return@registerForActivityResult
    }

    fun saveImageToFile(bitmap: Bitmap): File {
        val filesDir = context?.filesDir
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFile = File(filesDir, "IMG_$timestamp.jpg")

        val outputStream = FileOutputStream(imageFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        outputStream.flush()
        outputStream.close()

        return imageFile
    }

    private fun openCamera(context: Context,isFrom :Int) {
        val dialog = Dialog(context, android.R.style.Theme_Translucent_NoTitleBar)

        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_camera)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setGravity(Gravity.CENTER)

        val cross: ImageView = dialog.findViewById(R.id.ivCross)
        val camera: TextView = dialog.findViewById(R.id.tvCamera)
        val gallery: TextView = dialog.findViewById(R.id.tvGallery)
        val pdf: TextView = dialog.findViewById(R.id.tvPdf)

        if(isFrom == 1) {
            pdf.hideView()
        }
        else{
            pdf.showView()
            pdf.setOnClickListener {
                openOnlyPdfAccordingToPosition()
                dialog.dismiss()
            }
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
        val dialog = android.app.AlertDialog.Builder(requireContext())
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
package com.swavlambancard.udid.ui

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.swavlambancard.udid.R
import com.swavlambancard.udid.utilities.Utility
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL

class PdfViewerActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var ivBackPress: AppCompatImageView
    private lateinit var pdfContainer: LinearLayout
    private lateinit var clParent: LinearLayout
    private var pdfRenderer: PdfRenderer? = null
    private var fileDescriptor: ParcelFileDescriptor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_viewer)

        imageView = findViewById(R.id.imageView)
        clParent = findViewById(R.id.clParent)
        pdfContainer = findViewById(R.id.pdfContainer)
        ivBackPress = findViewById(R.id.ivBackPress)

        ivBackPress.setOnClickListener {
            finish()
            onBackPressedDispatcher.onBackPressed()
        }

        val fileUriString = intent.getStringExtra("fileUri")
        val mimeType = intent.getStringExtra("mimeType")

        val fileUri = fileUriString?.let { Uri.parse(it) }

        Log.d("PdfViewerActivity", "File URI: $fileUriString, MIME Type: $mimeType")

        try {
            resetViews() // Clear previous content

            if (fileUri != null) {
                val actualMimeType = mimeType ?: getMimeType(fileUri)

                when {
                    fileUri.scheme?.startsWith("http") == true -> {
                        if (mimeType == "application/pdf") {
                            displayPdfFromUrl(fileUri.toString())
                        } else if (mimeType?.startsWith("image/") == true) {
                            displayImage(fileUri)
                        } else {
                            Utility.showSnackbar(clParent, "Unsupported file type")
                        }
                    }
                    actualMimeType == "application/pdf" -> displayPdf(fileUri)
                    actualMimeType.startsWith("image/") -> displayImage(fileUri)
                    actualMimeType.startsWith("*/*") -> displayImage(fileUri)
                    else -> Utility.showSnackbar(clParent, "Unsupported file type")
                }

            } else {
                Utility.showSnackbar(clParent, "No valid file to display")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Utility.showSnackbar(clParent, "Error loading file: ${e.message}")
        }
    }

    private fun getMimeType(uri: Uri): String {
        val mimeType = contentResolver.getType(uri)
        if (mimeType != null) return mimeType

        // Fallback check based on file extension
        val fileExtension = uri.toString().substringAfterLast('.', "").lowercase()
        return when (fileExtension) {
            "pdf" -> "application/pdf"
            "jpg", "jpeg", "png", "gif", "bmp", "webp" -> "image/$fileExtension"
            else -> "*/*" // Unknown
        }
    }

    private fun displayImage(uri: Uri) {
        pdfContainer.removeAllViews()
        pdfContainer.visibility = View.GONE // Hide PDF

        Glide.with(this)
            .load(uri)
            .skipMemoryCache(true) // Skip memory cache
            .diskCacheStrategy(DiskCacheStrategy.NONE) // Disable disk cache
            .into(imageView)
        imageView.visibility = View.VISIBLE
    }

    private fun resetViews() {
        imageView.setImageDrawable(null) // Clear the previous image
        imageView.visibility = View.GONE // Hide image view

        pdfContainer.removeAllViews() // Remove previous PDF views
        pdfContainer.visibility = View.GONE // Hide PDF container

        pdfRenderer?.close()
        fileDescriptor?.close()
    }
    private fun displayPdfFromUrl(pdfUrl: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL(pdfUrl)
                val connection = url.openConnection()
                connection.connect()

                val inputStream = connection.getInputStream()
                val tempFile = File(cacheDir, "temp_pdf_from_url.pdf")
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }

                withContext(Dispatchers.Main) {
                    displayPdf(Uri.fromFile(tempFile))
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Utility.showSnackbar(clParent, "Failed to load PDF: ${e.message}")
                }
                e.printStackTrace()
            }
        }
    }
    private fun displayPdf(uri: Uri) {
        pdfContainer.removeAllViews()  // Clear previous pages

        val file = getFileFromUri(uri)
        fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        pdfRenderer = PdfRenderer(fileDescriptor!!)
        pdfContainer.visibility = View.VISIBLE // Ensure PDF container is shown

        for (i in 0 until pdfRenderer!!.pageCount) {
            val page = pdfRenderer!!.openPage(i)
            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            val imageView = ImageView(this).apply {
                setImageBitmap(bitmap)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                adjustViewBounds = true
            }
            pdfContainer.addView(imageView)

            page.close()
        }
    }

    private fun getFileFromUri(uri: Uri): File {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val file = File(cacheDir, "temp.pdf")
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        return file
    }

    private fun getFileExtension(uri: Uri): String {
        return uri.toString().substringAfterLast('.', "").lowercase()
    }

    private fun decodeBase64ToPdf(base64String: String): File? {
        return try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            val file = File(cacheDir, "decoded_pdf.pdf")

            FileOutputStream(file).use { outputStream ->
                outputStream.write(decodedBytes)
            }
            file
        } catch (e: Exception) {
            Log.e("PdfViewerActivity", "Failed to decode Base64 PDF", e)
            null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        pdfRenderer?.close()
        fileDescriptor?.close()

        // Delete all files inside cache directory
        cacheDir?.listFiles()?.forEach { file ->
            file.delete()
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}

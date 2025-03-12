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
import com.swavlambancard.udid.R
import com.swavlambancard.udid.utilities.Utility
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

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
        val fileUri = fileUriString?.takeIf { it != "null" }?.let { Uri.parse(it) }

        Log.d("PdfViewerActivity", "File URI: $fileUriString")

        try {
            resetViews() // Clear previous content

            if (!fileUriString.isNullOrBlank()) {
                if (fileUriString.startsWith("data:application/pdf;base64,")) {
                    val base64Data = fileUriString.removePrefix("data:application/pdf;base64,")
                    val decodedFile = decodeBase64ToPdf(base64Data)
                    if (decodedFile != null) {
                        displayPdf(Uri.fromFile(decodedFile))
                    } else {
                        Utility.showSnackbar(clParent, "Failed to decode PDF")
                    }
                } else if (fileUriString.startsWith("data:image/")) {
                    Glide.with(this).load(fileUriString).into(imageView)
                    imageView.visibility = View.VISIBLE
                } else if (fileUri != null) {
                    if (isPdfFile(fileUri)) {
                        displayPdf(fileUri)
                    } else {
                        displayImage(fileUri)
                    }
                } else {
                    Utility.showSnackbar(clParent, "No valid file to display")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Utility.showSnackbar(clParent, "Error loading file: ${e.message}")
            Log.e("PdfViewerActivity", "Error loading file: ${e.message}")
        }
    }

    private fun isPdfFile(uri: Uri): Boolean {
        val contentResolver = applicationContext.contentResolver
        val mimeType = contentResolver.getType(uri)
        return mimeType == "application/pdf" || uri.toString().endsWith(".pdf", ignoreCase = true)
    }

    private fun displayImage(uri: Uri) {
        pdfContainer.removeAllViews()
        pdfContainer.visibility = View.GONE // Hide PDF

        Glide.with(this).load(uri).into(imageView)
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

        listOf("temp.pdf", "decoded_pdf.pdf").forEach {
            val file = File(cacheDir, it)
            if (file.exists()) file.delete()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}

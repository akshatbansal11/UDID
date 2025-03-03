package com.swavlambancard.udid.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.swavlambancard.udid.R
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class PdfViewerActivity : AppCompatActivity() {

    private lateinit var btnViewImage: Button
    private lateinit var btnViewSign: Button
    private lateinit var btnViewPdf: Button
    private lateinit var btnOpenInChrome: Button
    private lateinit var imageView: ImageView
    private lateinit var pdfContainer: LinearLayout

    private var imageUri: Uri? = null
    private var signUri: Uri? = null
    private var pdfUri: Uri? = null
    private var pdfUriTest: Uri? = null
    private var pdfRenderer: PdfRenderer? = null
    private var fileDescriptor: ParcelFileDescriptor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_viewer)

        btnViewImage = findViewById(R.id.btnViewImage)
        btnViewSign = findViewById(R.id.btnViewSign)
        btnViewPdf = findViewById(R.id.btnViewPdf)
        imageView = findViewById(R.id.imageView)
        pdfContainer = findViewById(R.id.pdfContainer)
        btnOpenInChrome = findViewById(R.id.btnOpenInChrome)



        btnOpenInChrome.setOnClickListener {
            val pdfUrl = "https://docs.google.com/viewer?url=" +//Open the PDF IN DRIVE
                    "https://udidrewamp.php-staging.com/uploads/pwdapplications/174097860434download_application_20250121_141737.pdf"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setPackage("com.android.chrome") // Forces it to open in Chrome if available

            try {
                startActivity(intent)
            } catch (e: Exception) {
                // If Chrome is not installed, open in any available browser
                intent.setPackage(null)
                startActivity(intent)
            }
        }

        imageUri = intent.getStringExtra("imageUri")?.let { Uri.parse(it) }
        signUri = intent.getStringExtra("signUri")?.let { Uri.parse(it) }
        pdfUri = intent.getStringExtra("pdfUri")?.let { Uri.parse(it) }

        btnViewImage.setOnClickListener {
            imageView.setImageURI(imageUri)
            imageView.visibility = ImageView.VISIBLE
        }

        btnViewSign.setOnClickListener {
            imageView.setImageURI(signUri)
            imageView.visibility = ImageView.VISIBLE
        }

        btnViewPdf.setOnClickListener {
            pdfUri?.let { displayPdf(it) }
        }
    }

    private fun displayPdf(uri: Uri) {
        pdfContainer.removeAllViews()  // Clear previous pages

        val file = getFileFromUri(uri)
        fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY) //ParcelFileDescriptor allows us to read the PDF file.
        pdfRenderer = PdfRenderer(fileDescriptor!!) //PdfRenderer opens the PDF for rendering.

        for (i in 0 until pdfRenderer!!.pageCount) {
            val page = pdfRenderer!!.openPage(i) //openPage(i): Opens each page one by one.
            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)//Bitmap.createBitmap(): Creates a blank image of the same size as the PDF page.
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)//page.render(): Draws the PDF page onto the Bitmap.

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

    //This copies the PDF from URI to a temporary file in cacheDir.
    private fun getFileFromUri(uri: Uri): File {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val file = File(cacheDir, "temp.pdf")
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        return file
    }

    override fun onDestroy() {
        super.onDestroy()
        pdfRenderer?.close()
        fileDescriptor?.close()
    }
}

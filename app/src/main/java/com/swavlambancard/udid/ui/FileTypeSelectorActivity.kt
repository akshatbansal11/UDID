package com.swavlambancard.udid.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.swavlambancard.udid.R

class FileTypeSelectorActivity : AppCompatActivity() {

    private lateinit var btnUploadImage: Button
    private lateinit var btnUploadSign: Button
    private lateinit var btnUploadPdf: Button
    private lateinit var btnProceed: Button

    private lateinit var txtImageStatus: TextView
    private lateinit var txtSignStatus: TextView
    private lateinit var txtPdfStatus: TextView

    private var imageUri: Uri? = null
    private var signUri: Uri? = null
    private var pdfUri: Uri? = null

    private val REQUEST_CODE_IMAGE = 101
    private val REQUEST_CODE_SIGN = 102
    private val REQUEST_CODE_PDF = 103

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_type_selector)

        btnUploadImage = findViewById(R.id.btnUploadImage)
        btnUploadSign = findViewById(R.id.btnUploadSign)
        btnUploadPdf = findViewById(R.id.btnUploadPdf)
        btnProceed = findViewById(R.id.btnProceed)

        txtImageStatus = findViewById(R.id.txtImageStatus)
        txtSignStatus = findViewById(R.id.txtSignStatus)
        txtPdfStatus = findViewById(R.id.txtPdfStatus)

        btnUploadImage.setOnClickListener { selectFile(REQUEST_CODE_IMAGE, "image/*") }
        btnUploadSign.setOnClickListener { selectFile(REQUEST_CODE_SIGN, "image/*") }
        btnUploadPdf.setOnClickListener { selectFile(REQUEST_CODE_PDF, "application/pdf") }

        btnProceed.setOnClickListener {
            val intent = Intent(this, PdfViewerActivity::class.java)
            intent.putExtra("imageUri", imageUri.toString())
            intent.putExtra("signUri", signUri.toString())
            intent.putExtra("pdfUri", pdfUri.toString())
            startActivity(intent)
        }

        btnProceed.visibility = View.GONE
    }

    private fun selectFile(requestCode: Int, type: String) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = type
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(Intent.createChooser(intent, "Select File"), requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data?.data != null) {
            when (requestCode) {
                REQUEST_CODE_IMAGE -> {
                    imageUri = data.data
                    txtImageStatus.text = "Image Selected ✅"
                }

                REQUEST_CODE_SIGN -> {
                    signUri = data.data
                    txtSignStatus.text = "Signature Selected ✅"
                }

                REQUEST_CODE_PDF -> {
                    pdfUri = data.data
                    txtPdfStatus.text = "PDF Selected ✅"
                }
            }

            if (imageUri != null && signUri != null && pdfUri != null) {
                btnProceed.visibility = View.VISIBLE
            }
        }
    }
}

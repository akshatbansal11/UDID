package com.swavlambancard.udid.ui.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.swavlambancard.udid.R
import com.swavlambancard.udid.databinding.ActivityUpdateRequestBinding
import com.swavlambancard.udid.model.UserData
import com.swavlambancard.udid.services.SUBMIT_AADHAAR_NUMBER
import com.swavlambancard.udid.services.SUBMIT_APPEAL
import com.swavlambancard.udid.services.SUBMIT_DATE_OF_BIRTH
import com.swavlambancard.udid.services.SUBMIT_EMAIL_ID
import com.swavlambancard.udid.services.SUBMIT_LOST_CARD
import com.swavlambancard.udid.services.SUBMIT_MOBILE_NUMBER
import com.swavlambancard.udid.services.SUBMIT_RENEWAL_CARD
import com.swavlambancard.udid.services.SUBMIT_SURRENDER_CARD
import com.swavlambancard.udid.services.SUBMIT_UPDATED_NAME
import com.swavlambancard.udid.utilities.AppConstants
import com.swavlambancard.udid.utilities.BaseActivity
import com.swavlambancard.udid.utilities.JSEncryptService
import com.swavlambancard.udid.utilities.Preferences.getPreferenceOfLogin
import com.swavlambancard.udid.utilities.UDID
import com.swavlambancard.udid.utilities.Utility.showSnackbar
import com.swavlambancard.udid.utilities.hideView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UpdateRequestActivity : BaseActivity<ActivityUpdateRequestBinding>() {
    private var mBinding: ActivityUpdateRequestBinding? = null
    override val layoutId: Int
        get() = R.layout.activity_update_request

    override fun initView() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
    }

    override fun setVariables() {

        mBinding?.ivProfile?.let {
            Glide.with(this)
                .load(
                    getPreferenceOfLogin(
                        this,
                        AppConstants.LOGIN_DATA,
                        UserData::class.java
                    ).photo_path
                )
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(it)
        }

        if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.update_n_name)) {
            mBinding?.tvHeading?.text = getString(R.string.update_n_name)
            mBinding?.tvContext?.text =
                getString(R.string.you_have_already_applied_the_admin_will_approve_your_request)
            mBinding?.llSubmitRequest?.hideView()
        } else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.aadhaar_number)) {
            mBinding?.tvHeading?.text = getString(R.string.aadhaar_number)
            mBinding?.tvContext?.text =
                getString(R.string.you_have_already_applied_the_admin_will_approve_your_request)
            mBinding?.llSubmitRequest?.hideView()
        } else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.date_of_birth_)) {
            mBinding?.tvHeading?.text = getString(R.string.date_of_birth_)
            mBinding?.tvContext?.text =
                getString(R.string.you_have_already_applied_the_admin_will_approve_your_request)
            mBinding?.llSubmitRequest?.hideView()
        } else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.mobile_number_)) {
            mBinding?.tvHeading?.text = getString(R.string.mobile_number_)
            mBinding?.tvContext?.text =
                getString(R.string.you_have_already_applied_the_admin_will_approve_your_request)
            mBinding?.llSubmitRequest?.hideView()
        } else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.email_id_)) {
            mBinding?.tvHeading?.text = getString(R.string.email_id_)
            mBinding?.tvContext?.text =
                getString(R.string.you_have_already_applied_the_admin_will_approve_your_request)
            mBinding?.llSubmitRequest?.hideView()
        } else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.surrender_n_card)) {
            mBinding?.tvHeading?.text = getString(R.string.surrender_n_card)
            mBinding?.tvContext?.text =
                getString(R.string.you_have_already_applied_the_admin_will_approve_your_request)
            mBinding?.llSubmitRequest?.hideView()
        } else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.appeal)) {
            mBinding?.tvHeading?.text = getString(R.string.appeal)
            mBinding?.tvContext?.text =
                getString(R.string.you_have_already_applied_the_admin_will_approve_your_request)
            mBinding?.llSubmitRequest?.hideView()
        } else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.renewal_card)) {
            mBinding?.tvHeading?.text = getString(R.string.renewal_card)
            mBinding?.tvContext?.text =
                getString(R.string.you_have_already_applied_the_admin_will_approve_your_request)
            mBinding?.llSubmitRequest?.hideView()
        } else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.lost_card_card_not_recieved)) {
            mBinding?.tvHeading?.text = getString(R.string.lost_card_card_not_recieved)
            mBinding?.tvContext?.text =
                getString(R.string.your_request_successfully_submitted_you_will_receive_udid_card_shortly)
            mBinding?.llSubmitRequest?.hideView()
        } else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.submit_update_name)) {
            mBinding?.tvHeading?.text = getString(R.string.update_n_name)
            mBinding?.tvStatus?.text = getString(R.string.your_updated_name_will_be_updated)
            mBinding?.tvContext?.hideView()
        } else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.submit_update_email_id)) {
            mBinding?.tvHeading?.text = getString(R.string.email_id_)
            mBinding?.tvStatus?.text = getString(R.string.your_updated_email_id_will_be_updated)
            mBinding?.tvContext?.hideView()
        } else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.submit_update_date_of_birth)) {
            mBinding?.tvHeading?.text = getString(R.string.date_of_birth_)
            mBinding?.tvStatus?.text =
                getString(R.string.your_updated_date_of_birth_will_be_updated)
            mBinding?.tvContext?.hideView()
        } else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.submit_update_aadhaar_number)) {
            mBinding?.tvHeading?.text = getString(R.string.aadhaar_number)
            mBinding?.tvStatus?.text =
                getString(R.string.your_updated_aadhaar_number_will_be_updated)
            mBinding?.tvContext?.hideView()
        } else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.submit_appeal)) {
            mBinding?.tvHeading?.text = getString(R.string.appeal)
            mBinding?.tvStatus?.text = getString(R.string.your_appeal_will_be_updated)
            mBinding?.tvContext?.hideView()
        } else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.submit_renewal_card)) {
            mBinding?.tvHeading?.text = getString(R.string.renewal_card)
            mBinding?.tvStatus?.text =
                getString(R.string.you_have_already_sent_a_renewal_request_please_contact_the_cmo)
            mBinding?.tvContext?.hideView()
        } else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.submit_lost_card)) {
            mBinding?.tvHeading?.text = getString(R.string.lost_card_card_not_recieved)
            mBinding?.tvStatus?.text =
                getString(R.string.your_lost_card_card_not_recieved_will_be_updated)
            mBinding?.tvContext?.hideView()
        } else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.submit_surrender_card)) {
            mBinding?.tvHeading?.text = getString(R.string.surrender_n_card)
            mBinding?.tvStatus?.text = getString(R.string.you_have_already_applied_the_admin_will_approve_your_request)
            mBinding?.tvContext?.hideView()
            mBinding?.tvSuccessfully?.hideView()
            mBinding?.tvSuccessfully?.text = getString(R.string.download_surrender_receipts)
        }
    }

    override fun setObservers() {
    }

    inner class ClickActions {
        fun backPress(view: View) {
            startActivity(Intent(this@UpdateRequestActivity, DashboardActivity::class.java))
            finishAffinity()
        }

        fun downloadReceipt(view: View) {
            when {
                intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.submit_update_name) -> {
                    downloadApplication(
                        this@UpdateRequestActivity,
                        "noEncryptionWithRequestType",
                        SUBMIT_UPDATED_NAME,
                        getString(R.string.name_receipt),
                        1
                    ) { result ->
                        result.fold(
                            onSuccess = { file ->
                                Log.d(
                                    "DownloadApplication",
                                    "PDF downloaded successfully: ${file.absolutePath}"
                                )
                                openPDF(file)
                            },
                            onFailure = { error ->
                                Log.d(
                                    "DownloadApplication",
                                    "PDF downloaded successfully: ${error.message}"
                                )
                            }
                        )
                    }
                }

                intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.submit_update_mobile_number) -> {
                    downloadApplication(
                        this@UpdateRequestActivity,
                        "noEncryptionWithRequestType",
                        SUBMIT_MOBILE_NUMBER,
                        getString(R.string.mobile_number_receipt),
                        3
                    ) { result ->
                        result.fold(
                            onSuccess = { file ->
                                Log.d(
                                    "DownloadApplication",
                                    "PDF downloaded successfully: ${file.absolutePath}"
                                )
                                openPDF(file)
                            },
                            onFailure = { error ->
                                Log.d(
                                    "DownloadApplication",
                                    "PDF downloaded successfully: ${error.message}"
                                )
                            }
                        )
                    }
                }

                intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.submit_update_email_id) -> {
                    downloadApplication(
                        this@UpdateRequestActivity,
                        "noEncryptionWithRequestType",
                        SUBMIT_EMAIL_ID,
                        getString(R.string.email_id_receipt),
                        2
                    ) { result ->
                        result.fold(
                            onSuccess = { file ->
                                Log.d(
                                    "DownloadApplication",
                                    "PDF downloaded successfully: ${file.absolutePath}"
                                )
                                openPDF(file)
                            },
                            onFailure = { error ->
                                Log.d(
                                    "DownloadApplication",
                                    "PDF downloaded successfully: ${error.message}"
                                )
                            }
                        )
                    }
                }

                intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.submit_update_date_of_birth) -> {
                    downloadApplication(
                        this@UpdateRequestActivity,
                        "noEncryptionWithRequestType",
                        SUBMIT_DATE_OF_BIRTH,
                        getString(R.string.date_of_birth_receipt),
                        5
                    ) { result ->
                        result.fold(
                            onSuccess = { file ->
                                Log.d(
                                    "DownloadApplication",
                                    "PDF downloaded successfully: ${file.absolutePath}"
                                )
                                openPDF(file)
                            },
                            onFailure = { error ->
                                Log.d(
                                    "DownloadApplication",
                                    "PDF downloaded successfully: ${error.message}"
                                )
                            }
                        )
                    }
                }

                intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.submit_update_aadhaar_number) -> {
                    downloadApplication(
                        this@UpdateRequestActivity,
                        "noEncryptionWithRequestType",
                        SUBMIT_AADHAAR_NUMBER,
                        getString(R.string.aadhaar_number_receipt),
                        4
                    ) { result ->
                        result.fold(
                            onSuccess = { file ->
                                Log.d(
                                    "DownloadApplication",
                                    "PDF downloaded successfully: ${file.absolutePath}"
                                )
                                openPDF(file)
                            },
                            onFailure = { error ->
                                Log.d(
                                    "DownloadApplication",
                                    "PDF downloaded successfully: ${error.message}"
                                )
                            }
                        )
                    }
                }

                intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.submit_renewal_card) -> {
                    downloadApplication(
                        this@UpdateRequestActivity,
                        "",
                        SUBMIT_RENEWAL_CARD,
                        getString(R.string.renewal_card),
                        7
                    ) { result ->
                        result.fold(
                            onSuccess = { file ->
                                Log.d(
                                    "DownloadApplication",
                                    "PDF downloaded successfully: ${file.absolutePath}"
                                )
                                openPDF(file)
                            },
                            onFailure = { error ->
                                Log.d(
                                    "DownloadApplication",
                                    "PDF downloaded successfully: ${error.message}"
                                )
                            }
                        )
                    }
                }

                intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.submit_appeal) -> {
                    downloadApplication(
                        this@UpdateRequestActivity,
                        "noEncryption",
                        SUBMIT_APPEAL,
                        getString(R.string.appeal),
                        null
                    ) { result ->
                        result.fold(
                            onSuccess = { file ->
                                Log.d(
                                    "DownloadApplication",
                                    "PDF downloaded successfully: ${file.absolutePath}"
                                )
                                openPDF(file)
                            },
                            onFailure = { error ->
                                Log.d(
                                    "DownloadApplication",
                                    "PDF downloaded successfully: ${error.message}"
                                )
                            }
                        )
                    }
                }

                intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.submit_surrender_card) -> {
                    downloadApplication(
                        this@UpdateRequestActivity,
                        "noEncryptionWithRequestType",
                        SUBMIT_SURRENDER_CARD,
                        getString(R.string.surrender_n_card),
                        8
                    ) { result ->
                        result.fold(
                            onSuccess = { file ->
                                Log.d(
                                    "DownloadApplication",
                                    "PDF downloaded successfully: ${file.absolutePath}"
                                )
                                openPDF(file)
                            },
                            onFailure = { error ->
                                Log.d(
                                    "DownloadApplication",
                                    "PDF downloaded successfully: ${error.message}"
                                )
                            }
                        )
                    }
                }

                intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.submit_lost_card) -> {
                    downloadApplication(
                        this@UpdateRequestActivity,
                        "noEncryptionWithRequestType",
                        SUBMIT_LOST_CARD,
                        "lost_card",
                        6
                    ) { result ->
                        result.fold(
                            onSuccess = { file ->
                                Log.d(
                                    "DownloadApplication",
                                    "PDF downloaded successfully: ${file.absolutePath}"
                                )
                                openPDF(file)
                            },
                            onFailure = { error ->
                                Log.d(
                                    "DownloadApplication",
                                    "PDF downloaded successfully: ${error.message}"
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    fun downloadApplication(
        context: Context,
        isFrom: String,
        baseUrl: String,
        fileName: String,
        requestType: Int?,
        completion: (Result<File>) -> Unit,
    ) {
        showLoader(context)

        val url = baseUrl.toHttpUrlOrNull() ?: run {
            dismissLoader()
            mBinding?.clParent?.let { showSnackbar(it, getString(R.string.invalid_url)) }
            completion(Result.failure(Exception("Invalid URL")))
            return
        }

        val requestBody = JSONObject().apply {
            when (isFrom) {
                "update" -> {
                    put(
                        "application_number", JSEncryptService.encrypt(
                            getPreferenceOfLogin(
                                context,
                                AppConstants.LOGIN_DATA,
                                UserData::class.java
                            ).application_number.toString()
                        )
                    )
                    put("type", "mobile")
                    put("request_type", requestType)
                }

                "noEncryption" -> {
                    put(
                        "application_number",
                        getPreferenceOfLogin(
                            context,
                            AppConstants.LOGIN_DATA,
                            UserData::class.java
                        ).application_number.toString()
                    )
                    put("type", "mobile")
                }

                "noEncryptionWithRequestType" -> {
                    put(
                        "application_number",
                        getPreferenceOfLogin(
                            context,
                            AppConstants.LOGIN_DATA,
                            UserData::class.java
                        ).application_number.toString()
                    )
                    put("request_type", requestType)
                    put("type", "mobile")
                }

                else -> {
                    put(
                        "application_number", JSEncryptService.encrypt(
                            getPreferenceOfLogin(
                                context,
                                AppConstants.LOGIN_DATA,
                                UserData::class.java
                            ).application_number.toString()
                        )
                    )
                    put("type", "mobile")
                }
            }
        }.toString()

        Log.e("Data:", requestBody)

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .post(
                requestBody.toRequestBody("application/json".toMediaType())
            )
            .addHeader("Authorization", UDID.getToken())
            .addHeader("Content-Type", "application/json")
            .build()
        println(UDID.getToken())
        println(request)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                dismissLoader()
                mBinding?.clParent?.let {
                    showSnackbar(
                        it,
                        getString(R.string.failed_to_download, e.localizedMessage)
                    )
                }
                completion(Result.failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                dismissLoader()
                if (response.isSuccessful) {
                    when (response.code) {
                        200, 201 -> {
                            val data = response.body?.bytes()
                            if (data != null) {
                                convertToPDF(fileName, data, completion)
                            } else {
                                mBinding?.clParent?.let {
                                    showSnackbar(
                                        it,
                                        getString(R.string.no_data_received)
                                    )
                                }
                                completion(Result.failure(Exception("No data received")))
                            }
                        }

                        else -> {
                            mBinding?.clParent?.let {
                                showSnackbar(
                                    it,
                                    getString(
                                        R.string.unexpected_response,
                                        response.code.toString()
                                    )
                                )
                            }
                        }
                    }
                } else {
                    when (response.code) {
                        400, 403, 404 -> {
                            mBinding?.clParent?.let {
                                showSnackbar(
                                    it,
                                    getString(R.string.request_failed, response.message)
                                )
                            }
                            completion(Result.failure(Exception("Request failed: ${response.message}")))
                        }

                        401 -> {
                            mBinding?.clParent?.let { showSnackbar(it, response.message) }
                            UDID.closeAndRestartApplication()
                            completion(Result.failure(Exception(response.message)))
                        }

                        500 -> {
                            mBinding?.clParent?.let { showSnackbar(it, response.message) }
                            completion(Result.failure(Exception(response.message)))
                        }

                        else -> {
                            mBinding?.clParent?.let {
                                showSnackbar(
                                    it,
                                    getString(R.string.unexpected_error, response.message)
                                )
                            }
                        }
                    }
                }
            }
        })
    }

    private fun convertToPDF(
        fileName: String,
        data: ByteArray,
        completion: (Result<File>) -> Unit,
    ) {
        try {
            val temporaryDirectory = File(this.cacheDir, "pdfs")
            if (!temporaryDirectory.exists()) {
                temporaryDirectory.mkdirs()
            }

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

            val pdfFile = File(temporaryDirectory, "$fileName $timestamp.pdf")
            pdfFile.writeBytes(data)

            completion(Result.success(pdfFile))
        } catch (e: Exception) {
            completion(Result.failure(e))
        }
    }

    fun openPDF(pdfFile: File) {
        try {
            // Create a URI for the PDF file using FileProvider for secure file access
            val pdfUri: Uri =
                FileProvider.getUriForFile(
                    this,
                    "${this.packageName}.fileprovider", // Ensure your app's FileProvider is correctly configured
                    pdfFile
                )

            // Create an Intent to open the PDF file
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(pdfUri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Grant permission for the PDF viewer
            }

            // Start the activity to view the PDF
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle the exception (e.g., show a toast if no PDF viewer is available)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@UpdateRequestActivity, DashboardActivity::class.java))
        finishAffinity()
    }
}
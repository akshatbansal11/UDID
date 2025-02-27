package com.swavlambancard.udid.ui.activity

import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.swavlambancard.udid.R
import com.swavlambancard.udid.databinding.ActivityUpdateRequestBinding
import com.swavlambancard.udid.model.UserData
import com.swavlambancard.udid.utilities.AppConstants
import com.swavlambancard.udid.utilities.BaseActivity
import com.swavlambancard.udid.utilities.JSEncryptService
import com.swavlambancard.udid.utilities.Preferences.getPreferenceOfLogin
import com.swavlambancard.udid.utilities.Utility.showSnackbar
import com.swavlambancard.udid.utilities.hideView
import com.swavlambancard.udid.utilities.toast
import com.swavlambancard.udid.viewModel.ViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File

class UpdateRequestActivity : BaseActivity<ActivityUpdateRequestBinding>() {
    private var mBinding: ActivityUpdateRequestBinding? = null
    private var viewModel = ViewModel()
    override val layoutId: Int
        get() = R.layout.activity_update_request

    override fun initView() {
        mBinding = viewDataBinding
        viewModel.init()
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
            mBinding?.tvStatus?.text =
                getString(R.string.you_have_already_applied_the_admin_will_approve_your_request)
            mBinding?.tvContext?.hideView()
            mBinding?.tvSuccessfully?.hideView()
            mBinding?.tvSuccessfully?.text = getString(R.string.download_surrender_receipts)
        }
    }

    override fun setObservers() {
        viewModel.downloadResult.observe(this) { result ->
            result.onSuccess { file ->
                openPDF(file)
            }.onFailure { error ->
                toast("Download failed: ${error.message}")
            }
        }

        viewModel.errors.observe(this) {
            mBinding?.clParent?.let { it1 -> showSnackbar(it1, it) }
        }
    }

    inner class ClickActions {
        fun backPress(view: View) {
            startActivity(Intent(this@UpdateRequestActivity, DashboardActivity::class.java))
            finishAffinity()
        }

        fun downloadReceipt(view: View) {

            if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.update_n_name)) {
                startDownload(
                    "noEncryptionWithRequestType",
                    1
                )
            }
            else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.aadhaar_number)) {
                startDownload(
                    "noEncryptionWithRequestType",
                    4
                )
            }
            else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.date_of_birth_)) {
                startDownload(
                    "noEncryptionWithRequestType",
                    5
                )
            }
            else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.surrender_n_card)) {
                startDownload(
                    "noEncryptionWithRequestType",
                    8
                )
            }
            else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.appeal)) {
                startDownload(
                    "noEncryption",
                    null
                )

            }
            else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.renewal_card)) {
                startDownload(
                    "",
                    7
                )

            }
            else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.lost_card_card_not_recieved)) {
                startDownload(
                    "noEncryptionWithRequestType",
                    6
                )
            }
            else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.submit_update_name)) {
                startDownload(
                    "noEncryptionWithRequestType",
                    1
                )

            }
            else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.submit_update_date_of_birth)) {
                startDownload(
                    "noEncryptionWithRequestType",
                    5
                )

            }
            else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.submit_update_aadhaar_number)) {
                startDownload(
                    "noEncryptionWithRequestType",
                    4
                )

            }
            else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.submit_appeal)) {
                startDownload(
                    "noEncryption",
                    null
                )
            }
            else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.submit_renewal_card)) {
                startDownload(
                    "",
                    7
                )

            }
            else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.submit_lost_card)) {
                startDownload(
                    "noEncryptionWithRequestType",
                    6
                )
            }
            else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.submit_surrender_card)) {
                startDownload(
                    "noEncryptionWithRequestType",
                    8
                )
            }
        }
    }

    fun startDownload(isFrom: String, requestType: Int?) {
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

        if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.update_n_name)) {
            viewModel.downloadUpdatedName(
                this, requestBody.toRequestBody("application/json".toMediaType())
            )

        } else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.aadhaar_number)) {
            viewModel.downloadAadhaarNumber(
                this, requestBody.toRequestBody("application/json".toMediaType())
            )
        } else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.date_of_birth_)) {
            viewModel.downloadDateOfBirth(
                this, requestBody.toRequestBody("application/json".toMediaType())
            )
        } else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.surrender_n_card)) {
            viewModel.downloadSurrenderCard(
                this, requestBody.toRequestBody("application/json".toMediaType())
            )

        } else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.appeal)) {
            viewModel.downloadAppeal(
                this, requestBody.toRequestBody("application/json".toMediaType())
            )

        } else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.renewal_card)) {
            viewModel.downloadRenewalCard(
                this, requestBody.toRequestBody("application/json".toMediaType())
            )
        } else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.lost_card_card_not_recieved)) {
            viewModel.downloadLostCard(
                this, requestBody.toRequestBody("application/json".toMediaType())
            )

        } else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.submit_update_name)) {
            viewModel.downloadUpdatedName(
                this, requestBody.toRequestBody("application/json".toMediaType())
            )
        } else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.submit_update_date_of_birth)) {
            viewModel.downloadDateOfBirth(
                this, requestBody.toRequestBody("application/json".toMediaType())
            )
        } else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.submit_update_aadhaar_number)) {
            viewModel.downloadAadhaarNumber(
                this, requestBody.toRequestBody("application/json".toMediaType())
            )

        } else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.submit_appeal)) {
            viewModel.downloadAppeal(
                this, requestBody.toRequestBody("application/json".toMediaType())
            )

        } else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.submit_renewal_card)) {
            viewModel.downloadRenewalCard(
                this, requestBody.toRequestBody("application/json".toMediaType())
            )

        } else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.submit_lost_card)) {
            viewModel.downloadLostCard(
                this, requestBody.toRequestBody("application/json".toMediaType())
            )

        } else if (intent.extras?.getString(AppConstants.UPDATE_REQUEST) == getString(R.string.submit_surrender_card)) {
            viewModel.downloadSurrenderCard(
                this, requestBody.toRequestBody("application/json".toMediaType())
            )
        }
    }


    private fun openPDF(pdfFile: File) {
        try {
            // Create a URI for the PDF file using FileProvider for secure file access
            val pdfUri: Uri = FileProvider.getUriForFile(
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
package com.swavlambancard.udid.ui.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.swavlambancard.udid.R
import com.swavlambancard.udid.callBack.DialogCallback
import com.swavlambancard.udid.databinding.ActivityDashboardBinding
import com.swavlambancard.udid.model.DashboardData
import com.swavlambancard.udid.model.UserData
import com.swavlambancard.udid.ui.adapter.DashboardAdapter
import com.swavlambancard.udid.utilities.AppConstants
import com.swavlambancard.udid.utilities.BaseActivity
import com.swavlambancard.udid.utilities.JSEncryptService
import com.swavlambancard.udid.utilities.Preferences
import com.swavlambancard.udid.utilities.Preferences.getPreferenceOfLogin
import com.swavlambancard.udid.utilities.UDID
import com.swavlambancard.udid.utilities.Utility
import com.swavlambancard.udid.utilities.Utility.showConfirmationAlertDialog
import com.swavlambancard.udid.utilities.Utility.showSnackbar
import com.swavlambancard.udid.viewModel.ViewModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale

class DashboardActivity : BaseActivity<ActivityDashboardBinding>() {

    private var mBinding: ActivityDashboardBinding? = null
    private var dashboardAdapter: DashboardAdapter? = null
    private var gridLayoutManager: GridLayoutManager? = null
    private var dashboardList = ArrayList<DashboardData>()
    private var backCounts = 0
    private var viewModel = ViewModel()

    override val layoutId: Int
        get() = R.layout.activity_dashboard

    override fun initView() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
        viewModel.init()
        trackerAdapter()
    }

    override fun setVariables() {
        mBinding?.leftDrawerMenu?.llLogout?.setOnClickListener {

            showConfirmationAlertDialog(this, object : DialogCallback {
                override fun onYes() {
                    viewModel.getLogout(
                        this@DashboardActivity,
                        getPreferenceOfLogin(
                            context, AppConstants.LOGIN_DATA, UserData::class.java
                        ).application_number.toString().toRequestBody(MultipartBody.FORM),
                        "mobile".toRequestBody(MultipartBody.FORM)
                    )
                }
            })
        }

        mBinding?.contentNav?.ivSetting?.setOnClickListener {
            if (mBinding?.drawerLayout?.isDrawerOpen(GravityCompat.END) == true) {
                mBinding?.drawerLayout?.closeDrawer(GravityCompat.START)
            } else {
                mBinding?.drawerLayout?.openDrawer(GravityCompat.END)
            }
        }
        mBinding?.contentNav?.ivLanguage?.setOnClickListener {
            startActivity(Intent(this, LanguageActivity::class.java))
        }

        mBinding?.leftDrawerMenu?.llPrivacyPolicyHeading?.setOnClickListener {
            startActivity(
                Intent(this, WebViewActivity::class.java).putExtra(
                    AppConstants.WEB_URL,
                    "privacy_policy"
                )
            )
        }
        mBinding?.leftDrawerMenu?.llTermsAndConditionHeading?.setOnClickListener {
            startActivity(
                Intent(this, WebViewActivity::class.java).putExtra(
                    AppConstants.WEB_URL,
                    "terms_and_conditions"
                )
            )
        }
    }

    override fun setObservers() {

        viewModel.logoutResult.observe(this) {
            val userResponseModel = it
            if (userResponseModel != null) {
                when (userResponseModel._resultflag) {
                    0 -> {
                        mBinding?.contentNav?.rlParent?.let { it1 ->
                            showSnackbar(
                                it1, userResponseModel.message
                            )
                        }
                    }

                    else -> {
                        Preferences.removeAllPreference(this@DashboardActivity)
                        Utility.clearAllPreferencesExceptDeviceToken(this@DashboardActivity)
                        val intent = Intent(this@DashboardActivity, LoginActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }
                }
            }
        }

        viewModel.downloadResult.observe(this) { result ->
            result.onSuccess { file ->
                openPDF(file)
            }.onFailure { error ->
                Toast.makeText(this, "Download failed: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.errors.observe(this) {
            mBinding?.contentNav?.let { it1 -> showSnackbar(it1.rlParent, it) }
        }
    }

    inner class ClickActions

    private fun trackerAdapter() {
        dashboardList.add(DashboardData(getString(R.string.my_n_account), R.drawable.my_account))
        dashboardList.add(
            DashboardData(
                getString(R.string.update_n_name), R.drawable.update_namesvg
            )
        )
        dashboardList.add(
            DashboardData(
                getString(R.string.update_aadhar_n_number), R.drawable.ic_aadhaar
            )
        )
        dashboardList.add(
            DashboardData(
                getString(R.string.update_date_n_of_birth), R.drawable.ic_dob
            )
        )
        dashboardList.add(
            DashboardData(
                getString(R.string.update_mobile_n_number), R.drawable.mobile
            )
        )
        dashboardList.add(DashboardData(getString(R.string.update_n_email_id), R.drawable.ic_email))
        dashboardList.add(
            DashboardData(
                getString(R.string.surrender_n_card), R.drawable.surrender_card
            )
        )
        dashboardList.add(
            DashboardData(
                getString(R.string.track_your_n_card), R.drawable.track_your_card
            )
        )
        dashboardList.add(
            DashboardData(
                getString(R.string.apply_for_n_re_issue), R.drawable.apply_for_reissue
            )
        )
        dashboardList.add(
            DashboardData(
                getString(R.string.lost_card_card_not_recieved), R.drawable.ic_lost_card
            )
        )
        dashboardList.add(DashboardData(getString(R.string.appeal), R.drawable.ic_appeal))
        dashboardList.add(
            DashboardData(
                getString(R.string.update_personal_profile), R.drawable.ic_personal_profile
            )
        )
        dashboardList.add(
            DashboardData(
                getString(R.string.application_statuss), R.drawable.applicaton_status
            )
        )
        dashboardList.add(
            DashboardData(
                getString(R.string.download_application), R.drawable.download_application
            )
        )
        dashboardList.add(
            DashboardData(
                getString(R.string.download_receipt), R.drawable.ic_download_receipt
            )
        )
        dashboardList.add(
            DashboardData(
                getString(R.string.e_disability_certificate), R.drawable.e_disability_certificate
            )
        )
        dashboardList.add(DashboardData(getString(R.string.e_udid_card), R.drawable.e_udid_card))
        dashboardList.add(
            DashboardData(
                getString(R.string.doctor_s_diagnosis_sheet), R.drawable.ic_doctor_sheet
            )
        )
        dashboardList.add(
            DashboardData(
                getString(R.string.feedback_query), R.drawable.write_grievence
            )
        )

//        menuValidate()
        dashboardAdapter = DashboardAdapter(this, dashboardList)
        gridLayoutManager = GridLayoutManager(this, 3)
        mBinding?.contentNav?.rvApplicationStatus?.layoutManager = gridLayoutManager
        mBinding?.contentNav?.rvApplicationStatus?.adapter = dashboardAdapter
    }

    fun startDownload(isFrom: String) {
        val requestBody = JSONObject().apply {
            put(
                "application_number", JSEncryptService.encrypt(
                    getPreferenceOfLogin(
                        context, AppConstants.LOGIN_DATA, UserData::class.java
                    ).application_number.toString()
                )
            )
            put("type", "mobile")
        }.toString()
        when (isFrom) {
            getString(R.string.application) -> viewModel.downloadApplication(
                this, requestBody.toRequestBody("application/json".toMediaType())
            )
            getString(R.string.receipt) -> viewModel.downloadReceipt(
                this, requestBody.toRequestBody("application/json".toMediaType())
            )
            getString(R.string.your_e_disability_certificate) -> viewModel.downloadEDisabilityCertificate(
                this, requestBody.toRequestBody("application/json".toMediaType())
            )
            getString(R.string.your_e_udid_card) -> viewModel.downloadUdidCard(
                this, requestBody.toRequestBody("application/json".toMediaType())
            )
            getString(R.string.doctor_diagnosis_sheet) -> viewModel.downloadDoctorDiagnosisSheet(
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

    private fun menuValidate() {
        if (getPreferenceOfLogin(
                this, AppConstants.LOGIN_DATA, UserData::class.java
            ).udid_number != null
        ) {
            if (getPreferenceOfLogin(
                    this, AppConstants.LOGIN_DATA, UserData::class.java
                ).pwd_card_expiry_date == null || !isWithin90Days(
                    getPreferenceOfLogin(
                        this@DashboardActivity, AppConstants.LOGIN_DATA, UserData::class.java
                    ).pwd_card_expiry_date.toString()
                ) || getPreferenceOfLogin(
                    this, AppConstants.LOGIN_DATA, UserData::class.java
                ).disability_type_pt == "P" || getPreferenceOfLogin(
                    this, AppConstants.LOGIN_DATA, UserData::class.java
                ).disability_type_pt == "Permanent"
            ) {
                dashboardList.remove(
                    DashboardData(
                        getString(R.string.apply_for_n_re_issue), R.drawable.apply_for_reissue
                    )
                )
            }

            // 2. Disable 'track-your-card' menu
            if (getPreferenceOfLogin(
                    this, AppConstants.LOGIN_DATA, UserData::class.java
                ).pwddispatch == null
            ) {
                dashboardList.remove(
                    DashboardData(
                        getString(R.string.track_your_n_card), R.drawable.track_your_card
                    )
                )
            }

            // 3. Disable 'updatePersonalProfile' menu
            if (!listOf(1, 20, 3, 29, 32).contains(
                    getPreferenceOfLogin(
                        this, AppConstants.LOGIN_DATA, UserData::class.java
                    ).application_status
                )
            ) {
                dashboardList.remove(
                    DashboardData(
                        getString(R.string.update_personal_profile), R.drawable.ic_personal_profile
                    )
                )
            }

            // 4. Manage 'appeal' menu
            when (getPreferenceOfLogin(
                this, AppConstants.LOGIN_DATA, UserData::class.java
            ).appealrequest) {
                0 -> dashboardList.remove(
                    DashboardData(
                        getString(R.string.appeal), R.drawable.ic_appeal
                    )
                ) // Hide 'appeal' menu
            }

            // 5. Handle application status = 13
            if (getPreferenceOfLogin(
                    this, AppConstants.LOGIN_DATA, UserData::class.java
                ).application_status == 13
            ) {
                dashboardList.clear()
                dashboardList.addAll(
                    listOf(
                        DashboardData(
                            getString(R.string.my_n_account), R.drawable.my_account
                        ), DashboardData(
                            getString(R.string.application_statuss), R.drawable.applicaton_status
                        )
                    )
                )
            }
        } else {
            // 6. Handle cases where udidNumber is missing
            when {
                listOf(1, 3, 29, 32).contains(
                    getPreferenceOfLogin(
                        this, AppConstants.LOGIN_DATA, UserData::class.java
                    ).application_status
                ) -> {
                    dashboardList.clear()
                    dashboardList.addAll(
                        listOf(
                            DashboardData(
                                getString(R.string.my_n_account), R.drawable.my_account
                            ), DashboardData(
                                getString(R.string.update_personal_profile),
                                R.drawable.ic_personal_profile
                            ), DashboardData(
                                getString(R.string.download_application),
                                R.drawable.download_application
                            ), DashboardData(
                                getString(R.string.download_receipt), R.drawable.ic_download_receipt
                            )
                        )
                    )
                }

                getPreferenceOfLogin(
                    this, AppConstants.LOGIN_DATA, UserData::class.java
                ).appealrequest == 1 -> {
                    dashboardList.clear()
                    dashboardList.addAll(
                        listOf(
                            DashboardData(
                                getString(R.string.my_n_account), R.drawable.my_account
                            ),
                            DashboardData(getString(R.string.appeal), R.drawable.ic_appeal),
                            DashboardData(
                                getString(R.string.download_application),
                                R.drawable.download_application
                            ),
                            DashboardData(
                                getString(R.string.download_receipt), R.drawable.ic_download_receipt
                            )
                        )
                    )
                }

                else -> {
                    dashboardList.clear()
                    dashboardList.addAll(
                        listOf(
                            DashboardData(
                                getString(R.string.my_n_account), R.drawable.my_account
                            ), DashboardData(
                                getString(R.string.application_statuss),
                                R.drawable.applicaton_status
                            ), DashboardData(
                                getString(R.string.download_application),
                                R.drawable.download_application
                            ), DashboardData(
                                getString(R.string.download_receipt), R.drawable.ic_download_receipt
                            )
                        )
                    )
                }
            }
        }
    }

    private fun isWithin90Days(pwdCardExpiryDate: String): Boolean {
        // Parse the expiry date (format: yyyy-MM-dd)
        val expiryDate = LocalDate.parse(pwdCardExpiryDate)

        // Get the current date
        val currentDate = LocalDate.now()

        // Calculate the days between the current date and expiry date
        val daysUntilExpiry = ChronoUnit.DAYS.between(currentDate, expiryDate)

        // Return true if within 90 days, false otherwise
        return daysUntilExpiry in 0..90
    }

    override fun onBackPressed() {
        super.onBackPressed()
        backCounts++
        Utility.Run.after(2000) {
            backCounts = 0
        }
        if (backCounts >= 2) {
            finishAffinity()
        } else {
            mBinding?.contentNav?.rlParent?.let {
                showSnackbar(
                    it, getString(R.string.press_back_again)
                )
            }
        }
    }
}
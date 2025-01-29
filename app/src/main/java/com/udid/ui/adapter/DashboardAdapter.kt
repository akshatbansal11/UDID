package com.udid.ui.adapter


import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.udid.R
import com.udid.databinding.DashboardItemBinding
import com.udid.model.DashboardData
import com.udid.model.UserData
import com.udid.ui.activity.AppealActivity
import com.udid.ui.activity.ApplicationStatusActivity
import com.udid.ui.activity.DashboardActivity
import com.udid.ui.activity.FeedbackAndQueryActivity
import com.udid.ui.activity.LostCardActivity
import com.udid.ui.activity.MyAccountActivity
import com.udid.ui.activity.RenewalCardActivity
import com.udid.ui.activity.SurrenderCardActivity
import com.udid.ui.activity.TrackYourCardActivity
import com.udid.ui.activity.UpdateAadharNumberActivity
import com.udid.ui.activity.UpdateDateOfBirthActivity
import com.udid.ui.activity.UpdateEmailIDActivity
import com.udid.ui.activity.UpdateMobileNumberActivity
import com.udid.ui.activity.UpdateNameActivity
import com.udid.ui.activity.UpdateRequestActivity
import com.udid.utilities.AppConstants
import com.udid.utilities.Preferences.getPreferenceOfLogin
import com.udid.utilities.toast

class DashboardAdapter(
    private val context: Context,
    private var items: ArrayList<DashboardData>,

    ) : RecyclerView.Adapter<DashboardAdapter.MyViewHolder>() {

    class MyViewHolder(val mBinding: DashboardItemBinding) :
        RecyclerView.ViewHolder(mBinding.root)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MyViewHolder {
        val mBinding: DashboardItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(viewGroup.context),
            R.layout.dashboard_item,
            viewGroup,
            false
        )
        return MyViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = items[position]

        holder.mBinding.ivImage.setImageResource(currentItem.image)
        holder.mBinding.tvTitle.text = currentItem.title
        when (currentItem.title) {
            context.getString(R.string.my_n_account) -> {
                holder.mBinding.llParent.setOnClickListener {
                    context.startActivity(Intent(context, MyAccountActivity::class.java))
                }
            }

            context.getString(R.string.update_n_name) -> {
                holder.mBinding.llParent.setOnClickListener {
                    if (getPreferenceOfLogin(
                            context,
                            AppConstants.LOGIN_DATA,
                            UserData::class.java
                        ).updaterequest.Name == 1
                    ) {
                        context.startActivity(
                            Intent(context, UpdateRequestActivity::class.java)
                                .putExtra(
                                    AppConstants.UPDATE_REQUEST,
                                    context.getString(R.string.update_n_name)
                                )
                        )
                    } else {
                        context.startActivity(Intent(context, UpdateNameActivity::class.java))
                    }
                }
            }

            context.getString(R.string.update_aadhar_n_number) -> {
                holder.mBinding.llParent.setOnClickListener {
                    if (getPreferenceOfLogin(
                            context,
                            AppConstants.LOGIN_DATA,
                            UserData::class.java
                        ).updaterequest.AadhaarNumber == 1
                    ) {
                        context.startActivity(
                            Intent(context, UpdateRequestActivity::class.java)
                                .putExtra(
                                    AppConstants.UPDATE_REQUEST,
                                    context.getString(R.string.aadhaar_number)
                                )
                        )
                    } else {
                        context.startActivity(
                            Intent(
                                context,
                                UpdateAadharNumberActivity::class.java
                            )
                        )
                    }
                }
            }

            context.getString(R.string.update_date_n_of_birth) -> {
                holder.mBinding.llParent.setOnClickListener {
                    if (getPreferenceOfLogin(
                            context,
                            AppConstants.LOGIN_DATA,
                            UserData::class.java
                        ).updaterequest.DateOfBirth == 1
                    ) {
                        context.startActivity(
                            Intent(context, UpdateRequestActivity::class.java)
                                .putExtra(
                                    AppConstants.UPDATE_REQUEST,
                                    context.getString(R.string.date_of_birth_)
                                )
                        )
                    } else {
                        context.startActivity(
                            Intent(
                                context,
                                UpdateDateOfBirthActivity::class.java
                            )
                        )
                    }
                }
            }

            context.getString(R.string.update_mobile_n_number) -> {
                holder.mBinding.llParent.setOnClickListener {
                    if (getPreferenceOfLogin(
                            context,
                            AppConstants.LOGIN_DATA,
                            UserData::class.java
                        ).updaterequest.Mobile == 1
                    ) {
                        context.startActivity(
                            Intent(context, UpdateRequestActivity::class.java)
                                .putExtra(
                                    AppConstants.UPDATE_REQUEST,
                                    context.getString(R.string.mobile_number_)
                                )
                        )
                    } else {
                        context.startActivity(
                            Intent(
                                context,
                                UpdateMobileNumberActivity::class.java
                            )
                        )
                    }
                }
            }

            context.getString(R.string.update_n_email_id) -> {
                holder.mBinding.llParent.setOnClickListener {
                    if (getPreferenceOfLogin(
                            context,
                            AppConstants.LOGIN_DATA,
                            UserData::class.java
                        ).updaterequest.Email == 1
                    ) {
                        context.startActivity(
                            Intent(context, UpdateRequestActivity::class.java)
                                .putExtra(
                                    AppConstants.UPDATE_REQUEST,
                                    context.getString(R.string.email_id_)
                                )
                        )
                    } else {
                        context.startActivity(Intent(context, UpdateEmailIDActivity::class.java))
                    }
                }
            }

            context.getString(R.string.surrender_n_card) -> {
                holder.mBinding.llParent.setOnClickListener {
                    if (getPreferenceOfLogin(
                            context,
                            AppConstants.LOGIN_DATA,
                            UserData::class.java
                        ).surrenderrequest == 1
                    ) {
                        context.startActivity(
                            Intent(context, UpdateRequestActivity::class.java)
                                .putExtra(
                                    AppConstants.UPDATE_REQUEST,
                                    context.getString(R.string.surrender_n_card)
                                )
                        )
                    } else {
                        context.startActivity(Intent(context, SurrenderCardActivity::class.java))
                    }
                }
            }

            context.getString(R.string.track_your_n_card) -> {
                holder.mBinding.llParent.setOnClickListener {
                    context.startActivity(Intent(context, TrackYourCardActivity::class.java))
                }
            }

            context.getString(R.string.apply_for_n_re_issue) -> {
                holder.mBinding.llParent.setOnClickListener {
                    if (getPreferenceOfLogin(
                            context,
                            AppConstants.LOGIN_DATA,
                            UserData::class.java
                        ).renewalrequest == 1
                    ) {
                        context.startActivity(
                            Intent(context, UpdateRequestActivity::class.java)
                                .putExtra(
                                    AppConstants.UPDATE_REQUEST,
                                    context.getString(R.string.renewal_card)
                                )
                        )
                    } else {
                        context.startActivity(Intent(context, RenewalCardActivity::class.java))
                    }
                }
            }

            context.getString(R.string.lost_card_card_not_recieved) -> {
                holder.mBinding.llParent.setOnClickListener {
                    if (getPreferenceOfLogin(
                            context,
                            AppConstants.LOGIN_DATA,
                            UserData::class.java
                        ).lostcardrequest == 1
                    ) {
                        context.startActivity(
                            Intent(context, UpdateRequestActivity::class.java)
                                .putExtra(
                                    AppConstants.UPDATE_REQUEST,
                                    context.getString(R.string.lost_card_card_not_recieved)
                                )
                        )
                    } else {
                        context.startActivity(Intent(context, LostCardActivity::class.java))
                    }
                }
            }

            context.getString(R.string.appeal) -> {
                holder.mBinding.llParent.setOnClickListener {
                    if (getPreferenceOfLogin(
                            context,
                            AppConstants.LOGIN_DATA,
                            UserData::class.java
                        ).appealrequest == 1
                    ) {
                        context.startActivity(
                            Intent(context, UpdateRequestActivity::class.java)
                                .putExtra(
                                    AppConstants.UPDATE_REQUEST,
                                    context.getString(R.string.appeal)
                                )
                        )
                    } else {
                        context.startActivity(Intent(context, AppealActivity::class.java))
                    }
                }
            }

            context.getString(R.string.update_personal_profile) -> {
                holder.mBinding.llParent.setOnClickListener {
                    context.toast(context.getString(R.string.coming_soon))
                }
            }

            context.getString(R.string.application_statuss) -> {
                holder.mBinding.llParent.setOnClickListener {
                    context.startActivity(Intent(context, ApplicationStatusActivity::class.java))
                }
            }

            context.getString(R.string.download_application) -> {
                holder.mBinding.llParent.setOnClickListener {
                    (context as DashboardActivity).downloadApplication(
                        context, AppConstants.DOWNLOAD_APPLICATION,
                        context.getString(
                            R.string.application
                        )
                    ) { result ->
                        result.fold(
                            onSuccess = { file ->
                                Log.d(
                                    "DownloadApplication",
                                    "PDF downloaded successfully: ${file.absolutePath}"
                                )
                                context.openPDF(file)
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

            context.getString(R.string.download_receipt) -> {
                holder.mBinding.llParent.setOnClickListener {
                    (context as DashboardActivity).downloadApplication(
                        context, AppConstants.DOWNLOAD_RECEIPT,
                        context.getString(R.string.receipt)
                    ) { result ->
                        result.fold(
                            onSuccess = { file ->
                                Log.d(
                                    "DownloadApplication",
                                    "PDF downloaded successfully: ${file.absolutePath}"
                                )
                                context.openPDF(file)
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

            context.getString(R.string.e_disability_certificate) -> {
                holder.mBinding.llParent.setOnClickListener {
                    (context as DashboardActivity).downloadApplication(
                        context, AppConstants.DOWNLOAD_YOUR_E_DISABILITY_CERTIFICATE,
                        context.getString(R.string.your_e_disability_certificate)
                    ) { result ->
                        result.fold(
                            onSuccess = { file ->
                                Log.d(
                                    "DownloadApplication",
                                    "PDF downloaded successfully: ${file.absolutePath}"
                                )
                                context.openPDF(file)
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

            context.getString(R.string.e_udid_card) -> {
                holder.mBinding.llParent.setOnClickListener {
                    (context as DashboardActivity).downloadApplication(
                        context, AppConstants.DOWNLOAD_YOUR_UDID_CARD,
                        context.getString(R.string.your_e_udid_card),
                    ) { result ->
                        result.fold(
                            onSuccess = { file ->
                                Log.d(
                                    "DownloadApplication",
                                    "PDF downloaded successfully: ${file.absolutePath}"
                                )
                                context.openPDF(file)
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

            context.getString(R.string.doctor_s_diagnosis_sheet) -> {
                holder.mBinding.llParent.setOnClickListener {
                    (context as DashboardActivity).downloadApplication(
                        context, AppConstants.DOWNLOAD_DOCTOR_DIAGNOSIS_SHEET,
                        context.getString(R.string.doctor_diagnosis_sheet),
                    ) { result ->
                        result.fold(
                            onSuccess = { file ->
                                Log.d(
                                    "DownloadApplication",
                                    "PDF downloaded successfully: ${file.absolutePath}"
                                )
                                context.openPDF(file)
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

            context.getString(R.string.feedback_query) -> {
                holder.mBinding.llParent.setOnClickListener {
                    context.startActivity(Intent(context, FeedbackAndQueryActivity::class.java))
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

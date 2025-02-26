package com.swavlambancard.udid.ui.activity

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import android.widget.TextView
import com.swavlambancard.udid.R
import com.swavlambancard.udid.databinding.ActivityApplyForUdidFormBinding
import com.swavlambancard.udid.model.PendingApplicationWise
import com.swavlambancard.udid.model.RejectApplicationRequest
import com.swavlambancard.udid.model.UserData
import com.swavlambancard.udid.utilities.AppConstants
import com.swavlambancard.udid.utilities.BaseActivity
import com.swavlambancard.udid.utilities.Preferences.getPreferenceOfLogin
import com.swavlambancard.udid.utilities.Utility.showSnackbar
import com.swavlambancard.udid.utilities.hideView
import com.swavlambancard.udid.utilities.showView
import com.swavlambancard.udid.utilities.toast
import com.swavlambancard.udid.viewModel.ViewModel
import java.util.Calendar

class ApplyForUdidFormActivity: BaseActivity<ActivityApplyForUdidFormBinding>() {

    override val layoutId: Int
        get() = R.layout.activity_apply_for_udid_form
    var date: String? = null
    private var isFrom: Int? = null
    private var viewModel = ViewModel()
    private var mBinding: ActivityApplyForUdidFormBinding? = null

    override fun initView() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
        viewModel.init()
        mBinding?.radioGroup?.setOnCheckedChangeListener { _, checkedButton ->
            when (checkedButton) {
                R.id.radio_option1 -> {
                    mBinding?.llUdidCardReject?.hideView()
                    mBinding?.llUdidCardPending?.hideView()
                    mBinding?.llUdidCard?.hideView()
                    isFrom = 1
                }

                R.id.radio_option2 -> {
                    mBinding?.llUdidCardReject?.hideView()
                    mBinding?.llUdidCardPending?.hideView()
                    mBinding?.llUdidCard?.hideView()
                    isFrom = 2
                }

                R.id.radio_option3 -> {
                    mBinding?.llUdidCardReject?.showView()
                    mBinding?.llUdidCardPending?.hideView()
                    mBinding?.llUdidCard?.hideView()
                    date = ""
                    mBinding?.etDobPending?.text = ""
                    mBinding?.etDob?.text = ""
                }

                R.id.radio_option4 -> {
                    mBinding?.llUdidCardReject?.hideView()
                    mBinding?.llUdidCardPending?.hideView()
                    mBinding?.llUdidCard?.showView()
                }

                R.id.radio_option5 -> {
                    mBinding?.llUdidCardReject?.hideView()
                    mBinding?.llUdidCardPending?.showView()
                    mBinding?.llUdidCard?.hideView()
                    date = ""
                    mBinding?.etDobPending?.text = ""
                    mBinding?.etDob?.text = ""

                }
            }
        }
    }

    override fun setVariables() {

    }

    override fun setObservers() {
        viewModel.rejectApplicationRequestResult.observe(this) {
            val userResponseModel = it
            if (userResponseModel?._resultflag != 0) {
                toast(userResponseModel.message)
            } else {
                mBinding?.clParent?.let { it1 -> showSnackbar(it1, userResponseModel.message) }
            }
        }
        viewModel.pendingApplicationWiseResult.observe(this) {
            val userResponseModel = it
            if (userResponseModel?._resultflag != 0) {
                toast(userResponseModel.message)
            } else {
                mBinding?.clParent?.let { it1 -> showSnackbar(it1, userResponseModel.message) }
            }
        }
        viewModel.errors.observe(this) {
            mBinding?.let { it1 -> showSnackbar(it1.clParent, it) }
        }
    }

    inner class ClickActions {
        fun backPress(view: View) {
            onBackPressedDispatcher.onBackPressed()
        }

        fun dob(view: View) {
            calenderOpen(this@ApplyForUdidFormActivity, mBinding?.etDob!!)
        }

        fun dobPending(view: View) {
            calenderOpen(this@ApplyForUdidFormActivity, mBinding?.etDobPending!!)
        }

        fun submit(view: View) {
            val selectedRadioButtonId = mBinding?.radioGroup?.checkedRadioButtonId
            when (selectedRadioButtonId) {
                -1 -> {
                    mBinding?.clParent?.let {
                        showSnackbar(
                            it,
                            getString(R.string.please_select_an_option)
                        )
                    }
                }

                R.id.radio_option1 -> {
                    startActivity(
                        Intent(this@ApplyForUdidFormActivity, PersonalProfileActivity::class.java)
                            .putExtra(AppConstants.IS_FROM, "login")
                            .putExtra(AppConstants.CHECK, isFrom)
                    )
                }

                R.id.radio_option2 -> {
                    startActivity(
                        Intent(this@ApplyForUdidFormActivity, PersonalProfileActivity::class.java)
                            .putExtra(AppConstants.IS_FROM, "login")
                            .putExtra(AppConstants.CHECK, isFrom)
                    )
                }

                R.id.radio_option3 -> {
                    rejectApplicationRequestApi()

                }

                R.id.radio_option4 -> {
                    startActivity(
                        Intent(
                            this@ApplyForUdidFormActivity,
                            PwdLoginActivity::class.java
                        )
                    )
                }

                R.id.radio_option5 -> {
                    pendingApplicationWiseApi()
                }
            }
        }
    }

    private fun pendingApplicationWiseApi() {
        viewModel.pendingApplicationWise(
            this, PendingApplicationWise(
                mBinding?.etEnrollmentPending?.text.toString().trim(), date, "mobile"
            )
        )
    }

    private fun rejectApplicationRequestApi() {
        viewModel.rejectApplicationRequest(
            this, RejectApplicationRequest(
                mBinding?.etEnrollment?.text.toString().trim(), date, "mobile"
            )
        )
    }

    private fun calenderOpen(context: Context, editText: TextView) {
        val cal: Calendar = Calendar.getInstance()
        Log.d("Calendar", "tvDob clicked")
        // Parse the date from editText if it contains a valid date
        val currentText = editText.text.toString()
        if (currentText.isNotEmpty()) {
            try {
                val parts = currentText.split("/")
                if (parts.size == 3) {
                    val day = parts[0].toInt()
                    val month = parts[1].toInt() - 1 // Months are 0-based in Calendar
                    val year = parts[2].toInt()
                    cal.set(year, month, day)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val year: Int = cal.get(Calendar.YEAR)
        val month: Int = cal.get(Calendar.MONTH)
        val day: Int = cal.get(Calendar.DAY_OF_MONTH)

        val dialog = DatePickerDialog(
            context,
            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedMonth =
                    String.format("%02d", selectedMonth + 1) // Add 1 to month and format
                val formattedDay =
                    String.format("%02d", selectedDay) // Format day as two digits if needed
                Log.d(
                    "Date",
                    "onDateSet: MM/dd/yyyy: $formattedMonth/$formattedDay/$selectedYear"
                )
                date = "$selectedYear-$formattedMonth-$formattedDay"
                editText.text = "$formattedDay/$formattedMonth/$selectedYear"
            },
            year, month, day
        )
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }
}
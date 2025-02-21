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
import com.swavlambancard.udid.utilities.AppConstants
import com.swavlambancard.udid.utilities.BaseActivity
import com.swavlambancard.udid.utilities.Utility.showSnackbar
import com.swavlambancard.udid.utilities.hideView
import com.swavlambancard.udid.utilities.showView
import java.util.Calendar

class ApplyForUdidFormActivity() : BaseActivity<ActivityApplyForUdidFormBinding>() {

    override val layoutId: Int
        get() = R.layout.activity_apply_for_udid_form
    var date: String? = null
    private var mBinding: ActivityApplyForUdidFormBinding? = null

    override fun initView() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
        mBinding?.etDob?.setOnClickListener {
            calenderOpen(this@ApplyForUdidFormActivity, mBinding?.etDob!!)
        }
        mBinding?.etDobPending?.setOnClickListener {
            calenderOpen(this@ApplyForUdidFormActivity, mBinding?.etDobPending!!)
        }
        mBinding?.radioGroup?.setOnCheckedChangeListener { radioGroup, checkedButton ->
            when (checkedButton) {
                R.id.radio_option1 -> {
                    mBinding?.llUdidCardReject?.hideView()
                    mBinding?.llUdidCardPending?.hideView()
                    mBinding?.llUdidCard?.hideView()
                }

                R.id.radio_option2 -> {
                    mBinding?.llUdidCardReject?.hideView()
                    mBinding?.llUdidCardPending?.hideView()
                    mBinding?.llUdidCard?.hideView()
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
        mBinding?.tvSubmit?.setOnClickListener {
            val selectedRadioButtonId = mBinding?.radioGroup?.checkedRadioButtonId
            if (selectedRadioButtonId == -1) {
                mBinding?.main?.let {
                    showSnackbar(
                        it,
                        getString(R.string.please_select_an_option)
                    )
                }
            } else {

                startActivity(Intent(this, PersonalProfileActivity::class.java)
                    .putExtra(AppConstants.IS_FROM,"login"))
            }
        }
    }

    inner class ClickActions {
        fun backPress(view: View) {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun setVariables() {

    }

    override fun setObservers() {

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
package com.udid.ui.activity

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.udid.R
import com.udid.databinding.ActivityUpdateAadharNumberBinding
import com.udid.databinding.ActivityUpdateDateOfBirthBinding
import com.udid.utilities.BaseActivity
import com.udid.utilities.Utility.convertDate
import com.udid.viewModel.ViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UpdateDateOfBirthActivity : BaseActivity<ActivityUpdateDateOfBirthBinding>() {

    private var mBinding: ActivityUpdateDateOfBirthBinding? = null
    private var viewModel= ViewModel()
    private var etUpdatedDOB: String? = null


    override val layoutId: Int
        get() = R.layout.activity_update_date_of_birth

    override fun initView() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
        viewModel.init()
    }
    inner class ClickActions {
        fun backPress(view: View){
            onBackPressedDispatcher.onBackPressed()
        }
        @RequiresApi(Build.VERSION_CODES.O)
        fun openCalendar(view: View){
            openCalendar("etUpdatedDOB", mBinding?.etUpdatedDOB!!)
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun openCalendar(type: String, selectedTextView: TextView) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, day ->
                val calendarInstance = Calendar.getInstance().apply {
                    set(year, month, day)
                }
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
                val formattedDate = sdf.format(calendarInstance.time)

                // Handle each case
                when (type) {
                    "etUpdatedDOB" -> etUpdatedDOB = formattedDate
                    else -> {
                        // Optional: Handle unknown types
                        Log.w("Calendar", "Unknown type: $type")
                    }
                }

                // Set the selected date in the TextView
                selectedTextView.text = convertDate(formattedDate)
                selectedTextView.setTextColor(
                    ContextCompat.getColor(this, R.color.black)
                )
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    override fun setVariables() {
    }

    override fun setObservers() {
    }

}
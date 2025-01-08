package com.udid.ui.activity

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.udid.R
import com.udid.databinding.ActivityDashboardBinding
import com.udid.model.DashboardData
import com.udid.ui.adapter.DashboardAdapter
import com.udid.utilities.BaseActivity
import com.udid.utilities.Preferences
import com.udid.utilities.Utility

class DashboardActivity : BaseActivity<ActivityDashboardBinding>() {

    private var mBinding: ActivityDashboardBinding ?= null
    private var dashboardAdapter: DashboardAdapter ?= null
    private var gridLayoutManager: GridLayoutManager ?= null
    private var dashboardList: ArrayList<DashboardData> ?= null

    override val layoutId: Int
        get() = R.layout.activity_dashboard

    override fun initView() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()


        trackerAdapter()
    }

    inner class ClickActions {
        fun backPress(view: View){
            onBackPressedDispatcher.onBackPressed()
        }
        fun logout(view: View){
            Preferences.removeAllPreference(this@DashboardActivity)
            Utility.clearAllPreferencesExceptDeviceToken(this@DashboardActivity)
            val intent = Intent(this@DashboardActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    private fun trackerAdapter() {

        dashboardList = arrayListOf(
            DashboardData(getString(R.string.my_n_account),R.drawable.my_account),
            DashboardData(getString(R.string.update_n_name),R.drawable.update_namesvg),
            DashboardData(getString(R.string.update_aadhar_n_number),R.drawable.ic_aadhaar),
            DashboardData(getString(R.string.update_date_n_of_birth),R.drawable.ic_dob),
            DashboardData(getString(R.string.update_mobile_n_number),R.drawable.mobile),
            DashboardData(getString(R.string.update_n_email_id),R.drawable.ic_email),
            DashboardData(getString(R.string.surrender_n_card),R.drawable.surrender_card),
            DashboardData(getString(R.string.track_your_n_card),R.drawable.track_your_card),
            DashboardData(getString(R.string.apply_for_n_re_issue),R.drawable.apply_for_reissue),
            DashboardData(getString(R.string.application_statuss),R.drawable.applicaton_status),
            DashboardData(getString(R.string.medical_camps_hospitals),R.drawable.medical_camps),
            DashboardData(getString(R.string.district_welfare_office),R.drawable.district_welfare),
            DashboardData(getString(R.string.disability_scheme),R.drawable.disability),
            DashboardData(getString(R.string.write_your_grievance),R.drawable.write_grievence),
            DashboardData(getString(R.string.download_application),R.drawable.download_application),
            DashboardData(getString(R.string.e_disability_certificate),R.drawable.e_disability_certificate),
            DashboardData(getString(R.string.e_udid_card),R.drawable.e_udid_card),
//            DashboardData(getString(R.string.logout),R.drawable.ic_logout),
        )

        dashboardAdapter = DashboardAdapter(this, dashboardList!!)
        gridLayoutManager = GridLayoutManager(this, 3)
        mBinding?.rvApplicationStatus?.layoutManager = gridLayoutManager
        mBinding?.rvApplicationStatus?.adapter = dashboardAdapter
    }

    override fun setVariables() {
    }

    override fun setObservers() {
    }
}
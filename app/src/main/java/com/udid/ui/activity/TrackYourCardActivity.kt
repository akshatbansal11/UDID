package com.udid.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.udid.R
import com.udid.databinding.ActivityTrackYourCardBinding
import com.udid.model.ApplicationStatus
import com.udid.model.ApplicationStatusRequest
import com.udid.model.StatusArray
import com.udid.model.UserData
import com.udid.ui.adapter.TrackerAdapter
import com.udid.utilities.AppConstants
import com.udid.utilities.BaseActivity
import com.udid.utilities.JSEncryptService
import com.udid.utilities.Preferences
import com.udid.utilities.Preferences.getPreferenceOfLogin
import com.udid.utilities.Utility
import com.udid.viewModel.ViewModel

class TrackYourCardActivity : BaseActivity<ActivityTrackYourCardBinding>() {
    private var mBinding: ActivityTrackYourCardBinding? = null
    private var trackerAdapter: TrackerAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    private var trackerSelectedList = ArrayList<ApplicationStatus>()
    private var trackerList = ArrayList<StatusArray>()
    private var viewModel= ViewModel()
    override val layoutId: Int
        get() = R.layout.activity_track_your_card

    override fun initView() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
        viewModel.init()
        trackerAdapter()
        viewModel.getAppStatus(
            this@TrackYourCardActivity,
            ApplicationStatusRequest(
                JSEncryptService.encrypt(
                getPreferenceOfLogin(
                    this,
                    AppConstants.LOGIN_DATA,
                    UserData::class.java
                ).application_number.toString()
                ).toString()
            )
        )

    }

    override fun setVariables() {
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setObservers() {
        viewModel.appStatusResult.observe(this) {
            val userResponseModel = it
            if (userResponseModel._resultflag == 0 && userResponseModel.message == "You are not authorized to access that location") {
                Preferences.removeAllPreference(this)
                Utility.clearAllPreferencesExceptDeviceToken(this)
                Utility.savePreferencesString(this,AppConstants.WALK_THROUGH,"false")
                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
            else if (userResponseModel != null) {
                if (userResponseModel._resultflag == 0) {
                    mBinding?.rlParent?.let { it1 ->
                        Utility.showSnackbar(
                            it1,
                            userResponseModel.message
                        )
                    }
                } else if (userResponseModel.applicationstatus.isNotEmpty()) {
                    Log.d("Statuss", userResponseModel.applicationstatus.toString())
                    trackerList.clear()
                    trackerSelectedList.clear()
                    trackerSelectedList.addAll(userResponseModel.applicationstatus)
                    trackerList.addAll(userResponseModel.statusArray)
                    trackerAdapter?.notifyDataSetChanged()
                }
            }
        }
        viewModel.errors.observe(this) {
            mBinding?.let { it1 -> Utility.showSnackbar(it1.rlParent, it) }
        }
    }

    inner class ClickActions {
        fun backPress(view: View) {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun trackerAdapter() {
        Log.d("Statuss", trackerList.toString())
        trackerAdapter = TrackerAdapter(this@TrackYourCardActivity, trackerSelectedList,trackerList)
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding?.rvTracker?.layoutManager = layoutManager
        mBinding?.rvTracker?.adapter = trackerAdapter
    }
}
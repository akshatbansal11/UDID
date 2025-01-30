package com.udid.ui.activity

import android.view.View
import com.udid.viewModel.SharedDataViewModel
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.udid.R
import com.udid.databinding.ActivityPwdApplicationViewBinding
import com.udid.model.PwdApplication
import com.udid.ui.fragments.DisabilityDetailFragment
import com.udid.ui.fragments.HospitalAssesmentFragment
import com.udid.ui.fragments.PersonalDetailFragment
import com.udid.ui.fragments.ProofOfAddressFragment
import com.udid.ui.fragments.ProofOfIDFragment
import com.udid.utilities.BaseActivity

class PwdApplicationViewActivity : BaseActivity<ActivityPwdApplicationViewBinding>() {
    private var mBinding: ActivityPwdApplicationViewBinding? = null
    private var currentFragment: androidx.fragment.app.Fragment? = null
    private lateinit var sharedViewModel: SharedDataViewModel

    override val layoutId: Int
        get() = R.layout.activity_pwd_application_view

    override fun initView() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
        // Initialize ViewModel
        sharedViewModel = ViewModelProvider(this).get(SharedDataViewModel::class.java)

        // Set dummy data to ViewModel
        sharedViewModel.userData.value = getDummyData()
        replaceFragment(PersonalDetailFragment())
    }

    private fun getDummyData(): PwdApplication {
        return PwdApplication(
            applicantFullName = "John Doe",
            applicantMobileNo = "9876543210",
            applicantEmail = "john.doe@example.com",
            applicantDob = "1990-01-01",
            gender = 1, // 1 for Male, 2 for Female
            guardian = "Jane Doe",
            photo = 1, // Assuming 1 means the photo is uploaded
            sign = 1, // Assuming 1 means the signature is uploaded
            aadhaarCard = 1,
            aadhaarNo = "123456789012",
            aadhaarCheckBox = 1,
            aadhaarEnrollment = "Enrollment123",
            aadhaarUploadSlip = 1,
            identityProof = "Driving License",
            identityProofSlip = 1,
            documentAddressProof = "Electricity Bill",
            documentAddressProofPhoto = 1,
            documentAddress = "123 Main Street",
            state = "StateName",
            district = "DistrictName",
            city = "CityName",
            village = "VillageName",
            pincode = "123456",
            disabilityType = "Visual Impairment",
            disabilityDue = "Since Birth",
            disabilityBirth = 1,
            disabilitySince = "1990-01-01",
            treatingHospital = 1,
            hospitalState = "HospitalState",
            hospitalDistrict = "HospitalDistrict",
            hospitalName = "XYZ Hospital",
            hospitalCheckBox = 1
        )
    }


    override fun setVariables() {
        // Initialize variables here if needed
    }

    override fun setObservers() {
        // Set observers for LiveData or other reactive programming if needed
    }

    inner class ClickActions {
        fun backPress(view: View) {
            finish()
            onBackPressedDispatcher.onBackPressed()
        }
        fun personalDetails(view: View){
            replaceFragment(PersonalDetailFragment())
        }
        fun proofOfId(view: View){
            replaceFragment(ProofOfIDFragment())
            mBinding!!.horizontalScrollView.smoothScrollTo(55, 0)
        }
        fun proofOfCorrespondId(view: View){
            replaceFragment(ProofOfAddressFragment())
            mBinding!!.horizontalScrollView.smoothScrollTo(330, 0)
        }
        fun disabilityDetails(view: View){
            replaceFragment(DisabilityDetailFragment())
            mBinding!!.horizontalScrollView.smoothScrollTo(750, 0)
        }
        fun hospitalAssessments(view: View){
            replaceFragment(HospitalAssesmentFragment())
            mBinding!!.horizontalScrollView.smoothScrollTo(2050, 0)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
//        onBackPressedDispatcher.onBackPressed()
    }

    private fun replaceFragment(fragment: androidx.fragment.app.Fragment) {
        currentFragment = fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragFrame, fragment)
            .addToBackStack(null)
            .commit()
        updateImagesForCurrentFragment()
    }

    private fun updateImagesForCurrentFragment() {
        when (currentFragment) {
            is PersonalDetailFragment -> {
                mBinding?.ivPD?.setImageResource(R.drawable.va_blue_ellipse)
                mBinding?.ivPOI?.setImageResource(R.drawable.va_orange_ellipse)
                mBinding?.ivPOCA?.setImageResource(R.drawable.va_ellipse_orange_down)
                mBinding?.ivDD?.setImageResource(R.drawable.va_orange_ellipse)
                mBinding?.ivHA?.setImageResource(R.drawable.va_ellipse_orange_down)
                mBinding?.tvPD?.setTextColor(ContextCompat.getColor(this,R.color.DarkBlue))
                mBinding?.tvPOI?.setTextColor(ContextCompat.getColor(this,R.color.orange))
                mBinding?.tvPOCA?.setTextColor(ContextCompat.getColor(this,R.color.orange))
                mBinding?.tvDD?.setTextColor(ContextCompat.getColor(this,R.color.orange))
                mBinding?.tvHA?.setTextColor(ContextCompat.getColor(this,R.color.orange))
            }
            is ProofOfIDFragment -> {
                mBinding?.ivPD?.setImageResource(R.drawable.va_ellipse_orange_down)
                mBinding?.ivPOI?.setImageResource(R.drawable.va_blue_ellipse_up)
                mBinding?.ivPOCA?.setImageResource(R.drawable.va_ellipse_orange_down)
                mBinding?.ivDD?.setImageResource(R.drawable.va_orange_ellipse)
                mBinding?.ivHA?.setImageResource(R.drawable.va_ellipse_orange_down)
                mBinding?.tvPD?.setTextColor(ContextCompat.getColor(this,R.color.orange))
                mBinding?.tvPOI?.setTextColor(ContextCompat.getColor(this,R.color.DarkBlue))
                mBinding?.tvPOCA?.setTextColor(ContextCompat.getColor(this,R.color.orange))
                mBinding?.tvDD?.setTextColor(ContextCompat.getColor(this,R.color.orange))
                mBinding?.tvHA?.setTextColor(ContextCompat.getColor(this,R.color.orange))
            }
            is ProofOfAddressFragment -> {
                mBinding?.ivPD?.setImageResource(R.drawable.va_ellipse_orange_down)
                mBinding?.ivPOI?.setImageResource(R.drawable.va_orange_ellipse)
                mBinding?.ivPOCA?.setImageResource(R.drawable.va_blue_ellipse)
                mBinding?.ivDD?.setImageResource(R.drawable.va_orange_ellipse)
                mBinding?.ivHA?.setImageResource(R.drawable.va_ellipse_orange_down)
                mBinding?.tvPD?.setTextColor(ContextCompat.getColor(this,R.color.orange))
                mBinding?.tvPOI?.setTextColor(ContextCompat.getColor(this,R.color.orange))
                mBinding?.tvPOCA?.setTextColor(ContextCompat.getColor(this,R.color.DarkBlue))
                mBinding?.tvDD?.setTextColor(ContextCompat.getColor(this,R.color.orange))
                mBinding?.tvHA?.setTextColor(ContextCompat.getColor(this,R.color.orange))
            }
            is DisabilityDetailFragment -> {
                mBinding?.ivPD?.setImageResource(R.drawable.va_ellipse_orange_down)
                mBinding?.ivPOI?.setImageResource(R.drawable.va_orange_ellipse)
                mBinding?.ivPOCA?.setImageResource(R.drawable.va_ellipse_orange_down)
                mBinding?.ivDD?.setImageResource(R.drawable.va_blue_ellipse_up)
                mBinding?.ivHA?.setImageResource(R.drawable.va_ellipse_orange_down)
                mBinding?.tvPD?.setTextColor(ContextCompat.getColor(this,R.color.orange))
                mBinding?.tvPOI?.setTextColor(ContextCompat.getColor(this,R.color.orange))
                mBinding?.tvPOCA?.setTextColor(ContextCompat.getColor(this,R.color.orange))
                mBinding?.tvDD?.setTextColor(ContextCompat.getColor(this,R.color.DarkBlue))
                mBinding?.tvHA?.setTextColor(ContextCompat.getColor(this,R.color.orange))
            }
            is HospitalAssesmentFragment -> {
                mBinding?.ivPD?.setImageResource(R.drawable.va_ellipse_orange_down)
                mBinding?.ivPOI?.setImageResource(R.drawable.va_orange_ellipse)
                mBinding?.ivPOCA?.setImageResource(R.drawable.va_ellipse_orange_down)
                mBinding?.ivDD?.setImageResource(R.drawable.va_orange_ellipse)
                mBinding?.ivHA?.setImageResource(R.drawable.va_blue_ellipse)
                mBinding?.tvPD?.setTextColor(ContextCompat.getColor(this,R.color.orange))
                mBinding?.tvPOI?.setTextColor(ContextCompat.getColor(this,R.color.orange))
                mBinding?.tvPOCA?.setTextColor(ContextCompat.getColor(this,R.color.orange))
                mBinding?.tvDD?.setTextColor(ContextCompat.getColor(this,R.color.orange))
                mBinding?.tvHA?.setTextColor(ContextCompat.getColor(this,R.color.DarkBlue))
            }
            else -> {
                // Handle other fragments if needed
            }
        }
    }
}

package com.udid.ui.fragments

import com.udid.R
import com.udid.databinding.FragmentOnboardingOneBinding
import com.udid.model.OnBoardingImageData
import com.udid.utilities.BaseFragment

class OnboardingOneFragment : BaseFragment<FragmentOnboardingOneBinding>() {

    private var mBinding: FragmentOnboardingOneBinding? = null

    override val layoutId: Int
        get() = R.layout.fragment_onboarding_one

    override fun init() {
        val imageData = OnBoardingImageData(
            R.drawable.on_boarding_one, // Replace with your image resource ID
            getString(R.string.world_disability_day),
            getString(R.string.modi_coined_the_word_divyang)
        )

        // Set data to views using binding
        mBinding?.imageView?.setImageResource(imageData.imageResId)
        mBinding?.titleTextView?.text = imageData.title
        mBinding?.descriptionTextView?.text = imageData.description
    }

    override fun setVariables() {
    }

    override fun setObservers() {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }
}

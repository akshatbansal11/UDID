package com.udid.ui.fragments

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.RotateDrawable
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.udid.R
import com.udid.databinding.FragmentDisabilityDetailsBinding
import com.udid.databinding.FragmentHospitalAssesmentBinding
import com.udid.model.ResultGetDropDown
import com.udid.ui.adapter.BottomSheetAdapter
import com.udid.utilities.BaseFragment
import com.udid.utilities.hideView
import com.udid.utilities.showView
import com.udid.viewModel.SharedDataViewModel


class DisabilityDetailFragment : BaseFragment<FragmentDisabilityDetailsBinding>() {
    private lateinit var sharedViewModel: SharedDataViewModel
    private var mBinding: FragmentDisabilityDetailsBinding? = null
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var stateAdapter: BottomSheetAdapter
    private var layoutManager: LinearLayoutManager? = null
    private var districtId: Int? = null // Store selected state

    private val guardian = listOf(
        ResultGetDropDown(id = 1, name = "Father"),
        ResultGetDropDown(id = 2, name = "Mother"),
        ResultGetDropDown(id = 3, name = "Guardian")
    )
    private var gender: Int = 0
    override val layoutId: Int
        get() = R.layout.fragment_disability_details

    override fun init() {
        mBinding=viewDataBinding
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedDataViewModel::class.java)
        sharedViewModel.userData.observe(viewLifecycleOwner) { userData ->
            mBinding?.tvDisabilityType?.setText(userData.disabilityType)
            if (!userData.disabilityType.isNullOrEmpty()) {
                mBinding?.tvDisabilityType?.setTextColor(Color.parseColor("#000000"))
            }
            mBinding?.tvDueTo?.setText(userData.disabilityDue)
            if (!userData.disabilityDue.isNullOrEmpty()) {
                mBinding?.tvDueTo?.setTextColor(Color.parseColor("#000000"))
            }
            if (userData.disabilityBirth == 1) {
                mBinding?.rbYes?.isChecked = true
                mBinding?.tvApplicantMotherName?.hideView()
                mBinding?.tvDisabilitySince?.hideView()
            } else if (userData.disabilityBirth == 2) {
                mBinding?.rbNo?.isChecked = true
                mBinding?.tvApplicantMotherName?.showView()
                mBinding?.tvDisabilitySince?.showView()
            } else {
                mBinding?.rbYes?.isChecked = false
                mBinding?.rbNo?.isChecked = false
                mBinding?.tvApplicantMotherName?.hideView()
                mBinding?.tvDisabilitySince?.hideView()
            }
            mBinding?.tvDisabilitySince?.setText(userData.disabilitySince)
            if (!userData.disabilitySince.isNullOrEmpty()) {
                mBinding?.tvDisabilitySince?.setTextColor(Color.parseColor("#000000"))
            }
        }
        if (mBinding?.rbNo?.isChecked == true) {
            mBinding?.tvApplicantMotherName?.showView()
            mBinding?.tvDisabilitySince?.showView()
        } else {
            mBinding?.tvApplicantMotherName?.hideView()
            mBinding?.tvDisabilitySince?.hideView()
        }
        mBinding?.rgBirth?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbYes -> {
                    mBinding?.tvApplicantMotherName?.hideView()
                    mBinding?.tvDisabilitySince?.hideView()
                    gender = 1
                }
                R.id.rbNo -> {
                    mBinding?.tvApplicantMotherName?.showView()
                    mBinding?.tvDisabilitySince?.showView()
                    gender = 2
                }

                else -> {
                    gender = 0
                }
            }
            sharedViewModel.userData.value?.disabilityBirth = gender
        }
        mBinding?.tvDisabilityType?.addTextChangedListener {
            sharedViewModel.userData.value?.disabilityType = it.toString()
        }
        mBinding?.tvDueTo?.addTextChangedListener {
            sharedViewModel.userData.value?.disabilityDue = it.toString()
        }
        mBinding?.tvDisabilitySince?.addTextChangedListener {
            sharedViewModel.userData.value?.disabilitySince = it.toString()
        }
        mBinding?.tvDisabilityType?.setOnClickListener {
            showBottomSheetDialog("Type")
        }
        mBinding?.tvDueTo?.setOnClickListener {
            showBottomSheetDialog("Due")
        }
        mBinding?.tvDisabilitySince?.setOnClickListener {
            showBottomSheetDialog("Since")
        }
    }
    private fun showBottomSheetDialog(type: String) {
        bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_state, null)
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val rvBottomSheet = view.findViewById<RecyclerView>(R.id.rvBottomSheet)
        val close = view.findViewById<TextView>(R.id.tvClose)

        close.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        // Define a variable for the selected list and TextView
        val selectedList: List<ResultGetDropDown>
        val selectedTextView: TextView
        // Initialize based on type
        when (type) {
            "Type" -> {
                selectedList = guardian
                selectedTextView = mBinding!!.tvDisabilityType
            }
            "Due" -> {
                selectedList = guardian
                selectedTextView = mBinding!!.tvDueTo
            }
            "Since" -> {
                selectedList = guardian
                selectedTextView = mBinding!!.tvDisabilitySince
            }


            else -> return
        }

        // Set up the adapter
        stateAdapter = BottomSheetAdapter(requireContext(), selectedList) { selectedItem, id ->
            // Handle state item click
            selectedTextView.text = selectedItem
            if (id != -1) {
                districtId = id
            }
            selectedTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            bottomSheetDialog.dismiss()
        }



        layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rvBottomSheet.layoutManager = layoutManager
        rvBottomSheet.adapter = stateAdapter
        bottomSheetDialog.setContentView(view)


        // Rotate drawable
        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_down)
        var rotatedDrawable = rotateDrawable(drawable, 180f)
        selectedTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, rotatedDrawable, null)

        // Set a dismiss listener to reset the view visibility
        bottomSheetDialog.setOnDismissListener {
            rotatedDrawable = rotateDrawable(drawable, 0f)
            selectedTextView.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                rotatedDrawable,
                null
            )
        }

        // Show the bottom sheet
        bottomSheetDialog.show()
    }

    private fun rotateDrawable(drawable: Drawable?, angle: Float): Drawable? {
        drawable?.mutate() // Mutate the drawable to avoid affecting other instances

        val rotateDrawable = RotateDrawable()
        rotateDrawable.drawable = drawable
        rotateDrawable.fromDegrees = 0f
        rotateDrawable.toDegrees = angle
        rotateDrawable.level = 10000 // Needed to apply the rotation

        return rotateDrawable
    }
    override fun setVariables() {
    }

    override fun setObservers() {
    }

}
package com.udid.ui.fragments

import android.content.Intent
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
import com.google.gson.Gson
import com.udid.R
import com.udid.databinding.FragmentHospitalAssesmentBinding
import com.udid.model.DropDownResult
import com.udid.ui.activity.UserDataActivity
import com.udid.ui.adapter.BottomSheetAdapter
import com.udid.utilities.BaseFragment
import com.udid.utilities.hideView
import com.udid.utilities.showView
import com.udid.viewModel.SharedDataViewModel


class HospitalAssesmentFragment : BaseFragment<FragmentHospitalAssesmentBinding>() {
    private lateinit var sharedViewModel: SharedDataViewModel
    private var mBinding: FragmentHospitalAssesmentBinding? = null
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var stateAdapter: BottomSheetAdapter
    private var layoutManager: LinearLayoutManager? = null
    private var districtId: Int? = null // Store selected state

    private val guardian = listOf(
        DropDownResult(id = 1, name = "Father"),
        DropDownResult(id = 2, name = "Mother"),
        DropDownResult(id = 3, name = "Guardian")
    )
    private var gender: Int = 0
    override val layoutId: Int
        get() = R.layout.fragment_hospital_assesment

    override fun init() {
        mBinding = viewDataBinding



        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedDataViewModel::class.java)
        sharedViewModel.userData.observe(viewLifecycleOwner) { userData ->
            if (userData.treatingHospital == 1) {
                mBinding?.rbYes?.isChecked = true
                mBinding?.tvTitleHospitalState?.showView()
                mBinding?.tvHospitalState?.showView()
                mBinding?.tvTitleHospitalDistrict?.showView()
                mBinding?.tvHospitalDistrict?.showView()
            } else {
                mBinding?.tvTitleHospitalState?.hideView()
                mBinding?.tvHospitalState?.hideView()
                mBinding?.tvTitleHospitalDistrict?.hideView()
                mBinding?.tvHospitalDistrict?.hideView()
            }
            mBinding?.tvHospitalState?.setText(userData.hospitalState)
            if (!userData.hospitalState.isNullOrEmpty()) {
                mBinding?.tvHospitalState?.setTextColor(Color.parseColor("#000000"))
            }
            mBinding?.tvHospitalDistrict?.setText(userData.hospitalDistrict)
            if (!userData.hospitalDistrict.isNullOrEmpty()) {
                mBinding?.tvHospitalDistrict?.setTextColor(Color.parseColor("#000000"))
            }
            mBinding?.tvHospitalName?.setText(userData.hospitalName)
            if (!userData.hospitalName.isNullOrEmpty()) {
                mBinding?.tvHospitalName?.setTextColor(Color.parseColor("#000000"))
            }
            mBinding?.checkboxConfirm?.isChecked = userData.hospitalCheckBox == 1

        }

        mBinding?.checkboxConfirm?.setOnCheckedChangeListener { group, checkedId ->
            sharedViewModel.userData.value?.hospitalCheckBox = if (checkedId) 1 else 0
        }


        if (mBinding?.rbNo?.isChecked == true) {
            mBinding?.tvTitleHospitalState?.hideView()
            mBinding?.tvHospitalState?.hideView()
            mBinding?.tvTitleHospitalDistrict?.hideView()
            mBinding?.tvHospitalDistrict?.hideView()
        } else {
            mBinding?.tvTitleHospitalState?.showView()
            mBinding?.tvHospitalState?.showView()
            mBinding?.tvTitleHospitalDistrict?.showView()
            mBinding?.tvHospitalDistrict?.showView()
        }
        mBinding?.rgTreatingState?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbNo -> {
                    mBinding?.tvTitleHospitalState?.hideView()
                    mBinding?.tvHospitalState?.hideView()
                    mBinding?.tvTitleHospitalDistrict?.hideView()
                    mBinding?.tvHospitalDistrict?.hideView()
                    gender = 2
                }

                R.id.rbYes -> {
                    mBinding?.tvTitleHospitalState?.showView()
                    mBinding?.tvHospitalState?.showView()
                    mBinding?.tvTitleHospitalDistrict?.showView()
                    mBinding?.tvHospitalDistrict?.showView()
                    gender = 1
                }

                else -> {
                    gender = 2
                }
            }
            sharedViewModel.userData.value?.treatingHospital = gender
        }
        mBinding?.tvHospitalState?.addTextChangedListener {
            sharedViewModel.userData.value?.hospitalState = it.toString()
        }
        mBinding?.tvHospitalDistrict?.addTextChangedListener {
            sharedViewModel.userData.value?.hospitalDistrict = it.toString()
        }
        mBinding?.tvHospitalName?.addTextChangedListener {
            sharedViewModel.userData.value?.hospitalName = it.toString()
        }
        mBinding?.tvHospitalState?.setOnClickListener {
            showBottomSheetDialog("tvHospitalState")
        }
        mBinding?.tvHospitalDistrict?.setOnClickListener {
            showBottomSheetDialog("tvHospitalDistrict")
        }
        mBinding?.tvHospitalName?.setOnClickListener {
            showBottomSheetDialog("tvHospitalName")
        }

        mBinding?.tvSendOtp?.setOnClickListener {
            val userData = sharedViewModel.userData.value
            if (userData != null) {
                val jsonData = Gson().toJson(userData) // Serialize to JSON
                val intent = Intent(requireContext(), UserDataActivity::class.java)
                intent.putExtra("jsonData", jsonData) // Pass JSON as an extra
                startActivity(intent)
            }
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
        val selectedList: List<DropDownResult>
        val selectedTextView: TextView
        // Initialize based on type
        when (type) {
            "tvHospitalState" -> {
                selectedList = guardian
                selectedTextView = mBinding!!.tvHospitalState
            }

            "tvHospitalDistrict" -> {
                selectedList = guardian
                selectedTextView = mBinding!!.tvHospitalDistrict
            }

            "tvHospitalName" -> {
                selectedList = guardian
                selectedTextView = mBinding!!.tvHospitalName
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
package com.udid.ui.fragments

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.RotateDrawable
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.udid.R
import com.udid.databinding.FragmentPersonalDetailsBinding
import com.udid.databinding.FragmentProofOfIDBinding
import com.udid.model.DropDownResult
import com.udid.ui.adapter.BottomSheetAdapter
import com.udid.utilities.BaseFragment
import com.udid.utilities.hideView
import com.udid.utilities.showView
import com.udid.viewModel.SharedDataViewModel


class ProofOfIDFragment : BaseFragment<FragmentProofOfIDBinding>() {
    private lateinit var sharedViewModel: SharedDataViewModel
    private var mBinding: FragmentProofOfIDBinding? = null
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var stateAdapter: BottomSheetAdapter
    private var layoutManager: LinearLayoutManager? = null
    private var districtId: String? = null // Store selected state

    private val guardian = listOf(
        DropDownResult(id = "1", name = "Father"),
        DropDownResult(id = "2", name = "Mother"),
        DropDownResult(id = "3", name = "Guardian")
    )
    private var gender: Int = 0
    override val layoutId: Int
        get() = R.layout.fragment_proof_of_i_d


    override fun init() {
        mBinding = viewDataBinding

        if (mBinding?.rbYes?.isChecked == true) {
            mBinding?.tvAadhaar?.showView()
            mBinding?.etAadhaarNo?.showView()
            mBinding?.checkboxConfirm?.showView()
            mBinding?.tvAadhaarNo?.hideView()
            mBinding?.etAadhaarEnrollment?.hideView()
            mBinding?.tvUploadAadhaarEnrollment?.hideView()
            mBinding?.llApplicantPhotoAadhaar?.hideView()
            mBinding?.tvSlipIvSize?.hideView()
        } else if (mBinding?.rbNo?.isChecked == true) {
            mBinding?.tvAadhaar?.hideView()
            mBinding?.etAadhaarNo?.hideView()
            mBinding?.checkboxConfirm?.hideView()
            mBinding?.tvAadhaarNo?.showView()
            mBinding?.etAadhaarEnrollment?.showView()
            mBinding?.tvUploadAadhaarEnrollment?.showView()
            mBinding?.llApplicantPhotoAadhaar?.showView()
            mBinding?.tvSlipIvSize?.showView()
        } else {
            mBinding?.tvAadhaar?.hideView()
            mBinding?.etAadhaarNo?.hideView()
            mBinding?.checkboxConfirm?.hideView()
            mBinding?.tvAadhaarNo?.hideView()
            mBinding?.etAadhaarEnrollment?.hideView()
            mBinding?.tvUploadAadhaarEnrollment?.hideView()
            mBinding?.llApplicantPhotoAadhaar?.hideView()
            mBinding?.tvSlipIvSize?.hideView()
        }


        mBinding?.rgAadhaar?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbYes -> {
                    mBinding?.tvAadhaar?.showView()
                    mBinding?.etAadhaarNo?.showView()
                    mBinding?.checkboxConfirm?.showView()
                    mBinding?.tvAadhaarNo?.hideView()
                    mBinding?.etAadhaarEnrollment?.hideView()
                    mBinding?.tvUploadAadhaarEnrollment?.hideView()
                    mBinding?.llApplicantPhotoAadhaar?.hideView()
                    mBinding?.tvSlipIvSize?.hideView()
                    gender = 1
                }

                R.id.rbNo -> {
                    mBinding?.tvAadhaar?.hideView()
                    mBinding?.etAadhaarNo?.hideView()
                    mBinding?.checkboxConfirm?.hideView()
                    mBinding?.tvAadhaarNo?.showView()
                    mBinding?.etAadhaarEnrollment?.showView()
                    mBinding?.tvUploadAadhaarEnrollment?.showView()
                    mBinding?.llApplicantPhotoAadhaar?.showView()
                    mBinding?.tvSlipIvSize?.showView()
                    gender = 2
                }

                else -> {
                    gender = 0
                }
            }
            sharedViewModel.userData.value?.aadhaarCard = gender

        }




        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedDataViewModel::class.java)
        sharedViewModel.userData.observe(viewLifecycleOwner) { userData ->
            if (userData.aadhaarCard == 1) {
                mBinding?.rbYes?.isChecked = true
                mBinding?.tvAadhaar?.showView()
                mBinding?.etAadhaarNo?.showView()
                mBinding?.checkboxConfirm?.showView()
                mBinding?.tvAadhaarNo?.hideView()
                mBinding?.etAadhaarEnrollment?.hideView()
                mBinding?.tvUploadAadhaarEnrollment?.hideView()
                mBinding?.llApplicantPhotoAadhaar?.hideView()
                mBinding?.tvSlipIvSize?.hideView()
            } else if (userData.aadhaarCard == 2) {
                mBinding?.rbNo?.isChecked = true
                mBinding?.tvAadhaar?.hideView()
                mBinding?.etAadhaarNo?.hideView()
                mBinding?.checkboxConfirm?.hideView()
                mBinding?.tvAadhaarNo?.showView()
                mBinding?.etAadhaarEnrollment?.showView()
                mBinding?.tvUploadAadhaarEnrollment?.showView()
                mBinding?.llApplicantPhotoAadhaar?.showView()
                mBinding?.tvSlipIvSize?.showView()
            } else {
                mBinding?.rbYes?.isChecked = false
                mBinding?.rbNo?.isChecked = false
                mBinding?.tvAadhaar?.hideView()
                mBinding?.etAadhaarNo?.hideView()
                mBinding?.checkboxConfirm?.hideView()
                mBinding?.tvAadhaarNo?.hideView()
                mBinding?.etAadhaarEnrollment?.hideView()
                mBinding?.tvUploadAadhaarEnrollment?.hideView()
                mBinding?.llApplicantPhotoAadhaar?.hideView()
                mBinding?.tvSlipIvSize?.hideView()
            }
            mBinding?.etAadhaarNo?.setText(userData.aadhaarNo)
            mBinding?.etAadhaarEnrollment?.setText(userData.aadhaarEnrollment)
            mBinding?.tvIdentityProof?.setText(userData.identityProof)
            if (!userData.identityProof.isNullOrEmpty()) {
                mBinding?.tvIdentityProof?.setTextColor(Color.parseColor("#000000"))
            }
            mBinding?.checkboxConfirm?.isChecked = userData.aadhaarCheckBox == 1
        }



        mBinding?.etAadhaarNo?.addTextChangedListener {
            sharedViewModel.userData.value?.aadhaarNo = it.toString()
        }
        mBinding?.checkboxConfirm?.setOnCheckedChangeListener { group, checkedId ->
            sharedViewModel.userData.value?.aadhaarCheckBox = if (checkedId) 1 else 0
        }

        mBinding?.etAadhaarEnrollment?.addTextChangedListener {
            sharedViewModel.userData.value?.aadhaarEnrollment = it.toString()
        }
        mBinding?.tvIdentityProof?.addTextChangedListener {
            sharedViewModel.userData.value?.identityProof = it.toString()
        }

        mBinding?.tvIdentityProof?.setOnClickListener {
            showBottomSheetDialog("Guardian")
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
            "Guardian" -> {
                selectedList = guardian
                selectedTextView = mBinding!!.tvIdentityProof
            }

            else -> return
        }

        // Set up the adapter
        stateAdapter = BottomSheetAdapter(requireContext(), selectedList) { selectedItem, id ->
            // Handle state item click
            selectedTextView.text = selectedItem
                districtId = id
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
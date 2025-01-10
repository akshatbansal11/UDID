package com.udid.ui.fragments

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.RotateDrawable
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.udid.R
import com.udid.databinding.FragmentOnboardingOneBinding
import com.udid.databinding.FragmentProofOfCAddBinding
import com.udid.model.DropDownResult
import com.udid.ui.adapter.BottomSheetAdapter
import com.udid.utilities.BaseFragment
import com.udid.viewModel.SharedDataViewModel


class ProofOfAddressFragment : BaseFragment<FragmentProofOfCAddBinding>() {
    private lateinit var sharedViewModel: SharedDataViewModel
    private var mBinding: FragmentProofOfCAddBinding? = null
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var stateAdapter: BottomSheetAdapter
    private var layoutManager: LinearLayoutManager? = null
    private var districtId: Int? = null // Store selected state

    private val guardian = listOf(
        DropDownResult(id = 1, name = "Father"),
        DropDownResult(id = 2, name = "Mother"),
        DropDownResult(id = 3, name = "Guardian")
    )
    override val layoutId: Int
        get() = R.layout.fragment_proof_of_c_add

    override fun init() {
        mBinding = viewDataBinding
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedDataViewModel::class.java)
        sharedViewModel.userData.observe(viewLifecycleOwner) { userData ->
            mBinding?.tvNatureDocument?.setText(userData.documentAddressProof)
            if (!userData.documentAddressProof.isNullOrEmpty()) {
                mBinding?.tvNatureDocument?.setTextColor(Color.parseColor("#000000"))
            }
            mBinding?.etAddress?.setText(userData.documentAddress)
            mBinding?.tvState?.setText(userData.state)
            if (!userData.state.isNullOrEmpty()) {
                mBinding?.tvState?.setTextColor(Color.parseColor("#000000"))
            }
            mBinding?.tvDistrict?.setText(userData.district)
            if (!userData.district.isNullOrEmpty()) {
                mBinding?.tvDistrict?.setTextColor(Color.parseColor("#000000"))
            }
            mBinding?.tvCity?.setText(userData.city)
            if (!userData.city.isNullOrEmpty()) {
                mBinding?.tvCity?.setTextColor(Color.parseColor("#000000"))
            }
            mBinding?.tvVillage?.setText(userData.village)
            if (!userData.village.isNullOrEmpty()) {
                mBinding?.tvVillage?.setTextColor(Color.parseColor("#000000"))
            }
            mBinding?.tvPincode?.setText(userData.pincode)
            if (!userData.pincode.isNullOrEmpty()) {
                mBinding?.tvPincode?.setTextColor(Color.parseColor("#000000"))
            }
        }

        // Save data when fields change
        mBinding?.tvNatureDocument?.addTextChangedListener {
            sharedViewModel.userData.value?.documentAddressProof = it.toString()
        }

        mBinding?.etAddress?.addTextChangedListener {
            sharedViewModel.userData.value?.documentAddress = it.toString()
        }

        mBinding?.tvState?.addTextChangedListener {
            sharedViewModel.userData.value?.state = it.toString()
        }

        mBinding?.tvDistrict?.addTextChangedListener {
            sharedViewModel.userData.value?.district = it.toString()
        }
        mBinding?.tvCity?.addTextChangedListener {
            sharedViewModel.userData.value?.city = it.toString()
        }
        mBinding?.tvVillage?.addTextChangedListener {
            sharedViewModel.userData.value?.village = it.toString()
        }
        mBinding?.tvPincode?.addTextChangedListener {
            sharedViewModel.userData.value?.pincode = it.toString()
        }


        //ClickListener
        mBinding?.tvNatureDocument?.setOnClickListener {
            showBottomSheetDialog("Nature")
        }
        mBinding?.tvState?.setOnClickListener {
            showBottomSheetDialog("State")
        }
        mBinding?.tvDistrict?.setOnClickListener {
            showBottomSheetDialog("District")
        }
        mBinding?.tvCity?.setOnClickListener {
            showBottomSheetDialog("City")
        }
        mBinding?.tvVillage?.setOnClickListener {
            showBottomSheetDialog("Village")
        }
        mBinding?.tvPincode?.setOnClickListener {
            showBottomSheetDialog("Pin")
        }

//        mBinding?.tvHead?.setOnClickListener {
//            sharedViewModel.userData.value?.let { userData ->
//                // Process all data here
//                Toast.makeText(context, "Data Submitted: $userData ", Toast.LENGTH_LONG).show()
//            }
//        }

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
            "Nature" -> {
                selectedList = guardian
                selectedTextView = mBinding!!.tvNatureDocument
            }
            "State" -> {
                selectedList = guardian
                selectedTextView = mBinding!!.tvState
            }
            "District" -> {
                selectedList = guardian
                selectedTextView = mBinding!!.tvDistrict
            }
            "City" -> {
                selectedList = guardian
                selectedTextView = mBinding!!.tvCity
            }
            "Village" -> {
                selectedList = guardian
                selectedTextView = mBinding!!.tvVillage
            }
            "Pin" -> {
                selectedList = guardian
                selectedTextView = mBinding!!.tvPincode
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

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }
}
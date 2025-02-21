package com.swavlambancard.udid.ui.fragments

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.swavlambancard.udid.R
import com.swavlambancard.udid.databinding.FragmentHospitalAssesmentBinding
import com.swavlambancard.udid.model.DropDownRequest
import com.swavlambancard.udid.model.DropDownResult
import com.swavlambancard.udid.model.Fields
import com.swavlambancard.udid.model.Filters
import com.swavlambancard.udid.ui.activity.LoginActivity
import com.swavlambancard.udid.ui.adapter.BottomSheetAdapter
import com.swavlambancard.udid.utilities.BaseFragment
import com.swavlambancard.udid.utilities.Utility.rotateDrawable
import com.swavlambancard.udid.utilities.Utility.showSnackbar
import com.swavlambancard.udid.utilities.hideView
import com.swavlambancard.udid.utilities.showView
import com.swavlambancard.udid.viewModel.SharedDataViewModel
import com.swavlambancard.udid.viewModel.ViewModel
import okhttp3.MultipartBody

class HospitalAssessmentFragment : BaseFragment<FragmentHospitalAssesmentBinding>() {
    private lateinit var sharedViewModel: SharedDataViewModel
    private var viewModel = ViewModel()
    private var mBinding: FragmentHospitalAssesmentBinding? = null
    private var bottomSheetDialog: BottomSheetDialog? = null
    private var bottomSheetAdapter: BottomSheetAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    private var treatingHospitalTag: Int = 2
    var body: MultipartBody.Part? = null
    private var stateList = ArrayList<DropDownResult>()
    private var stateId: String? = null
    private var districtList = ArrayList<DropDownResult>()
    private var districtId: String? = null
    private var hospitalList = ArrayList<DropDownResult>()
    private var hospitalId: String? = null
    override val layoutId: Int
        get() = R.layout.fragment_hospital_assesment

    override fun init() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
        viewModel.init()
        sharedViewModel = ViewModelProvider(requireActivity())[SharedDataViewModel::class.java]
        sharedViewModel.userData.observe(viewLifecycleOwner) { userData ->
            when(userData.treatingHospitalTag){
                1->{
                    mBinding?.rbYes?.isChecked = true
                    mBinding?.llTreatingHospital?.showView()
                }
                2->{
                    mBinding?.rbNo?.isChecked = true
                    mBinding?.llTreatingHospital?.hideView()
                }
                else->{
                    mBinding?.rbYes?.isChecked = false
                    mBinding?.rbNo?.isChecked = false
                    mBinding?.llTreatingHospital?.hideView()
                }
            }
            districtId = if(userData.treatingHospitalTag == 1) {
                userData.hospitalDistrictId
            } else{
                userData.districtCode
            }
            mBinding?.etHospitalTreatingState?.text = userData.hospitalStateName
            stateId = userData.hospitalStateId
            mBinding?.etHospitalTreatingDistrict?.text = userData.hospitalDistrictName
            mBinding?.etHospitalName?.text = userData.hospitalNameName
            hospitalId = userData.hospitalNameId
            when(userData.hospitalCheckBox) {
                0 -> {
                    mBinding?.cbConfirm?.isChecked = false
                }

                1 -> {
                    mBinding?.cbConfirm?.isChecked = true
                }
            }
        }

        mBinding?.rgTreatingState?.setOnCheckedChangeListener { _, checkedId ->
            treatingHospitalTag = when (checkedId) {
                R.id.rbYes -> {
                    1
                }

                R.id.rbNo -> {
                    2
                }

                else -> {
                    0
                }
            }
            sharedViewModel.userData.value?.treatingHospitalTag = treatingHospitalTag
        }
        mBinding?.etHospitalTreatingState?.addTextChangedListener {
            sharedViewModel.userData.value?.hospitalStateName = it.toString()
        }
        mBinding?.etHospitalTreatingDistrict?.addTextChangedListener {
            sharedViewModel.userData.value?.hospitalDistrictName = it.toString()
        }
        mBinding?.etHospitalName?.addTextChangedListener {
            sharedViewModel.userData.value?.hospitalNameName = it.toString()
        }
        mBinding?.cbConfirm?.setOnCheckedChangeListener { _, isChecked ->
            sharedViewModel.userData.value?.hospitalCheckBox = if (isChecked) 1 else 0
        }
    }

    override fun setVariables() {
    }

    override fun setObservers() {

        viewModel.dropDownResult.observe(this) {
            val userResponseModel = it
            if (userResponseModel?._result != null && userResponseModel._result.isNotEmpty()) {
                when (userResponseModel.model) {
                    "States" -> {
                        stateList.clear()
                        stateList.add(
                            DropDownResult(
                                "0",
                                getString(R.string.please_select_hospital_treating_state_uts)
                            )
                        )
                        stateList.addAll(userResponseModel._result)
                    }

                    "Districts" -> {
                        districtList.clear()
                        districtList.add(
                            DropDownResult(
                                "0",
                                getString(R.string.choose_hospital_treating_district)
                            )
                        )
                        districtList.addAll(userResponseModel._result)
                    }

                    "Hospitals" -> {
                        hospitalList.clear()
                        hospitalList.add(
                            DropDownResult(
                                "0",
                                getString(R.string.select_hospital_name)
                            )
                        )
                        hospitalList.addAll(userResponseModel._result)
                    }
                }
                bottomSheetAdapter?.notifyDataSetChanged()
            }
        }

        viewModel.errors.observe(this) {
            mBinding?.let { it1 -> showSnackbar(it1.llParent, it) }
        }
        sharedViewModel.errors.observe(this) {
            mBinding?.let { it1 -> showSnackbar(it1.llParent, it) }
        }
    }

    inner class ClickActions {
        fun submit(view: View) {
//            if (valid()) {
                val dialog = ThankYouDialog("12345678900987")
                dialog.show((context as AppCompatActivity).supportFragmentManager, "ThankYouDialog")
//            }
        }

        fun cancel(view: View) {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }

        fun yes(view: View) {
            mBinding?.llTreatingHospital?.showView()
        }

        fun no(view: View) {
            mBinding?.llTreatingHospital?.hideView()
        }

        fun hospitalTreatingState(view: View) {
            showBottomSheetDialog("state")
        }

        fun hospitalTreatingDistrict(view: View) {
            if (mBinding?.etHospitalTreatingState?.text.toString().trim().isNotEmpty()) {
                showBottomSheetDialog("district")
            } else {
                mBinding?.llParent?.let {
                    showSnackbar(
                        it,
                        getString(R.string.please_select_state_first)
                    )
                }
            }
        }

        fun hospitalName(view: View) {
            if(!districtId.isNullOrEmpty()) {
                showBottomSheetDialog("hospitalName")
            }
            else{
                mBinding?.llParent?.let {
                    showSnackbar(
                        it,
                        getString(R.string.please_select_district_first)
                    )
                }
            }
        }
    }

    private fun stateListApi() {
        viewModel.getDropDown(
            requireContext(), DropDownRequest(
                model = "States",
                fields = Fields(state_code = "name"),
                type = "mobile"
            )
        )
    }

    private fun districtListApi() {
        viewModel.getDropDown(
            requireContext(), DropDownRequest(
                model = "Districts",
                filters = Filters(state_code = stateId),
                fields = Fields(district_code = "district_name"),
                type = "mobile"
            )
        )
    }

    private fun hospitalListApi() {
        viewModel.getDropDown(
            requireContext(), DropDownRequest(
                model = "Hospitals",
                filters = Filters(district_code = districtId),
                fields = Fields(id = "hospital_name"),
                type = "mobile"
            )
        )
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
            bottomSheetDialog?.dismiss()
        }

        val selectedList: List<DropDownResult>
        val selectedTextView: TextView?
        when (type) {
            "state" -> {
                if (stateList.isEmpty()) {
                    stateListApi()
                }
                selectedList = stateList
                selectedTextView = mBinding?.etHospitalTreatingState
            }

            "district" -> {
//                if (districtList.isEmpty()) {
                    districtListApi()
//                }
                selectedList = districtList
                selectedTextView = mBinding?.etHospitalTreatingDistrict
            }

            "hospitalName" -> {
//                if (hospitalList.isEmpty()) {
                    hospitalListApi()
//                }
                selectedList = hospitalList
                selectedTextView = mBinding?.etHospitalName
            }

            else -> return
        }

        bottomSheetAdapter =
            BottomSheetAdapter(requireContext(), selectedList) { selectedItem, id ->
                selectedTextView?.text = selectedItem
                when (type) {
                    "state" -> {
                        if (selectedItem == "Select Hospital Treating State / UTs") {
                            selectedTextView?.text = ""
                            mBinding?.etHospitalTreatingDistrict?.text = ""
                            districtList.clear()
                        } else {
                            stateId = id
                            sharedViewModel.userData.value?.hospitalStateId = id
                        }
                    }

                    "district" -> {
                        if (selectedItem == "Choose Hospital Treating District") {
                            selectedTextView?.text = ""
                            mBinding?.etHospitalName?.text = ""
                            hospitalList.clear()
                        } else {
                            districtId = id
                            sharedViewModel.userData.value?.hospitalDistrictId = id
                        }
                    }

                    "hospitalName" -> {
                        if (selectedItem == "Select Hospital Name") {
                            selectedTextView?.text = ""
                        } else {
                            hospitalId = id
                            sharedViewModel.userData.value?.hospitalNameId = id
                        }
                    }
                }
                selectedTextView?.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
                bottomSheetDialog?.dismiss()
            }

        layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rvBottomSheet.layoutManager = layoutManager
        rvBottomSheet.adapter = bottomSheetAdapter
        bottomSheetDialog?.setContentView(view)


        // Rotate drawable
        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_down)
        var rotatedDrawable = rotateDrawable(drawable, 180f)
        selectedTextView?.setCompoundDrawablesWithIntrinsicBounds(null, null, rotatedDrawable, null)

        // Set a dismiss listener to reset the view visibility
        bottomSheetDialog?.setOnDismissListener {
            rotatedDrawable = rotateDrawable(drawable, 0f)
            selectedTextView?.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                rotatedDrawable,
                null
            )
        }

        // Show the bottom sheet
        bottomSheetDialog?.show()
    }

    private fun valid(): Boolean {
        if (treatingHospitalTag == 1) {
            if (mBinding?.etHospitalTreatingState?.text.toString().trim().isEmpty()) {
                mBinding?.llParent?.let {
                    showSnackbar(
                        it,
                        getString(R.string.please_select_state_uts)
                    )
                }
                return false
            } else if (mBinding?.etHospitalTreatingDistrict?.text.toString().trim().isEmpty()) {
                mBinding?.llParent?.let {
                    showSnackbar(
                        it,
                        getString(R.string.please_select_district)
                    )
                }
                return false
            }
        } else if (mBinding?.etHospitalName?.text.toString().trim().isEmpty()) {
            mBinding?.llParent?.let {
                showSnackbar(
                    it,
                    getString(R.string.select_hospital_name)
                )
            }
            return false
        } else if (mBinding?.cbConfirm?.isChecked == true) {
            mBinding?.llParent?.let {
                showSnackbar(
                    it,
                    getString(R.string.you_must_check_the_box_to_confirm_that_you_have_read_and_understood_the_process)
                )
            }
            return false
        }
        return true
    }
}
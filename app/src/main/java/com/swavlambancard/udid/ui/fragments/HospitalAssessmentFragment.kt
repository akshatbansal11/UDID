package com.swavlambancard.udid.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.RotateDrawable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.GsonBuilder
import com.swavlambancard.udid.R
import com.swavlambancard.udid.databinding.FragmentHospitalAssesmentBinding
import com.swavlambancard.udid.model.DropDownRequest
import com.swavlambancard.udid.model.DropDownResult
import com.swavlambancard.udid.model.Fields
import com.swavlambancard.udid.model.Filters
import com.swavlambancard.udid.model.PwdApplication
import com.swavlambancard.udid.ui.activity.LoginActivity
import com.swavlambancard.udid.ui.activity.UserDataActivity
import com.swavlambancard.udid.ui.adapter.BottomSheetAdapter
import com.swavlambancard.udid.utilities.BaseFragment
import com.swavlambancard.udid.utilities.Utility.rotateDrawable
import com.swavlambancard.udid.utilities.Utility.showSnackbar
import com.swavlambancard.udid.utilities.hideView
import com.swavlambancard.udid.utilities.showView
import com.swavlambancard.udid.utilities.toast
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
    private var treatingHospitalTag: Int = 0
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
//      sharedViewModel.userData.observe(viewLifecycleOwner) { userData ->
//            if (userData.treatingHospital == 1) {
//                mBinding?.rbYes?.isChecked = true
//                mBinding?.tvTitleHospitalState?.showView()
//                mBinding?.tvHospitalState?.showView()
//                mBinding?.tvTitleHospitalDistrict?.showView()
//                mBinding?.tvHospitalDistrict?.showView()
//            } else {
//                mBinding?.tvTitleHospitalState?.hideView()
//                mBinding?.tvHospitalState?.hideView()
//                mBinding?.tvTitleHospitalDistrict?.hideView()
//                mBinding?.tvHospitalDistrict?.hideView()
//            }
//            mBinding?.tvHospitalState?.setText(userData.hospitalState)
//            if (!userData.hospitalState.isNullOrEmpty()) {
//                mBinding?.tvHospitalState?.setTextColor(Color.parseColor("#000000"))
//            }
//            mBinding?.tvHospitalDistrict?.setText(userData.hospitalDistrict)
//            if (!userData.hospitalDistrict.isNullOrEmpty()) {
//                mBinding?.tvHospitalDistrict?.setTextColor(Color.parseColor("#000000"))
//            }
//            mBinding?.tvHospitalName?.setText(userData.hospitalName)
//            if (!userData.hospitalName.isNullOrEmpty()) {
//                mBinding?.tvHospitalName?.setTextColor(Color.parseColor("#000000"))
//            }
//            mBinding?.checkboxConfirm?.isChecked = userData.hospitalCheckBox == 1
//
//        }
//
//        mBinding?.checkboxConfirm?.setOnCheckedChangeListener { group, checkedId ->
//            sharedViewModel.userData.value?.hospitalCheckBox = if (checkedId) 1 else 0
//        }
//
//
//        if (mBinding?.rbNo?.isChecked == true) {
//            mBinding?.tvTitleHospitalState?.hideView()
//            mBinding?.tvHospitalState?.hideView()
//            mBinding?.tvTitleHospitalDistrict?.hideView()
//            mBinding?.tvHospitalDistrict?.hideView()
//        } else {
//            mBinding?.tvTitleHospitalState?.showView()
//            mBinding?.tvHospitalState?.showView()
//            mBinding?.tvTitleHospitalDistrict?.showView()
//            mBinding?.tvHospitalDistrict?.showView()
//        }
//        mBinding?.rgTreatingState?.setOnCheckedChangeListener { group, checkedId ->
//            when (checkedId) {
//                R.id.rbNo -> {
//                    mBinding?.tvTitleHospitalState?.hideView()
//                    mBinding?.tvHospitalState?.hideView()
//                    mBinding?.tvTitleHospitalDistrict?.hideView()
//                    mBinding?.tvHospitalDistrict?.hideView()
//                    gender = 2
//                }
//
//                R.id.rbYes -> {
//                    mBinding?.tvTitleHospitalState?.showView()
//                    mBinding?.tvHospitalState?.showView()
//                    mBinding?.tvTitleHospitalDistrict?.showView()
//                    mBinding?.tvHospitalDistrict?.showView()
//                    gender = 1
//                }
//
//                else -> {
//                    gender = 2
//                }
//            }
//            sharedViewModel.userData.value?.treatingHospital = gender
//        }
//        mBinding?.tvHospitalState?.addTextChangedListener {
//            sharedViewModel.userData.value?.hospitalState = it.toString()
//        }
//        mBinding?.tvHospitalDistrict?.addTextChangedListener {
//            sharedViewModel.userData.value?.hospitalDistrict = it.toString()
//        }
//        mBinding?.tvHospitalName?.addTextChangedListener {
//            sharedViewModel.userData.value?.hospitalName = it.toString()
//        }
//        mBinding?.tvHospitalState?.setOnClickListener {
//            showBottomSheetDialog("tvHospitalState")
//        }
//        mBinding?.tvHospitalDistrict?.setOnClickListener {
//            showBottomSheetDialog("tvHospitalDistrict")
//        }
//        mBinding?.tvHospitalName?.setOnClickListener {
//            showBottomSheetDialog("tvHospitalName")
//        }
//
//        mBinding?.tvSendOtp?.setOnClickListener {
//            // Fetch the current data from the ViewModel or form
//            val userData = sharedViewModel.userData.value ?: PwdApplication()
//
//            // Call the validate method
//            val errors = userData.validate()
//
//            if (errors.isEmpty()) {
//                // If validation passes, proceed to send the OTP
//                sendOtp(userData)
//            } else {
//                // Show validation errors to the user
//                val errorMessage = errors.joinToString("\n") // Combine all errors into one string
//                showErrorDialog(errorMessage) // Custom method to show errors in a dialog/toast
//            }
//        }

    }

    override fun setVariables() {
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
        }
    }

    override fun setObservers() {
        viewModel.errors.observe(this) {
            mBinding?.let { it1 -> showSnackbar(it1.llParent, it) }
        }
        sharedViewModel.errors.observe(this) {
            mBinding?.let { it1 -> showSnackbar(it1.llParent, it) }
        }
    }
    inner class ClickActions {
        fun submit(view: View) {
            if (sharedViewModel.valid()) {
                startActivity(Intent(requireContext(),LoginActivity::class.java))
            }
        }

        fun cancel(view: View){
            startActivity(Intent(requireContext(),LoginActivity::class.java))
        }

        fun yes(view: View){
            mBinding?.llTreatingHospital?.showView()
        }
        fun no(view: View){
            mBinding?.llTreatingHospital?.hideView()
        }
        fun hospitalTreatingState(view: View){
            showBottomSheetDialog("state")
        }
        fun hospitalTreatingDistrict(view: View){
            if (mBinding?.etHospitalTreatingState?.text.toString().trim().isNotEmpty()) {
                showBottomSheetDialog("district")
            } else {
                mBinding?.llParent?.let {showSnackbar(it,
                    getString(R.string.please_select_state_first))}
            }
        }
        fun hospitalName(view: View){
            showBottomSheetDialog("hospitalName")
        }
    }

    private fun stateListApi() {
        viewModel.getDropDown(requireContext(), DropDownRequest(
            model = "States",
            fields = Fields(state_code = "name"),
            type = "mobile"
        ))
    }

    private fun districtListApi() {
        viewModel.getDropDown(requireContext(), DropDownRequest(
            model = "Districts",
            filters = Filters(state_code = stateId),
            fields = Fields(district_code = "district_name"),
            type = "mobile"
        ))
    }

    private fun hospitalListApi() {
        viewModel.getDropDown(requireContext(), DropDownRequest(
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
                if (districtList.isEmpty()) {
                    districtListApi()
                }
                selectedList = districtList
                selectedTextView = mBinding?.etHospitalTreatingDistrict
            }

            "hospitalName" -> {
                if (hospitalList.isEmpty()) {
                    hospitalListApi()
                }
                selectedList = hospitalList
                selectedTextView = mBinding?.etHospitalName
            }

            else -> return
        }

        bottomSheetAdapter = BottomSheetAdapter(requireContext(), selectedList) { selectedItem, id ->
            selectedTextView?.text = selectedItem
            when (type) {
                "state" -> {
                    if (selectedItem == "Select State Name") {
                        selectedTextView?.text = ""
                        mBinding?.etHospitalTreatingDistrict?.text = ""
                        districtList.clear()
                    } else {
                        stateId = id
                    }
                }

                "district" -> {
                    if (selectedItem == "Select District Name") {
                        selectedTextView?.text = ""
                        mBinding?.etHospitalName?.text = ""
                        hospitalList.clear()
                    } else {
                        districtId = id
                    }
                }

                "hospitalName" -> {
                    if (selectedItem == "Select Hospital Name") {
                        selectedTextView?.text = ""
                    } else {
                        hospitalId = id
                    }
                }
            }
            selectedTextView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
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

        return true
    }


    private fun sendOtp(userData: PwdApplication) {
        // Serialize and beautify JSON
        val gson = GsonBuilder().setPrettyPrinting().create()
        val prettyJsonData = gson.toJson(userData)

        // Log or print beautified JSON for debugging
        Log.d("PrettyJSON", prettyJsonData)

        // Pass the beautified JSON to another activity
        val intent = Intent(requireContext(), UserDataActivity::class.java)
        intent.putExtra("jsonData", prettyJsonData)
        startActivity(intent)
    }

    private fun showErrorDialog(message: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Validation Errors")
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }
}
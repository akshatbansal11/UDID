package com.udid.ui.fragments

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.RotateDrawable
import android.util.Log
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
import com.udid.databinding.FragmentProofOfCAddBinding
import com.udid.model.ResultGetDropDown
import com.udid.ui.adapter.BottomSheetAdapter
import com.udid.utilities.BaseFragment
import com.udid.utilities.Utility.convertDate
import com.udid.utilities.hideView
import com.udid.utilities.showView
import com.udid.viewModel.SharedDataViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class PersonalDetailFragment : BaseFragment<FragmentPersonalDetailsBinding>() {

    private lateinit var sharedViewModel: SharedDataViewModel
    private var mBinding: FragmentPersonalDetailsBinding? = null
    private var etApplicantDateOfBirth: String? = null
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var stateAdapter: BottomSheetAdapter
    private var layoutManager: LinearLayoutManager? = null
    private var districtId: Int? = null // Store selected state

    private val guardian = listOf(
        ResultGetDropDown(id = 1, name = "Father"),
        ResultGetDropDown(id = 2, name = "Mother"),
        ResultGetDropDown(id = 3, name = "Guardian")
    )
    private var gender:Int=0
    override val layoutId: Int
        get() = R.layout.fragment_personal_details


    override fun init() {
        mBinding=viewDataBinding
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedDataViewModel::class.java)
        sharedViewModel.userData.observe(viewLifecycleOwner) { userData ->
            mBinding?.etApplicantFullName?.setText(userData.applicantFullName)
            mBinding?.etApplicantMobileNo?.setText(userData.applicantMobileNo)
            mBinding?.etApplicantEmailId?.setText(userData.applicantEmail)
            mBinding?.etApplicantDateOfBirth?.text = convertDate(userData.applicantDob)
            mBinding?.tvGuardian?.setText(userData.guardian)
            if(!userData.guardian.isNullOrEmpty()){
                mBinding?.tvGuardian?.setTextColor(Color.parseColor("#000000"))
            }
            if(userData.gender==1){
                mBinding?.rbMale?.isChecked=true
            }
            else if(userData.gender==2){
                mBinding?.rbFemale?.isChecked=true
            }
            else if(userData.gender==3){
                mBinding?.rbTransgender?.isChecked=true
            }else{
                mBinding?.rbMale?.isChecked=false
                mBinding?.rbFemale?.isChecked=false
                mBinding?.rbTransgender?.isChecked=false
            }

        }

        // Save data when fields change
        mBinding?.etApplicantFullName?.addTextChangedListener {
            sharedViewModel.userData.value?.applicantFullName = it.toString()
        }

        mBinding?.etApplicantMobileNo?.addTextChangedListener {
            sharedViewModel.userData.value?.applicantMobileNo = it.toString()
        }

        mBinding?.etApplicantEmailId?.addTextChangedListener {
            sharedViewModel.userData.value?.applicantEmail = it.toString()
        }

//        mBinding?.etApplicantDateOfBirth?.addTextChangedListener {
//            sharedViewModel.userData.value?.applicantDob = etApplicantDateOfBirth.toString()
//        }

        mBinding?.tvGuardian?.addTextChangedListener {
            sharedViewModel.userData.value?.guardian = it.toString()
        }

        mBinding?.rgGender?.setOnCheckedChangeListener { group, checkedId ->
            gender =when (checkedId) {
                R.id.rbMale -> 1
                R.id.rbFemale -> 2
                R.id.rbTransgender -> 3
                else->0
            }
            sharedViewModel.userData.value?.gender =gender
        }





        mBinding?.etApplicantDateOfBirth?.setOnClickListener {
            openCalendar("etApplicantDateOfBirth", mBinding?.etApplicantDateOfBirth!!)
        }
        mBinding?.tvGuardian?.setOnClickListener {
            showBottomSheetDialog("Guardian")
        }

    }

    private fun openCalendar(type: String, selectedTextView: TextView) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val calendarInstance = Calendar.getInstance().apply {
                    set(year, month, day)
                }
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
                val formattedDate = sdf.format(calendarInstance.time)

                // Handle each case
                when (type) {
                    "etApplicantDateOfBirth" -> sharedViewModel.userData.value?.applicantDob = formattedDate
                    else -> {
                        // Optional: Handle unknown types
                        Log.w("Calendar", "Unknown type: $type")
                    }
                }

                // Set the selected date in the TextView
                selectedTextView.text = convertDate(formattedDate)
                selectedTextView.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.black)
                )
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
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
            "Guardian" -> {
                selectedList = guardian
                selectedTextView = mBinding!!.tvGuardian
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

    override fun setObservers() {}


}
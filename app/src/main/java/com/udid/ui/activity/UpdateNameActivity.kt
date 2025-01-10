package com.udid.ui.activity

import android.graphics.drawable.Drawable
import android.graphics.drawable.RotateDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.udid.R
import com.udid.databinding.ActivityUpdateNameBinding
import com.udid.model.DropDownResult
import com.udid.ui.adapter.BottomSheetAdapter
import com.udid.utilities.AppConstants
import com.udid.utilities.BaseActivity
import com.udid.utilities.Utility
import com.udid.utilities.Utility.showSnackbar
import com.udid.utilities.showView
import com.udid.viewModel.ViewModel

class UpdateNameActivity : BaseActivity<ActivityUpdateNameBinding>() {

    private var mBinding: ActivityUpdateNameBinding? = null
    private var viewModel= ViewModel()
    private var bottomSheetDialog: BottomSheetDialog ?= null
    private var bottomSheetAdapter: BottomSheetAdapter ?=  null
    private var layoutManager: LinearLayoutManager? = null
    private var reasonToUpdateNameList = ArrayList<DropDownResult>()
    private var reasonToUpdateNameId: Int? = null
    private var identityProofList = ArrayList<DropDownResult>()
    private var identityProofId: Int? = null

    override val layoutId: Int
        get() = R.layout.activity_update_name

    override fun initView() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
        viewModel.init()
    }

    inner class ClickActions {
        fun backPress(view: View){
            onBackPressedDispatcher.onBackPressed()
        }
        fun uploadFile(view: View){

        }
        fun identityProof(view: View){
            showBottomSheetDialog("identity_proof")
        }
        fun reasonToUpdateName(view: View){
            showBottomSheetDialog("reason_to_update_name")
        }
        fun generateOtp(view: View){
            if(valid())
            {
                mBinding?.llOtp?.showView()
            }
        }
        fun submit(view: View){
            onBackPressedDispatcher.onBackPressed()
        }
    }


    override fun setVariables() {
        mBinding?.tvCurrentName?.text = Utility.getPreferenceString(this,AppConstants.FULL_NAME)
    }

    override fun setObservers() {

//        viewModel.dropDownStateResult.observe(this) {
//            val userResponseModel = it
//            if (userResponseModel?.data != null && userResponseModel.data.isNotEmpty()) {
//                stateList.clear()
//                stateList.addAll(userResponseModel.data)
//                bottomSheetAdapter.notifyDataSetChanged()
//            }
//        }
//        viewModel.dropDownDistrictResult.observe(this) {
//            val userResponseModel = it
//            if (userResponseModel?.data != null && userResponseModel.data.isNotEmpty()) {
//                districtList.clear()
//                districtList.addAll(userResponseModel.data)
//                bottomSheetAdapter.notifyDataSetChanged()
//            }
//        }
    }

    private fun identityProofListApi() {
//        viewModel.getStateListApi(this)
    }
    private fun reasonToUpdateNameListApi() {
//        viewModel.getStateListApi(this)
    }

    private fun showBottomSheetDialog(type: String) {
        bottomSheetDialog = BottomSheetDialog(this)
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

        // Define a variable for the selected list and TextView
        val selectedList: List<DropDownResult>
        val selectedTextView: TextView?
        // Initialize based on type
        when (type) {
            "identity_proof" -> {
                identityProofListApi()
                selectedList = identityProofList
                selectedTextView = mBinding?.etIdentityProof
            }
            "reason_to_update_name" -> {
                reasonToUpdateNameListApi()
                selectedList = reasonToUpdateNameList
                selectedTextView = mBinding?.etReasonToUpdateName
            }

            else -> return
        }

        // Set up the adapter
        bottomSheetAdapter = BottomSheetAdapter(this, selectedList) { selectedItem, id ->
            // Handle state item click
            selectedTextView?.text = selectedItem
            when (type) {
                "identity_proof" -> {
                    identityProofId = id
                }
                "reason_to_update_name" -> {
                    reasonToUpdateNameId = id
                }
            }
            selectedTextView?.setTextColor(ContextCompat.getColor(this, R.color.black))
            bottomSheetDialog?.dismiss()
        }

        layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvBottomSheet.layoutManager = layoutManager
        rvBottomSheet.adapter = bottomSheetAdapter
        bottomSheetDialog?.setContentView(view)


        // Rotate drawable
        val drawable = ContextCompat.getDrawable(this, R.drawable.ic_arrow_down)
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

    private fun rotateDrawable(drawable: Drawable?, angle: Float): Drawable? {
        drawable?.mutate() // Mutate the drawable to avoid affecting other instances

        val rotateDrawable = RotateDrawable()
        rotateDrawable.drawable = drawable
        rotateDrawable.fromDegrees = 0f
        rotateDrawable.toDegrees = angle
        rotateDrawable.level = 10000 // Needed to apply the rotation

        return rotateDrawable
    }

    private fun valid(): Boolean {
        if (mBinding?.etCurrentName?.text.toString().isEmpty()) {
            mBinding?.clParent?.let {
                showSnackbar(it, "Please Enter the Updated Name")
            }
            return false
        } else if (mBinding?.etUpdatedNameRegionalLanguage?.text.toString().isEmpty()) {
            mBinding?.clParent?.let {
                showSnackbar(it, "Please Enter the Updated Name")

            }
            return false
        }
        else if (mBinding?.etIdentityProof?.text.toString().isEmpty()) {
            mBinding?.clParent?.let {
                showSnackbar(it, "Please Select the identity proof")

            }
            return false
        }
        else if (mBinding?.etFileName?.text.toString().isEmpty()) {
            mBinding?.clParent?.let {
                showSnackbar(it, "Please upload the document")
            }
            return false
        } else if (mBinding?.etReasonToUpdateName?.text.toString().isEmpty()) {
            mBinding?.clParent?.let {
                showSnackbar(it, "Please Enter the Reason")
            }
            return false
        }
        return true
    }
}
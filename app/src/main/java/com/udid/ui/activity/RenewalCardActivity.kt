package com.udid.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.udid.R
import com.udid.databinding.ActivityRenewalCardBinding
import com.udid.model.DropDownRequest
import com.udid.model.DropDownResult
import com.udid.model.Fields
import com.udid.model.Filters
import com.udid.model.GenerateOtpRequest
import com.udid.model.Order
import com.udid.model.UserData
import com.udid.ui.adapter.BottomSheetAdapter
import com.udid.utilities.AppConstants
import com.udid.utilities.BaseActivity
import com.udid.utilities.EncryptionModel
import com.udid.utilities.JSEncryptService
import com.udid.utilities.Preferences
import com.udid.utilities.Preferences.getPreferenceOfLogin
import com.udid.utilities.URIPathHelper
import com.udid.utilities.Utility.rotateDrawable
import com.udid.utilities.Utility.showSnackbar
import com.udid.utilities.hideView
import com.udid.utilities.showView
import com.udid.utilities.toast
import com.udid.viewModel.ViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class RenewalCardActivity : BaseActivity<ActivityRenewalCardBinding>() {

    private var mBinding: ActivityRenewalCardBinding? = null
    private var viewModel = ViewModel()
    private var bottomSheetDialog: BottomSheetDialog? = null
    private var bottomSheetAdapter: BottomSheetAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    private var stateList = ArrayList<DropDownResult>()
    private var stateId: String? = null
    private var districtList = ArrayList<DropDownResult>()
    private var districtId: String? = null
    private var subDistrictList = ArrayList<DropDownResult>()
    private var subDistrictId: String? = null
    private var hospitalList = ArrayList<DropDownResult>()
    private var hospitalId: String? = null
    private var radioTag: Int = 0
    var body: MultipartBody.Part? = null

    override val layoutId: Int
        get() = R.layout.activity_renewal_card

    override fun initView() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
        viewModel.init()
        mBinding?.rgRenewalCard?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbHome -> {
                    mBinding?.llPresent?.hideView()
                    mBinding?.llTreating?.hideView()
                    radioTag = 1
                }

                R.id.rbPresent -> {
                    mBinding?.llPresent?.showView()
                    mBinding?.llTreating?.hideView()
                    radioTag = 2
                }

                R.id.rbTreating -> {
                    mBinding?.llPresent?.hideView()
                    mBinding?.llTreating?.showView()
                    radioTag = 3
                }

                else -> {
                    radioTag = 0
                }
            }
        }

    }

    override fun setVariables() {
        mBinding?.ivProfile?.let {
            Glide.with(this)
                .load(getPreferenceOfLogin(
                    this,
                    AppConstants.LOGIN_DATA,
                    UserData::class.java
                ).photo_path)
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(it)
        }
    }

    override fun setObservers() {

        viewModel.dropDownResult.observe(this) {
            val userResponseModel = it
            if (userResponseModel?._result != null && userResponseModel._result.isNotEmpty()) {
                when (userResponseModel.model) {
                    "States" -> {
                        stateList.clear()
                        stateList.add(DropDownResult("0", getString(R.string.select_state_name)))
                        stateList.addAll(userResponseModel._result)
                    }

                    "Districts" -> {
                        districtList.clear()
                        districtList.add(DropDownResult("0", getString(R.string.select_district_name)))
                        districtList.addAll(userResponseModel._result)
                    }

                    "Subdistricts" -> {
                        subDistrictList.clear()
                        subDistrictList.add(DropDownResult("0", getString(R.string.select_sub_district_name)))
                        subDistrictList.addAll(userResponseModel._result)
                    }

                    "Hospitals" -> {
                        hospitalList.clear()
                        hospitalList.add(DropDownResult("0", getString(R.string.select_hospital_name)))
                        hospitalList.addAll(userResponseModel._result)
                    }
                }
                bottomSheetAdapter?.notifyDataSetChanged()
            }
        }

        viewModel.generateOtpLoginResult.observe(this) {
            val userResponseModel = it
            if (userResponseModel?._resultflag != 0) {
                toast(userResponseModel.message)
                mBinding?.llOtp?.showView()
                mBinding?.scrollView?.post {
                    mBinding?.scrollView?.fullScroll(View.FOCUS_DOWN)
                }
            } else {
                mBinding?.clParent?.let { it1 -> showSnackbar(it1, userResponseModel.message) }
            }
        }

        viewModel.renewCardResult.observe(this) {
            val userResponseModel = it
            if (userResponseModel?._resultflag != 0) {
                toast(userResponseModel.message)
                if (getPreferenceOfLogin(this, AppConstants.LOGIN_DATA, UserData::class.java) != null) {
                    Preferences.setPreference(this, AppConstants.LOGIN_DATA, getPreferenceOfLogin(this, AppConstants.LOGIN_DATA, UserData::class.java).copy(
                        renewalrequest = 0
                    ))
                }
                startActivity(Intent(this, UpdateRequestActivity::class.java)
                    .putExtra(AppConstants.UPDATE_REQUEST, getString(R.string.submit_renewal_card)))
            } else {
                mBinding?.clParent?.let { it1 -> showSnackbar(it1, userResponseModel.message) }
            }
        }

        viewModel.errors.observe(this) {
            mBinding?.let { it1 -> showSnackbar(it1.clParent, it) }
        }

    }

    inner class ClickActions {
        fun backPress(view: View) {
            onBackPressedDispatcher.onBackPressed()
        }

        fun uploadFile(view: View) {
            checkStoragePermission(this@RenewalCardActivity)
        }

        fun hospital(view: View) {
            if (mBinding?.etPresentDistrictName?.text.toString().trim().isNotEmpty()|| mBinding?.etTreatingDistrictName?.text.toString().trim().isNotEmpty() ) {
                showBottomSheetDialog("hospital")
            } else {
                mBinding?.clParent?.let {showSnackbar(it,
                    getString(R.string.please_select_district_first))}
            }
        }

        fun subDistrict(view: View) {
            if (mBinding?.etPresentDistrictName?.text.toString().trim().isNotEmpty()|| mBinding?.etTreatingDistrictName?.text.toString().trim().isNotEmpty() ) {
                showBottomSheetDialog("subDistrict")
            } else {
                mBinding?.clParent?.let {showSnackbar(it, getString(R.string.please_select_district_first))}
            }
        }

        fun district(view: View) {
            if (mBinding?.etPresentStateName?.text.toString().trim().isNotEmpty() || mBinding?.etTreatingStateName?.text.toString().trim().isNotEmpty()) {
                showBottomSheetDialog("district")
            } else {
                mBinding?.clParent?.let {showSnackbar(it,
                    getString(R.string.please_select_state_first))}
            }
        }

        fun state(view: View) {
            showBottomSheetDialog("state")
        }

        fun submit(view: View) {
            if (valid()) {
                if (mBinding?.etEnterOtp?.text.toString().trim().isNotEmpty()) {
                    renewCardApi()
                } else {
                    showSnackbar(mBinding?.clParent!!, getString(R.string.please_enter_the_otp))
                }
            }
        }

        fun generateOtp(view: View) {
            if (valid()) {
                generateOtpApi()
            }
        }
    }

    private fun stateListApi() {
        viewModel.getDropDown(this, DropDownRequest(
            model = "States",
            fields = Fields(state_code = "name"),
            type = "mobile"
        ))
    }

    private fun districtListApi() {
        viewModel.getDropDown(this, DropDownRequest(
            model = "Districts",
            filters = Filters(state_code = stateId),
            fields = Fields(district_code = "district_name"),
            type = "mobile"
        ))
    }

    private fun subDistrictListApi() {
        viewModel.getDropDown(this, DropDownRequest(
            model = "Subdistricts",
            filters = Filters(district_code = districtId),
            fields = Fields(subdistrict_code = "subdistrict_name"),
            order = Order(subdistrict_name = "ASC"),
            type = "mobile"
        ))
    }

    private fun hospitalListApi() {
        viewModel.getDropDown(this, DropDownRequest(
            model = "Hospitals",
            filters = Filters(district_code = districtId),
            fields = Fields(id = "hospital_name"),
            type = "mobile"
        ))
    }

    private fun generateOtpApi() {
        viewModel.getGenerateOtpLoginApi(
            this,
            GenerateOtpRequest(
                application_number = JSEncryptService.encrypt(
                    getPreferenceOfLogin(
                        this,
                        AppConstants.LOGIN_DATA,
                        UserData::class.java
                    ).application_number.toString()
                )
            )
        )
    }

    private fun renewCardApi(){
        when (radioTag) {
            1 -> {
                viewModel.getRenewCard(
                    context = this@RenewalCardActivity,
                    applicationNumber = EncryptionModel.aesEncrypt(
                        getPreferenceOfLogin(
                            this@RenewalCardActivity,
                            AppConstants.LOGIN_DATA,
                            UserData::class.java
                        ).application_number.toString()
                    )
                        .toRequestBody(MultipartBody.FORM),
                    renewalType = EncryptionModel.aesEncrypt("1").toRequestBody(MultipartBody.FORM),
                    currentAddress = null,
                    hospitalTreatingStateCode = null,
                    hospitalTreatingDistrictCode = null,
                    hospitalTreatingSubDistrictCode = null,
                    currentPincode = null,
                    hospitalTreatingId = null,
                    type = "mobile".toRequestBody(MultipartBody.FORM),
                    address_proof_file = null
                )
            }
            2 ->{
//                println(getPreferenceOfLogin(
//                    this@RenewalCardActivity,
//                    AppConstants.LOGIN_DATA,
//                    UserData::class.java
//                ).application_number.toString())
//                println(mBinding?.etPermanentAddress?.text.toString().trim())
//                println(stateId.toString())
//                println(districtId.toString())
//                println(subDistrictId.toString())
//                println(mBinding?.etPincode?.text.toString().trim())
//                println(hospitalId.toString())
                viewModel.getRenewCard(
                    context = this@RenewalCardActivity,
                    applicationNumber = EncryptionModel.aesEncrypt(
                        getPreferenceOfLogin(
                            this@RenewalCardActivity,
                            AppConstants.LOGIN_DATA,
                            UserData::class.java
                        ).application_number.toString()
                    )
                        .toRequestBody(MultipartBody.FORM),
                    renewalType = EncryptionModel.aesEncrypt("2").toRequestBody(MultipartBody.FORM),
                    currentAddress = EncryptionModel.aesEncrypt(
                        mBinding?.etPermanentAddress?.text.toString().trim()
                    ).toRequestBody(MultipartBody.FORM),
                    hospitalTreatingStateCode = EncryptionModel.aesEncrypt(
                        stateId.toString()
                    ).toRequestBody(MultipartBody.FORM),
                    hospitalTreatingDistrictCode = EncryptionModel.aesEncrypt(
                        districtId.toString()
                    ).toRequestBody(MultipartBody.FORM),
                    hospitalTreatingSubDistrictCode = EncryptionModel.aesEncrypt(
                        subDistrictId.toString()
                    ).toRequestBody(MultipartBody.FORM),
                    currentPincode = EncryptionModel.aesEncrypt(
                        mBinding?.etPincode?.text.toString().trim()
                    ).toRequestBody(MultipartBody.FORM),
                    hospitalTreatingId = EncryptionModel.aesEncrypt(
                        hospitalId.toString()
                    ).toRequestBody(MultipartBody.FORM),
                    type = "mobile".toRequestBody(MultipartBody.FORM),
                    address_proof_file = body
                )
            }
            3 ->{
                viewModel.getRenewCard(
                    context = this@RenewalCardActivity,
                    applicationNumber = EncryptionModel.aesEncrypt(
                        getPreferenceOfLogin(
                            this@RenewalCardActivity,
                            AppConstants.LOGIN_DATA,
                            UserData::class.java
                        ).application_number.toString()
                    )
                        .toRequestBody(MultipartBody.FORM),
                    renewalType = EncryptionModel.aesEncrypt("3").toRequestBody(MultipartBody.FORM),
                    currentAddress = null,
                    hospitalTreatingStateCode = EncryptionModel.aesEncrypt(
                        stateId.toString()
                    ).toRequestBody(MultipartBody.FORM),
                    hospitalTreatingDistrictCode = EncryptionModel.aesEncrypt(
                        districtId.toString()
                    ).toRequestBody(MultipartBody.FORM),
                    hospitalTreatingSubDistrictCode = null,
                    currentPincode = null,
                    hospitalTreatingId = EncryptionModel.aesEncrypt(
                        hospitalId.toString()
                    ).toRequestBody(MultipartBody.FORM),
                    type = "mobile".toRequestBody(MultipartBody.FORM),
                    address_proof_file = null
                )
            }
        }
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

        val selectedList: List<DropDownResult>
        val selectedTextView: TextView?
        when (type) {
            "state" -> {
                if (stateList.isEmpty()) {
                    stateListApi()
                }
                selectedList = stateList
                selectedTextView = mBinding?.etPresentStateName
            }

            "district" -> {
                if (districtList.isEmpty()) {
                    districtListApi()
                }
                selectedList = districtList
                selectedTextView = mBinding?.etPresentDistrictName
            }

            "subDistrict" -> {
                if (subDistrictList.isEmpty()) {
                    subDistrictListApi()
                }
                selectedList = subDistrictList
                selectedTextView = mBinding?.etSubDistrictName
            }

            "hospital" -> {
                if (hospitalList.isEmpty()) {
                    hospitalListApi()
                }
                selectedList = hospitalList
                selectedTextView = mBinding?.etPresentHospitalName
            }

            else -> return
        }

        bottomSheetAdapter = BottomSheetAdapter(this, selectedList) { selectedItem, id ->
            selectedTextView?.text = selectedItem
            when (type) {
                "state" -> {
                    mBinding?.etTreatingStateName?.text = selectedItem
                    if (selectedItem == "Select State Name") {
                        selectedTextView?.text = ""
                        mBinding?.etPresentDistrictName?.text = ""
                        mBinding?.etTreatingDistrictName?.text = ""
                        districtList.clear()
                    } else {
                        stateId = id
                    }
                }

                "district" -> {
                    mBinding?.etTreatingDistrictName?.text = selectedItem
                    if (selectedItem == "Select District Name") {
                        selectedTextView?.text = ""
                        mBinding?.etPresentHospitalName?.text = ""
                        mBinding?.etTreatingHospitalName?.text = ""
                        mBinding?.etSubDistrictName?.text = ""
                        subDistrictList.clear()
                        hospitalList.clear()
                    } else {
                        districtId = id
                    }
                }

                "subDistrict" -> {
                    if (selectedItem == "Select Sub-District Name") {
                        selectedTextView?.text = ""
                    } else {
                        subDistrictId = id
                    }
                }

                "hospital" -> {
                    mBinding?.etTreatingHospitalName?.text = selectedItem
                    if (selectedItem == "Select Hospital Name") {
                        selectedTextView?.text = ""
                    } else {
                        hospitalId = id
                    }
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

    private fun valid(): Boolean {
        when (radioTag) {
            0 -> {
                mBinding?.clParent?.let { showSnackbar(it,
                    getString(R.string.please_select_one_of_the_options)) }
                return false
            }

            2 -> {
                if (mBinding?.etPermanentAddress?.text.toString().trim().isEmpty()) {
                    mBinding?.clParent?.let { showSnackbar(it,
                        getString(R.string.please_enter_address)) }
                    return false
                } else if (mBinding?.etFileName?.text.toString().isEmpty()) {
                    mBinding?.clParent?.let {
                        showSnackbar(it, getString(R.string.please_select_address_proof))
                    }
                    return false
                } else if (mBinding?.etPresentStateName?.text.toString().trim().isEmpty()) {
                    mBinding?.clParent?.let {
                        showSnackbar(
                            it,
                            getString(R.string.please_select_hospital_treating_state_uts)
                        )
                    }
                    return false
                } else if (mBinding?.etPresentDistrictName?.text.toString().trim().isEmpty()) {
                    mBinding?.clParent?.let {
                        showSnackbar(
                            it,
                            getString(R.string.please_select_hospital_treating_district)
                        )
                    }
                    return false
                } else if (mBinding?.etSubDistrictName?.text.toString().trim().isEmpty()) {
                    mBinding?.clParent?.let {
                        showSnackbar(it,
                            getString(R.string.please_select_hospital_treating_sub_district))
                    }
                    return false
                } else if (mBinding?.etPincode?.text.toString().trim().isEmpty()) {
                    mBinding?.clParent?.let { showSnackbar(it, getString(R.string.please_enter_pincode)) }
                    return false
                } else if (mBinding?.etPincode?.text?.length != 6) {
                    mBinding?.clParent?.let { showSnackbar(it,
                        getString(R.string.pincode_should_be_6_digits)) }
                    return false
                } else if (mBinding?.etPresentHospitalName?.text.toString().trim().isEmpty()) {
                    mBinding?.clParent?.let { showSnackbar(it,
                        getString(R.string.please_select_hospital_treating_)) }
                    return false
                }
                return true
            }

            3 -> {

                if (mBinding?.etTreatingStateName?.text.toString().trim().isEmpty()) {
                    mBinding?.clParent?.let {
                        showSnackbar(
                            it,
                            getString(R.string.please_select_hospital_treating_state_uts_)
                        )
                    }
                    return false
                } else if (mBinding?.etTreatingDistrictName?.text.toString().trim().isEmpty()) {
                    mBinding?.clParent?.let {
                        showSnackbar(
                            it,
                            getString(R.string.please_select_hospital_treating_district_)
                        )
                    }
                    return false
                } else if (mBinding?.etTreatingHospitalName?.text.toString().trim().isEmpty()) {
                    mBinding?.clParent?.let { showSnackbar(it, getString(R.string.please_select_hospital_treating_)) }
                    return false
                }

                return true
            }

            else -> return true
        }
    }

    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAPTURE_IMAGE_REQUEST -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    val imageFile = saveImageToFile(imageBitmap)
                    photoFile = imageFile

                    val fileSizeInBytes = photoFile?.length() ?: 0
                    if (isFileSizeWithinLimit(fileSizeInBytes, 500.0)) { // 500 KB limit
                        mBinding?.etFileName?.text = photoFile?.name
                        uploadImage(photoFile!!)
                    } else {
                        compressFile(photoFile!!) // Compress if size exceeds limit
                        mBinding?.etFileName?.text = photoFile?.name
                        uploadImage(photoFile!!)
                    }
                }

                PICK_IMAGE -> {
                    val selectedImageUri = data?.data
                    if (selectedImageUri != null) {
                        val uriPathHelper = URIPathHelper()
                        val filePath = uriPathHelper.getPath(this, selectedImageUri)

                        val fileExtension =
                            filePath?.substringAfterLast('.', "").orEmpty().lowercase()
                        if (fileExtension in listOf("png", "jpg", "jpeg")) {
                            val file = filePath?.let { File(it) }
                            val fileSizeInBytes = file?.length() ?: 0
                            if (isFileSizeWithinLimit(fileSizeInBytes, 500.0)) { // 500 KB limit
                                mBinding?.etFileName?.text = file?.name
                                uploadImage(file!!)
                            } else {
                                compressFile(file!!) // Compress if size exceeds limit
                                mBinding?.etFileName?.text = file.name
                                uploadImage(file)
                            }
                        } else {
                            mBinding?.clParent?.let {
                                showSnackbar(
                                    it,
                                    getString(R.string.format_not_supported)
                                )
                            }
                        }
                    }
                }

                REQUEST_iMAGE_PDF -> {
                    data?.data?.let { uri ->
                        val projection = arrayOf(
                            MediaStore.MediaColumns.DISPLAY_NAME,
                            MediaStore.MediaColumns.SIZE
                        )

                        val cursor = contentResolver.query(uri, projection, null, null, null)
                        cursor?.use {
                            if (it.moveToFirst()) {
                                val documentName =
                                    it.getString(it.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME))
                                val fileSizeInBytes =
                                    it.getLong(it.getColumnIndex(MediaStore.MediaColumns.SIZE))
                                println(fileSizeInBytes)
                                if (isFileSizeWithinLimit(fileSizeInBytes, 500.0)) { // 500 KB limit
                                    uploadDocument(documentName, uri)
                                    mBinding?.etFileName?.text = documentName
                                } else {
                                    mBinding?.clParent?.let {
                                        showSnackbar(
                                            it,
                                            getString(R.string.file_size_exceeds_5_mb)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun uploadImage(file: File) {
        lifecycleScope.launch {
            val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            body =
                MultipartBody.Part.createFormData(
                    "address_proof_file",
                    file.name, reqFile
                )
        }
    }

    private fun uploadDocument(documentName: String?, uri: Uri) {
        val requestBody = convertToRequestBody(this, uri)
        body = MultipartBody.Part.createFormData(
            "address_proof_file",
            documentName,
            requestBody
        )
    }
}
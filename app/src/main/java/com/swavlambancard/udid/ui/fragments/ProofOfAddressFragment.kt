package com.swavlambancard.udid.ui.fragments

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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.swavlambancard.udid.R
import com.swavlambancard.udid.databinding.FragmentProofOfCAddBinding
import com.swavlambancard.udid.model.DropDownRequest
import com.swavlambancard.udid.model.DropDownResult
import com.swavlambancard.udid.model.Fields
import com.swavlambancard.udid.model.Filters
import com.swavlambancard.udid.model.Order
import com.swavlambancard.udid.model.PincodeRequest
import com.swavlambancard.udid.ui.activity.PersonalProfileActivity
import com.swavlambancard.udid.ui.adapter.BottomSheetAdapter
import com.swavlambancard.udid.utilities.BaseFragment
import com.swavlambancard.udid.utilities.EncryptionModel
import com.swavlambancard.udid.utilities.URIPathHelper
import com.swavlambancard.udid.utilities.Utility.rotateDrawable
import com.swavlambancard.udid.utilities.Utility.showSnackbar
import com.swavlambancard.udid.viewModel.SharedDataViewModel
import com.swavlambancard.udid.viewModel.ViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


class ProofOfAddressFragment : BaseFragment<FragmentProofOfCAddBinding>() {
    private lateinit var sharedViewModel: SharedDataViewModel
    private var viewModel = ViewModel()
    private var mBinding: FragmentProofOfCAddBinding? = null
    private var bottomSheetDialog: BottomSheetDialog? = null
    private var bottomSheetAdapter: BottomSheetAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    private var addressProofList = ArrayList<DropDownResult>()
    private var addressProofId: String? = null
    private var stateList = ArrayList<DropDownResult>()
    private var stateId: String? = null
    private var districtList = ArrayList<DropDownResult>()
    private var districtId: String? = null
    private var subDistrictList = ArrayList<DropDownResult>()
    private var subDistrictId: String? = null
    private var villageList = ArrayList<DropDownResult>()
    private var villageId: String? = null
    private var pincodeList = ArrayList<DropDownResult>()
    private var pincodeId: String? = null
    var body: MultipartBody.Part? = null
    private var addressProofName: String? = null

    override val layoutId: Int
        get() = R.layout.fragment_proof_of_c_add

    override fun init() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
        viewModel.init()
        sharedViewModel = ViewModelProvider(requireActivity())[SharedDataViewModel::class.java]
//        sharedViewModel.userData.observe(viewLifecycleOwner) { userData ->
//            mBinding?.tvNatureDocument?.setText(userData.documentAddressProof)
//            if (!userData.documentAddressProof.isNullOrEmpty()) {
//                mBinding?.tvNatureDocument?.setTextColor(Color.parseColor("#000000"))
//            }
//            mBinding?.etAddress?.setText(userData.documentAddress)
//            mBinding?.tvState?.setText(userData.state)
//            if (!userData.state.isNullOrEmpty()) {
//                mBinding?.tvState?.setTextColor(Color.parseColor("#000000"))
//            }
//            mBinding?.tvDistrict?.setText(userData.district)
//            if (!userData.district.isNullOrEmpty()) {
//                mBinding?.tvDistrict?.setTextColor(Color.parseColor("#000000"))
//            }
//            mBinding?.tvCity?.setText(userData.city)
//            if (!userData.city.isNullOrEmpty()) {
//                mBinding?.tvCity?.setTextColor(Color.parseColor("#000000"))
//            }
//            mBinding?.tvVillage?.setText(userData.village)
//            if (!userData.village.isNullOrEmpty()) {
//                mBinding?.tvVillage?.setTextColor(Color.parseColor("#000000"))
//            }
//            mBinding?.tvPincode?.setText(userData.pincode)
//            if (!userData.pincode.isNullOrEmpty()) {
//                mBinding?.tvPincode?.setTextColor(Color.parseColor("#000000"))
//            }
//        }
//
//        // Save data when fields change
//        mBinding?.tvNatureDocument?.addTextChangedListener {
//            sharedViewModel.userData.value?.documentAddressProof = it.toString()
//        }
//
//        mBinding?.etAddress?.addTextChangedListener {
//            sharedViewModel.userData.value?.documentAddress = it.toString()
//        }
//
//        mBinding?.tvState?.addTextChangedListener {
//            sharedViewModel.userData.value?.state = it.toString()
//        }
//
//        mBinding?.tvDistrict?.addTextChangedListener {
//            sharedViewModel.userData.value?.district = it.toString()
//        }
//        mBinding?.tvCity?.addTextChangedListener {
//            sharedViewModel.userData.value?.city = it.toString()
//        }
//        mBinding?.tvVillage?.addTextChangedListener {
//            sharedViewModel.userData.value?.village = it.toString()
//        }
//        mBinding?.tvPincode?.addTextChangedListener {
//            sharedViewModel.userData.value?.pincode = it.toString()
//        }
//
//
//        //ClickListener
//        mBinding?.tvNatureDocument?.setOnClickListener {
//            showBottomSheetDialog("Nature")
//        }
//        mBinding?.tvState?.setOnClickListener {
//            showBottomSheetDialog("State")
//        }
//        mBinding?.tvDistrict?.setOnClickListener {
//            showBottomSheetDialog("District")
//        }
//        mBinding?.tvCity?.setOnClickListener {
//            showBottomSheetDialog("City")
//        }
//        mBinding?.tvVillage?.setOnClickListener {
//            showBottomSheetDialog("Village")
//        }
//        mBinding?.tvPincode?.setOnClickListener {
//            showBottomSheetDialog("Pin")
//        }

//        mBinding?.tvHead?.setOnClickListener {
//            sharedViewModel.userData.value?.let { userData ->
//                // Process all data here
//                Toast.makeText(context, "Data Submitted: $userData ", Toast.LENGTH_LONG).show()
//            }
//        }

    }

    override fun setVariables() {
    }

    override fun setObservers() {
        viewModel.dropDownResult.observe(this) {
            val userResponseModel = it
            if (userResponseModel?._result != null && userResponseModel._result.isNotEmpty()) {
                when (userResponseModel.model) {
                    "Naturedocuments" -> {
                        addressProofList.clear()
                        addressProofList.add(DropDownResult("0", getString(R.string.select_nature_of_document)))
                        addressProofList.addAll(userResponseModel._result)
                    }
                    "States" -> {
                        stateList.clear()
                        stateList.add(DropDownResult("0", getString(R.string.choose_state_uts_)))
                        stateList.addAll(userResponseModel._result)
                    }

                    "Districts" -> {
                        districtList.clear()
                        districtList.add(DropDownResult("0", getString(R.string.choose_district)))
                        districtList.addAll(userResponseModel._result)
                    }

                    "Subdistricts" -> {
                        subDistrictList.clear()
                        subDistrictList.add(
                            DropDownResult(
                                "0",
                                getString(R.string.choose_city_sub_district_tehsil)
                            )
                        )
                        subDistrictList.addAll(userResponseModel._result)
                    }
                    "Villages" -> {
                        villageList.clear()
                        villageList.add(
                            DropDownResult(
                                "0",
                                getString(R.string.choose_village_block)
                            )
                        )
                        villageList.addAll(userResponseModel._result)
                    }
                }
                bottomSheetAdapter?.notifyDataSetChanged()
            }
        }
        viewModel.pincodeDropDownResult.observe(this) {
            val userResponseModel = it
            if (userResponseModel?._result != null && userResponseModel._result.isNotEmpty()) {
                when (userResponseModel.model) {
                    "Pincodes" -> {
                        pincodeList.clear()
                        pincodeList.add(DropDownResult("0", getString(R.string.choose_pincode)))
                        pincodeList.addAll(userResponseModel._result)
                    }
                }
                bottomSheetAdapter?.notifyDataSetChanged()
            }
        }
        viewModel.uploadFile.observe(this) {
            val userResponseModel = it
            if (userResponseModel?._result != null) {
                if (userResponseModel._resultflag == 0) {
                    mBinding?.llParent?.let { it1 ->
                        showSnackbar(
                            it1,
                            userResponseModel.message
                        )
                    }
                } else {
                    addressProofName = userResponseModel._result.file_name
                }
            }
        }
        viewModel.errors.observe(this) {
            mBinding?.let { it1 -> showSnackbar(it1.llParent, it) }
        }
    }

    inner class ClickActions {
        fun next(view: View) {
            if (valid()) {
                (requireActivity() as PersonalProfileActivity).replaceFragment(DisabilityDetailFragment())
            }
        }

        fun back(view: View) {
            (requireActivity() as PersonalProfileActivity).replaceFragment(ProofOfIDFragment())
        }

        fun uploadFile(view: View) {
            checkStoragePermission(requireContext())
        }

        fun addressProof(view: View) {
            showBottomSheetDialog("addressProof")
        }

        fun state(view: View) {
            showBottomSheetDialog("state")
        }

        fun district(view: View) {
            if (mBinding?.etState?.text.toString().trim()
                    .isNotEmpty()
            ) {
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

        fun subDistrict(view: View) {
            if (mBinding?.etDistrict?.text.toString().trim()
                    .isNotEmpty()
            ) {
                showBottomSheetDialog("subDistrict")
            } else {
                mBinding?.llParent?.let {
                    showSnackbar(
                        it,
                        getString(R.string.please_select_district_first)
                    )
                }
            }
        }

        fun villageBlock(view: View) {
            if (mBinding?.etSubDistrict?.text.toString().trim().isNotEmpty()
            ) {
                showBottomSheetDialog("village")
            } else {
                mBinding?.llParent?.let {
                    showSnackbar(
                        it,
                        getString(R.string.please_select_sub_district_first)
                    )
                }
            }
        }

        fun pincode(view: View) {
            if (mBinding?.etDistrict?.text.toString().trim()
                    .isNotEmpty()
            ) {
                showBottomSheetDialog("pincode")
            } else {
                mBinding?.llParent?.let {
                    showSnackbar(
                        it,
                        getString(R.string.please_select_district_first)
                    )
                }
            }
        }
    }

    private fun addressProofApi() {
        viewModel.getDropDown(
            requireContext(), DropDownRequest(
                model = "Naturedocuments",
                fields = Fields(id = "name"),
                type = "mobile"
            )
        )
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

    private fun villageApi() {
        viewModel.getDropDown(
            requireContext(), DropDownRequest(
                model = "Villages",
                fields = Fields(village_code = "village_name"),
                filters = Filters(subdistrict_code = subDistrictId),
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

    private fun subDistrictListApi() {
        viewModel.getDropDown(
            requireContext(), DropDownRequest(
                model = "Subdistricts",
                filters = Filters(district_code = districtId),
                fields = Fields(subdistrict_code = "subdistrict_name"),
                order = Order(subdistrict_name = "ASC"),
                type = "mobile"
            )
        )
    }

    private fun pincodeListApi() {
        viewModel.getPincodeDropDown(
            requireContext(), PincodeRequest(
                model = "Pincodes",
                district_code = districtId.toString(),
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
            "addressProof" -> {
                if (addressProofList.isEmpty()) {
                    addressProofApi()
                }
                selectedList = addressProofList
                selectedTextView = mBinding?.etNatureDocumentAddressProof
            }

            "state" -> {
                if (stateList.isEmpty()) {
                    stateListApi()
                }
                selectedList = stateList
                selectedTextView = mBinding?.etState
            }

            "district" -> {
                if (districtList.isEmpty()) {
                    districtListApi()
                }
                selectedList = districtList
                selectedTextView = mBinding?.etDistrict
            }

            "subDistrict" -> {
                if (subDistrictList.isEmpty()) {
                    subDistrictListApi()
                }
                selectedList = subDistrictList
                selectedTextView = mBinding?.etSubDistrict
            }

            "village" -> {
                if (villageList.isEmpty()) {
                    villageApi()
                }
                selectedList = villageList
                selectedTextView = mBinding?.etVillage
            }

            "pincode" -> {
                if (pincodeList.isEmpty()) {
                    pincodeListApi()
                }
                selectedList = pincodeList
                selectedTextView = mBinding?.etPincode
            }

            else -> return
        }

        bottomSheetAdapter =
            BottomSheetAdapter(requireContext(), selectedList) { selectedItem, id ->
                selectedTextView?.text = selectedItem
                when (type) {
                    "addressProof" -> {
                        if (selectedItem == "Select Nature of Document") {
                            selectedTextView?.text = ""
                            mBinding?.etNatureDocumentAddressProof?.text = ""
                        } else {
                            addressProofId = id
                        }
                    }

                    "state" -> {
                        if (selectedItem == "Choose State / UTs") {
                            selectedTextView?.text = ""
                            mBinding?.etState?.text = ""
                            districtList.clear()
                        } else {
                            stateId = id
                        }
                    }

                    "district" -> {
                        if (selectedItem == "Choose District") {
                            selectedTextView?.text = ""
                            mBinding?.etSubDistrict?.text = ""
                            subDistrictList.clear()
                        } else {
                            districtId = id
                        }
                    }

                    "subDistrict" -> {
                        if (selectedItem == "Choose City / Sub District / Tehsil") {
                            selectedTextView?.text = ""
                        } else {
                            subDistrictId = id
                        }
                    }

                    "village" -> {
                        if (selectedItem == "Choose Village / Block") {
                            selectedTextView?.text = ""
                        } else {
                            villageId = id
                        }
                    }

                    "pincode" -> {
                        if (selectedItem == "Choose Pincode") {
                            selectedTextView?.text = ""
                            mBinding?.etPincode?.text = ""
                        } else {
                            pincodeId = id
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
        if (mBinding?.etNatureDocumentAddressProof?.text.toString().trim().isEmpty()) {
            mBinding?.llParent?.let {
                showSnackbar(
                    it,
                    getString(R.string.please_select_nature_of_document)
                )
            }
            return false
        } else if (mBinding?.etFileName?.text.toString().isEmpty()) {
            mBinding?.llParent?.let {
                showSnackbar(it, getString(R.string.please_select_address_proof))
            }
            return false
        } else if (mBinding?.etAddress?.text.toString().isEmpty()) {
            mBinding?.llParent?.let {
                showSnackbar(it, getString(R.string.please_enter_correspondence_address))
            }
            return false
        } else if (mBinding?.etState?.text.toString().trim().isEmpty()) {
            mBinding?.llParent?.let {
                showSnackbar(
                    it,
                    getString(R.string.please_select_state_uts)
                )
            }
            return false
        } else if (mBinding?.etDistrict?.text.toString().trim().isEmpty()) {
            mBinding?.llParent?.let {
                showSnackbar(
                    it,
                    getString(R.string.please_select_district)
                )
            }
            return false
        } else if (mBinding?.etSubDistrict?.text.toString().trim().isEmpty()) {
            mBinding?.llParent?.let {
                showSnackbar(
                    it,
                    getString(R.string.please_select_city_sub_district_tehsil)
                )
            }
            return false
        } else if (mBinding?.etPincode?.text.toString().trim().isEmpty()) {
            mBinding?.llParent?.let { showSnackbar(it, getString(R.string.please_enter_pincode)) }
            return false
        }
        return true
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
                    } else {
                        compressFile(photoFile!!) // Compress if size exceeds limit
                        mBinding?.etFileName?.text = photoFile?.name
                    }
                    uploadImage(photoFile!!)
                }

                PICK_IMAGE -> {
                    val selectedImageUri = data?.data
                    if (selectedImageUri != null) {
                        val uriPathHelper = URIPathHelper()
                        val filePath = uriPathHelper.getPath(requireContext(), selectedImageUri)

                        val fileExtension =
                            filePath?.substringAfterLast('.', "").orEmpty().lowercase()
                        if (fileExtension in listOf("png", "jpg", "jpeg")) {
                            val file = filePath?.let { File(it) }
                            val fileSizeInBytes = file?.length() ?: 0
                            if (isFileSizeWithinLimit(fileSizeInBytes, 500.0)) { // 500 KB limit
                                mBinding?.etFileName?.text = file?.name
                            } else {
                                compressFile(file!!) // Compress if size exceeds limit
                                mBinding?.etFileName?.text = file.name
                            }
                            uploadImage(file!!)
                        } else {
                            mBinding?.llParent?.let {
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

                        val cursor = requireContext().contentResolver.query(
                            uri,
                            projection,
                            null,
                            null,
                            null
                        )
                        cursor?.use {
                            if (it.moveToFirst()) {
                                val documentName =
                                    it.getString(it.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME))
                                val fileSizeInBytes =
                                    it.getLong(it.getColumnIndex(MediaStore.MediaColumns.SIZE))
                                if (isFileSizeWithinLimit(fileSizeInBytes, 500.0)) { // 500 KB limit
                                    uploadDocument(documentName, uri)
                                    mBinding?.etFileName?.text = documentName
                                } else {
                                    mBinding?.llParent?.let {
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
                    "document",
                    file.name, reqFile
                )
        }
        uploadFileApi()
    }

    private fun uploadDocument(documentName: String?, uri: Uri) {
        val requestBody = convertToRequestBody(requireContext(), uri)
        body = MultipartBody.Part.createFormData(
            "document",
            documentName,
            requestBody
        )
        uploadFileApi()
    }

    private fun uploadFileApi() {
        viewModel.uploadFile(
            requireContext(),
            EncryptionModel.aesEncrypt("address_proof_file").toRequestBody(MultipartBody.FORM),
            body
        )
    }
}
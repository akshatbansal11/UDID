package com.swavlambancard.udid.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
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
import com.swavlambancard.udid.ui.PdfViewerActivity
import com.swavlambancard.udid.ui.activity.PersonalProfileActivity
import com.swavlambancard.udid.ui.adapter.BottomSheetAdapter
import com.swavlambancard.udid.utilities.BaseFragment
import com.swavlambancard.udid.utilities.EncryptionModel
import com.swavlambancard.udid.utilities.URIPathHelper
import com.swavlambancard.udid.utilities.Utility.getNameById
import com.swavlambancard.udid.utilities.Utility.openFile
import com.swavlambancard.udid.utilities.Utility.rotateDrawable
import com.swavlambancard.udid.utilities.Utility.setBlueUnderlinedText
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
    private var imageUri: Uri? = null
    private var cameraUri: Uri? = null
    private var pdfUri: Uri? = null

    override val layoutId: Int
        get() = R.layout.fragment_proof_of_c_add

    override fun init() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
        viewModel.init()
        sharedViewModel = ViewModelProvider(requireActivity())[SharedDataViewModel::class.java]
        if(sharedViewModel.userData.value?.isFrom != "login"){
            addressProofApi()
        }

        sharedViewModel.userData.observe(viewLifecycleOwner) { userData ->

            if(sharedViewModel.userData.value?.isFrom != "login") {
                mBinding?.etFileName?.let {
                    setBlueUnderlinedText(
                        it,
                        userData.documentAddressProofPhoto.toString()
                    )
                }
                mBinding?.etFileName?.setOnClickListener {
                    openFile(userData.documentAddressProofPhoto.toString(),requireContext())
                }
            }
            else{
                if(userData.documentAddressProofPhoto!=null){
                    mBinding?.etFileName?.let {
                        setBlueUnderlinedText(
                            it,
                            userData.documentAddressProofPhoto.toString()
                        )
                    }
                }

            }

            mBinding?.etNatureDocumentAddressProof?.text = userData.natureDocumentAddressProofName
            addressProofId = userData.natureDocumentAddressProofCode
            mBinding?.etAddress?.setText(userData.address)
            mBinding?.etState?.text = userData.stateName
            mBinding?.etDistrict?.text = userData.districtName
            mBinding?.etSubDistrict?.text = userData.subDistrictName
            mBinding?.etVillage?.text = userData.villageName
            mBinding?.etPincode?.text = userData.pincodeName
            stateId = userData.stateCode
            districtId = userData.districtCode
            subDistrictId = userData.subDistrictCode
            villageId = userData.villageCode
            pincodeId = userData.pincodeCode
        }
        mBinding?.etNatureDocumentAddressProof?.addTextChangedListener {
            sharedViewModel.userData.value?.natureDocumentAddressProofName = it.toString()
        }
        mBinding?.etFileName?.addTextChangedListener {
            sharedViewModel.userData.value?.documentAddressProofPhoto = it.toString()
        }

        mBinding?.etAddress?.addTextChangedListener {
            sharedViewModel.userData.value?.address = it.toString()
        }

        mBinding?.etState?.addTextChangedListener {
            sharedViewModel.userData.value?.stateName = it.toString()
        }

        mBinding?.etDistrict?.addTextChangedListener {
            sharedViewModel.userData.value?.districtName = it.toString()
        }
        mBinding?.etSubDistrict?.addTextChangedListener {
            sharedViewModel.userData.value?.subDistrictName = it.toString()
        }
        mBinding?.etVillage?.addTextChangedListener {
            sharedViewModel.userData.value?.villageName = it.toString()
        }
        mBinding?.etPincode?.addTextChangedListener {
            sharedViewModel.userData.value?.pincodeName = it.toString()
        }
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
                        addressProofList.add(
                            DropDownResult(
                                "0",
                                "Select Nature of Document"
                            )
                        )
                        addressProofList.addAll(userResponseModel._result)
                        if(sharedViewModel.userData.value?.isFrom != "login"){
                            mBinding?.etNatureDocumentAddressProof?.text = getNameById(sharedViewModel.userData.value?.natureDocumentAddressProofCode.toString(),addressProofList)
                        }
                    }

                    "States" -> {
                        stateList.clear()
                        stateList.add(DropDownResult("0", "Choose State / UTs"))
                        stateList.addAll(userResponseModel._result)
                    }

                    "Districts" -> {
                        districtList.clear()
                        districtList.add(DropDownResult("0", "Choose District"))
                        districtList.addAll(userResponseModel._result)
                    }

                    "Subdistricts" -> {
                        subDistrictList.clear()
                        subDistrictList.add(
                            DropDownResult(
                                "0",
                                "Choose City / Sub District / Tehsil"
                            )
                        )
                        subDistrictList.addAll(userResponseModel._result)
                    }

                    "Villages" -> {
                        villageList.clear()
                        villageList.add(
                            DropDownResult(
                                "0",
                                "Choose Village / Block"
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
                        pincodeList.add(DropDownResult("0", "Choose Pincode"))
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
                    mBinding?.etFileName?.text = userResponseModel._result.file_name
                    mBinding?.etFileName?.let {
                        setBlueUnderlinedText(
                            it,
                            sharedViewModel.userData.value?.documentAddressProofPhoto.toString()
                        )
                    }
                    when {
                        pdfUri != null -> sharedViewModel.userData.value?.documentAddressProofPhotoPath =
                            pdfUri.toString()

                        cameraUri != null -> sharedViewModel.userData.value?.documentAddressProofPhotoPath =
                            cameraUri.toString()

                        imageUri != null -> sharedViewModel.userData.value?.documentAddressProofPhotoPath =
                            imageUri.toString()

                    }
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
                (requireActivity() as PersonalProfileActivity).replaceFragment(
                    DisabilityDetailFragment()
                )
            }
        }

        fun back(view: View) {
            (requireActivity() as PersonalProfileActivity).replaceFragment(ProofOfIDFragment())
        }

        fun fileCorrespondenceAddress(view: View) {
            if(sharedViewModel.userData.value?.documentAddressProofPhotoPath==null){
                return
            }
            if(sharedViewModel.userData.value?.isFrom != "login"){
                val documentPath = sharedViewModel.userData.value?.documentAddressProofPhotoPath
                if (documentPath.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "No document found", Toast.LENGTH_SHORT).show()
                    return
                }

                val uri = Uri.parse(documentPath)

                if (documentPath.endsWith(".pdf", ignoreCase = true)) {
                    // Open PDF in Chrome using Google Docs Viewer
                    val pdfUrl = "https://docs.google.com/viewer?url=$uri"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl))
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.setPackage("com.android.chrome") // Forces it to open in Chrome if available

                    try {
                        startActivity(intent)
                    } catch (e: Exception) {
                        intent.setPackage(null) // Open in any available browser
                        startActivity(intent)
                    }
                } else {
                    // Open Image in Chrome by using "file://" or "content://"
                    val intent = Intent(requireContext(), PdfViewerActivity::class.java)
                    intent.putExtra("fileUri", uri.toString())
                    startActivity(intent)                }
            }
            else{
                val intent = Intent(requireContext(), PdfViewerActivity::class.java)
                intent.putExtra("fileUri", sharedViewModel.userData.value?.documentAddressProofPhotoPath)
                startActivity(intent)
            }

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
//                if (districtList.isEmpty()) {
                    districtListApi()
//                }
                selectedList = districtList
                selectedTextView = mBinding?.etDistrict
            }

            "subDistrict" -> {
//                if (subDistrictList.isEmpty()) {
                    subDistrictListApi()
//                }
                selectedList = subDistrictList
                selectedTextView = mBinding?.etSubDistrict
            }

            "village" -> {
//                if (villageList.isEmpty()) {
                    villageApi()
//                }
                selectedList = villageList
                selectedTextView = mBinding?.etVillage
            }

            "pincode" -> {
//                if (pincodeList.isEmpty()) {
                    pincodeListApi()
//                }
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
                            sharedViewModel.userData.value?.natureDocumentAddressProofCode = id
                        }
                    }

                    "state" -> {
                        if (selectedItem == "Choose State / UTs") {
                            selectedTextView?.text = ""
                            mBinding?.etState?.text = ""
                            districtList.clear()
                        } else {
                            stateId = id
                            sharedViewModel.userData.value?.stateCode = id
                        }
                    }

                    "district" -> {
                        if (selectedItem == "Choose District") {
                            selectedTextView?.text = ""
                            mBinding?.etSubDistrict?.text = ""
                            subDistrictList.clear()
                        } else {
                            districtId = id
                            sharedViewModel.userData.value?.districtCode = id
                        }
                    }

                    "subDistrict" -> {
                        if (selectedItem == "Choose City / Sub District / Tehsil") {
                            selectedTextView?.text = ""
                        } else {
                            subDistrictId = id
                            sharedViewModel.userData.value?.subDistrictCode = id
                        }
                    }

                    "village" -> {
                        if (selectedItem == "Choose Village / Block") {
                            selectedTextView?.text = ""
                        } else {
                            villageId = id
                            sharedViewModel.userData.value?.villageCode = id
                        }
                    }

                    "pincode" -> {
                        if (selectedItem == "Choose Pincode") {
                            selectedTextView?.text = ""
                            mBinding?.etPincode?.text = ""
                        } else {
                            pincodeId = id
                            sharedViewModel.userData.value?.pincodeCode = id
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
                    cameraUri = Uri.fromFile(imageFile) // Get URI from file
                    imageUri = null
                    pdfUri = null
                    photoFile = imageFile
                    val fileSizeInBytes = photoFile?.length() ?: 0
                    if (isFileSizeWithinLimit(fileSizeInBytes, 500.0)) { // 500 KB limit
                    } else {
                        compressFile(photoFile!!) // Compress if size exceeds limit
                    }
                    uploadImage(photoFile!!)
                }

                PICK_IMAGE -> {
                    imageUri = data?.data
                    cameraUri = null
                    pdfUri = null
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
                            } else {
                                compressFile(file!!) // Compress if size exceeds limit
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
                    pdfUri = data?.data
                    cameraUri = null
                    imageUri = null
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
            EncryptionModel.aesEncrypt("mobile").toRequestBody(MultipartBody.FORM),
            body
        )
    }
}
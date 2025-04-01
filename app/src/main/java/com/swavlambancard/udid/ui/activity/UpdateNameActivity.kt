package com.swavlambancard.udid.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.swavlambancard.udid.utilities.Preferences.getPreferenceOfLogin
import com.swavlambancard.udid.R
import com.swavlambancard.udid.databinding.ActivityUpdateNameBinding
import com.swavlambancard.udid.model.DropDownRequest
import com.swavlambancard.udid.model.DropDownResult
import com.swavlambancard.udid.model.Fields
import com.swavlambancard.udid.model.Filters
import com.swavlambancard.udid.model.GenerateOtpRequest
import com.swavlambancard.udid.model.LanguageLocalize
import com.swavlambancard.udid.model.UserData
import com.swavlambancard.udid.ui.adapter.BottomSheetAdapter
import com.swavlambancard.udid.utilities.AppConstants
import com.swavlambancard.udid.utilities.BaseActivity
import com.swavlambancard.udid.utilities.EncryptionModel
import com.swavlambancard.udid.utilities.JSEncryptService
import com.swavlambancard.udid.utilities.Translator
import com.swavlambancard.udid.utilities.URIPathHelper
import com.swavlambancard.udid.utilities.Utility.rotateDrawable
import com.swavlambancard.udid.utilities.Utility.showSnackbar
import com.swavlambancard.udid.utilities.hideView
import com.swavlambancard.udid.utilities.showView
import com.swavlambancard.udid.utilities.toast
import com.swavlambancard.udid.viewModel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class UpdateNameActivity : BaseActivity<ActivityUpdateNameBinding>() {

    private var mBinding: ActivityUpdateNameBinding? = null
    private var viewModel = ViewModel()
    private var bottomSheetDialog: BottomSheetDialog? = null
    private var bottomSheetAdapter: BottomSheetAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    private var reasonToUpdateNameList = ArrayList<DropDownResult>()
    private var reasonToUpdateNameId: String? = null
    private var identityProofList = ArrayList<DropDownResult>()
    private var identityProofId: String? = null
    private var regionalLanguageList = ArrayList<DropDownResult>()
    private var regionalLanguageId: String? = null
    var body: MultipartBody.Part? = null
    private val handler = android.os.Handler()
    private var translationRunnable: Runnable? = null

    override val layoutId: Int
        get() = R.layout.activity_update_name

    override fun initView() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()
        viewModel.init()

        mBinding?.etUpdatedName?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                translationRunnable?.let { handler.removeCallbacks(it) } // Cancel previous task

                translationRunnable = Runnable {
                    lifecycleScope.launch {
                        translateText(s.toString().trim())
                    }
                }
                handler.postDelayed(translationRunnable!!, 1000) // Delay translation by 500ms
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    override fun setVariables() {
        mBinding?.etCurrentName?.text =if(getPreferenceOfLogin(
                this,
                AppConstants.LOGIN_DATA,
                UserData::class.java
            ).full_name.isNullOrEmpty()) getString(R.string.na) else
            getPreferenceOfLogin(
                this,
                AppConstants.LOGIN_DATA,
                UserData::class.java
            ).full_name
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
                if (userResponseModel.model == "Identityproofs") {
                    identityProofList.clear()
                    identityProofList.add(DropDownResult("0", "Select Identity Proof"))
                    identityProofList.addAll(userResponseModel._result)
                    bottomSheetAdapter?.notifyDataSetChanged()
                } else if (userResponseModel.model == "Languages") {
                    regionalLanguageList.clear()
                    regionalLanguageList.add(DropDownResult("0", "Choose Regional Language"))
                    regionalLanguageList.addAll(userResponseModel._result)
                    bottomSheetAdapter?.notifyDataSetChanged()
                }
                else if (userResponseModel.model == "Updationreason") {
                    reasonToUpdateNameList.clear()
                    reasonToUpdateNameList.add(DropDownResult("0", getString(R.string.spelling_correction_in_name)))
                    reasonToUpdateNameList.addAll(userResponseModel._result)
                    bottomSheetAdapter?.notifyDataSetChanged()
                }
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

        viewModel.updateNameResult.observe(this) {
            val userResponseModel = it
            if (userResponseModel?._resultflag != 0) {
                toast(userResponseModel.message)

                if (getPreferenceOfLogin(this, AppConstants.LOGIN_DATA, UserData::class.java) != null) {
                    com.swavlambancard.udid.utilities.Preferences.setPreference(this, AppConstants.LOGIN_DATA, getPreferenceOfLogin(this, AppConstants.LOGIN_DATA, UserData::class.java).copy(
                        updaterequest = getPreferenceOfLogin(this, AppConstants.LOGIN_DATA, UserData::class.java).updaterequest?.copy(
                            Name = 1 // Replace with the new value
                        )
                    ))
                }

                startActivity(Intent(this, UpdateRequestActivity::class.java)
                    .putExtra(AppConstants.UPDATE_REQUEST, getString(R.string.submit_update_name)))
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
            checkStoragePermission(this@UpdateNameActivity)
        }

        fun identityProof(view: View) {
            showBottomSheetDialog("identity_proof")
        }
        fun regionalLanguage(view: View) {
            showBottomSheetDialog("regional_language")
        }

        fun reasonToUpdateName(view: View) {
            showBottomSheetDialog("reason_to_update_name")
        }

        fun generateOtp(view: View) {
            if (valid()) {
                generateOtpApi()
            }
        }

        fun submit(view: View) {
            if (valid()) {
                if (mBinding?.etEnterOtp?.text.toString().trim().isNotEmpty()) {
                    updateNameApi()
                } else {
                    showSnackbar(mBinding?.clParent!!, getString(R.string.please_enter_the_otp))
                }
            }
        }
    }

    private suspend fun translateText(inputText: String) {
        if (inputText.isEmpty() || regionalLanguageId.isNullOrEmpty()) {
            withContext(Dispatchers.Main) {// all the ui updations are taking place in ui thread
                mBinding?.etUpdatedNameRegionalLanguage?.setText("")
            }
            return
        }

        val translatedText = withContext(Dispatchers.IO) {
            Translator.getTranslation(
                "en",
                regionalLanguageId!!,
                inputText
            )// this is the main function responsible to give the translation so earlier on we were using a callback function inside main thread now we are using suspend cancelable coroutine which solves the blocking issue
        }

        withContext(Dispatchers.Main) {
            Log.d("Translation", "Translated: $translatedText")
            mBinding?.etUpdatedNameRegionalLanguage?.setText(translatedText)
        }
    }

    private fun identityProofApi() {
        viewModel.getDropDown(
            this, DropDownRequest(
                Fields(id = "identity_name"),
                model = "Identityproofs",
                type = "mobile"
            )
        )
    }
    private fun regionalLanguageApi() {
        viewModel.getDropDown(
            this, DropDownRequest(
                Fields(iso_code = "language_name"),
                model = "Languages",
                type = "mobile"
            )
        )
    }

    private fun reasonToUpdateNameListApi() {
        viewModel.getDropDown(
            this, DropDownRequest(
                Fields(reason = "reason"),
                model = "Updationreason",
                filters = Filters(
                    status = 1,
                    request_code = "NA"
                ),
                type = "mobile",
                order = "DESC"
            )
        )
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

    private fun updateNameApi() {
        viewModel.getUpdateName(
            context = this,
            applicationNumber = EncryptionModel.aesEncrypt(
                getPreferenceOfLogin(
                    this,
                    AppConstants.LOGIN_DATA,
                    UserData::class.java
                ).application_number.toString()
            )
                .toRequestBody(MultipartBody.FORM),
            name = EncryptionModel.aesEncrypt(mBinding?.etUpdatedName?.text.toString().trim())
                .toRequestBody(MultipartBody.FORM),
            nameRegionalLanguage = EncryptionModel.aesEncrypt(
                mBinding?.etUpdatedNameRegionalLanguage?.text.toString().trim()
            ).toRequestBody(MultipartBody.FORM),
            addressProofId = EncryptionModel.aesEncrypt(identityProofId.toString())
                .toRequestBody(MultipartBody.FORM),
            reason = EncryptionModel.aesEncrypt(reasonToUpdateNameId.toString())
                .toRequestBody(MultipartBody.FORM),
            otherReason = EncryptionModel.aesEncrypt(
                mBinding?.etAnyOtherReason?.text.toString().trim()
            )
                .toRequestBody(MultipartBody.FORM),
            otp = EncryptionModel.aesEncrypt(mBinding?.etEnterOtp?.text.toString().trim()).toRequestBody(MultipartBody.FORM),
            type = "mobile".toRequestBody(MultipartBody.FORM),
            document = body
        )
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
                if (identityProofList.isEmpty()) {
                    identityProofApi()
                }
                selectedList = identityProofList
                selectedTextView = mBinding?.etIdentityProof
            }

            "regional_language" -> {
                if (regionalLanguageList.isEmpty()) {
                    regionalLanguageApi()
                }
                selectedList = regionalLanguageList
                selectedTextView = mBinding?.etRegionalLanguage
            }
            "reason_to_update_name" -> {
                if (reasonToUpdateNameList.isEmpty()) {
                    reasonToUpdateNameListApi()
                }
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
                    if (selectedItem == "Select Identity Proof") {
                        selectedTextView?.text = ""
                    } else {
                        identityProofId = id
                    }
                }
                "regional_language" -> {
                    if (selectedItem == "Choose Regional Language") {
                        selectedTextView?.text = ""
                    } else {
                        regionalLanguageId = id
                        mBinding?.etUpdatedNameRegionalLanguage?.setText("Translating...")

                        // Cancel any pending translation and introduce delay
                        translationRunnable?.let { handler.removeCallbacks(it) }
                        translationRunnable = Runnable {
                            lifecycleScope.launch {
                                translateText(
                                    mBinding?.etUpdatedName?.text.toString().trim()
                                )
                            }
                        }
                        handler.postDelayed(
                            translationRunnable!!,
                            500
                        ) // Delay translation by 500ms
                    }
                }

                "reason_to_update_name" -> {
                    when (selectedItem) {
                        "Any other " -> {
                            mBinding?.tvAnyOtherReason?.showView()
                            mBinding?.etAnyOtherReason?.showView()
                            reasonToUpdateNameId = id
                        }

                        "Spelling Correction in Name" -> {
                            mBinding?.tvAnyOtherReason?.hideView()
                            mBinding?.etAnyOtherReason?.hideView()
                            selectedTextView?.text = ""
                            mBinding?.etAnyOtherReason?.setText("")
                        }

                        else -> {
                            mBinding?.tvAnyOtherReason?.hideView()
                            mBinding?.etAnyOtherReason?.hideView()
                            mBinding?.etAnyOtherReason?.setText("")
                            reasonToUpdateNameId = id
                        }
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
        if (mBinding?.etUpdatedName?.text.toString().isEmpty()) {
            mBinding?.clParent?.let {
                showSnackbar(it, getString(R.string.please_enter_the_updated_name))
            }
            return false
        } else if (mBinding?.etRegionalLanguage?.text.toString().isEmpty()) {
            mBinding?.clParent?.let {
                showSnackbar(it, getString(R.string.please_select_regional_language))

            }
            return false
        } else if (mBinding?.etUpdatedNameRegionalLanguage?.text.toString().isEmpty()) {
            mBinding?.clParent?.let {
                showSnackbar(it, getString(R.string.please_enter_the_updated_regional_name))

            }
            return false
        } else if (mBinding?.etIdentityProof?.text.toString().isEmpty()) {
            mBinding?.clParent?.let {
                showSnackbar(it, getString(R.string.please_select_the_identity_proof))

            }
            return false
        } else if (mBinding?.etFileName?.text.toString().isEmpty()) {
            mBinding?.clParent?.let {
                showSnackbar(it, getString(R.string.please_upload_the_document))
            }
            return false
        } else if (mBinding?.etReasonToUpdateName?.text.toString().isEmpty()) {
            mBinding?.clParent?.let {
                showSnackbar(it, getString(R.string.please_enter_the_reason))
            }
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

    private fun uploadDocument(documentName: String?, uri: Uri) {
        val requestBody = convertToRequestBody(this, uri)
        body = MultipartBody.Part.createFormData(
            "document",
            documentName,
            requestBody
        )
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
    }
}
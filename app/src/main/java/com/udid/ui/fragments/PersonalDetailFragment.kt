package com.udid.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.RotateDrawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.udid.R
import com.udid.databinding.FragmentPersonalDetailsBinding
import com.udid.databinding.FragmentProofOfCAddBinding
import com.udid.model.DropDownResult
import com.udid.ui.adapter.BottomSheetAdapter
import com.udid.utilities.BaseFragment
import com.udid.utilities.URIPathHelper
import com.udid.utilities.Utility
import com.udid.utilities.Utility.convertDate
import com.udid.utilities.Utility.showSnackbar
import com.udid.utilities.hideView
import com.udid.utilities.showView
import com.udid.viewModel.SharedDataViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
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
    private var districtId: String? = null // Store selected state
    var body: MultipartBody.Part? = null
    private var sign=0
    private val guardian = listOf(
        DropDownResult(id = "1", name = "Father"),
        DropDownResult(id = "2", name = "Mother"),
        DropDownResult(id = "3", name = "Guardian")
    )
    private var gender:Int=0
    override val layoutId: Int
        get() = R.layout.fragment_personal_details


    @RequiresApi(Build.VERSION_CODES.O)
    override fun init() {
        mBinding=viewDataBinding
        mBinding?.clickAction = ClickActions()
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
        mBinding?.tvChooseFile?.setOnClickListener {
            sign=0
            checkStoragePermission(requireContext())
        }
        mBinding?.tvChooseFileSign?.setOnClickListener {
            sign=1
            checkStoragePermission(requireContext())
        }

    }
    inner class ClickActions {
        fun generateOtp(view: View){
            if(valid()){
                mBinding?.clParent?.let { Utility.showSnackbar(it,"Done OTP") }
            }
        }
    }

    private fun valid(): Boolean {
        val etApplicantFullName = mBinding?.etApplicantFullName?.text?.toString()
        val etApplicantMobileNo = mBinding?.etApplicantMobileNo?.text?.toString()
        val etDoc = mBinding?.etDoc?.text?.toString()
        val etApplicantDateOfBirth = mBinding?.etApplicantDateOfBirth?.hint?.toString()
        val tvGuardian = mBinding?.tvGuardian?.hint?.toString()

        // Check if Aadhaar number is null or empty
        if (etApplicantFullName.isNullOrEmpty()) {
            mBinding?.clParent?.let { Utility.showSnackbar(it,"Please enter Applicant's Full Name.") }
            return false
        }
        if (etApplicantMobileNo.isNullOrEmpty()) {
            mBinding?.clParent?.let {
                Utility.showSnackbar(it, "Mobile number is required.")
            }
            return false
        }
        if (etApplicantMobileNo.length != 10) {
            mBinding?.clParent?.let {
                Utility.showSnackbar(it, "Mobile number must be exactly 10 digits.")
            }
            return false
        }

        if (etApplicantDateOfBirth=="dd/mm/yyyy") {
            mBinding?.clParent?.let { Utility.showSnackbar(it, "Please select date of birth.") }
            return false
        }
        if (gender==0) {
            mBinding?.clParent?.let { Utility.showSnackbar(it, "Please select gender.") }
            return false
        }
        if (tvGuardian=="Select Relation with PWD") {
            mBinding?.clParent?.let { Utility.showSnackbar(it, "Please select guardian relation.") }
            return false
        }
        if (etDoc=="No File Chosen") {
            mBinding?.clParent?.let { Utility.showSnackbar(it, "Photo Is Required") }
            return false
        }
        return true
    }
    @RequiresApi(Build.VERSION_CODES.O)
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
                selectedTextView.hint = ""
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
        val selectedList: List<DropDownResult>
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
            selectedTextView.hint = ""
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

    override fun setObservers() {}

    private fun uploadImage(file: File) {
        lifecycleScope.launch {
            val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            body =
                MultipartBody.Part.createFormData(
                    "document_name",
                    file.name, reqFile
                )
//            viewModel.getProfileUploadFile(
//                context = requireContext(@AddAscadStateActivity,
//                document_name = body,
//                user_id = getPreferenceOfScheme(
//                    requireContext(@AddAscadStateActivity,
//                    AppConstants.SCHEME,
//                    Result::class.java
//                )?.user_id,
//                table_name = getString(R.string.ascad_state).toRequestBody(
//                    MultipartBody.FORM
//                ),
//            )
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
                    if(sign==1){
                        mBinding?.etDocSign?.text = photoFile?.name

                    }
                    else{
                        mBinding?.etDoc?.text = photoFile?.name

                    }
                }

                PICK_IMAGE -> {
                    val selectedImageUri = data?.data
                    if (selectedImageUri != null) {
                        val uriPathHelper = URIPathHelper()
                        val filePath = uriPathHelper.getPath(requireContext(), selectedImageUri)

                        val fileExtension =
                            filePath?.substringAfterLast('.', "").orEmpty().lowercase()
                        // Validate file extension
                        if (fileExtension in listOf("png", "jpg", "jpeg")) {
                            val file = filePath?.let { File(it) }

                            // Check file size (5 MB = 5 * 1024 * 1024 bytes)
                            file?.let {
                                val fileSizeInMB = it.length() / (1024 * 1024.0) // Convert to MB
                                if (fileSizeInMB <= 5) {
                                    if(sign==1){
                                        mBinding?.etDocSign?.text = file.name

                                    }
                                    else{
                                        mBinding?.etDoc?.text = file.name

                                    }
//                                    mBinding?.llUploadOne?.showView()
//                                    mBinding?.ivPicOne?.setImageURI(selectedImageUri)
                                    uploadImage(it)
                                } else {
                                    mBinding?.let {
                                        showSnackbar(
                                            it.clParent,
                                            "File size exceeds 5 MB"
                                        )
                                    }
                                }
                            }
                        } else {
                            mBinding?.let { showSnackbar(it.clParent, "Format not supported") }
                        }
                    }
                }

                REQUEST_iMAGE_PDF -> {
                    data?.data?.let { uri ->
                        val projection = arrayOf(
                            MediaStore.MediaColumns.DISPLAY_NAME,
                            MediaStore.MediaColumns.SIZE
                        )


                        val cursor = requireActivity().contentResolver.query(uri, projection, null, null, null)
                        cursor?.use {
                            if (it.moveToFirst()) {
                                val documentName =
                                    it.getString(it.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME))
                                val fileSizeInBytes =
                                    it.getLong(it.getColumnIndex(MediaStore.MediaColumns.SIZE))
                                val fileSizeInMB =
                                    fileSizeInBytes / (1024 * 1024.0) // Convert to MB

                                // Validate file size (5 MB = 5 * 1024 * 1024 bytes)
                                if (fileSizeInMB <= 5) {
                                    uploadDocument(documentName, uri)
                                    if(sign==1){
                                        mBinding?.etDocSign?.text = documentName

                                    }
                                    else{
                                        mBinding?.etDoc?.text = documentName

                                    }
//                                    mBinding?.ivPicOne?.setImageResource(R.drawable.ic_pdf)
                                } else {
                                    mBinding?.let {
                                        showSnackbar(
                                            it.clParent,
                                            "File size exceeds 5 MB"
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
        val requestBody = convertToRequestBody(requireContext(), uri)
        body = MultipartBody.Part.createFormData(
            "document_name",
            documentName,
            requestBody
        )
//        viewModel.getProfileUploadFile(
//            context = requireContext(,
//            document_name = body,
//            user_id = getPreferenceOfScheme(requireContext(, AppConstants.SCHEME, Result::class.java)?.user_id,
//            table_name = getString(R.string.ascad_state).toRequestBody(MultipartBody.FORM),
//        )
    }

    fun convertToRequestBody(context: Context, uri: Uri): RequestBody {
        val contentResolver: ContentResolver = context.contentResolver
        val tempFileName = "temp_${System.currentTimeMillis()}.pdf"
        val file = File(context.cacheDir, tempFileName)

        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            // Handle the error appropriately
        }

        return file.asRequestBody("application/pdf".toMediaType())
    }
}
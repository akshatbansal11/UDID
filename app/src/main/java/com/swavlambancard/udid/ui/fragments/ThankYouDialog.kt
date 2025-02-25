package com.swavlambancard.udid.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import com.swavlambancard.udid.R
import com.swavlambancard.udid.databinding.DialogThankYouBinding
import com.swavlambancard.udid.model.UserData
import com.swavlambancard.udid.utilities.AppConstants
import com.swavlambancard.udid.utilities.BaseActivity
import com.swavlambancard.udid.utilities.BaseActivity.Companion
import com.swavlambancard.udid.utilities.JSEncryptService
import com.swavlambancard.udid.utilities.Preferences.getPreferenceOfLogin
import com.swavlambancard.udid.utilities.Utility.showSnackbar
import com.swavlambancard.udid.utilities.toast
import com.swavlambancard.udid.viewModel.ViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File

class ThankYouDialog(private val applicationNumber:String) : DialogFragment() {

    private var mBinding: DialogThankYouBinding? = null
    private var viewModel = ViewModel()
    private val binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DialogThankYouBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.window?.apply {
            statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        mBinding?.clickAction = ClickActions()
        viewModel.init()
        mBinding?.tvEnrollmentNo?.text = "Enrollment No: $applicationNumber"
        observer()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawableResource(android.R.color.white)  // Ensure white background
            setGravity(Gravity.CENTER)
            clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }
    }

    private fun observer(){
        viewModel.downloadResult.observe(this) { result ->
            result.onSuccess { file ->
                openPDF(file)
            }.onFailure { error ->
                requireContext().toast("Download failed: ${error.message}")
            }
        }

        viewModel.errors.observe(this) {
            mBinding?.flParent?.let { it1 -> showSnackbar(it1, it) }
        }
    }
    inner class ClickActions {
        fun close(view: View){
            dismiss()
        }

        fun downloadApplication(view: View){
            startDownload(getString(R.string.application))
//            mBinding?.flParent?.let { showSnackbar(it,"Downloaded") }
        }

        fun downloadReceipt(view: View){
            startDownload(getString(R.string.receipt))
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        activity?.window?.apply {
            statusBarColor = ContextCompat.getColor(requireContext(), R.color.DarkBlue)
            // Set light status bar icons
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        mBinding = null
    }

    fun startDownload(isFrom: String) {
        val requestBody = JSONObject().apply {
            put(
                "application_number", JSEncryptService.encrypt(
                    getPreferenceOfLogin(
                        BaseActivity.context, AppConstants.LOGIN_DATA, UserData::class.java
                    ).application_number.toString()
                )
            )
            put("type", "mobile")
        }.toString()
        if (isFrom == getString(R.string.application)) viewModel.downloadApplication(
            requireContext(), requestBody.toRequestBody("application/json".toMediaType())
        )
        else if (isFrom == getString(R.string.receipt)) viewModel.downloadReceipt(
            requireContext(), requestBody.toRequestBody("application/json".toMediaType())
        )
    }

    private fun openPDF(pdfFile: File) {
        try {
            // Create a URI for the PDF file using FileProvider for secure file access
            val pdfUri: Uri = FileProvider.getUriForFile(
                requireContext(),
                "${requireActivity().packageName}.fileprovider", // Ensure your app's FileProvider is correctly configured
                pdfFile
            )

            // Create an Intent to open the PDF file
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(pdfUri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Grant permission for the PDF viewer
            }

            // Start the activity to view the PDF
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle the exception (e.g., show a toast if no PDF viewer is available)
        }
    }
}

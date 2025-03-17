package com.swavlambancard.udid.services

import com.swavlambancard.udid.model.ApplicationStatusRequest
import com.swavlambancard.udid.model.ApplicationStatusResponse
import com.swavlambancard.udid.model.CodeDropDownRequest
import com.swavlambancard.udid.model.CommonResponse
import com.swavlambancard.udid.model.DropDownRequest
import com.swavlambancard.udid.model.DropDownResponse
import com.swavlambancard.udid.model.EditProfileRequest
import com.swavlambancard.udid.model.EditProfileResponse
import com.swavlambancard.udid.model.GenerateOtpRequest
import com.swavlambancard.udid.model.LoginResponse
import com.swavlambancard.udid.model.MyAccountResponse
import com.swavlambancard.udid.model.OTPResponse
import com.swavlambancard.udid.model.PendingApplicationWise
import com.swavlambancard.udid.model.PincodeRequest
import com.swavlambancard.udid.model.RejectAndPendingResponse
import com.swavlambancard.udid.model.RejectApplicationRequest
import com.swavlambancard.udid.model.SavePWDFormResponse
import com.swavlambancard.udid.model.UploadFileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Streaming

const val LOGIN = "login"
const val GENERATE_OTP_LOGIN = "getotp"
const val MY_ACCOUNT = "myaccount"
const val APP_STATUS = "getAppapplicationstatus"
const val DROP_DOWN = "getDropdown"
const val UPDATE_NAME = "updateName"
const val UPDATE_MOBILE = "updateMobile"
const val UPDATE_AADHAAR = "updateAdhar"
const val UPDATE_DOB = "updateDOB"
const val UPDATE_EMAIL = "updateEmail"
const val SURRENDER_CARD = "surender"
const val LOST_CARD = "reIssuance"
const val FEEDBACK_QUERY = "contactUs"
const val APPEAL = "saveAppeal"
const val RENEW_CARD = "renewcard"
const val LOGOUT = "logout"
const val DOWNLOAD_APPLICATION = "downloadApplication"
const val DOWNLOAD_RECEIPT = "downloadApplicationReciept"
const val DOWNLOAD_YOUR_E_DISABILITY_CERTIFICATE = "downloadCertificate"
const val DOWNLOAD_YOUR_UDID_CARD = "downloadUdidCard"
const val DOWNLOAD_DOCTOR_DIAGNOSIS_SHEET = "downloadDoctorDiagonstics"
const val SUBMIT_UPDATED_NAME = "downloadUpdateNameReciept"
const val SUBMIT_AADHAAR_NUMBER = "downloadUpdateNameReciept"
const val SUBMIT_MOBILE_NUMBER = "downloadUpdateNameReciept"
const val SUBMIT_EMAIL_ID = "downloadUpdateNameReciept"
const val SUBMIT_DATE_OF_BIRTH = "downloadUpdateNameReciept"
const val SUBMIT_RENEWAL_CARD = "downloadRenewalReciept"
const val SUBMIT_APPEAL = "downloadAppealReciept"
const val SUBMIT_SURRENDER_CARD = "downloadUpdateSurrenderReciept"
const val SUBMIT_LOST_CARD = "downloadUpdateLostCardReciept"
const val GET_CODE_DROP_DOWN = "getCodeDropdown"
const val PINCODE_DROP_DOWN = "getPincodeDropdown"
const val UPLOAD_FILE = "uploadFile"
//const val EDIT_APPLICATION = "editApplication"
const val EDIT_APPLICATION = "editApplication2"
const val SAVE_PWD_FORM = "savePWDForm"
const val UPDATE_PWD_FORM = "updatePWDForm"
//const val SAVE_PWD_FORM = "savePWDForm2"
//const val UPDATE_PWD_FORM = "updatePWDForm2"
const val APPLICATION_REJECT_REQUEST = "applicationRejectRequest"
const val PENDING_APPLICATION_WISE = "pendingapplicationwise"
const val DOWNLOAD_REJECTION_LETTER = "rejectionCertificate"

interface MyService {

    @Headers("Content-Type: text/plain")
    @POST(LOGIN)
    suspend fun getLogin(@Body request: RequestBody): Response<LoginResponse>

    @Headers("Content-Type: application/json")
    @POST(GENERATE_OTP_LOGIN)
    suspend fun getOtpLogin(@Body request: GenerateOtpRequest): Response<OTPResponse>

    @Headers("Content-Type: text/plain")
    @POST(MY_ACCOUNT)
    suspend fun getMyAccount(@Body request: RequestBody): Response<MyAccountResponse>

    @Headers("Content-Type: application/json")
    @POST(APP_STATUS)
    suspend fun getAppStatus(@Body request: ApplicationStatusRequest): Response<ApplicationStatusResponse>

    @Headers("Content-Type: application/json")
    @POST(DROP_DOWN)
    suspend fun getDropDown(@Body request: DropDownRequest): Response<DropDownResponse>

    @Headers("Content-Type: application/json")
    @POST(GET_CODE_DROP_DOWN)
    suspend fun getCodeDropDown(@Body request: CodeDropDownRequest): Response<DropDownResponse>

    @Headers("Content-Type: application/json")
    @POST(PINCODE_DROP_DOWN)
    suspend fun getPincodeDropDown(@Body request: PincodeRequest): Response<DropDownResponse>

    @Multipart
    @POST(UPDATE_NAME)
    suspend fun updateName(
        @Part("application_number") applicationNumber: RequestBody?,
        @Part("name") name: RequestBody?,
        @Part("name_i18n") nameRegionalLanguage: RequestBody?,
        @Part("reason") reason: RequestBody?,
        @Part("address_proof_id") addressProofId: RequestBody?,
        @Part("other_reason") otherReason: RequestBody?,
        @Part("otp") otp: RequestBody?,
        @Part("type") type: RequestBody?,
        @Part document: MultipartBody.Part?,
    ): Response<CommonResponse>

    @Multipart
    @POST(UPDATE_MOBILE)
    @Headers("Content-Type: application/json")
    suspend fun updateMobile(
        @Part("application_number") applicationNumber: RequestBody?,
        @Part("mobile") mobile: RequestBody?,
        @Part("otp") otp: RequestBody?,
        @Part("type") type: RequestBody?,
    ): Response<CommonResponse>

    @Multipart
    @POST(UPDATE_AADHAAR)
    suspend fun updateAadhaar(
        @Part("application_number") applicationNumber: RequestBody?,
        @Part("aadhaar_no") aadhaarNo: RequestBody?,
        @Part("address_proof_id") addressProofId: RequestBody?,
        @Part("reason") reason: RequestBody?,
        @Part("other_reason") otherReason: RequestBody?,
        @Part("otp") otp: RequestBody?,
        @Part("type") type: RequestBody?,
        @Part document: MultipartBody.Part?,
    ): Response<CommonResponse>

    @Multipart
    @POST(UPDATE_DOB)
    suspend fun updateDob(
        @Part("application_number") applicationNumber: RequestBody?,
        @Part("dob") dob: RequestBody?,
        @Part("reason") reason: RequestBody?,
        @Part("other_reason") otherReason: RequestBody?,
        @Part("otp") otp: RequestBody?,
        @Part("type") type: RequestBody?,
        @Part document: MultipartBody.Part?,
    ): Response<CommonResponse>

    @Multipart
    @POST(UPDATE_EMAIL)
    @Headers("Content-Type: application/json")
    suspend fun updateEmail(
        @Part("application_number") applicationNumber: RequestBody?,
        @Part("email") email: RequestBody?,
        @Part("otp") otp: RequestBody?,
        @Part("type") type: RequestBody?,
    ): Response<CommonResponse>

    @Multipart
    @Headers("Content-Type: application/json")
    @POST(SURRENDER_CARD)
    suspend fun surrenderCard(
        @Part("application_number") applicationNumber: RequestBody?,
        @Part("reason") reason: RequestBody?,
        @Part("other_reason") otherReason: RequestBody?,
        @Part("otp") otp: RequestBody?,
        @Part("type") type: RequestBody?,
    ): Response<CommonResponse>

    @Multipart
    @POST(LOST_CARD)
    suspend fun lostCard(
        @Part("application_number") applicationNumber: RequestBody?,
        @Part("reason") reason: RequestBody?,
        @Part("other_reason") otherReason: RequestBody?,
        @Part("otp") otp: RequestBody?,
        @Part("type") type: RequestBody?,
        @Part document: MultipartBody.Part?,
    ): Response<CommonResponse>

    @Multipart
    @POST(FEEDBACK_QUERY)
    suspend fun feedBack(
        @Part("full_name") fullName: RequestBody?,
        @Part("mobile") mobile: RequestBody?,
        @Part("subject") subject: RequestBody?,
        @Part("email") email: RequestBody?,
        @Part("message") message: RequestBody?,
        @Part("type") type: RequestBody?,
        @Part document: MultipartBody.Part?,
    ): Response<CommonResponse>

    @Multipart
    @POST(APPEAL)
    suspend fun appeal(
        @Part("application_number") applicationNumber: RequestBody?,
        @Part("reason") reason: RequestBody?,
        @Part("type") type: RequestBody?,
        @Part document: MultipartBody.Part?,
    ): Response<CommonResponse>

    @Multipart
    @POST(RENEW_CARD)
    suspend fun getRenewCard(
        @Part("application_number") applicationNumber: RequestBody?,
        @Part("renewal_type") renewalType: RequestBody?,
        @Part("current_address") currentAddress: RequestBody?,
        @Part("hospital_treating_state_code") hospitalTreatingStateCode: RequestBody?,
        @Part("hospital_treating_district_code") hospitalTreatingDistrictCode: RequestBody?,
        @Part("hospital_treating_sub_district_code") hospitalTreatingSubDistrictCode: RequestBody?,
        @Part("current_pincode") currentPincode: RequestBody?,
        @Part("hospital_treating_id") hospitalTreatingId: RequestBody?,
        @Part("type") type: RequestBody?,
        @Part address_proof_file: MultipartBody.Part?,
    ): Response<CommonResponse>

    @Multipart
    @POST(LOGOUT)
    suspend fun logout(
        @Part("application_number") applicationNumber: RequestBody?,
        @Part("type") type: RequestBody?,
    ): Response<CommonResponse>

    @Headers("Content-Type: application/json")
    @POST(DOWNLOAD_APPLICATION)
    suspend fun downloadApplication(@Body request: RequestBody): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST(DOWNLOAD_RECEIPT)
    suspend fun downloadReceipt(@Body request: RequestBody): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST(DOWNLOAD_YOUR_E_DISABILITY_CERTIFICATE)
    suspend fun downloadEDisabilityCertificate(@Body request: RequestBody): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST(DOWNLOAD_YOUR_UDID_CARD)
    suspend fun downloadUdidCard(@Body request: RequestBody): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST(DOWNLOAD_DOCTOR_DIAGNOSIS_SHEET)
    suspend fun downloadDoctorDiagnosisSheet(@Body request: RequestBody): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST(SUBMIT_UPDATED_NAME)
    suspend fun downloadUpdatedName(@Body request: RequestBody): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST(SUBMIT_AADHAAR_NUMBER)
    suspend fun downloadAadhaarNumber(@Body request: RequestBody): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST(SUBMIT_DATE_OF_BIRTH)
    suspend fun downloadDateOfBirth(@Body request: RequestBody): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST(SUBMIT_EMAIL_ID)
    suspend fun downloadEmailId(@Body request: RequestBody): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST(SUBMIT_MOBILE_NUMBER)
    suspend fun downloadMobileNumber(@Body request: RequestBody): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST(SUBMIT_APPEAL)
    suspend fun downloadAppeal(@Body request: RequestBody): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST(SUBMIT_RENEWAL_CARD)
    suspend fun downloadRenewalCard(@Body request: RequestBody): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST(SUBMIT_SURRENDER_CARD)
    suspend fun downloadSurrenderCard(@Body request: RequestBody): Response<ResponseBody>

   @Headers("Content-Type: application/json")
    @POST(SUBMIT_LOST_CARD)
    suspend fun downloadLostCard(@Body request: RequestBody): Response<ResponseBody>

    @Multipart
    @POST(UPLOAD_FILE)
    suspend fun getUploadFile(
        @Part("document_type") documentType: RequestBody?,
        @Part("type") type: RequestBody?,
        @Part document: MultipartBody.Part?,
    ): Response<UploadFileResponse>

    @Streaming
    @Headers("Content-Type: application/json")
    @POST(EDIT_APPLICATION)
    suspend fun editApplication(@Body request: EditProfileRequest): Response<EditProfileResponse>

    @Multipart
    @POST(SAVE_PWD_FORM)
    suspend fun savePwdForm(
        //Personal Details
        @Part("type") type: RequestBody?,
        @Part("application_number") applicationNumber: RequestBody?,
        @Part("full_name") fullName: RequestBody?,
        @Part("full_name_i18n") regionalFullName: RequestBody?,
        @Part("regional_language") regionalLanguage: RequestBody?,
        @Part("mobile") mobile: RequestBody?,
        @Part("email") email: RequestBody?,
        @Part("dob") dob: RequestBody?,
        @Part("gender") gender: RequestBody?,//=>M/F/T
        @Part("guardian_relation") guardianRelation: RequestBody?,//Mother/Father/Guardian
        @Part("relation_pwd") relationPwd: RequestBody?,//Mother/Father/Guardian
        @Part("father_name") fatherName: RequestBody?,
        @Part("mother_name") motherName: RequestBody?,
        @Part("guardian_name") guardianName: RequestBody?,
        @Part("guardian_contact") guardianContact: RequestBody?,
        @Part("photo") photo: RequestBody?,
        @Part("signature_thumb_print") sign: RequestBody?,
        // Proof id Identity Card
        @Part("aadhaar_no") aadhaarNo: RequestBody?,
        @Part("share_aadhar_info") shareAadhaarInfo: RequestBody?,//0/1
        @Part("aadhar_info") aadhaarInfo: RequestBody?,//=> Yes(1)/No(0)
        @Part("aadhar_enrollment_no") aadhaarEnrollmentNo: RequestBody?,
        @Part("aadhar_enrollment_slip") aadhaarEnrollmentSlip: RequestBody?,
        @Part("identitity_proof_id") identityProofId: RequestBody?,
        @Part("identitity_proof_file") identityProofFile: RequestBody?,
        //Address For Correspondence
        @Part("address_proof_id") addressProofId: RequestBody?,
        @Part("address_proof_file") addressProofFile: RequestBody?,
        @Part("current_address") currentAddress: RequestBody?,
        @Part("current_state_code") currentStateCode: RequestBody?,
        @Part("current_district_code") currentDistrictCode: RequestBody?,
        @Part("current_subdistrict_code") currentSubDistrictCode: RequestBody?,
        @Part("current_village_code") currentVillageCode: RequestBody?,
        @Part("current_pincode") currentPincode: RequestBody?,
        //Disability Details
        @Part("disability_type_id") disabilityTypeId: RequestBody?,
        @Part("disability_due_to") disabilityDueTo: RequestBody?,
        @Part("disability_since_birth") disabilitySinceBirth: RequestBody?,//Since(No)/Birth(Yes)
        @Part("disability_since") disabilitySince: RequestBody?,
        @Part("have_disability_cert") haveDisabilityCert: RequestBody?,//1(yes)/0(no)
        @Part("disability_cert_doc") disabilityCertDoc: RequestBody?,
        @Part("serial_number") serialNumber: RequestBody?,
        @Part("date_of_certificate") dateOfCertificate: RequestBody?,
        @Part("detail_of_authority") detailOfAuthority: RequestBody?,
        @Part("disability_per") disabilityPer: RequestBody?,
        //Hospital for assessment
        @Part("is_hospital_treating_other_state") isHospitalTreatingOtherState: RequestBody?,//=> 0/1
        @Part("hospital_treating_state_code") hospitalTreatingStateCode: RequestBody?,
        @Part("hospital_treating_district_code") hospitalTreatingDistrictCode: RequestBody?,
        @Part("hospital_treating_id") hospitalTreatingId: RequestBody?,
        @Part("declaration") declaration: RequestBody?,//=>0/1
    ): Response<SavePWDFormResponse>

    @Multipart
    @POST(UPDATE_PWD_FORM)
    suspend fun updatePwdForm(
        @Part("type") type: RequestBody?,
        //Personal Details
        @Part("application_number") applicationNumber: RequestBody?,
        @Part("full_name") fullName: RequestBody?,
        @Part("full_name_i18n") regionalFullName: RequestBody?,
        @Part("regional_language") regionalLanguage: RequestBody?,
        @Part("mobile") mobile: RequestBody?,
        @Part("email") email: RequestBody?,
        @Part("dob") dob: RequestBody?,
        @Part("gender") gender: RequestBody?,//=>M/F/T
        @Part("guardian_relation") guardianRelation: RequestBody?,//Mother/Father/Guardian
        @Part("relation_pwd") relationPwd: RequestBody?,//Mother/Father/Guardian
        @Part("father_name") fatherName: RequestBody?,
        @Part("mother_name") motherName: RequestBody?,
        @Part("guardian_name") guardianName: RequestBody?,
        @Part("guardian_contact") guardianContact: RequestBody?,
        @Part("photo") photo: RequestBody?,
        @Part("signature_thumb_print") sign: RequestBody?,
        // Proof id Identity Card
        @Part("aadhaar_no") aadhaarNo: RequestBody?,
        @Part("share_aadhar_info") shareAadhaarInfo: RequestBody?,//0/1
        @Part("aadhar_info") aadhaarInfo: RequestBody?,//=> Yes(1)/No(0)
        @Part("aadhar_enrollment_no") aadhaarEnrollmentNo: RequestBody?,
        @Part("aadhar_enrollment_slip") aadhaarEnrollmentSlip: RequestBody?,
        @Part("identitity_proof_id") identityProofId: RequestBody?,
        @Part("identitity_proof_file") identityProofFile: RequestBody?,
        //Address For Correspondence
        @Part("address_proof_id") addressProofId: RequestBody?,
        @Part("address_proof_file") addressProofFile: RequestBody?,
        @Part("current_address") currentAddress: RequestBody?,
        @Part("current_state_code") currentStateCode: RequestBody?,
        @Part("current_district_code") currentDistrictCode: RequestBody?,
        @Part("current_subdistrict_code") currentSubDistrictCode: RequestBody?,
        @Part("current_village_code") currentVillageCode: RequestBody?,
        @Part("current_pincode") currentPincode: RequestBody?,
        //Disability Details
        @Part("disability_type_id") disabilityTypeId: RequestBody?,
        @Part("disability_due_to") disabilityDueTo: RequestBody?,
        @Part("disability_since_birth") disabilitySinceBirth: RequestBody?,//Since(No)/Birth(Yes)
        @Part("disability_since") disabilitySince: RequestBody?,
        @Part("have_disability_cert") haveDisabilityCert: RequestBody?,//1(yes)/0(no)
        @Part("disability_cert_doc") disabilityCertDoc: RequestBody?,
        @Part("serial_number") serialNumber: RequestBody?,
        @Part("date_of_certificate") dateOfCertificate: RequestBody?,
        @Part("detail_of_authority") detailOfAuthority: RequestBody?,
        @Part("disability_per") disabilityPer: RequestBody?,
        //Hospital for assessment
        @Part("is_hospital_treating_other_state") isHospitalTreatingOtherState: RequestBody?,//=> 0/1
        @Part("hospital_treating_state_code") hospitalTreatingStateCode: RequestBody?,
        @Part("hospital_treating_district_code") hospitalTreatingDistrictCode: RequestBody?,
        @Part("hospital_treating_id") hospitalTreatingId: RequestBody?,
    ): Response<SavePWDFormResponse>

    @Headers("Content-Type: application/json")
    @POST(APPLICATION_REJECT_REQUEST)
    suspend fun rejectApplicationRequest(@Body request: RejectApplicationRequest): Response<RejectAndPendingResponse>

    @Headers("Content-Type: application/json")
    @POST(PENDING_APPLICATION_WISE)
    suspend fun pendingApplicationWise(@Body request: PendingApplicationWise): Response<RejectAndPendingResponse>

    @Headers("Content-Type: application/json")
    @POST(DOWNLOAD_REJECTION_LETTER)
    suspend fun downloadRejectionLetter(@Body request: RequestBody): Response<ResponseBody>
}






package com.swavlambancard.udid.model

import java.io.Serializable

data class CommonResponse(
    val _resultflag: Int,
    val message: String
)

data class LoginResponse(
    val _result: Result,
    val _resultflag: Int,
    val message: String
)

data class Result(
    val data: String,
    val token: String
)

data class MyAccountResponse(
    val _result: String,
    val _resultflag: Int,
    val message: String
)

data class DropDownResponse(
    val _result: List<DropDownResult>,
    val _resultflag: Int,
    val message: String,
    val model: String,
    val total_count: Int
)

data class DropDownResult(
    var id: String,
    var name: String
):Serializable

data class OTPResponse(
    val _resultflag: Int,
    val message: String,
    val otp: String
)

data class PwdApplicationStatus(
    val status_name: String
):Serializable


data class ApplicationStatusResponse(
    val _resultflag: Int,
    val applicationstatus: List<ApplicationStatus>,
    val message: String,
    val statusArray: List<StatusArray>
)

data class ApplicationStatus(
    val Pwdapplicationstatus: PwdApplicationStatus,
    val pwd_application_number: String,
    val pwd_application_status: Int,
    val remark: Any
)

data class StatusArray(
    val id: Int,
    val status_name: String
): Serializable

data class UserData(
    val login_status: Boolean?,
    val id: Int?,
    val application_number: String?,
    val udid_number: String?,
    val regional_language: String?,
    val full_name_i18n: String?,
    val full_name: String?,
    val father_name: String?,
    val dob: String?,
    val gender: String?,
    val mobile: Long?,
    val email: String?,
    val photo: String?,
    val current_address: String?,
    val current_state_code: Int?,
    val current_district_code: Int?,
    val current_subdistrict_code: Int?,
    val current_village_code: Int?,
    val current_pincode: Int?,
    val disability_type_id: String?,
    val application_status: Int?,
    val certificate_generate_date: String?,
    val rejected_date: String?,
    val disability_type_pt: String?,
    val pwd_card_expiry_date: String?,
    val aadhaar_no: String?,
    val transfer_date: String?,
    val pwdapplicationstatus: UserDataPwdApplicationStatus,
    val pwddispatch: PwdDispatch?,
    val photo_path: String?,
    val appealrequest: Int?,
    val renewalrequest: Int?,
    val surrenderrequest: Int?,
    val lostcardrequest: Int?,
    val updaterequest: UpdateRequest?
)

data class MyAccountData(
    val aadhaar_no: String?,
    val application_number: String?,
    val application_status: Int?,
    val certificate_generate_date: String?,
    val current_address: String?,
    val current_pincode: Int?,
    val disability_condition_category: String?,
    val disability_type_id: String?,
    val disability_type_pt: String?,
    val disability_types: String?,
    val district: District?,
    val dob: String?,
    val email: String?,
    val father_name: String?,
    val final_disability_percentage: Int?,
    val full_name: String?,
    val full_name_i18n: String?,
    val gender: String?,
    val have_disability_cert: Boolean?,
    val hospital: Hospital?,
    val hospital_district: HospitalDistrict?,
    val hospital_state: HospitalState?,
    val mobile: Long?,
    val photo: String?,
    val photo_path: String?,
    val pwd_card_expiry_date: String?,
    val pwdapplicationstatus: PwdApplicationStatus?,
    val reject_remark: String?,
    val state: State?,
    val subdistrict: Subdistrict?,
    val udid_number: String?
)

data class District(
    val district_name: String
)

data class Hospital(
    val hospital_name: String
)

data class HospitalDistrict(
    val district_name: String
)

data class HospitalState(
    val name: String
)
data class Village(
    val village_name:String?
)

data class State(
    val name: String
)

data class Subdistrict(
    val subdistrict_name: String
)

data class UserDataPwdApplicationStatus(
    val status_name: String
)

data class PwdDispatch(
    val dispatch_date: String?
)

data class UpdateRequest(
    val Name: Int?,
    val Email: Int?,
    val Mobile: Int?,
    val AadhaarNumber: Int?,
    val DateOfBirth: Int?
)

data class UploadFileResponse(
    val _result: UploadFileResult,
    val _resultflag: Int,
    val message: String
)

data class UploadFileResult(
    val file_name: String
)

data class EditProfileResponse(
    val _result: String,
    val _resultflag: Int,
    val message: String
)

data class EditApplication(
    val aadhaar_no: String,
    val aadhar_enrollment_no: String,
    val aadhar_enrollment_slip: String,
    val aadhar_info: Int,
    val address_proof_file: String,
    val address_proof_id: String,
    val application_number: String,
    val application_status: Int,
    val certificate_generate_date: String,
    val current_address: String,
    val current_district_code: String,
    val current_pincode: String,
    val current_state_code: String,
    val current_subdistrict_code: String,
    val current_village_code: String,
    val date_of_certificate: String,
    val detail_of_authority: String,
    val disability_cert_doc: String,
    val disability_due_to: String,
    val disability_per: String,
    val disability_since: String,
    val disability_since_birth: String,
    val disability_type_id: String,
    val disability_types: String,
    val district: District,
    val dob: String,
    val email: String,
    val employment_status: Any,
    val father_name: String,
    val final_disability_percentage: Any,
    val full_name: String,
    val full_name_i18n: String,
    val gender: String,
    val guardian_contact: String,
    val guardian_name: String,
    val guardian_relation: String,
    val have_disability_cert: Boolean,
    val hospital: Hospital,
    val hospital_district: HospitalDistrict,
    val village: Village?,
    val hospital_state: HospitalState,
    val hospital_treating_district_code: String,
    val hospital_treating_id: String,
    val hospital_treating_state_code: String,
    val identitity_proof_file: String,
    val identitity_proof_id: String,
    val income: Long,
    val is_hospital_treating_other_state: Boolean,
    val mobile: String,
    val mother_name: String,
    val photo: String,
    val pwd_card_expiry_date: String,
    val pwdapplicationstatus: UserDataPwdApplicationStatus,
    val qualification_id: Any,
    val regional_language: String,
    val relation_pwd: String,
    val serial_number: String,
    val share_aadhar_info: Int,
    val signature_thumb_print: String,
    val state: State,
    val subdistrict: Subdistrict
)

data class SavePWDFormResponse(
    val _result: ApplicationNoResult,
    val _resultflag: Int,
    val message: String,
)

data class ApplicationNoResult(
    val application_number: String
)

data class RejectAndPendingResponse(
    val _result: List<RejectAndPendingResult>,
    val _resultflag: Int,
    val message: String
)

data class RejectAndPendingResult(
    val api_request_type: String,
    val application_number: String,
    val application_type: Int,
    val id: Int
)


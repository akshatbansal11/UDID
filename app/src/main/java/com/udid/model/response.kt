package com.udid.model

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
    val id: String,
    val name: String
)


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
    val rejected_date: String??,
    val disability_type_pt: String?,
    val pwd_card_expiry_date: String??,
    val aadhaar_no: String?,
    val transfer_date: String??,
    val pwdapplicationstatus: UserDataPwdApplicationStatus,
    val pwddispatch: PwdDispatch?,
    val photo_path: String?,
    val appealrequest: Int?,
    val renewalrequest: Int?,
    val surrenderrequest: Int?,
    val lostcardrequest: Int?,
    val updaterequest: UpdateRequest
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

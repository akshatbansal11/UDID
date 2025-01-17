package com.udid.services

import com.udid.model.ApplicationStatusRequest
import com.udid.model.ApplicationStatusResponse
import com.udid.model.CommonResponse
import com.udid.model.DropDownRequest
import com.udid.model.DropDownResponse
import com.udid.model.GenerateOtpRequest
import com.udid.model.LoginResponse
import com.udid.model.MyAccountResponse
import com.udid.model.OTPResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

const val LOGIN = "login"
const val GENERATE_OTP_LOGIN = "getotp"
const val MY_ACCOUNT = "myaccount"
const val APP_STATUS = "getAppapplicationstatus "
const val DROP_DOWN = "getDropdown"
const val UPDATE_NAME = "updateName"
const val UPDATE_MOBILE = "updateMobile"
const val UPDATE_AADHAAR = "updateAdhar"
const val UPDATE_DOB = "updateDOB"
const val UPDATE_EMAIL = "updateEmail"
const val SURRENDER_CARD = "surender"
const val LOST_CARD = "reIssuance"
const val FEEDBACK_QUERY = "contactUs"

interface MyService {

    @POST(LOGIN)
    suspend fun getLogin(@Body request: String): Response<LoginResponse>
    @POST(GENERATE_OTP_LOGIN)
    suspend fun getOtpLogin(@Body request: GenerateOtpRequest): Response<OTPResponse>
    @POST(MY_ACCOUNT)
    suspend fun getMyAccount(@Body request: String): Response<MyAccountResponse>
    @POST(APP_STATUS)
    suspend fun getAppStatus(@Body request: ApplicationStatusRequest): Response<ApplicationStatusResponse>
    @POST(DROP_DOWN)
    suspend fun getDropDown(@Body request: DropDownRequest): Response<DropDownResponse>

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
        @Part document: MultipartBody.Part?,
    ):Response<CommonResponse>

    @Multipart
    @POST(UPDATE_MOBILE)
    suspend fun updateMobile(
        @Part("application_number") applicationNumber: RequestBody?,
        @Part("mobile") mobile: RequestBody?,
        @Part("otp") otp: RequestBody?
    ):Response<CommonResponse>

    @Multipart
    @POST(UPDATE_AADHAAR)
    suspend fun updateAadhaar(
        @Part("application_number") applicationNumber: RequestBody?,
        @Part("aadhaar_no") aadhaarNo: RequestBody?,
        @Part("address_proof_id") addressProofId: RequestBody?,
        @Part("reason") reason: RequestBody?,
        @Part("other_reason") otherReason: RequestBody?,
        @Part("otp") otp: RequestBody?,
        @Part document: MultipartBody.Part?,
    ):Response<CommonResponse>

    @Multipart
    @POST(UPDATE_DOB)
    suspend fun updateDob(
        @Part("application_number") applicationNumber: RequestBody?,
        @Part("dob") dob: RequestBody?,
        @Part("reason") reason: RequestBody?,
        @Part("other_reason") otherReason: RequestBody?,
        @Part("otp") otp: RequestBody?,
        @Part document: MultipartBody.Part?,
    ):Response<CommonResponse>

    @Multipart
    @POST(UPDATE_EMAIL)
    suspend fun updateEmail(
        @Part("application_number") applicationNumber: RequestBody?,
        @Part("email") email: RequestBody?,
        @Part("otp") otp: RequestBody?
    ):Response<CommonResponse>

    @Multipart
    @POST(SURRENDER_CARD)
    suspend fun surrenderCard(
        @Part("application_number") applicationNumber: RequestBody?,
        @Part("reason") reason: RequestBody?,
        @Part("other_reason") otherReason: RequestBody?,
        @Part("otp") otp: RequestBody?,
    ):Response<CommonResponse>

    @Multipart
    @POST(LOST_CARD)
    suspend fun lostCard(
        @Part("application_number") applicationNumber: RequestBody?,
        @Part("reason") reason: RequestBody?,
        @Part("other_reason") otherReason: RequestBody?,
        @Part("otp") otp: RequestBody?,
        @Part document: MultipartBody.Part?
    ):Response<CommonResponse>

    @Multipart
    @POST(FEEDBACK_QUERY)
    suspend fun feedBack(
        @Part("full_name") fullName: RequestBody?,
        @Part("mobile") mobile: RequestBody?,
        @Part("subject") subject: RequestBody?,
        @Part("email") email: RequestBody?,
        @Part("message") message: RequestBody?,
        @Part document: MultipartBody.Part?
    ):Response<CommonResponse>
}


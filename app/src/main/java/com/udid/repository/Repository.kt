package com.udid.repository

import com.udid.model.ApplicationStatusRequest
import com.udid.model.ApplicationStatusResponse
import com.udid.model.CommonResponse
import com.udid.model.DropDownRequest
import com.udid.model.DropDownResponse
import com.udid.model.GenerateOtpRequest
import com.udid.model.LoginResponse
import com.udid.model.MyAccountResponse
import com.udid.model.OTPResponse
import com.udid.services.MyService
import com.udid.services.ServiceGenerator
import com.udid.services.ServiceGeneratorLogin
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response

object Repository {

    private var repository: Repository? = null
    private lateinit var api: MyService
    private lateinit var apiLogin: MyService

    val instance: Repository
        get() {
            repository = Repository

            api = ServiceGenerator.createService(MyService::class.java)

            apiLogin = ServiceGeneratorLogin.createServiceLogin(MyService::class.java)
            return repository!!
        }

    suspend fun getLogin(request: RequestBody): Response<LoginResponse> {
        return apiLogin.getLogin(request)
    }

    suspend fun getMyAccount(request: RequestBody): Response<MyAccountResponse> {
        return api.getMyAccount(request)
    }

    suspend fun getGenerateOtpLogin(request: GenerateOtpRequest): Response<OTPResponse> {
        return api.getOtpLogin(request)
    }

    suspend fun getAppStatus(request: ApplicationStatusRequest): Response<ApplicationStatusResponse> {
        return api.getAppStatus(request)
    }

    suspend fun getDropDown(request: DropDownRequest): Response<DropDownResponse> {
        return api.getDropDown(request)
    }

    suspend fun updateName(
        applicationNumber: RequestBody?,
        name: RequestBody?,
        nameRegionalLanguage: RequestBody?,
        reason: RequestBody?,
        addressProofId: RequestBody?,
        otherReason: RequestBody?,
        otp: RequestBody?,
        type: RequestBody?,
        document: MultipartBody.Part?,
    ): Response<CommonResponse> {
        return api.updateName(
            applicationNumber,
            name,
            nameRegionalLanguage,
            reason,
            addressProofId,
            otherReason,
            otp,
            type,
            document,
        )
    }

    suspend fun updateMobile(
        applicationNumber: RequestBody?,
        mobile: RequestBody?,
        otp: RequestBody?,
        type: RequestBody?
    ): Response<CommonResponse> {
        return api.updateMobile(
            applicationNumber,
            mobile,
            otp,
            type
        )
    }

    suspend fun updateAadhaar(
        applicationNumber: RequestBody?,
        aadhaarNo: RequestBody?,
        addressProofId: RequestBody?,
        reason: RequestBody?,
        otherReason: RequestBody?,
        otp: RequestBody?,
        type: RequestBody?,
        document: MultipartBody.Part?,
    ): Response<CommonResponse> {
        return api.updateAadhaar(
            applicationNumber,
            aadhaarNo,
            addressProofId,
            reason,
            otherReason,
            otp,
            type,
            document
        )
    }

    suspend fun updateDob(
        applicationNumber: RequestBody?,
        dob: RequestBody?,
        reason: RequestBody?,
        otherReason: RequestBody?,
        otp: RequestBody?,
        type: RequestBody?,
        document: MultipartBody.Part?,
    ): Response<CommonResponse> {
        return api.updateDob(
            applicationNumber,
            dob,
            reason,
            otherReason,
            otp,
            type,
            document
        )
    }

    suspend fun updateEmail(
        applicationNumber: RequestBody?,
        email: RequestBody?,
        otp: RequestBody?,
        type: RequestBody?,
    ): Response<CommonResponse> {
        return api.updateEmail(
            applicationNumber,
            email,
            otp,
            type
        )
    }

    suspend fun surrenderCard(
        applicationNumber: RequestBody?,
        reason: RequestBody?,
        otherReason: RequestBody?,
        otp: RequestBody?,
        type: RequestBody?,
    ): Response<CommonResponse> {
        return api.surrenderCard(
            applicationNumber,
            reason,
            otherReason,
            otp,
            type
        )
    }

    suspend fun lostCard(
        applicationNumber: RequestBody?,
        reason: RequestBody?,
        otherReason: RequestBody?,
        otp: RequestBody?,
        type: RequestBody?,
        document: MultipartBody.Part?,
    ): Response<CommonResponse> {
        return api.lostCard(
            applicationNumber,
            reason,
            otherReason,
            otp,
            type,
            document
        )
    }

    suspend fun feedBack(
        fullName: RequestBody?,
        mobile: RequestBody?,
        subject: RequestBody?,
        email: RequestBody?,
        message: RequestBody?,
        type: RequestBody?,
        document: MultipartBody.Part?,
    ): Response<CommonResponse> {
        return api.feedBack(
            fullName,
            mobile,
            subject,
            email,
            message,
            type,
            document
        )
    }

    suspend fun appeal(
        applicationNumber: RequestBody?,
        reason: RequestBody?,
        type: RequestBody?,
        document: MultipartBody.Part?,
    ): Response<CommonResponse> {
        return api.appeal(
            applicationNumber,
            reason,
            type,
            document
        )
    }

    suspend fun getRenewCard(
        applicationNumber: RequestBody?,
        renewalType: RequestBody?,
        currentAddress: RequestBody?,
        hospitalTreatingStateCode: RequestBody?,
        hospitalTreatingDistrictCode: RequestBody?,
        hospitalTreatingSubDistrictCode: RequestBody?,
        currentPincode: RequestBody?,
        hospitalTreatingId: RequestBody?,
        type: RequestBody?,
        address_proof_file: MultipartBody.Part?
    ): Response<CommonResponse> {
        return api.getRenewCard(
            applicationNumber,
            renewalType,
            currentAddress,
            hospitalTreatingStateCode,
            hospitalTreatingDistrictCode,
            hospitalTreatingSubDistrictCode,
            currentPincode,
            hospitalTreatingId,
            type,
            address_proof_file
        )
    }

    suspend fun logout(
        applicationNumber: RequestBody?,
        type: RequestBody?,
    ): Response<CommonResponse> {
        return api.logout(
            applicationNumber,
            type
        )
    }

    suspend fun downloadApplication(
        request: RequestBody): Response<ResponseBody> {
        return api.downloadApplication(request)
    }
}



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
import retrofit2.Response
import retrofit2.http.Part

object Repository{

    private var repository: Repository? = null
    private lateinit var api: MyService
    private lateinit var apiLogin: MyService

    val instance: Repository
        get() {
            repository = Repository

            api = ServiceGenerator.createService(MyService::class.java)

            apiLogin =  ServiceGeneratorLogin.createServiceLogin(MyService::class.java)
            return repository!!
        }

    suspend fun getLogin(request: String): Response<LoginResponse> {
        return apiLogin.getLogin(request)
    }

    suspend fun getMyAccount(request: String): Response<MyAccountResponse> {
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
        document: MultipartBody.Part?
    ): Response<CommonResponse>{
        return api.updateName(
            applicationNumber,
            name,
            nameRegionalLanguage,
            reason,
            addressProofId,
            otherReason,
            otp,
            document
        )
    }

    suspend fun updateMobile(
        applicationNumber: RequestBody?,
        mobile: RequestBody?,
        otp: RequestBody?
    ): Response<CommonResponse>{
        return api.updateMobile(
            applicationNumber,
            mobile,
            otp
        )
    }

    suspend fun updateAadhaar(
        applicationNumber: RequestBody?,
        aadhaarNo: RequestBody?,
        addressProofId: RequestBody?,
        reason: RequestBody?,
        otherReason: RequestBody?,
        otp: RequestBody?,
        document: MultipartBody.Part?
    ): Response<CommonResponse>{
        return api.updateAadhaar(
            applicationNumber,
            aadhaarNo,
            addressProofId,
            reason,
            otherReason,
            otp,
            document
        )
    }

    suspend fun updateDob(
        applicationNumber: RequestBody?,
        dob: RequestBody?,
        reason: RequestBody?,
        otherReason: RequestBody?,
        otp: RequestBody?,
        document: MultipartBody.Part?
    ): Response<CommonResponse>{
        return api.updateDob(
            applicationNumber,
            dob,
            reason,
            otherReason,
            otp,
            document
        )
    }

    suspend fun updateEmail(
        applicationNumber: RequestBody?,
        email: RequestBody?,
        otp: RequestBody?
    ): Response<CommonResponse>{
        return api.updateEmail(
            applicationNumber,
            email,
            otp
        )
    }

    suspend fun surrenderCard(
        applicationNumber: RequestBody?,
        reason: RequestBody?,
        otherReason: RequestBody?,
        otp: RequestBody?,
    ): Response<CommonResponse>{
        return api.surrenderCard(
            applicationNumber,
            reason,
            otherReason,
            otp
        )
    }

    suspend fun lostCard(
        applicationNumber: RequestBody?,
        reason: RequestBody?,
        otherReason: RequestBody?,
        otp: RequestBody?,
        document: MultipartBody.Part?
    ): Response<CommonResponse>{
        return api.lostCard(
            applicationNumber,
            reason,
            otherReason,
            otp,
            document
        )
    }

    suspend fun feedBack(
        fullName: RequestBody?,
        mobile: RequestBody?,
        subject: RequestBody?,
        email: RequestBody?,
        message: RequestBody?,
        document: MultipartBody.Part?
    ): Response<CommonResponse>{
        return api.feedBack(
            fullName,
            mobile,
            subject,
            email,
            message,
            document
        )
    }
}



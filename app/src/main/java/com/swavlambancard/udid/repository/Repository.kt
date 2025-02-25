package com.swavlambancard.udid.repository

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
import com.swavlambancard.udid.model.PincodeRequest
import com.swavlambancard.udid.model.SavePWDFormResponse
import com.swavlambancard.udid.model.UploadFileResponse
import com.swavlambancard.udid.services.MyService
import com.swavlambancard.udid.services.ServiceGenerator
import com.swavlambancard.udid.services.ServiceGeneratorLogin
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Part

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

    suspend fun getCodeDropDown(request: CodeDropDownRequest): Response<DropDownResponse> {
        return api.getCodeDropDown(request)
    }

    suspend fun getPincodeDropDown(request: PincodeRequest): Response<DropDownResponse> {
        return api.getPincodeDropDown(request)
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
        type: RequestBody?,
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
        address_proof_file: MultipartBody.Part?,
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
        request: RequestBody,
    ): Response<ResponseBody> {
        return api.downloadApplication(request)
    }

    suspend fun downloadReceipt(
        request: RequestBody,
    ): Response<ResponseBody> {
        return api.downloadReceipt(request)
    }

    suspend fun downloadEDisabilityCertificate(
        request: RequestBody,
    ): Response<ResponseBody> {
        return api.downloadEDisabilityCertificate(request)
    }

    suspend fun downloadUdidCard(
        request: RequestBody,
    ): Response<ResponseBody> {
        return api.downloadUdidCard(request)
    }

    suspend fun downloadDoctorDiagnosisSheet(
        request: RequestBody,
    ): Response<ResponseBody> {
        return api.downloadDoctorDiagnosisSheet(request)
    }
    suspend fun downloadUpdatedName(
        request: RequestBody,
    ): Response<ResponseBody> {
        return api.downloadUpdatedName(request)
    }
    suspend fun downloadAadhaarNumber(
        request: RequestBody,
    ): Response<ResponseBody> {
        return api.downloadAadhaarNumber(request)
    }

    suspend fun downloadDateOfBirth(
        request: RequestBody,
    ): Response<ResponseBody> {
        return api.downloadDateOfBirth(request)
    }

    suspend fun downloadEmailId(
        request: RequestBody,
    ): Response<ResponseBody> {
        return api.downloadEmailId(request)
    }

    suspend fun downloadMobileNumber(
        request: RequestBody,
    ): Response<ResponseBody> {
        return api.downloadMobileNumber(request)
    }

    suspend fun downloadAppeal(
        request: RequestBody,
    ): Response<ResponseBody> {
        return api.downloadAppeal(request)
    }

    suspend fun downloadRenewalCard(
        request: RequestBody,
    ): Response<ResponseBody> {
        return api.downloadRenewalCard(request)
    }

    suspend fun downloadSurrenderCard(
        request: RequestBody,
    ): Response<ResponseBody> {
        return api.downloadSurrenderCard(request)
    }

    suspend fun downloadLostCard(
        request: RequestBody,
    ): Response<ResponseBody> {
        return api.downloadLostCard(request)
    }

    suspend fun uploadFile(
        documentType: RequestBody?,
        document: MultipartBody.Part?,
    ): Response<UploadFileResponse> {
        return apiLogin.getUploadFile(
            documentType,
            document
        )
    }

    suspend fun editApplication(
        request: EditProfileRequest,
    ): Response<EditProfileResponse> {
        return api.editApplication(
            request
        )
    }

    suspend fun savePwdForm(
        fullName: RequestBody?,
        regionalFullName: RequestBody?,
        regionalLanguage: RequestBody?,
        mobile: RequestBody?,
        email: RequestBody?,
        dob: RequestBody?,
        gender: RequestBody?,//=>M/F/T
        guardianRelation: RequestBody?,//Mother/Father/Guardian
        fatherName: RequestBody?,
        motherName: RequestBody?,
        guardianName: RequestBody?,
        guardianContact: RequestBody?,
        photo: RequestBody?,
        sign: RequestBody?,
        // Proof id Identity Card
        aadhaarNo: RequestBody?,
        shareAadhaarInfo: RequestBody?,//0/1
        aadhaarInfo: RequestBody?,//Yes(1)/No(0)
        aadhaarEnrollmentNo: RequestBody?,
        aadhaarEnrollmentSlip: RequestBody?,
        identityProofId: RequestBody?,
        identityProofFile: RequestBody?,
        //Address For Correspondence
        addressProofId: RequestBody?,
        addressProofFile: RequestBody?,
        currentAddress: RequestBody?,
        currentStateCode: RequestBody?,
        currentDistrictCode: RequestBody?,
        currentSubDistrictCode: RequestBody?,
        currentVillageCode: RequestBody?,
        currentPincode: RequestBody?,
        //Disability Details
        disabilityTypeId: RequestBody?,
        disabilityDueTo: RequestBody?,
        disabilitySinceBirth: RequestBody?,//Since(No)/Birth(Yes)
        disabilitySince: RequestBody?,
        haveDisabilityCert: RequestBody?,//1(yes)/0(no)
        disabilityCertDoc: RequestBody?,
        serialNumber: RequestBody?,
        dateOfCertificate: RequestBody?,
        detailOfAuthority: RequestBody?,
        disabilityPer: RequestBody?,
        //Hospital for assessment
        isHospitalTreatingOtherState: RequestBody?,//=> 0/1
        hospitalTreatingStateCode: RequestBody?,
        hospitalTreatingDistrictCode: RequestBody?,
        declaration: RequestBody?,//=>0/1
    ): Response<SavePWDFormResponse> {
        return api.savePwdForm(
            fullName,
            regionalFullName,
            regionalLanguage,
            mobile,
            email,
            dob,
            gender,
            guardianRelation,
            fatherName,
            motherName,
            guardianName,
            guardianContact,
            photo,
            sign,
            aadhaarNo,
            shareAadhaarInfo,
            aadhaarInfo,
            aadhaarEnrollmentNo,
            aadhaarEnrollmentSlip,
            identityProofId,
            identityProofFile,
            addressProofId,
            addressProofFile,
            currentAddress,
            currentStateCode,
            currentDistrictCode,
            currentSubDistrictCode,
            currentVillageCode,
            currentPincode,
            disabilityTypeId,
            disabilityDueTo,
            disabilitySinceBirth,
            disabilitySince,
            haveDisabilityCert,
            disabilityCertDoc,
            serialNumber,
            dateOfCertificate,
            detailOfAuthority,
            disabilityPer,
            isHospitalTreatingOtherState,
            hospitalTreatingStateCode,
            hospitalTreatingDistrictCode,
            declaration
        )
    }
}



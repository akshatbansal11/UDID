package com.udid.model

import java.io.Serializable

data class DashboardData(
    val title: String,
    val image: Int,
): Serializable

data class TrackerData(
    val title: String,
    val unCompleteCircle: Int?
): Serializable

data class OnBoardingImageData(
    val imageResId: Int, // Resource ID of the image
    val title: String,
    val description: String
)

data class DropDownRequest(
    val fields: Fields,
    val filters: Filters,
    val model: String,
    val type: String
)

data class Fields(
    val reason: String,
    val state_code: String
)

data class Filters(
    val request_code: String,
    val status: Int
)

data class OtpRequest(
    val application_number: String,
    val otp: String
)

data class LoginRequest(
    val application_number: String,
    val dob: String?
)
data class MyAccountRequest(
    val application_number: String
)
data class ApplicationStatusRequest(
    val application_number: String
)

data class ResultGetDropDown(
    val id: Int,
    val name: String
)

data class PwdApplication(
    var applicantFullName: String = "",
    var applicantMobileNo: String = "",
    var applicantEmail: String = "",
    var applicantDob: String = "",
    var gender: Int = 0,
    var guardian: String = "",
    var photo: Int = 0,
    var sign: Int = 0,
    var aadhaarCard: Int = 0,
    var aadhaarNo: String = "",
    var aadhaarCheckBox: Int = 0,
    var aadhaarEnrollment: String = "",
    var aadhaarUploadSlip: Int = 0,
    var identityProof: String = "",
    var identityProofSlip: Int = 0,
    var documentAddressProof: String = "",
    var documentAddressProofPhoto: Int = 0,
    var documentAddress: String = "",
    var state: String = "",
    var district: String = "",
    var city: String = "",
    var village: String = "",
    var pincode: String = "",
    var disabilityType: String = "",
    var disabilityDue: String = "",
    var disabilityBirth: Int = 0,
    var disabilitySince: String = "",
    var treatingHospital: Int = 0,
    var hospitalState: String = "",
    var hospitalDistrict: String = "",
    var hospitalName: String = "",
    var hospitalCheckBox: Int = 0,
)

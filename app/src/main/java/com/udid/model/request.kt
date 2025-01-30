package com.udid.model

import java.io.Serializable

data class DashboardData(
    val title: String,
    val image: Int,
): Serializable

data class OnBoardingImageData(
    val imageResId: Int, // Resource ID of the image
    val title: String,
    val description: String
)

data class DropDownRequest(
    val fields: Fields,
    val filters: Filters?= null,
    val model: String,
    val type: String,
    val order: Any?= null
)

data class Fields(
    val reason: String?= null,
    val state_code: String?= null,
    val id: String?= null,
    val subdistrict_code: String?= null,
    val district_code: String?= null
)

data class Filters(
    val request_code: String?= null,
    val status: Int?= null,
    val district_code: String?= null,
    val state_code: String?= null
)

data class Order(
    val subdistrict_name :String?= null
)

data class GenerateOtpRequest(
    val application_number: String?,
    val type: String = "mobile"
)
data class ApplicationStatusRequest(
    val application_number: String,
    val type: String = "mobile"
)

data class LogoutRequest(
    val application_number: String,
    val type: String = "mobile"
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
    var hospitalCheckBox: Int = 0
) {
    fun validate(): List<String> {
        val errors = mutableListOf<String>()

        if (applicantFullName.isBlank()) errors.add("Applicant name cannot be empty.")
        if (!applicantMobileNo.matches(Regex("^\\d{10}$"))) errors.add("Mobile number must be a 10-digit number.")
        if (applicantDob.isBlank()) errors.add("Date of birth cannot be empty.")
        if (gender !in 1..2) errors.add("Gender must be 1 (Male) or 2 (Female).")
        if (guardian.isBlank()) errors.add("Guardian name cannot be empty.")
        if (photo <= 0) errors.add("Photo must be uploaded.")
        if (sign <= 0) errors.add("Signature must be uploaded.")
        if (aadhaarCard <= 0) {
            errors.add("Please select Aadhaar Information.")

        }else if(aadhaarCard==1){
            if (!aadhaarNo.matches(Regex("^\\d{12}$"))) errors.add("Aadhaar number must be a 12-digit number.")
            if (aadhaarCheckBox != 1) errors.add("Aadhaar checkbox must be checked.")
        }else{
            if (aadhaarEnrollment.isBlank()) errors.add("Aadhaar enrollment details cannot be empty.")
            if (aadhaarUploadSlip <= 0) errors.add("Aadhaar upload slip must be uploaded.")
        }
        if (identityProof.isBlank()) errors.add("Identity proof type cannot be empty.")
        if (identityProofSlip <= 0) errors.add("Identity proof slip must be uploaded.")
        if (documentAddressProof.isBlank()) errors.add("Address proof type cannot be empty.")
        if (documentAddressProofPhoto <= 0) errors.add("Address proof photo must be uploaded.")
        if (documentAddress.isBlank()) errors.add("Address cannot be empty.")
        if (state.isBlank()) errors.add("State cannot be empty.")
        if (district.isBlank()) errors.add("District cannot be empty.")
        if (city.isBlank()) errors.add("City cannot be empty.")
        if (village.isBlank()) errors.add("Village cannot be empty.")
        if (!pincode.matches(Regex("^\\d{6}$"))) errors.add("Pincode must be a 6-digit number.")
        if (disabilityType.isBlank()) errors.add("Disability type cannot be empty.")
        if (disabilityDue.isBlank()) errors.add("Disability due reason cannot be empty.")
        if (disabilityBirth != 1 && disabilityBirth != 2) {
            errors.add("Disability birth must be 1 (Yes) or 0 (No).")
        }else if(disabilityBirth == 2){
            if (disabilitySince.isBlank()) errors.add("Disability since date cannot be empty.")
        }
        if (treatingHospital == 1) {
            if (hospitalState.isBlank()) errors.add("Hospital state cannot be empty.")
            if (hospitalDistrict.isBlank()) errors.add("Hospital district cannot be empty.")
        }
        if (hospitalName.isBlank()) errors.add("Hospital name cannot be empty.")
        if (hospitalCheckBox != 1) errors.add("Hospital checkbox must be checked.")

        return errors
    }
}

package com.swavlambancard.udid.model

import java.io.Serializable

data class DashboardData(
    val title: String,
    val image: Int,
) : Serializable

data class OnBoardingImageData(
    val imageResId: Int, // Resource ID of the image
    val title: String,
    val description: String,
)

data class PincodeRequest(
    val model: String,
    val district_code: String,
    val type: String,
)

data class DropDownRequest(
    val fields: Fields,
    val filters: Filters? = null,
    val model: String,
    val type: String,
    val order: Any? = null,
)

data class Fields(
    val reason: String? = null,
    val state_code: String? = null,
    val id: String? = null,
    val subdistrict_code: String? = null,
    val district_code: String? = null,
    val village_code: String? = null,
)

data class Filters(
    val request_code: String? = null,
    val status: Int? = null,
    val district_code: String? = null,
    val state_code: String? = null,
    val subdistrict_code: String? = null,
)

data class Order(
    val subdistrict_name: String? = null,
)

data class GenerateOtpRequest(
    val application_number: String?,
    val type: String = "mobile",
)

data class ApplicationStatusRequest(
    val application_number: String,
    val type: String = "mobile",
)

data class CodeDropDownRequest(
    val type: String,
    val listname: String,
)

data class PwdApplication(

    //aes encryption
    //disability type multiple array of string to json string and then encrypt
    //Personal Details
    var applicantFullName: String? = null,// full_name
    var full_name_i18n: String? = null,// full_name_i18n
    var regionalLanguageCode: String? = null,// regional_language
    var applicantMobileNo: String? = null,//mobile
    var stateName: String? = null,//current_state_code
    var stateCode: String? = null,//current_state_code
    var applicantEmail: String? = "",//email
    var applicantDob: String? = null,//dob
    var gender: String? =null,// [gender] =>M/F/T
    var applicantsFMGName: String? =null,//[guardian_relation]=>mother/father/guardian
    var applicantsFMGCode: String? =null,//[guardian_relation]=>mother/father/guardian
    var fatherName: String ?= "",//father_name
    var motherName: String ?= "",//mother_name
    var guardianName: String ?= "",//guardian_name
    var relationWithPersonName: String ?= null,// [relation_pwd] => Self
    var relationWithPersonCode: String ?= null,// [relation_pwd] => Self
    var guardianContact: String ?= null,//guardian_contact
    var photo: String? =null,//photo
    var sign: String? =null,//signature_thumb_print
    // Proof id Identity Card
    var aadhaarNo: String ?= null,//aadhaar_no
    var aadhaarCheckBox: Int? =0,//[share_aadhar_info] => 0/1(checked)
    var aadhaarInfo: Int? =0,//[aadhar_info] => Yes(1)/No(0)
    var aadhaarTag: Int? =null,
    var aadhaarEnrollmentNo: String ?= null,//aadhar_enrollment_no
    var aadhaarEnrollmentUploadSlip: String? =null,//aadhar_enrollment_slip
    var identityProofId: String ?= null,//identitity_proof_id
    var identityProofName: String ?= null,//identitity_proof_id
    var identityProofUpload: String? =null,//identitity_proof_file

    //Address For Correspondence
    var natureDocumentAddressProofName: String ?= null,//address_proof_id
    var natureDocumentAddressProofCode: String ?= null,//address_proof_id
    var documentAddressProofPhoto: String? =null,//address_proof_file
    var address: String? =null,//current_address
    var stateName1: String ?= null,//current_state_code
    var stateCode1: String ?= null,//current_state_code
    var districtName: String ?= null,//current_district_code
    var districtCode: String ?= null,//current_district_code
    var subDistrictName: String ?= null,//current_subdistrict_code
    var subDistrictCode: String ?= null,//current_subdistrict_code
    var villageName: String ?= null,//current_village_code
    var villageCode: String ?= null,//current_village_code
    var pincodeName: String ?= null,//current_pincode
    var pincodeCode: String ?= null,//current_pincode
    //Disability Details
    var disabilityTypeName: String ?= null,//[disability_type_id] => 11
    var disabilityTypeCode: ArrayList<String> ?= null,//[disability_type_id] => 11
    var disabilityTypeList: ArrayList<DropDownResult> ?= null,//[disability_type_id] => 11
    var disabilityDueToName: String ?= null,// [disability_due_to] => Accident
    var disabilityDueToCode: String ?= null,// [disability_due_to] => Accident
    var disabilityBirth: String? =null,//[disability_since_birth] => Since(No)/Birth(Yes)
    var disabilitySinceName: String ?= null,//[disability_since] => 2022
    var disabilitySinceCode: String ?= "",//[disability_since] => 2022
    var uploadDisabilityCertificate: String? =null,// disability_cert_doc
    var detailOfAuthorityName: String? =null,// [detail_of_authority] => Medical Authority
    var detailOfAuthorityCode: String? =null,// [detail_of_authority] => Medical Authority
    var serialNumber: String? =null,//  [serial_number] => FD3245
    var haveDisabilityCertificate: Int? =null,// [have_disability_cert] => 1(yes)/0(no)
    var dateOfCertificate: String ?= null,// [date_of_certificate] => 01/08/2023
    var disabilityPercentage: String ?= null,//[disability_per] => 30%
    //Hospital for assessment/issue of UDID card /disability certificate
    var treatingHospitalTag: String? ="0",//is_hospital_treating_other_state => 0/1
    var hospitalStateId: String ?= "",//hospital_treating_state_code
    var hospitalStateName: String ?= "",//hospital_treating_state_code
    var hospitalDistrictId: String ?= "",//hospital_treating_district_code
    var hospitalDistrictName: String ?= "",//hospital_treating_district_code
    var hospitalNameId: String ?= null,
    var hospitalNameName: String ?= null,//hospital_treating_id
    var hospitalCheckBox: String? =null,//declaration =>0/1
)

data class EditProfileRequest(
    val application_number: String,
    val type: String
)

data class LanguageLocalize(
    val stateId: String,
    val langCode: String,
    val languageName: String
)

data class RejectApplicationRequest(
    val application_number: String?,
    val dob: String?,
    val type: String?
)
data class PendingApplicationWise(
    val application_number: String?,
    val dob: String?,
    val type: String?
)

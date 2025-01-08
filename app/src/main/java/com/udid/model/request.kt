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
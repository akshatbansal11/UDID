package com.udid.services

import com.udid.model.AppLoginResponse
import com.udid.model.ApplicationStatusRequest
import com.udid.model.LoginRequest
import com.udid.model.LoginResponse
import com.udid.model.MyAccountRequest
import com.udid.model.MyAccountResponse
import com.udid.model.ApplicationStatusResponse
import com.udid.model.OTPResponse
import com.udid.model.OtpRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

const val LOGIN = "login"
const val OTP_LOGIN = "otplogin"
const val MY_ACCOUNT = "myaccount"
const val APP_STATUS = "getAppapplicationstatus "

interface MyService {

    @POST(LOGIN)
    suspend fun getLogin(@Body request: String): Response<LoginResponse>
    @POST(OTP_LOGIN)
    suspend fun getOtpLogin(@Body request: OtpRequest): Response<LoginResponse>
    @POST(MY_ACCOUNT)
    suspend fun getMyAccount(@Body request: String): Response<MyAccountResponse>
    @POST(APP_STATUS)
    suspend fun getAppStatus(@Body request: ApplicationStatusRequest): Response<ApplicationStatusResponse>

}


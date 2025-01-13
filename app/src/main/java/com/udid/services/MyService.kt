package com.udid.services

import com.udid.model.ApplicationStatusRequest
import com.udid.model.ApplicationStatusResponse
import com.udid.model.DropDownRequest
import com.udid.model.DropDownResponse
import com.udid.model.GenerateOtpRequest
import com.udid.model.LoginResponse
import com.udid.model.MyAccountResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

const val LOGIN = "login"
const val GENERATE_OTP_LOGIN = "getotp"
const val MY_ACCOUNT = "myaccount"
const val APP_STATUS = "getAppapplicationstatus "
const val DROP_DOWN = "getDropdown "

interface MyService {

    @POST(LOGIN)
    suspend fun getLogin(@Body request: String): Response<LoginResponse>
    @POST(GENERATE_OTP_LOGIN)
    suspend fun getOtpLogin(@Body request: GenerateOtpRequest): Response<LoginResponse>
    @POST(MY_ACCOUNT)
    suspend fun getMyAccount(@Body request: String): Response<MyAccountResponse>
    @POST(APP_STATUS)
    suspend fun getAppStatus(@Body request: ApplicationStatusRequest): Response<ApplicationStatusResponse>
    @POST(DROP_DOWN)
    suspend fun getDropDown(@Body request: DropDownRequest): Response<DropDownResponse>

}


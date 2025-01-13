package com.udid.repository

import com.udid.model.ApplicationStatusRequest
import com.udid.model.ApplicationStatusResponse
import com.udid.model.DropDownRequest
import com.udid.model.DropDownResponse
import com.udid.model.GenerateOtpRequest
import com.udid.model.LoginResponse
import com.udid.model.MyAccountResponse
import com.udid.services.MyService
import com.udid.services.ServiceGenerator
import com.udid.services.ServiceGeneratorLogin
import retrofit2.Response

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

    suspend fun getGenerateOtpLogin(request: GenerateOtpRequest): Response<LoginResponse> {
        return api.getOtpLogin(request)
    }

    suspend fun getAppStatus(request: ApplicationStatusRequest): Response<ApplicationStatusResponse> {
        return api.getAppStatus(request)
    }
    suspend fun getDropDown(request: DropDownRequest): Response<DropDownResponse> {
        return api.getDropDown(request)
    }
}



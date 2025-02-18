package com.swavlambancard.udid.viewModel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.swavlambancard.udid.model.PwdApplication


class SharedDataViewModel : ViewModel() {
    val userData = MutableLiveData(PwdApplication()) // Initialize with default fields
    val errors = MutableLiveData<String>()
    init {
        userData.value = PwdApplication() // Initialize with default empty fields
    }

    fun valid(): Boolean{
        if(userData.value?.applicantFullName.toString().isEmpty()){
            errors.postValue("Please enter Applicant Full Name")
            return false
        }
        return true
    }
}

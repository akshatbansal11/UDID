package com.swavlambancard.udid.viewModel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.swavlambancard.udid.model.PwdApplication


class SharedDataViewModel : ViewModel() {
    val userData = MutableLiveData(PwdApplication()) // Initialize with default fields

    init {
        userData.value = PwdApplication() // Initialize with default empty fields
    }
}

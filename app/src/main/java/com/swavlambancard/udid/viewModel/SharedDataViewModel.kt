package com.swavlambancard.udid.viewModel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.swavlambancard.udid.model.PwdApplication


class SharedDataViewModel : ViewModel() {
    val userData = MutableLiveData(PwdApplication()) // Initialize with default fields// toh prevent data from being affected by recreational changes

    init {
        userData.value = PwdApplication() // Initialize with default empty fields
    }
}

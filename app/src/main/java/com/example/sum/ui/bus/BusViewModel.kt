package com.example.sum.ui.bus

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sum.utility.mainViewModel.MainViewModel

class BusViewModel : ViewModel() {

    /*private val _text = MutableLiveData<String>().apply {
        value = "This is the bus status and information fragment"
    }
    val text: LiveData<String> = _text*/
    private  lateinit var viewModel: MainViewModel

    //val items = arrayOf("Stop 1", "Stop 2", "Stop 3", "Stop 4")
    val schedules = arrayOf(
        "00:00",
        "01:00",
        "02:00",
        "03:00",
        "04:00",
        "05:00",
        "06:00",
        "07:00",
        "08:00",
        "09:00",
        "10:00",
        "11:00",
        "12:00",
        "13:00",
        "14:00",
        "15:00",
        "16:00",
        "17:00",
        "18:00",
        "19:00",
        "20:00",
        "21:00",
        "22:00",
        "23:00"
    )
}
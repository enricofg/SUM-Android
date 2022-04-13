package com.example.sum.ui.bus

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BusViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the bus status and information fragment"
    }
    val text: LiveData<String> = _text
}
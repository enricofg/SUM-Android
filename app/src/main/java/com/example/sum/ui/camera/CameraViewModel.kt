package com.example.sum.ui.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CameraViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the dashboard fragment"
    }
    val text: LiveData<String> = _text
}
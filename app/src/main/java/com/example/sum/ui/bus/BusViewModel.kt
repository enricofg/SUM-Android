package com.example.sum.ui.bus

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sum.utility.mainViewModel.MainViewModel
import com.example.sum.utility.model.data.stops.StopItem
import com.example.sum.utility.model.data.stopsSchedules.StopSchedule
import java.util.ArrayList

class BusViewModel : ViewModel() {
    //val stopsList = ArrayList<String>()
    var stopsList = ArrayList<StopItem>()
    var stopsSchedules = ArrayList<StopNameSchedule>()
    lateinit var selectedStop: StopItem
}
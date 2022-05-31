package com.example.sum.utility.mainViewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sum.utility.model.data.buses.Buses
import com.example.sum.utility.model.data.lines.Lines
import com.example.sum.utility.model.data.stops.Stop
import com.example.sum.utility.model.data.stopsSchedules.StopSchedule
import com.example.sum.utility.repository.repository
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel(private val repository: repository) : ViewModel() {
    val stops: MutableLiveData<Response<Stop>> = MutableLiveData()
    val buses: MutableLiveData<Response<Buses>> = MutableLiveData()
    val lines: MutableLiveData<Response<Lines>> = MutableLiveData()
    private val stopsStopSchedule: MutableLiveData<Response<StopSchedule>> = MutableLiveData()
    fun getStops(){
        viewModelScope.launch {
            val response: Response<Stop> = repository.getStops()
            this@MainViewModel.stops.value= response
        }
    }
    fun getBuses(){
        viewModelScope.launch {
            val response: Response<Buses> = repository.getBuses()
            this@MainViewModel.buses.value= response
        }
    }
    fun getLines(){
        viewModelScope.launch {
            val response: Response<Lines> = repository.getLines()
            this@MainViewModel.lines.value= response
        }
    }
    fun getStopsSchedules(){
        viewModelScope.launch {
            val response: Response<StopSchedule> = repository.getStopsSchedules()
            this@MainViewModel.stopsStopSchedule.value= response
        }
    }
}
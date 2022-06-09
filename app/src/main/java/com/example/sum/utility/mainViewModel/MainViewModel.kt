package com.example.sum.utility.mainViewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sum.utility.model.data.buses.Buses
import com.example.sum.utility.model.data.lines.Lines
import com.example.sum.utility.model.data.stops.Stop
import com.example.sum.utility.model.data.stopsSchedules.StopsList
import com.example.sum.utility.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Error

class MainViewModel(private val repository: Repository) : ViewModel() {
    val stops: MutableLiveData<Response<Stop>> = MutableLiveData()
    val stopsName: MutableLiveData<Response<Stop>> = MutableLiveData()
    val buses: MutableLiveData<Response<Buses>> = MutableLiveData()
    val lines: MutableLiveData<Response<Lines>> = MutableLiveData()
    val stopSchedule: MutableLiveData<Response<StopsList>> = MutableLiveData()

    fun getStops() {
        viewModelScope.launch {
            val response: Response<Stop> = repository.getStops()
            stops.value = response
        }
    }

    fun getStops(id: Int) {
        viewModelScope.launch {
            //val response: Response<Stop> = repository.getStop(id)
            val response: Response<Stop> = repository.getStops()
            stopsName.value = response
        }
    }

    fun getBuses() {
        viewModelScope.launch {
            val response: Response<Buses> = repository.getBuses()
            buses.value = response
        }
    }

    fun getLines() {
        viewModelScope.launch {
            val response: Response<Lines> = repository.getLines()
            lines.value = response
        }
    }

    fun getStopsSchedules() {
        viewModelScope.launch {
            val response: Response<StopsList> = repository.getStopsSchedules()
            stopSchedule.value = response
        }
    }

    fun getStopsSchedules(id: Int) {
        viewModelScope.launch {
            val response: Response<StopsList> = repository.getStopsSchedule(id)
            stopSchedule.value = response
        }
    }
}
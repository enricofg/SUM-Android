package com.example.sum.utility.mainViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sum.utility.model.data.stops.Stop
import com.example.sum.utility.repository.repository
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel(private val repository: repository) : ViewModel() {
    val response: MutableLiveData<Response<Stop>> = MutableLiveData()
    fun getStops(){
        viewModelScope.launch {
            val response: Response<Stop> = repository.getStops()
            this@MainViewModel.response.value= response
        }
    }
}
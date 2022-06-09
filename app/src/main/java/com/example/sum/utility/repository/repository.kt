package com.example.sum.utility.repository

import com.example.sum.utility.api.RetrofitInstance
import com.example.sum.utility.model.data.buses.Buses
import com.example.sum.utility.model.data.lines.Lines
import com.example.sum.utility.model.data.stops.Stop
import com.example.sum.utility.model.data.stopsSchedules.StopSchedule
import retrofit2.Response

class repository {
    suspend fun getStops(): Response<Stop> {
        return RetrofitInstance.api.getStops()
    }
    suspend fun getBuses(): Response<Buses> {
        return RetrofitInstance.api.getBuses()
    }
    suspend fun getLines(): Response<Lines> {
        return RetrofitInstance.api.getLines()
    }
    suspend fun getStopsSchedules(): Response<StopSchedule> {
        return RetrofitInstance.api.getStopsSchedules()
    }
    suspend fun getStopsSchedules(id:Int): Response<StopSchedule> {
        return RetrofitInstance.api.getStopsSchedule(id)
    }

}
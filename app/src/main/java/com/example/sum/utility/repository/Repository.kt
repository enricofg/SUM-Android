package com.example.sum.utility.repository

import com.example.sum.utility.api.RetrofitInstance
import com.example.sum.utility.model.data.buses.Buses
import com.example.sum.utility.model.data.lines.Lines
import com.example.sum.utility.model.data.stops.Stop
import com.example.sum.utility.model.data.stopsSchedules.StopSchedule
import com.example.sum.utility.model.data.stopsSchedules.StopsList
import retrofit2.Response

class Repository {

    suspend fun getStops(): Response<Stop> {
        return RetrofitInstance.api.getStops()
    }

    suspend fun getStop(id: Int): Response<Stop> {
        return RetrofitInstance.api.getStop(id)
    }

    suspend fun getBuses(): Response<Buses> {
        return RetrofitInstance.api.getBuses()
    }

    suspend fun getLines(): Response<Lines> {
        return RetrofitInstance.api.getLines()
    }

    suspend fun getStopsSchedules(): Response<StopsList> {
        return RetrofitInstance.api.getStopsSchedules()
    }

    suspend fun getStopsSchedule(id: Int): Response<StopsList> {
        return RetrofitInstance.api.getStopsSchedule(id)
    }

}
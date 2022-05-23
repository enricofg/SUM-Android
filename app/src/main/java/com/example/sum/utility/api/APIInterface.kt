package com.example.sum.utility.api

import com.example.sum.utility.model.data.buses.Buses
import com.example.sum.utility.model.data.lines.Lines
import com.example.sum.utility.model.data.stops.Stop
import com.example.sum.utility.model.data.stopsSchedules.StopSchedule
import retrofit2.Response
import retrofit2.http.GET


interface APIInterface {
    @GET("stops")
    suspend fun getStops(): Response<Stop>

    @GET("Buses")
    fun getBuses(): Buses

    @GET("Lines")
    fun getLines(): Lines

    @GET("StopsSchedules")
    fun getStopsSchedules(): StopSchedule

}

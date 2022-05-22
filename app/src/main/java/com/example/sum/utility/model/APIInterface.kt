package com.example.sum.utility.model

import com.example.sum.utility.model.data.buses.Buses
import com.example.sum.utility.model.data.lines.Lines
import com.example.sum.utility.model.data.stops.Stop
import com.example.sum.utility.model.data.stopsSchedules.StopSchedule
import retrofit2.Call
import retrofit2.http.GET


interface APIInterface {
    @GET("stops")
    fun getStops(): Call<Stop>

    @GET("Buses")
    fun getBuses(): Call<Buses>

    @GET("Lines")
    fun getLines(): Call<Lines>

    @GET("StopsSchedules")
    fun getStopsSchedules(): Call<StopSchedule>

}

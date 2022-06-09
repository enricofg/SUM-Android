package com.example.sum.utility.api

import com.example.sum.utility.model.data.buses.Buses
import com.example.sum.utility.model.data.lines.Lines
import com.example.sum.utility.model.data.stops.Stop
import com.example.sum.utility.model.data.stopsSchedules.StopSchedule
import com.example.sum.utility.model.data.stopsSchedules.StopsList
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path


interface APIInterface {
    @GET("stops")
    suspend fun getStops(): Response<Stop>

    @GET("Buses")
    suspend fun getBuses(): Response<Buses>

    @GET("Lines")
    suspend fun getLines(): Response<Lines>

    @GET("StopsSchedules")
    suspend fun getStopsSchedules(): Response<StopsList>

    @GET("StopsSchedules/{id}")
    suspend fun getStopsSchedules2(@Path("id")id: Int): Response<StopsList>

}

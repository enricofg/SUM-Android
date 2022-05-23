package com.example.sum.utility.repository

import com.example.sum.utility.api.RetrofitInstance
import com.example.sum.utility.model.data.stops.Stop
import retrofit2.Response

class repository {
    suspend fun getStops(): Response<Stop> {
        return RetrofitInstance.api.getStops()
    }
}
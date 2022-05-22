package com.example.sum.utility
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class APIHelper {



    object RetrofitHelper {
        val baseUrl = "https://smarturbanmoving.azurewebsites.net/api/"
        fun getInstance(): Retrofit{
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        }
    }

}


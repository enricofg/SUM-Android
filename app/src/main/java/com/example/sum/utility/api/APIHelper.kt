package com.example.sum.utility.api
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create


object RetrofitInstance {

        const val baseUrl = "https://smarturbanmoving.azurewebsites.net/api/"
        private val retrofit by lazy{
             Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        val api : APIInterface by lazy{
            retrofit.create(APIInterface::class.java)
        }
    }




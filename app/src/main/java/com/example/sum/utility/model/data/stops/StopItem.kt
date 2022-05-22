package com.example.sum.utility.model.data.stops

data class StopItem(
    val Latitude: Double,
    val Line_Id: Int,
    val Longitude: Double,
    val Stop_Id: Int,
    val Stop_Name: String,
    val sc: List<Sc>
)
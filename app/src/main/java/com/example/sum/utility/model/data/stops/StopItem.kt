package com.example.sum.utility.model.data.stops

data class StopItem(
    val Latitude: Double,
    val Line_Id: Int,
    val Longitude: Double,
    val Stop_Id: Int,
    val Stop_Name: String,
    val Schedule_Time: String,
    val sc: List<Sc>
){
    override fun toString(): String {
        return Stop_Name
    }
}

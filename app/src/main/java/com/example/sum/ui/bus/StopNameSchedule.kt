package com.example.sum.ui.bus

class StopNameSchedule(
    val Line_Name: String,
    val Stop_Name: String,
    val Schedule_Time: String,
){
    override fun toString(): String {
        return "$Schedule_Time - $Stop_Name - Linha $Line_Name"
    }
}

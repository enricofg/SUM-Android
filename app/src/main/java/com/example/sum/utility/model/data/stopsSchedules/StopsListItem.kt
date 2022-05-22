package com.example.sum.utility.model.data.stopsSchedules

data class StopsListItem(
    val Line_Id: Int,
    val Line_Name: String,
    val StopSchedule: List<StopSchedule>
)
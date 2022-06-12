package com.example.sum.ui.camera.geospatial

import com.google.android.gms.maps.model.LatLng

class Direction(
    val location: LatLng,
    val maneuver: String,
    val distance: String,
    val duration: String
) {
    override fun toString(): String {
        return "$location - $maneuver - $distance - $duration"
    }
}
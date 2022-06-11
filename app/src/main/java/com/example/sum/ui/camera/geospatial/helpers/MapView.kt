package com.example.sum.ui.camera.geospatial.helpers

import android.content.Context
import android.graphics.*
import android.util.Log
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.sum.BuildConfig.MAPS_API_KEY
import com.example.sum.R
import com.example.sum.ui.camera.GeoCameraActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import org.json.JSONObject
import java.io.InputStream


class MapView(val activity: GeoCameraActivity, val googleMap: GoogleMap) {
    private val CAMERA_MARKER_COLOR: Int = Color.argb(255, 26, 115, 232)
    private val EARTH_MARKER_COLOR: Int = Color.argb(255, 125, 125, 125)

    var setInitialCameraPosition = false
    val cameraMarker = createMarker(CAMERA_MARKER_COLOR)
    var cameraIdle = true
    var destination: LatLng? = null
    var routeLoaded: Boolean = false

    val earthMarker = createMarker(EARTH_MARKER_COLOR)

    init {
        googleMap.uiSettings.apply {
            isMapToolbarEnabled = true
            isIndoorLevelPickerEnabled = false
            isZoomControlsEnabled = true
            isTiltGesturesEnabled = false
            isScrollGesturesEnabled = true
        }

        googleMap.setOnMarkerClickListener { false }

        // Add listeners to keep track of when the GoogleMap camera is moving.
        googleMap.setOnCameraMoveListener { cameraIdle = false }
        googleMap.setOnCameraIdleListener { cameraIdle = true }
    }

    fun updateMapPosition(latitude: Double, longitude: Double, heading: Double) {
        val position = LatLng(latitude, longitude)
        activity.runOnUiThread {
            // If the map is already in the process of a camera update, then don't move it.
            if (!cameraIdle) {
                return@runOnUiThread
            }
            cameraMarker.isVisible = true
            cameraMarker.position = position
            cameraMarker.rotation = heading.toFloat()

            val cameraPositionBuilder: CameraPosition.Builder = if (!setInitialCameraPosition) {
                // Set the camera position with an initial default zoom level.
                setInitialCameraPosition = true
                CameraPosition.Builder().zoom(21f).target(position)
            } else {
                // Set the camera position and keep the same zoom level.
                CameraPosition.Builder()
                    .zoom(googleMap.cameraPosition.zoom)
                    .target(position)
            }
            googleMap.moveCamera(
                CameraUpdateFactory.newCameraPosition(cameraPositionBuilder.build())
            )

            if (!routeLoaded) {
                val startingPoint = LatLng(position.latitude, position.longitude)
                destination?.let { getDirections(startingPoint, it) }
            }
        }
    }

    /** Creates and adds a 2D anchor marker on the 2D map view.  */
    private fun createMarker(
        color: Int
    ): Marker {
        val markersOptions = MarkerOptions()
            .position(LatLng(0.0, 0.0))
            .draggable(false)
            .anchor(0.1f, 0.1f)
            .flat(true)
            .visible(false)
            .icon(bitmapDescriptorFromVector(activity.applicationContext, color))
        //.icon(BitmapDescriptorFactory.fromBitmap(createColoredMarkerBitmap(color)))
        return googleMap.addMarker(markersOptions)!!
    }

    private fun bitmapDescriptorFromVector(
        context: Context,
        @ColorInt color: Int
    ): BitmapDescriptor? {
        val vectorDrawable =
            ContextCompat.getDrawable(context, R.drawable.ic_baseline_navigation_24)
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val wrappedDrawable = DrawableCompat.wrap(vectorDrawable)
        DrawableCompat.setTint(wrappedDrawable, color)

        val bitmap = Bitmap.createBitmap(
            wrappedDrawable.intrinsicWidth,
            wrappedDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        wrappedDrawable.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun getDirections(startingPoint: LatLng, destination: LatLng) {
        //val position = LatLng(latitude, longitude)
        val path: MutableList<List<LatLng>> = ArrayList()
        val urlDirections =
            "https://maps.googleapis.com/maps/api/directions/json?origin=${startingPoint.latitude},${startingPoint.longitude}&destination=${destination.latitude},${destination.longitude}&key=AIzaSyAlIse-fCJl0CntP-rQ5s0Y5z83BTp0MOY"
        Log.i("URL: ", urlDirections)
        val directionsRequest = object :
            StringRequest(Method.GET, urlDirections, Response.Listener { response ->
                val jsonResponse = JSONObject(response)
                Log.i("JSONResponse", jsonResponse.toString())
                // Get routes
                val routes = jsonResponse.getJSONArray("routes")

                val legs = routes.getJSONObject(0).getJSONArray("legs")
                val steps = legs.getJSONObject(0).getJSONArray("steps")
                for (i in 0 until steps.length()) {
                    val points =
                        steps.getJSONObject(i).getJSONObject("polyline").getString("points")
                    path.add(PolyUtil.decode(points))
                }
                for (i in 0 until path.size) {
                    this.googleMap.addPolyline(PolylineOptions().addAll(path[i]).color(Color.RED))
                }
            }, Response.ErrorListener {
            }) {}
        val requestQueue = Volley.newRequestQueue(activity)
        requestQueue.add(directionsRequest)
        routeLoaded = true
    }



}
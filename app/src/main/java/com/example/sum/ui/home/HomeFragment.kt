package com.example.sum.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.sum.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import java.io.IOException
import java.util.*


class HomeFragment : Fragment() , GoogleMap.OnMarkerClickListener {

    private lateinit var lastLocation: Location
    private lateinit var mMap:GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var permissionLaucher: ActivityResultLauncher<Array<String>>
    override fun onMarkerClick(p0: Marker) = false


    private val callback = OnMapReadyCallback { googleMap ->
        mMap= googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.isIndoorEnabled = true
        mMap.isBuildingsEnabled = true
        mMap.setOnMarkerClickListener(this)
        setUpMap()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        permissionLaucher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ permissions ->
        }
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
         fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext().applicationContext)


    }




    /**
     * This function is use to set up map permissions and and set users location
     */
    private fun setUpMap() {

        if (ActivityCompat.checkSelfPermission(
                requireContext().applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext().applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            permissionLaucher.launch(arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION))
            return
        }
        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null){
                lastLocation = location
                val currentLatLong = LatLng(location.latitude, location.longitude)


                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong,15f))

            }
        }
    }





}
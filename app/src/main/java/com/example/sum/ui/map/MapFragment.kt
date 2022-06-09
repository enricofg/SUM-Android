package com.example.sum.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.sum.R
import com.example.sum.utility.mainViewModel.MainViewModel
import com.example.sum.utility.mainViewModel.MainViewModelFactory
import com.example.sum.utility.model.data.stops.Stop
import com.example.sum.utility.model.data.stops.StopItem
import com.example.sum.utility.repository.repository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.util.*


class MapFragment : Fragment(), GoogleMap.OnMarkerClickListener {

    private lateinit var lastLocation: Location
    private lateinit var mMap: GoogleMap
    private lateinit var Adress: Stop
    private lateinit var ViewModel:MainViewModel
    private var adrees: StopItem? = null
    private var AddressList: List<String> = emptyList()
    private lateinit var input_search: AutoCompleteTextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    override fun onMarkerClick(p0: Marker) = false

    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.isIndoorEnabled = true
        mMap.isBuildingsEnabled = true
        mMap.setOnMarkerClickListener(this)

        //check current theme mode and set map theme according to it
        val nightModeFlags = requireContext().resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> {
                activity?.let {
                    val style = MapStyleOptions.loadRawResourceStyle(
                        it.applicationContext,
                        R.raw.map_dark_mode
                    )
                    mMap.setMapStyle(style)
                }
            }
            Configuration.UI_MODE_NIGHT_NO, Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                activity?.let {
                    val style = MapStyleOptions.loadRawResourceStyle(
                        it.applicationContext,
                        R.raw.map_light_mode
                    )
                    mMap.setMapStyle(style)
                }
            }
        }

        setUpMap()


    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_map, container, false)
        input_search = view.findViewById<AutoCompleteTextView>(R.id.input_search)
        val viewModelFactory = MainViewModelFactory(repository())

        ViewModel = ViewModelProvider(this,viewModelFactory)[MainViewModel::class.java]
        ViewModel.getStops()
        ViewModel.stops.observe(viewLifecycleOwner, Observer { response->

            if(response.isSuccessful){

                Adress = response.body()!!;
                Log.d("address", Adress.toString())
                if (AddressList.count() != Adress.count()) {
                    Adress.forEach {
                        AddressList += listOf<String>(it.Stop_Name)
                        if (AddressList.count() == Adress.count()) {
                            val adapter = ArrayAdapter(
                                requireContext().applicationContext,
                                android.R.layout.simple_list_item_1,
                                AddressList!!
                            )

                            input_search.setAdapter(adapter)
                        }
                    }
                }

            }
        })

        // listener that acts when text is change in map edit box
        input_search.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                // if the event is a key down event on the enter button
                if (event.action == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_ENTER
                ) {
                    Log.d("message",input_search.text.toString())
                    geolocation(input_search.text.toString())
                    return true
                }
                return false
            }
        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            }

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireContext().applicationContext)

    }

    /**
     * This function is use to set up map permissions and and set users location
     */
    @SuppressLint("MissingPermission")
    private fun setUpMap() {

        if (ActivityCompat.checkSelfPermission(
                requireContext().applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext().applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED&&
            ActivityCompat.checkSelfPermission(
                requireContext().applicationContext,
                Manifest.permission.INTERNET
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.INTERNET
                )
            )
            return
        }
        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLong = LatLng(location.latitude, location.longitude)


                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 15f))

            }
        }
    }


    private fun geolocation(PossibleLocation: String){

        try {

                adrees = Adress.find { it ->
                    it.Stop_Name.lowercase().contains(PossibleLocation.lowercase())
                }

        } catch (e: IOException) {
            Log.e("MapLocationError", "geoLocate: IOException: $e")
        }
        // if the user input is an valid address this piece of code will transport the user to the location and set up a marker
        if (adrees != null) {

            mMap.clear()
            val currentLatLong = LatLng(adrees!!.Latitude, adrees!!.Longitude)
            mMap.addMarker(
                MarkerOptions()
                    .position(currentLatLong)
                    .title(adrees!!.Stop_Name)
            )
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 15f))
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
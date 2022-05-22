package com.example.sum

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.sum.databinding.ActivityMainBinding
import com.example.sum.utility.APIHelper
import com.example.sum.utility.model.APIInterface
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.awaitResponse


class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val stops = APIHelper.RetrofitHelper.getInstance().create(APIInterface::class.java)


        GlobalScope.launch(Dispatchers.IO)
        {
            val response = stops.getStops().awaitResponse();
            if (response.isSuccessful){
                val data = response.body()!!
                Log.d(ContentValues.TAG,data.toString());

            }
        }


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_map, R.id.navigation_camera, R.id.navigation_schedules
            )
        )


        //hide action bar
        supportActionBar?.hide()

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}
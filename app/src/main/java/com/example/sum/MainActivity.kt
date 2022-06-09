package com.example.sum

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.sum.databinding.ActivityMainBinding
import com.example.sum.utility.mainViewModel.MainViewModel
import com.example.sum.utility.mainViewModel.MainViewModelFactory
import com.example.sum.utility.repository.Repository
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private  lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)

        //call api
        viewModel = ViewModelProvider(this,viewModelFactory)[MainViewModel::class.java]
        viewModel.getStops()
        viewModel.stops.observe(this, Observer { response->
            if(response.isSuccessful){
                response.body()?.forEach {
                    Log.d("response",it.Stop_Name )
                }

            }
        })

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
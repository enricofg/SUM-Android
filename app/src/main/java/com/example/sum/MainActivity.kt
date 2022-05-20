package com.example.sum

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.sum.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)


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

        //set app language according to sharedpreferences
        //Toast.makeText(applicationContext, resources.configuration.locale.toString(), Toast.LENGTH_LONG).show()
        //Toast.makeText(applicationContext, preferences.getString("selected_language","en").toString(), Toast.LENGTH_LONG).show()
    }

    fun restartApp() {
        val pm = packageManager
        val intent = pm.getLaunchIntentForPackage(packageName)
        finishAffinity() // Finishes all activities.
        startActivity(intent) // Start the launch activity
        overridePendingTransition(0, 0)
    }
}
package com.example.sum.ui.bus

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.sum.databinding.FragmentBusBinding
import com.example.sum.utility.mainViewModel.MainViewModel
import com.example.sum.utility.mainViewModel.MainViewModelFactory
import com.example.sum.utility.model.data.stops.StopItem
import com.example.sum.utility.repository.Repository
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.*


class BusFragment : Fragment(), AdapterView.OnItemClickListener {

    private var _binding: FragmentBusBinding? = null
    private lateinit var viewModel: MainViewModel
    lateinit var busViewModel: BusViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("ClickableViewAccessibility", "SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        busViewModel = ViewModelProvider(this)[BusViewModel::class.java]
        _binding = FragmentBusBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /**fragment variables*/
        val startingPointOptions = binding.startingPointOptions
        val departureTime = binding.scheduleDateField
        val schedulesList = binding.schedulesResultList
        val searchButton = binding.buttonSearchSchedule

        /**starting point list*/
        //starting point options list
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)

        //call api
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        viewModel.getStops()
        viewModel.stops.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                busViewModel.stopsList.clear()
                /*busViewModel.stopsList.add(StopItem(0.0, 0, 0.0, -1, "Show all", "", emptyList()))
                busViewModel.selectedStop = busViewModel.stopsList[0]*/
                response.body()?.forEach {
                    busViewModel.stopsList.add(it)

                    val stopsAdapter: ArrayAdapter<StopItem>? = activity?.let { activity ->
                        ArrayAdapter(
                            activity,
                            R.layout.simple_spinner_dropdown_item,
                            busViewModel.stopsList
                        )
                    }
                    startingPointOptions.adapter =
                        stopsAdapter //set the list adapter for the starting point options view element
                }
            }
        }

        startingPointOptions.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                busViewModel.selectedStop = busViewModel.stopsList[position]
            }
        }

        /**time picker*/
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePicker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setInputMode(INPUT_MODE_CLOCK)
                .setHour(hour)
                .setMinute(minute)
                .build()

        timePicker.addOnPositiveButtonClickListener {
            val newHour: Int = timePicker.hour
            val newMinute: Int = timePicker.minute
            departureTime.setText(String.format("%02d:%02d", newHour, newMinute))
        }

        /**time field*/
        //disable default keyboard for time input field
        departureTime.showSoftInputOnFocus = false
        //set default departure time to current time
        departureTime.setText(String.format("%02d:%02d", hour, minute))

        //listener for time input field touch
        departureTime.setOnTouchListener { _, event ->
            if (MotionEvent.ACTION_UP == event.action)
                timePicker.show(requireActivity().supportFragmentManager, "tag")
            false
        }

        /**search button listener*/
        searchButton.setOnClickListener {
            getSchedules(
                schedulesList,
                busViewModel.selectedStop.Stop_Id,
                departureTime.text.toString()
            )
        }
        schedulesList.onItemClickListener = this

        /** load passed stopId if it's not null */
        val sharedPreference =
            requireActivity().getSharedPreferences("SCHEDULE", Context.MODE_PRIVATE)
        val stopId = sharedPreference.getInt("ScheduleTime", -1)
        val stopName = sharedPreference.getString("ScheduleName", "null")

        if (stopId > -1) {
            busViewModel.selectedStop =
                StopItem(0.0, 0, 0.0, -1, stopName as String, "00:00", emptyList())
            getSchedules(schedulesList, stopId, departureTime.text.toString())
            sharedPreference.edit().clear().apply()
        }

        return root
    }

    private fun getSchedules(schedulesList: ListView, stopId: Int, departure: String) {
        if (stopId > -1) {
            viewModel.getStopsSchedules(stopId)
        } else {
            return
        }

        val format = SimpleDateFormat("kk:mm")
        var searchResultListAdapter: ArrayAdapter<StopNameSchedule>
        viewModel.stopSchedule.observe(viewLifecycleOwner, Observer { response ->
            busViewModel.stopsSchedules.clear()
            if (response.isSuccessful) {
                response.body()?.forEach {
                    for (stopSchedule in it.StopSchedule) {
                        if (format.parse(stopSchedule.Schedule_Time) >= format.parse(departure)) {
                            var stopName: String = if (stopId >= -1) {
                                busViewModel.selectedStop.Stop_Name
                            } else {
                                ""
                            }

                            val item = StopNameSchedule(
                                it.Line_Name,
                                stopName,
                                stopSchedule.Schedule_Time
                            )
                            busViewModel.stopsSchedules.add(item)

                            /**search result list*/
                            searchResultListAdapter =
                                activity?.let { activity ->
                                    ArrayAdapter(
                                        activity,
                                        R.layout.simple_list_item_1,
                                        busViewModel.stopsSchedules
                                    )
                                }!!
                            schedulesList.adapter = searchResultListAdapter
                        }
                    }
                }
            }
        })
        schedulesList.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val options: String = parent?.getItemAtPosition(position).toString()
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, options);
        startActivity(Intent.createChooser(shareIntent, "Send to"))
    }

}


package com.example.sum.ui.bus

import android.R
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.sum.databinding.FragmentBusBinding
import com.example.sum.utility.mainViewModel.MainViewModel
import com.example.sum.utility.mainViewModel.MainViewModelFactory
import com.example.sum.utility.model.data.stops.StopItem
import com.example.sum.utility.repository.Repository
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*


class BusFragment : Fragment(), AdapterView.OnItemClickListener {

    private var _binding: FragmentBusBinding? = null
    private lateinit var viewModel: MainViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val busViewModel = ViewModelProvider(this)[BusViewModel::class.java]
        _binding = FragmentBusBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /**fragment variables*/
        val startingPointOptions = binding.startingPointOptions
        val dateField = binding.scheduleDateField
        val schedulesList = binding.schedulesResultList
        val searchButton = binding.buttonSearchSchedule
        val dateFormat = SimpleDateFormat("dd/MM/yyyy") //HH:mm"

        /**starting point list*/
        //starting point options list
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)

        //call api
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        viewModel.getStops()
        viewModel.stops.observe(viewLifecycleOwner, Observer { response ->
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
        })

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

        /**date picker*/
        //material design date picker instantiation
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

        //listener for ok button inside date picker dialog
        datePicker.addOnPositiveButtonClickListener {
            val chosenDate = Date(it)
            dateField.setText(dateFormat.format(chosenDate))
        }

        /**date field*/
        //disable default keyboard for date input field
        dateField.showSoftInputOnFocus = false
        //set default date picker date to current date
        dateField.setText(dateFormat.format(MaterialDatePicker.todayInUtcMilliseconds()))
        //listener for date input field touch
        dateField.setOnTouchListener { _, event ->
            if (MotionEvent.ACTION_UP == event.action)
                datePicker.show(requireActivity().supportFragmentManager, "tag")
            false
        }

        /**search button listener*/
        searchButton.setOnClickListener {
            viewModel.getStopsSchedules(busViewModel.selectedStop.Stop_Id)
            viewModel.stopSchedule.observe(viewLifecycleOwner, Observer { response ->
                busViewModel.stopsSchedules.clear()
                if (response.isSuccessful) {
                    response.body()?.forEach {
                        for (stopSchedule in it.StopSchedule) {
                            val item = StopNameSchedule(
                                it.Line_Name,
                                busViewModel.selectedStop.Stop_Name,
                                stopSchedule.Schedule_Time
                            )
                            busViewModel.stopsSchedules.add(item)

                            /**search result list*/
                            val searchResultListAdapter: ArrayAdapter<StopNameSchedule>? =
                                activity?.let { activity ->
                                    ArrayAdapter(
                                        activity,
                                        R.layout.simple_list_item_1,
                                        busViewModel.stopsSchedules
                                    )
                                }
                            schedulesList.adapter = searchResultListAdapter
                        }
                    }
                }
            })
            schedulesList.visibility = View.VISIBLE
        }
        schedulesList.onItemClickListener = this

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val options: String = parent?.getItemAtPosition(position).toString()
        Toast.makeText(activity, "$options was clicked", Toast.LENGTH_LONG).show()
    }

}


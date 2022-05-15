package com.example.sum.ui.bus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sum.databinding.FragmentBusBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*


class BusFragment : Fragment(), AdapterView.OnItemClickListener {

    private var _binding: FragmentBusBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val busViewModel =
            ViewModelProvider(this)[BusViewModel::class.java]
        _binding = FragmentBusBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /**fragment variables*/
        val startingPointOptions = binding.startingPointOptions
        val dateField = binding.scheduleDateField
        val schedulesList = binding.schedulesResultList
        val searchButton = binding.buttonSearchSchedule
        val dateFormat = SimpleDateFormat("dd/MM/yyyy") //HH:mm"

        /*val textView: TextView = binding.textBus
        busViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/

        /**starting point list*/
        //starting point options list
        val startingPointListAdapter = activity?.let {
            ArrayAdapter(
                it,
                android.R.layout.simple_spinner_item,
                busViewModel.items
            )
        }
        startingPointOptions.adapter =
            startingPointListAdapter //set the list adapter for the starting point optins view element

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
        searchButton.setOnClickListener{
            Toast.makeText(activity, "Searching for ${startingPointOptions.selectedItem.toString()}'s schedule at ${dateField.text}", Toast.LENGTH_LONG).show()
            schedulesList.visibility = View.VISIBLE
        }

        /**search result list*/
        val searchResultListAdapter = activity?.let {
            ArrayAdapter(
                it,
                android.R.layout.simple_list_item_1,
                busViewModel.schedules
            )
        }
        schedulesList.adapter = searchResultListAdapter
        schedulesList.onItemClickListener = this

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val options: String = parent?.getItemAtPosition(position) as String
        Toast.makeText(activity, "$options was clicked", Toast.LENGTH_LONG).show()
    }
}
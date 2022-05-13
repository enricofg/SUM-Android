package com.example.sum.ui.bus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sum.databinding.FragmentBusBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*


class BusFragment : Fragment() {

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
            ViewModelProvider(this).get(BusViewModel::class.java)

        _binding = FragmentBusBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val startingPointOptions = binding.startingPointOptions
        val dateField = binding.scheduleDateField

        /*val textView: TextView = binding.textBus
        busViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/

        //starting point list
        val adapter = activity?.let {
            ArrayAdapter(
                it,
                android.R.layout.simple_spinner_item,
                busViewModel.items
            )
        }
        startingPointOptions.adapter = adapter

        //date picker
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

        //listener for ok button inside date picker dialog
        datePicker.addOnPositiveButtonClickListener {
            val chosenDate = Date(it)
            val format = SimpleDateFormat("dd/MM/yyyy") //HH:mm"
            dateField.setText(format.format(chosenDate))
        }

        //disable default keyboard for date input field
        dateField.showSoftInputOnFocus = false

        //listener for date input field touch
        dateField.setOnTouchListener { _, event ->
            if (MotionEvent.ACTION_UP == event.action)
                datePicker.show(requireActivity().supportFragmentManager, "tag")
            false
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
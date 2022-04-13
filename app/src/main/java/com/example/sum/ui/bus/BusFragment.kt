package com.example.sum.ui.bus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sum.databinding.FragmentBusBinding

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

        val textView: TextView = binding.textBus
        busViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
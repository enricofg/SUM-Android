package com.example.sum.ui.camera

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.sum.databinding.FragmentCameraBinding

class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val searchButton = binding.buttonOpenARNavigation
        val ocrButton = binding.buttonOpenOCR

        searchButton.setOnClickListener{
            activity?.let{
                val intent = Intent (it, GeoCameraActivity::class.java)
                it.startActivity(intent)
            }
        }

        ocrButton.setOnClickListener{
            activity?.let{
                val intent = Intent (it, StillImageActivity::class.java)
                it.startActivity(intent)
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
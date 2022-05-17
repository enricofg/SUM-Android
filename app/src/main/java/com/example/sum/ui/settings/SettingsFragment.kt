package com.example.sum.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.sum.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val languages = arrayOf("English", "Portuguese") //TODO: add flags next to text
        val themes = arrayOf("Light", "Dark") //TODO: add flags next to text
        val root: View = binding.root
        val languageOptions = binding.languageOptions
        val themesOptions = binding.themesOptions

        /**languages options list*/
        //languages options list
        val languageOptionsListAdapter = activity?.let {
            ArrayAdapter(
                it,
                android.R.layout.simple_spinner_item,
                languages
            )
        }
        languageOptions.adapter =
            languageOptionsListAdapter //set the list adapter for the starting point optins view element

        languageOptions?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 1) {
                    activity?.let {
                        val context = LocaleHelper.setLocale(it, "en");

                    }
                } else if (position == 2) {
                    activity?.let {
                        val context = LocaleHelper.setLocale(it, "pt");
                        val resources = context.resources;
                    }
                }
            }

        }

        /**languages options list*/
        //languages options list
        val themeOptionsListAdapter = activity?.let {
            ArrayAdapter(
                it,
                android.R.layout.simple_spinner_item,
                themes
            )
        }
        themesOptions.adapter =
            themeOptionsListAdapter

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
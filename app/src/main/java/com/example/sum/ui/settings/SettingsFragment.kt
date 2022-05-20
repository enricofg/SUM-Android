package com.example.sum.ui.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.sum.R
import com.example.sum.databinding.FragmentSettingsBinding


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val sharedPreferences =
            requireActivity().getSharedPreferences("APP_SETTINGS", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val languages = arrayOf(
            getString(R.string.choose_language), getString(R.string.english), getString(
                R.string.portuguese
            )
        ) //TODO: add country flags next to text
        val themes = arrayOf(
            getString(R.string.choose_theme), getString(R.string.light), getString(
                R.string.dark
            ), getString(R.string.auto)
        )
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

        var isLanguageOptionsTouched = false
        languageOptions.setOnTouchListener { _, _ ->
            isLanguageOptionsTouched = true
            false
        }

        languageOptions.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (!isLanguageOptionsTouched) return
                when (parent?.getItemAtPosition(position).toString()) {
                    //TODO: change the language of all items in the app according to the selected choice
                    "English" -> {
                        /**english selected*/
                        activity?.let {
                            Toast.makeText(
                                it.applicationContext,
                                parent?.getItemAtPosition(position).toString() + "English chosen",
                                Toast.LENGTH_LONG
                            ).show()
/*                            LocaleHelper.setLocale(it.applicationContext, "en") //set language to english
                            (activity as MainActivity).restartApp()*/
                        }
                    }
                    "Portuguese" -> {
                        /**portuguese selected*/
                        activity?.let {
                            Toast.makeText(
                                it.applicationContext,
                                parent?.getItemAtPosition(position)
                                    .toString() + "Portuguese chosen",
                                Toast.LENGTH_LONG
                            ).show()
/*                            LocaleHelper.setLocale(it.applicationContext, "pt") //set language to english
                            (activity as MainActivity).restartApp()*/
                        }
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

        var isThemesOptionsTouched = false
        themesOptions.setOnTouchListener { _, _ ->
            isThemesOptionsTouched = true
            false
        }

        themesOptions.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (!isThemesOptionsTouched) return
                when (parent?.getItemAtPosition(position).toString()) {
                    "Light" -> {
                        /**light theme selected*/
                        activity?.let {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                            editor.putString("theme", "light")
                            editor.commit()
                        }
                    }
                    "Dark" -> {
                        /**dark theme selected*/
                        activity?.let {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                            editor.putString("theme", "dark")
                            editor.commit()
                        }
                    }
                    "Auto" -> {
                        /**auto theme color selected*/
                        activity?.let {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                            editor.putString("theme", "auto")
                            editor.commit()
                        }
                    }
                }
            }
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
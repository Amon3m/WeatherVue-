package com.m3.weathervue.settings.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.productsmvvm.network.ApiClient
import com.m3.weathervue.R
import com.m3.weathervue.database.ConcreteLocalSource
import com.m3.weathervue.databinding.FragmentSettingsBinding
import com.m3.weathervue.home.view.HomeFragmentDirections
import com.m3.weathervue.home.viewmodel.SettingsViewModel
import com.m3.weathervue.home.viewmodel.SettingsViewModelFactory
import com.m3.weathervue.model.PreferenceManager
import com.m3.weathervue.model.Repository
import java.util.*


class SettingsFragment : Fragment() {
    lateinit var settingsViewModel: SettingsViewModel
    lateinit var settingsViewModelFactory: SettingsViewModelFactory
lateinit var binding:FragmentSettingsBinding
lateinit var preferenceManager: PreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settingsViewModelFactory = SettingsViewModelFactory(
            Repository.getInstance(
                ApiClient,
                ConcreteLocalSource(requireContext())
            )
        )
        settingsViewModel = ViewModelProvider(this, settingsViewModelFactory).get(SettingsViewModel::class.java)

        preferenceManager = PreferenceManager.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var lang:String=""


        when(settingsViewModel.getSavedTemperatureUnit(requireContext())){
            "c" -> { binding.radioGroupTemp.check(R.id.celsius_rbtn)
            }
            "k" -> { binding.radioGroupTemp.check(R.id.kelvin_rbtn)

            }
            "f" -> { binding.radioGroupTemp.check(R.id.fahrenheit_rbtn)}
        }


        binding.radioGroupTemp.setOnCheckedChangeListener{ group, checkedId ->
            var unit :String=""
            when (checkedId) {
                R.id.celsius_rbtn -> {
                    unit = "c"
                }
                R.id.kelvin_rbtn -> {
                    unit = "k"

                }
                R.id.fahrenheit_rbtn -> unit = "f"
            }
            settingsViewModel.chooseTemperature(unit,requireContext())
            settingsViewModel.updateSettingsChangeFlag()
        }

        when(settingsViewModel.getSavedLocationMode(requireContext())){
            "gps" -> { binding.radioGroupLoc.check(R.id.gps_rbtn)
            }
            "map" -> { binding.radioGroupLoc.check(R.id.map_rbtn)

            }
        }

        binding.radioGroupLoc.setOnCheckedChangeListener{ group, checkedId ->
            when (checkedId) {
                R.id.gps_rbtn -> {
                    settingsViewModel.updateGpsMode(true)
                    settingsViewModel.updateMapMode(false)
                    settingsViewModel.updateSettingsChangeFlag()

                    val action: SettingsFragmentDirections.ActionSettingsFragmentToHomeFragment=
                        SettingsFragmentDirections.actionSettingsFragmentToHomeFragment(0,0,false)

                    Navigation.findNavController(view).navigate(action)
                }
                R.id.map_rbtn -> {
                    settingsViewModel.updateGpsMode(false)
                    settingsViewModel.updateMapMode(true)
                    settingsViewModel.updateSettingsChangeFlag()

                    val action:SettingsFragmentDirections.ActionSettingsFragmentToMapsFragment=
                        SettingsFragmentDirections.actionSettingsFragmentToMapsFragment("HomeFragment")
                    Navigation.findNavController(requireView()).navigate(action)
                }
            }
        }

        if (!preferenceManager.language.isNullOrEmpty()){
        when(preferenceManager.language){
            "en" -> { binding.radioGroupLang.check(R.id.eng_rbtn)
                setAppLocale("en")


            }
            "ar" -> { binding.radioGroupLang.check(R.id.arb_rbtn)
                setAppLocale("ar")

                }
            }
        }
        binding.radioGroupLang.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.arb_rbtn -> {
                    lang = "ar"
                }
                R.id.eng_rbtn-> {
                    lang = "en"

                }
            }
            preferenceManager.language=lang

            settingsViewModel.updateSettingsChangeFlag()

            setAppLocale(lang)
            restartApp(requireContext())

        }

    }
    fun restartApp(context: Context) {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        context.startActivity(intent)
        if (context is Activity) {
            context.finish()
        }
    }
    private fun setAppLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val configuration = Configuration()
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }
}
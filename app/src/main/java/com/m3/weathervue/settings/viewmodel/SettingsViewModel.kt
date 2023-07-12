package com.m3.weathervue.home.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import com.example.productsmvvm.network.ApiState
import com.google.android.gms.maps.model.LatLng
import com.m3.weathervue.R
import com.m3.weathervue.home.view.HomeFragmentDirections
import com.m3.weathervue.model.PreferenceManager
import com.m3.weathervue.model.RepoInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class SettingsViewModel(private val repoInterface: RepoInterface) : ViewModel() {
    lateinit var preferenceManager: PreferenceManager
    private var _settingsChangeFlag = MutableStateFlow<Boolean>(false)
    val settingsChangeFlag: StateFlow<Boolean>
        get() = _settingsChangeFlag

    


    fun chooseTemperature(unit: String, context: Context) {
        preferenceManager = PreferenceManager.getInstance(context)
        preferenceManager.temperatureMode = unit
    }

    fun getSavedTemperatureUnit(context: Context): String {
        preferenceManager = PreferenceManager.getInstance(context)

        if (preferenceManager.temperatureMode != null)
            return preferenceManager.temperatureMode as String
        return ""
    }

    fun updateSettingsChangeFlag() {
        _settingsChangeFlag.value = !_settingsChangeFlag.value
        Log.e("flag","$_settingsChangeFlag")


    }

    fun getSavedLocationMode(context: Context): String {
        preferenceManager = PreferenceManager.getInstance(context)
        var mode:String=""
        if (preferenceManager.isGpsMode){
            mode="gps"}
        else if(preferenceManager.isMapMode){
            mode="map"}
        return mode

    }
    fun updateGpsMode(boolean: Boolean){
        preferenceManager.isGpsMode=boolean

    }
    fun updateMapMode(boolean: Boolean){
        preferenceManager.isMapMode=boolean

    }

    fun chooseLanguage(lang: String, requireContext: Context){
        preferenceManager = PreferenceManager.getInstance(requireContext)
        preferenceManager.language = lang

    }
}
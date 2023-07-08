package com.m3.weathervue.map.viewmodel

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MapsViewModel: ViewModel() {
        private val _locationFlow = MutableStateFlow<LatLng?>(null)
        val locationFlow: StateFlow<LatLng?>
            get() = _locationFlow

    private val _favLocationFlow = MutableStateFlow<LatLng?>(null)
    val favLocationFlow: StateFlow<LatLng?>
        get() = _favLocationFlow

        fun updateLocation(latLng: LatLng) {
            _locationFlow.value = latLng
        }
        fun updateFavLocation(latLng: LatLng) {
            _favLocationFlow.value = latLng
        }




}
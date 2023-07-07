package com.m3.weathervue.map.viewmodel

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MapsViewModel: ViewModel() {
        private val _locationFlow = MutableStateFlow<LatLng?>(null)
        val locationFlow: StateFlow<LatLng?>
            get() = _locationFlow

        fun updateLocation(latLng: LatLng) {
            _locationFlow.value = latLng
        }

}
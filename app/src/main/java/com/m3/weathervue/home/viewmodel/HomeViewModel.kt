package com.m3.weathervue.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.productsmvvm.network.ApiState
import com.m3.weathervue.model.RepoInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(private val repoInterface: RepoInterface) : ViewModel()  {
    private var _weather = MutableStateFlow<ApiState>(ApiState.Loading)
    val weather: StateFlow<ApiState>
        get() = _weather

    init {

       // getWeather()
    }



    fun getWeather( lat: Double,
                    lon: Double,
                   ) {
        viewModelScope.launch(Dispatchers.IO) {

            repoInterface.getWeatherFromNetwork(lat,lon, units = "metric", exclude = "",lang = "en", appid = "ccb811f49ff661e0a43e8d8727e0387a").catch { e->
                _weather.emit(ApiState.Failure(e.message?:""))
            }.collect {
                var products = it
                _weather.emit(ApiState.Success(products))
            }

        }
    }
}
package com.example.productsmvvm.network

import com.m3.weathervue.model.WeatherResponse

sealed class ApiState{
    class Success(val data : WeatherResponse) : ApiState()
    class Failure(val msg: String) : ApiState()
    object Loading : ApiState()
}

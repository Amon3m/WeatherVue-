package com.m3.weathervue.network

import com.example.productsmvvm.network.RemoteSource
import com.m3.weathervue.model.*

class FakeRemoteSource(private val weatherResponse: WeatherResponse) : RemoteSource {
    override suspend fun getWeatherFromNetwork(
        lat: Double,
        lon: Double,
        exclude: String,
        units: String,
        lang: String,
        appid: String
    ): WeatherResponse {
        return weatherResponse
    }
}

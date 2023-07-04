package com.example.productsmvvm.network

import com.m3.weathervue.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.Query

interface RemoteSource {
    suspend fun getWeatherFromNetwork(
      lat: Double=0.0,
      lon: Double=0.0,
      exclude:String="",
      units:String="",
      lang:String="",
      appid:String="ccb811f49ff661e0a43e8d8727e0387a"
    ): WeatherResponse
}
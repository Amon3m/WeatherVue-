package com.example.productsmvvm.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.m3.weathervue.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {
    @GET("onecall?")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("exclude") exclude:String,
        @Query("units") units:String,
        @Query("lang") lang:String,
    @Query("appid") appid:String="ccb811f49ff661e0a43e8d8727e0387a"
    ): WeatherResponse
}
package com.example.productsmvvm.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.m3.weathervue.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

object ApiClient : RemoteSource {

    override suspend fun getWeatherFromNetwork(
        lat: Double,
        lon: Double,
        exclude: String,
        units: String,
        lang: String,
        appid: String
    ): WeatherResponse {
        return Api.apiService.getWeather( lat, lon, exclude, units, lang, appid)
    }
}

object RetrofitHelper {
    var gson: Gson = GsonBuilder().create()
    var retrofitInstance: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
}

object Api {
    val apiService: ApiService by lazy {
        RetrofitHelper.retrofitInstance.create(ApiService::class.java)
    }
}
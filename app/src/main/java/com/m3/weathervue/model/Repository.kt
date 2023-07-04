package com.m3.weathervue.model


import com.example.productsmvvm.network.RemoteSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import retrofit2.Response


class Repository private constructor(
    var remoteSource: RemoteSource
) : RepoInterface {


    companion object {
        private var instance: Repository? = null
        fun getInstance(
            remoteSource: RemoteSource,
        ): Repository {
            return instance ?: synchronized(this) {
                val temp = Repository(remoteSource)
                instance = temp
                temp
            }
        }
    }



    override suspend fun getWeatherFromNetwork(
        lat: Double,
        lon: Double,
        exclude: String,
        units: String,
        lang: String,
        appid: String
    ): Flow<WeatherResponse> {
        return flowOf(remoteSource.getWeatherFromNetwork(lat,lon,exclude,units,lang,appid))
    }


}
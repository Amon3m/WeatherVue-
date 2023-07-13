package com.m3.weathervue.model


import com.example.productsmvvm.network.RemoteSource
import com.m3.weathervue.database.ConcreteLocalSource
import com.m3.weathervue.database.LocalSource
import kotlinx.coroutines.flow.*


class Repository private constructor(
    var remoteSource: RemoteSource,var concreteLocalSource: LocalSource
) : RepoInterface {


    companion object {
        private var instance: Repository? = null
        fun getInstance(
            remoteSource: RemoteSource,
            concreteLocalSource: LocalSource
        ): Repository {
            return instance ?: synchronized(this) {
                val temp = Repository(remoteSource, concreteLocalSource)
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

    override fun getFavoritesFromDatabase(): Flow<List<FavoritesModel>> {
        return concreteLocalSource.getFavoritesLocation()
    }

    override suspend fun addToFavorites(favorite: FavoritesModel) {
        concreteLocalSource.insert(favorite)
    }

    override suspend fun deleteFromFavorites(Favorite: FavoritesModel) {
        concreteLocalSource.delete(Favorite)
    }


}
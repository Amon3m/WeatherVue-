package com.m3.weathervue.model

import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface RepoInterface {
   // suspend fun getProductsFromNetwork(): Flow<List<Product>>
    suspend fun getWeatherFromNetwork(
        lat: Double,
        lon: Double,
        exclude:String,
        units:String,
        lang:String,
        appid:String
    ): Flow<WeatherResponse>
    fun getFavoritesFromDatabase(): Flow<List<FavoritesModel>>
    suspend fun addToFavorites(favorite: FavoritesModel)
    suspend fun deleteFromFavorites(Favorite: FavoritesModel)
}
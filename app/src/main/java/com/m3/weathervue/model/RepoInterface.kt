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
//    fun getProductsFromDatabase(): Flow<List<Product>>
//    suspend fun cacheProducts(products: List<Product>)
//    suspend fun addToFavorites(product: Product)
//    suspend fun deleteFromFavorites(product: Product)
}
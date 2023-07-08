package com.m3.weathervue.database

import com.m3.weathervue.model.FavoritesModel
import kotlinx.coroutines.flow.Flow

interface LocalSource {

    suspend fun insert(favoriteLocation: FavoritesModel)
    suspend fun delete(favoriteLocation: FavoritesModel)
    fun getFavoritesLocation(): Flow<List<FavoritesModel>>
}
package com.m3.weathervue.database

import android.content.Context
import com.m3.weathervue.model.FavoritesModel
import kotlinx.coroutines.flow.Flow

class ConcreteLocalSource(private val context: Context) : LocalSource  {

    private val dao: LocationDao by lazy {
        val db: FavoritesDatabase = FavoritesDatabase.getInstance(context)
        db.getLocationDao()
    }
    override suspend fun insert(favoriteLocation: FavoritesModel) {
        dao.insertFavorite(favoriteLocation)
    }

    override suspend fun delete(favoriteLocation: FavoritesModel) {
        dao.deleteFavorite(favoriteLocation)
    }

    override fun getFavoritesLocation(): Flow<List<FavoritesModel>> {
    return dao.getAll()
        }
}
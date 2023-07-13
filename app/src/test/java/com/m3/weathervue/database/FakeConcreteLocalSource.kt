package com.m3.weathervue.database

import com.m3.weathervue.model.FavoritesModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeConcreteLocalSource(private var tasks: MutableList<FavoritesModel> = mutableListOf()) : LocalSource {
    override suspend fun insert(favoriteLocation: FavoritesModel) {
        tasks.add(favoriteLocation)
    }

    override suspend fun delete(favoriteLocation: FavoritesModel) {
        tasks.remove(favoriteLocation)
    }

    override fun getFavoritesLocation(): Flow<List<FavoritesModel>> {
        return flowOf(tasks.toList())
        }
    }


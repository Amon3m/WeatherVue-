package com.m3.weathervue.database

import androidx.room.*
import com.m3.weathervue.model.FavoritesModel
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavorite(favoritesModel: FavoritesModel)

    @Delete
    suspend fun deleteFavorite(favoritesModel: FavoritesModel)

    @Query("SELECT * FROM favorite")
    fun getAll(): Flow<List<FavoritesModel>>
}
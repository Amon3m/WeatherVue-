package com.m3.weathervue.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.m3.weathervue.model.FavoritesModel

@Database(entities = [FavoritesModel::class], version = 1)
abstract class FavoritesDatabase : RoomDatabase(){
    abstract fun getLocationDao(): LocationDao
    companion object {
        @Volatile
        private var INSTANCE: FavoritesDatabase? = null
        fun getInstance(ctx: Context): FavoritesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    ctx.applicationContext, FavoritesDatabase::class.java, "favorite_database"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}
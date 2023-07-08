package com.m3.weathervue.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "favorite")
data class FavoritesModel(
    @PrimaryKey

    val locName:String,
    val longitudeval :Double,
    val latitude:Double
)

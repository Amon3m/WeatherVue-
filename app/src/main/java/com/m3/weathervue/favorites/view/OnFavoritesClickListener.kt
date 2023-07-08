package com.m3.weathervue.favorites.view

import com.m3.weathervue.model.FavoritesModel

interface OnFavoritesClickListener {
    fun onFavClick(favoritesModel: FavoritesModel)
    fun onDeleteClick(favoritesModel: FavoritesModel)


}
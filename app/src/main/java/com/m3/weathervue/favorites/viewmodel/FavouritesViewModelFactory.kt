package com.m3.weathervue.favorites.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.m3.weathervue.model.RepoInterface

class FavouritesViewModelFactory(private val _repo: RepoInterface) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass::class.java.isInstance(FavoritesViewModel::class.java)) {
            FavoritesViewModel(_repo) as T
        } else {
            throw IllegalArgumentException("View Model class not found")
        }
    }


}
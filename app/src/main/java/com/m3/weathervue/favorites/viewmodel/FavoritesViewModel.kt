package com.m3.weathervue.favorites.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.m3.weathervue.model.FavoritesModel
import com.m3.weathervue.model.RepoInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(private val repoInterface: RepoInterface) : ViewModel() {

    private val _favLocations = MutableStateFlow<List<FavoritesModel>>(listOf())
    val favLocations: StateFlow<List<FavoritesModel>>
        get() = _favLocations


    private val _isFromFavFlow = MutableStateFlow<Boolean>(false)
    val isFromFavFlow: StateFlow<Boolean>
        get() = _isFromFavFlow


    private var _favToHome =MutableStateFlow<LatLng?>(null)
    val favToHome: StateFlow<LatLng?>
        get() = _favToHome

    fun updateFavToHome(latLng: LatLng) {
        _favToHome.value = latLng
    }

    fun updateIsFromFav(isFromFavFlow: Boolean) {
        _isFromFavFlow.value =isFromFavFlow
    }

    init {
        getFavLocations()    }

     fun getFavLocations() {
        viewModelScope.launch(Dispatchers.IO) {
            repoInterface.getFavoritesFromDatabase().collect {
                _favLocations.emit(it)
            }
        }
    }

    fun removeFromFav(favoriteItem: FavoritesModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repoInterface.deleteFromFavorites(favoriteItem)
        }
    }


    fun addToFavorites(favoriteItem: FavoritesModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repoInterface.addToFavorites(favoriteItem)
        }
    }
}
package com.m3.weathervue.favorites.viewmodel

import com.google.android.gms.maps.model.LatLng
import com.m3.weathervue.model.FakeRepo
import com.m3.weathervue.model.FavoritesModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class FavoritesViewModelTest {
    private lateinit var viewModel: FavoritesViewModel
    private lateinit var fakeRepo: FakeRepo

    @Before
    fun setUp() {
        fakeRepo = FakeRepo()
        viewModel = FavoritesViewModel(fakeRepo)
    }



    @Test
    fun whenAddToFavorites_thenFavoriteAddedToRepo() = runBlockingTest {
        // Given
        val favorite = FavoritesModel("france", 4321.0, 5678.0)

        // When
        viewModel.addToFavorites(favorite)

        // Then
        assertEquals(listOf(favorite), fakeRepo.favLocations.value)
    }


    @Test
    fun givenLatLngToUpdate_whenUpdateFavToHome_thenFavToHomeUpdated() = runBlockingTest {
        // Given
        val latLng = LatLng(12.34, 56.78)
        val favToHome = mutableListOf<LatLng?>()

        // When
        val job = launch(Dispatchers.IO) {
            viewModel.favToHome.collect {
                favToHome.add(it)
            }
        }
        viewModel.updateFavToHome(latLng)
        job.cancel()

        // Then
        assertEquals(latLng, favToHome)
    }

    @Test
    fun givenIsFromFavFlowToUpdate_whenUpdateIsFromFav_thenIsFromFaveEqualTrue() = runBlocking {
        // Given
        val isFromFavFlow = true
        val isFromFavFlowObserver = mutableListOf<Boolean>()

        // When
        val job = launch(Dispatchers.IO) {
            viewModel.isFromFavFlow.collect {
                isFromFavFlowObserver.add(it)
            }
        }
        viewModel.updateIsFromFav(isFromFavFlow)
        job.cancelAndJoin()

        // Then
        val lastValue = isFromFavFlowObserver.lastOrNull()
        assertNotNull(lastValue)
        assertEquals(isFromFavFlow, lastValue)
    }

}

package com.m3.weathervue.model

import com.m3.weathervue.database.FakeConcreteLocalSource
import com.m3.weathervue.network.FakeRemoteSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class RepositoryTest{
    val task1=FavoritesModel("egypt",122222.1,22222.5)
    val task2=FavoritesModel("usa",122222.1,22222.5)
    val task3=FavoritesModel("ksa",122222.1,22222.5)
    val response=WeatherResponse(
        current = Current(
            sunrise = 1626123456,
            temp = "25.5",
            visibility = 10000,
            uvi = 8.2f,
            pressure = 1012,
            clouds = 30,
            feelsLike = null,
            windGust = null,
            dt = null,
            windDeg = null,
            dewPoint = null,
            sunset = null,
            weather = listOf(
                WeatherItem(
                    icon = "01d",
                    description = "Clear sky",
                    main = "Clear",
                    id = 800
                )
            ),
            humidity = 50,
            windSpeed = null
        ),
        timezone = "America/New_York",
        timezoneOffset = -14400,
        daily = listOf(
            DailyItem(
                dt = 1626105600,
                temp = Temp(
                    min = "20.2",
                    max = "28.5",
                    eve = null,
                    night = null,
                    day = null,
                    morn = null
                ),
                moonset = null,
                sunrise = null,
                moonPhase = null,
                uvi = null,
                moonrise = null,
                pressure = null,
                clouds = null,
                feelsLike = FeelsLike(
                    eve = null,
                    night = null,
                    day = null,
                    morn = null
                ),
                windGust = null,
                dewPoint = null,
                sunset = null,
                weather = listOf(
                    WeatherItem(
                        icon = "01d",
                        description = "Clear sky",
                        main = "Clear",
                        id = 800
                    )
                ),
                humidity = null,
                windSpeed = null,
                pop = null
            )
        ),
        lon = 4.4,
        hourly = listOf(
            HourlyItem(
                dt = 1626123600,
                temp = "24.8",
                visibility = 8000f,
                uvi = 7.5,
                pressure = 1011,
                clouds = 40,
                feelsLike = null,
                windGust = null,
                dewPoint = null,
                weather = listOf(
                    WeatherItem(
                        icon = "01d",
                        description = "Clear sky",
                        main = "Clear",
                        id = 800
                    )
                ),
                humidity = 60,
                windSpeed = null
            )
        ),
        lat = 44.4
    )

    val localList= mutableListOf(task1,task2,task3)
    lateinit var fakeConcreteLocalSource: FakeConcreteLocalSource
    lateinit var fakeRemoteSource: FakeRemoteSource
    lateinit var repository: Repository
    @Before
    fun setup(){
        fakeConcreteLocalSource= FakeConcreteLocalSource(localList)
        fakeRemoteSource= FakeRemoteSource(response)
        repository = Repository.getInstance(fakeRemoteSource, fakeConcreteLocalSource)
    }
    @Test
    fun getFavoritesInDatabase_whenGetFavoritesFromDatabase_ReturnsExpectedFavorites() = runBlockingTest() {
        // Given
        val expectedFavorites = listOf(task1, task2, task3)

        // When
        val favoritesFlow = repository.getFavoritesFromDatabase()
        val actualFavorites = mutableListOf<FavoritesModel>()
        favoritesFlow.collect { favorites ->
            actualFavorites.addAll(favorites)
        }

        // Then
        assertEquals(expectedFavorites, actualFavorites)
    }

    @Test
    fun insert_whenAddToFavorites_returnWithNewAdded() = runBlockingTest {
        // Given
        val newFavorite = FavoritesModel("france", 4321.0, 5678.0)

        // When
        repository.addToFavorites(newFavorite)
        val favoritesFlow = repository.getFavoritesFromDatabase()
        val favorites = mutableListOf<FavoritesModel>()
        favoritesFlow.collect { favs ->
            favorites.addAll(favs)
        }

        // Then
        assertTrue(favorites.contains(newFavorite))
    }

    @Test
    fun delete_whenDeleteFromFavorites_FavoriteIsRemovedFromDatabase() = runBlockingTest {
        // Given
        val favoriteToDelete = task2

        // When
        repository.deleteFromFavorites(favoriteToDelete)
        val favoritesFlow = repository.getFavoritesFromDatabase()
        val favorites = mutableListOf<FavoritesModel>()
        favoritesFlow.collect { favs ->
            favorites.addAll(favs)
        }

        // Then
        assertFalse(favorites.contains(favoriteToDelete))
    }


    @Test
    fun getWeatherFromNetwork_whenGetWeatherFromNetwork_returnsExpectedResponseEqualsActualResponse() = runBlockingTest() {
        // Given
        val lat = 12.34
        val lon = 56.78
        val exclude = "minutely"
        val units = "metric"
        val lang = "en"
        val appid = "1222244"
        val expectedResponse = response

        // When
        var actualResponse: WeatherResponse? = null
        repository.getWeatherFromNetwork(lat, lon, exclude, units, lang, appid).collect { response ->
            actualResponse = response
        }

        // Then
        assertEquals(expectedResponse, actualResponse)
    }

}
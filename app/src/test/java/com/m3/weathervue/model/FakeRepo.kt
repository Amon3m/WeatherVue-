package com.m3.weathervue.model

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
class FakeRepo(private val favList: MutableList<FavoritesModel> = mutableListOf()) : RepoInterface {
    private val weatherResponse: WeatherResponse = WeatherResponse(
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

    private val _favLocations = MutableStateFlow<List<FavoritesModel>>(favList)
    val favLocations: StateFlow<List<FavoritesModel>>
        get() = _favLocations

    private val _isFromFavFlow = MutableStateFlow<Boolean>(false)
    val isFromFavFlow: StateFlow<Boolean>
        get() = _isFromFavFlow

    private val _favToHome = MutableStateFlow<LatLng?>(null)
    val favToHome: StateFlow<LatLng?>
        get() = _favToHome

    override suspend fun getWeatherFromNetwork(
        lat: Double,
        lon: Double,
        exclude: String,
        units: String,
        lang: String,
        appid: String
    ): Flow<WeatherResponse> {
        return flow {
            emit(weatherResponse)
        }
    }

    override fun getFavoritesFromDatabase(): Flow<List<FavoritesModel>> {
        return _favLocations
    }

    override suspend fun addToFavorites(favorite: FavoritesModel) {
        favList.add(favorite)
        _favLocations.value = favList.toList()
    }


    override suspend fun deleteFromFavorites(favorite: FavoritesModel) {
        favList.remove(favorite)
        _favLocations.value = favList
    }

    fun setFavLocations(list: List<FavoritesModel>) {
        favList.clear()
        favList.addAll(list)
        _favLocations.value = favList
    }


}

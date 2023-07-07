package com.m3.weathervue

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager private constructor(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val PREF_FIRST_TIME = "pref_first_time"
        private const val PREF_LATITUDE = "pref_latitude"
        private const val PREF_LONGITUDE = "pref_longitude"
        private const val PREF_LANGUAGE = "pref_language"
        private const val PREF_IS_GPS_LOCATION = "pref_is_gps_location"
        private const val PREF_IS_MAP_LOCATION = "pref_is_map_location"

        private const val PREF_TEMPERATURE_MODE = "pref_temperature_mode"
        private const val PREF_WIND_MODE = "pref_wind_mode"

        @Volatile
        private var INSTANCE: PreferenceManager? = null

        fun getInstance(context: Context): PreferenceManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PreferenceManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    var isFirstTimeLaunch: Boolean
        get() = sharedPreferences.getBoolean(PREF_FIRST_TIME, true)
        set(value) = sharedPreferences.edit().putBoolean(PREF_FIRST_TIME, value).apply()

    var latitude: Long
        get() = sharedPreferences.getLong(PREF_LATITUDE, 0)
        set(value) = sharedPreferences.edit().putLong(PREF_LATITUDE, value).apply()

    var longitude: Long
        get() = sharedPreferences.getLong(PREF_LONGITUDE, 0)
        set(value) = sharedPreferences.edit().putLong(PREF_LONGITUDE, value).apply()

    var language: String?
        get() = sharedPreferences.getString(PREF_LANGUAGE, null)
        set(value) = sharedPreferences.edit().putString(PREF_LANGUAGE, value).apply()

    var isGpsMode: Boolean
        get() = sharedPreferences.getBoolean(PREF_IS_GPS_LOCATION, false)
        set(value) = sharedPreferences.edit().putBoolean(PREF_IS_GPS_LOCATION, value).apply()

    var isMapMode: Boolean
        get() = sharedPreferences.getBoolean(PREF_IS_MAP_LOCATION, false)
        set(value) = sharedPreferences.edit().putBoolean(PREF_IS_MAP_LOCATION, value).apply()


    var temperatureMode: String?
        get() = sharedPreferences.getString(PREF_TEMPERATURE_MODE, null)
        set(value) = sharedPreferences.edit().putString(PREF_TEMPERATURE_MODE, value).apply()

    var windMode: String?
        get() = sharedPreferences.getString(PREF_WIND_MODE, null)
        set(value) = sharedPreferences.edit().putString(PREF_WIND_MODE, value).apply()
}

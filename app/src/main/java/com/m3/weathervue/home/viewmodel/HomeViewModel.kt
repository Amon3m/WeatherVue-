package com.m3.weathervue.home.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import android.view.View
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.productsmvvm.network.ApiState
import com.m3.weathervue.R
import com.m3.weathervue.home.view.HomeFragmentDirections
import com.m3.weathervue.model.PreferenceManager
import com.m3.weathervue.model.RepoInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(private val repoInterface: RepoInterface) : ViewModel()  {
    lateinit var preferenceManager:PreferenceManager
    private var _weather = MutableStateFlow<ApiState>(ApiState.Loading)
    val weather: StateFlow<ApiState>
        get() = _weather

    init {

       // getWeather()
    }



    fun getWeather( lat: Double,
                    lon: Double,
                    context: Context,
                    view: View
                   ) {
        val unit=getSavedTemSettings(context)
        val lang=getSavedLangSettings(context)
        Log.e("lang","$lang")

        if (isNetworkAvailable(context)){


            viewModelScope.launch(Dispatchers.IO) {

                repoInterface.getWeatherFromNetwork(
                    lat,
                    lon,
                    units = unit,
                    exclude = "",
                    lang = lang,
                    appid = "ccb811f49ff661e0a43e8d8727e0387a"
                ).catch { e ->
                    _weather.emit(ApiState.Failure(e.message ?: ""))
                }.collect {
                    var products = it
                    _weather.emit(ApiState.Success(products))
                }

            }
        }else{
            showNoInternetDialogue(context,view)
        }
    }
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
    fun showNoInternetDialogue(context: Context,view:View) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.no_Internet))
            .setPositiveButton("retry") { dialog, _ ->

                refreshHome(view)
                dialog.dismiss()

            }

        builder.setCancelable(false)
        builder.create().show()


    }
    fun refreshHome(view:View){
        val action: HomeFragmentDirections.ActionHomeFragmentSelf =
            HomeFragmentDirections.actionHomeFragmentSelf(0,0,false)

        Navigation.findNavController(view).navigate(action)
    }

    fun getSavedTemSettings(context: Context):String{
        preferenceManager = PreferenceManager.getInstance(context)
        var unit=""
        if ( preferenceManager.temperatureMode!=null)
            when(preferenceManager.temperatureMode){
                "c" ,""-> {unit="metric"
                }
                "k" -> {unit=""

                }
                "f" -> { unit="imperial"
                }
            }
        return unit as String
    }

    fun getSavedLangSettings(context: Context):String{
        preferenceManager = PreferenceManager.getInstance(context)
        var lang=""
        if ( !preferenceManager.language.isNullOrEmpty())
            when (preferenceManager.language) {
                "ar" -> {
                    lang = "ar"
                }
                "en"-> {
                    lang = "en"

                }
            }
        return lang
    }
}
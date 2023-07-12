package com.m3.weathervue.home.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.productsmvvm.network.ApiClient
import com.example.productsmvvm.network.ApiState
import com.google.android.gms.location.*
import com.m3.weathervue.model.PreferenceManager
import com.m3.weathervue.R
import com.m3.weathervue.database.ConcreteLocalSource
import com.m3.weathervue.databinding.FragmentHomeBinding
import com.m3.weathervue.favorites.viewmodel.FavoritesViewModel
import com.m3.weathervue.favorites.viewmodel.FavouritesViewModelFactory
import com.m3.weathervue.home.viewmodel.HomeViewModel
import com.m3.weathervue.home.viewmodel.HomeViewModelFactory
import com.m3.weathervue.home.viewmodel.SettingsViewModel
import com.m3.weathervue.home.viewmodel.SettingsViewModelFactory
import com.m3.weathervue.map.viewmodel.MapsViewModel
import com.m3.weathervue.model.Repository
import com.m3.weathervue.model.WeatherResponse
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

const val PERMISSION_ID = 97

class HomeFragment : Fragment() {
    lateinit var homeViewModel: HomeViewModel
    lateinit var homeViewModelFactory: HomeViewModelFactory
    lateinit var binding: FragmentHomeBinding
    lateinit var hoursAdapter: HoursAdapter
    lateinit var dailyAdapter: DailyAdapter
    lateinit var myFusedLocationProviderClient: FusedLocationProviderClient
    lateinit var preferenceManager: PreferenceManager
    lateinit var mapsViewModel: MapsViewModel
    lateinit var favoritesViewModel: FavoritesViewModel
    lateinit var favouritesViewModelFactory: FavouritesViewModelFactory
    lateinit var settingsViewModelFactory: SettingsViewModelFactory
    lateinit var settingsViewModel: SettingsViewModel

    var gpsLat: Double = 0.0
    var gpsLong: Double = 0.0
    var isFromFav: Boolean = false
    lateinit var rootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferenceManager = PreferenceManager.getInstance(requireContext())

        myFusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        mapsViewModel = ViewModelProvider(requireActivity()).get(MapsViewModel::class.java)


        favouritesViewModelFactory =
            FavouritesViewModelFactory(
                Repository.getInstance(
                    ApiClient,
                    ConcreteLocalSource(requireContext())
                )
            )
        favoritesViewModel =
            ViewModelProvider(
                requireActivity(),
                favouritesViewModelFactory
            ).get(FavoritesViewModel::class.java)
        homeViewModelFactory = HomeViewModelFactory(
            Repository.getInstance(
                ApiClient,
                ConcreteLocalSource(requireContext())
            )
        )
        settingsViewModelFactory = SettingsViewModelFactory(
            Repository.getInstance(
                ApiClient,
                ConcreteLocalSource(requireContext())
            )
        )

        homeViewModel = ViewModelProvider(this, homeViewModelFactory).get(HomeViewModel::class.java)
        settingsViewModel =
            ViewModelProvider(this, settingsViewModelFactory).get(SettingsViewModel::class.java)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rootView = view
        Log.e("isFirstTimeLaunch", "${preferenceManager.isFirstTimeLaunch}")
        if (preferenceManager.isFirstTimeLaunch) {
            binding.back.visibility = View.GONE
            showFirstDialogue()
        }

        hoursAdapter = HoursAdapter(requireContext(), preferenceManager.temperatureMode,preferenceManager.language)
        dailyAdapter = DailyAdapter(requireContext(), preferenceManager.temperatureMode,preferenceManager.language)

        binding.hoursRecyclerview.apply {
            adapter = hoursAdapter
        }
        binding.weekRecyclerview.apply {
            adapter = dailyAdapter
        }

        if (preferenceManager.isMapMode && !isFromFav) {
            binding.back.visibility = View.GONE
            val lat = preferenceManager.latitude.toDouble()
            val lon = preferenceManager.longitude.toDouble()
            homeViewModel.getWeather(lat, lon, requireContext(), view)
        }
        lifecycleScope.launch {
            mapsViewModel.locationFlow.collect { latLng ->
                Log.e("locationFlow", "${latLng?.latitude}")
                if (!isFromFav) {
                    if (latLng != null) {
                        val lat = latLng.latitude
                        val lon = latLng.longitude
                        preferenceManager.latitude = lat.toLong()
                        preferenceManager.longitude = lon.toLong()
                        homeViewModel.getWeather(lat, lon, requireContext(), view)
                    }
                }
            }
        }
        lifecycleScope.launch {

            settingsViewModel.settingsChangeFlag.collect {
                Log.e("flag", "11")
                if (preferenceManager.isGpsMode) {
                    homeViewModel.getWeather(gpsLat, gpsLong, requireContext(), view)
                } else {
                    val latitude = preferenceManager.latitude.toDouble()
                    val longitude = preferenceManager.longitude.toDouble()
                    homeViewModel.getWeather(latitude, longitude, requireContext(), view)

                }

            }

        }
        lifecycleScope.launch {
            favoritesViewModel.isFromFavFlow.collect {
                isFromFav = it

                if (it) {
                    binding.back.visibility = View.VISIBLE
                    Log.e("isFromFavFlow", "$it")

                    lifecycleScope.launch {
                        favoritesViewModel.favToHome.collect { latLang ->

                            if (latLang != null) {

                                homeViewModel.getWeather(
                                    latLang.latitude,
                                    latLang.longitude,
                                    requireContext(), view
                                )
                            }
                        }
                    }

                }

            }
        }

        lifecycleScope.launch {
            homeViewModel.weather.collect {
                when (it) {
                    is ApiState.Success -> {
                        binding.animationView.visibility = View.GONE
                        binding.detCard.visibility = View.VISIBLE
                        binding.constraintLayout.visibility = View.VISIBLE
                        val data = it.data

                        hoursAdapter.submitList(data.hourly)
                        Log.e("from daily", "${data.daily?.get(0)?.dt}")

                        dailyAdapter.submitList(data.daily)

                        hoursAdapter.timeZone = data.timezone.toString()
                        bindData(data)
                        Log.e("lat and lon", "${data.current?.weather?.get(0)?.icon}")

                        Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()

                    }
                    is ApiState.Failure -> {
                        Toast.makeText(requireContext(), it.msg, Toast.LENGTH_SHORT).show()
                        // progressBar.visibility = View.GONE
                    }

                    else -> {
                        binding.detCard.visibility = View.GONE
                        binding.constraintLayout.visibility = View.GONE
                        Toast.makeText(requireContext(), "loading", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }

        binding.back.setOnClickListener {
            favoritesViewModel.updateIsFromFav(false)
            val action: HomeFragmentDirections.ActionHomeFragmentToFavoritesFragment =
                HomeFragmentDirections.actionHomeFragmentToFavoritesFragment(0, 0)
            Navigation.findNavController(requireView()).navigate(action)

        }
    }

    fun showFirstDialogue() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Access current location by")
            .setNegativeButton("GPS") { dialog, _ ->
                preferenceManager.isGpsMode = true
                preferenceManager.isFirstTimeLaunch = false
                getLastLocation()
                dialog.dismiss()
            }.setPositiveButton("Map") { dialog, _ ->

                val action: HomeFragmentDirections.ActionHomeFragmentToMapsFragment =
                    HomeFragmentDirections.actionHomeFragmentToMapsFragment("HomeFragment")
                Navigation.findNavController(requireView()).navigate(action)
                preferenceManager.isMapMode = true
                preferenceManager.isFirstTimeLaunch = false
                dialog.dismiss()

            }
        builder.setCancelable(false)

        builder.create().show()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun bindData(data: WeatherResponse) {
        if (preferenceManager.language=="ar"){
            binding.tempTxt.text = convertNumbersToArabic(data.current?.temp.toString().substringBefore("."))
            binding.HumidityNum.text =convertNumbersToArabic( data.current?.humidity.toString())
            binding.CloudNum.text = convertNumbersToArabic(data.current?.clouds.toString() )
            binding.VioletNum.text = convertNumbersToArabic(data.current?.uvi.toString() )
            binding.pressureNum.text = convertNumbersToArabic(data.current?.pressure.toString() )
            binding.visibilityNum.text = convertNumbersToArabic( data.current?.visibility.toString())
            binding.WindNum.text = convertNumbersToArabic( data.current?.windSpeed.toString())
        }else{
        binding.tempTxt.text = data.current?.temp.toString().substringBefore(".")
            binding.HumidityNum.text = data.current?.humidity.toString()
            binding.CloudNum.text = data.current?.clouds.toString()
            binding.VioletNum.text = data.current?.uvi.toString()
            binding.pressureNum.text = data.current?.pressure.toString()
            binding.visibilityNum.text = data.current?.visibility.toString()
            binding.WindNum.text = data.current?.windSpeed.toString()
        }
        binding.tempTxt.append("°" + preferenceManager.temperatureMode)
        Toast.makeText(requireContext(), "", Toast.LENGTH_SHORT).show()
        binding.dateTxt.text = convertDate(data.current?.dt, data.timezone)

        binding.weatherCon.text = data.current?.weather?.get(0)?.description.toString()

        val unit = when (preferenceManager.temperatureMode) {
            "c", "k" -> getString(R.string.meters_second)
            "f" -> getString(R.string.miles_hour)
            else -> ""
        }
        binding.WindNum.append(unit)


        val lat = data.lat as Double
        val lon = data.lon as Double
        var currIcon = data.current?.weather?.get(0)?.icon

        binding.locTxt.text = geocodingConvert(lat, lon)
        Log.e("lat and lon", "lat : $lat & long: $lon ")
        // currIcon="01n"
        bindIcon(currIcon)

    }

    private fun bindIcon(icon: String?) {
        var img = binding.imageView
        when (icon) {
            "01d" -> img.setImageResource(R.drawable.day_01d)
            "02d" -> img.setImageResource(R.drawable.day_02d)
            "03d" -> img.setImageResource(R.drawable.day_03d)
            "04d" -> img.setImageResource(R.drawable.day_04d)
            "09d", "10d" -> img.setImageResource(R.drawable.day_09_10d)
            "50d" -> img.setImageResource(R.drawable.day_50d)

            "01n" -> img.setImageResource(R.drawable.night_1n)
            "02n" -> img.setImageResource(R.drawable.night_02n)
            "03n" -> img.setImageResource(R.drawable.night_03n)
            "04n" -> img.setImageResource(R.drawable.night_04n)
            "09n", "10n" -> img.setImageResource(R.drawable.night_09n_10n)
            "50n" -> img.setImageResource(R.drawable.night_50n)


        }

    }

    fun geocodingConvert(lat: Double, lon: Double): String {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        var city = ""
        var country = ""
        var retryCount = 0

        while (retryCount < 3) {
            try {
                val addressList: List<Address>? = geocoder.getFromLocation(lat, lon, 1)
                if (addressList != null && addressList.isNotEmpty()) {
                    val address: Address = addressList[0]
                    city = address.adminArea ?: ""
                    country = address.countryName ?: ""
                    Log.e("loc", "City: $city, Country: $country")
                    address.adminArea
                }
                break
            } catch (e: IOException) {
                e.printStackTrace()
                retryCount++
            }
        }
        return "$city,$country"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertDate(dt: Long?, timeZone: String?): String {
        val dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(dt as Long), ZoneId.of(timeZone))
        val lang = homeViewModel.getSavedLangSettings(requireContext())

        var locale = Locale("en")
        if (lang == "ar") {
            locale = Locale(lang)
        }

        val dateFormatter = DateTimeFormatter.ofPattern("E، d MMM", locale)
        val formattedDate = dateTime.format(dateFormatter)

        if (lang == "ar") {
            val easternArabicNumerals = '\u0660'.toInt()..'٩'.toInt()
            val westernArabicNumerals = '0'.toInt()..'9'.toInt()
            val easternArabicDigits = easternArabicNumerals.map { it.toChar() }
            val westernArabicDigits = westernArabicNumerals.map { it.toChar() }

            val digitMap = westernArabicDigits.zip(easternArabicDigits).toMap()
            val easternArabicFormattedDate = formattedDate.map { digitMap.getOrElse(it) { it } }
            return easternArabicFormattedDate.joinToString(separator = "")
        }

        return formattedDate
    }

fun convertNumbersToArabic(arabicNumber: String): String {
    val number = arabicNumber.toFloatOrNull() ?: return ""
    val locale = Locale("ar")
    val symbols = DecimalFormatSymbols(locale).apply {
        zeroDigit = '\u0660'
        groupingSeparator = ','
    }
    val formatter = DecimalFormat("#,###.##", symbols)
    return formatter.format(number)
}


    override fun onResume() {
        super.onResume()

        //2
        Log.e("preferenceManager", "${preferenceManager.isGpsMode}")
        if (preferenceManager.isGpsMode && !isFromFav) {
            binding.back.visibility = View.GONE

            getLastLocation()
        }
    }

    //2
    private fun getLastLocation() {
        //3
        if (checkPermissions()) {
//5
            if (isLocationEnabled()) {
                requestNewLocationData()

            } else {
                Toast.makeText(requireContext(), "turn on location", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
//4
        } else {
            requestPermission()

        }
    }

    //3
    private fun checkPermissions(): Boolean {
        val result = ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

        return result
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private val myLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val myLastLocation: Location = locationResult.lastLocation
            gpsLat = myLastLocation.latitude
            gpsLong = myLastLocation.longitude
            Log.e("from Gps", "lat: $gpsLat  long : $gpsLong")
            homeViewModel.getWeather(gpsLat, gpsLong, requireContext(), rootView)


        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mylocationRequest = LocationRequest()
        mylocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mylocationRequest.interval = 900000  //15 min

        myFusedLocationProviderClient.requestLocationUpdates(
            mylocationRequest, myLocationCallback,
            Looper.myLooper()
        )

    }

}



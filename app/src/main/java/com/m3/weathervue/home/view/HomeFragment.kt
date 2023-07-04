package com.m3.weathervue.home.view

import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.lifecycle.lifecycleScope
import com.example.productsmvvm.network.ApiClient
import com.example.productsmvvm.network.ApiState
import com.m3.weathervue.R
import com.m3.weathervue.databinding.FragmentHomeBinding
import com.m3.weathervue.home.viewmodel.HomeViewModel
import com.m3.weathervue.home.viewmodel.HomeViewModelFactory
import com.m3.weathervue.model.Repository
import com.m3.weathervue.model.WeatherResponse
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.log


class HomeFragment : Fragment() {
lateinit var homeViewModel: HomeViewModel
lateinit var homeViewModelFactory: HomeViewModelFactory
lateinit var binding: FragmentHomeBinding
lateinit var hoursAdapter: HoursAdapter
lateinit var dailyAdapter: DailyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hoursAdapter= HoursAdapter(requireContext())
        dailyAdapter= DailyAdapter(requireContext())

        binding.hoursRecyclerview.apply {
            adapter=hoursAdapter
        }
        binding.weekRecyclerview.apply {
            adapter=dailyAdapter
        }
        homeViewModelFactory= HomeViewModelFactory(Repository.getInstance(ApiClient))
        homeViewModel=ViewModelProvider(this,homeViewModelFactory).get(HomeViewModel::class.java)

        lifecycleScope.launch{
            homeViewModel.weather.collect{
                when(it){
                    is  ApiState.Success ->{
                        binding.animationView.visibility = View.GONE
                        binding.detCard.visibility = View.VISIBLE
                        binding.constraintLayout.visibility=View.VISIBLE
                        val data= it.data

                        hoursAdapter.submitList(data.hourly)
                        Log.e("from daily","${data.daily?.get(0)?.dt}")

                        dailyAdapter.submitList(data.daily)

                        hoursAdapter.timeZone=data.timezone.toString()
                        bindData(data)
                        Log.e("lat and lon","${data.current?.weather?.get(0)?.icon}")


                        Toast.makeText( requireContext(), "Success", Toast.LENGTH_SHORT).show()

                    }
                    is ApiState.Failure -> {
                        Toast.makeText( requireContext(), it.msg, Toast.LENGTH_SHORT).show()
                       // progressBar.visibility = View.GONE
                    }

                    else -> {
                        binding.detCard.visibility = View.GONE
                        binding.constraintLayout.visibility=View.GONE
                        Toast.makeText( requireContext(), "loading", Toast.LENGTH_SHORT).show()
                    }
                }

                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    fun bindData(data: WeatherResponse){
        binding.tempTxt.text=data.current?.temp.toString().substringBefore(".")

        binding.dateTxt.text=convertDate(data.current?.dt,data.timezone)

        binding.weatherCon.text=data.current?.weather?.get(0)?.main.toString()

        binding.HumidityNum.text=data.current?.humidity.toString()
        binding.CloudNum.text=data.current?.clouds.toString()
        binding.VioletNum.text=data.current?.uvi.toString()
        binding.pressureNum.text=data.current?.pressure.toString()
        binding.visibilityNum.text=data.current?.visibility.toString()
        binding.WindNum.text=data.current?.windSpeed.toString()





        val lat = data.lat as Double
        val lon =data.lon as Double
        var currIcon =data.current?.weather?.get(0)?.icon

        binding.locTxt.text=geocodingConvert(lat,lon)
        Log.e("lat and lon","lat : $lat & long: $lon ")
       // currIcon="01n"
        bindIcon(currIcon)

    }

    private fun bindIcon(icon:String?) {
        var img=binding.imageView
      when(icon){
            "01d"->img.setImageResource(R.drawable.day_01d)
            "02d"->img.setImageResource(R.drawable.day_02d)
            "03d"->img.setImageResource(R.drawable.day_03d)
            "04d"->img.setImageResource(R.drawable.day_04d)
            "09d","10d"->img.setImageResource(R.drawable.day_09_10d)
            "50d"->img.setImageResource(R.drawable.day_50d)

            "01n"->img.setImageResource(R.drawable.night_1n)
            "02n"->img.setImageResource(R.drawable.night_02n)
            "03n"->img.setImageResource(R.drawable.night_03n)
            "04n"->img.setImageResource(R.drawable.night_04n)
            "09n","10n"->img.setImageResource(R.drawable.night_09n_10n)
            "50n"->img.setImageResource(R.drawable.night_50n)





        }

    }

    fun geocodingConvert(lat:Double,lon: Double):String{
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addressList: List<Address>? = geocoder.getFromLocation(lat, lon, 1)
        var addressLine:String=""
        if (addressList != null && addressList.isNotEmpty()) {
            val address: Address = addressList[0]
             addressLine= address.getAddressLine(0)
        }
        val lastString = addressLine.substringAfterLast(",").trim()

        return  lastString
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertDate(dt: Long?, timeZone: String?): String {
        val dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(dt as Long), ZoneId.of(timeZone))
        return dateTime.format(DateTimeFormatter.ofPattern("E, d MMM", Locale.ENGLISH))

    }
}



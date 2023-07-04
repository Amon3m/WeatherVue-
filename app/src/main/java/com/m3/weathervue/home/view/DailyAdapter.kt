package com.m3.weathervue.home.view

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.m3.weathervue.R
import com.m3.weathervue.databinding.HoursItemBinding
import com.m3.weathervue.databinding.WeekItemBinding
import com.m3.weathervue.model.DailyItem
import com.m3.weathervue.model.HourlyItem
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class DailyAdapter (val context: Context)
    :ListAdapter<DailyItem,DailyViewHolder>(DailyDiffUtil()) {
    lateinit var binding: WeekItemBinding
    var  timeZone:String="UTC"



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {

        val inflater:LayoutInflater=parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding= WeekItemBinding.inflate(inflater,parent,false)
        return DailyViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        val currentObject =getItem(position)
        holder.binding.dayTxt.text=convertDate(currentObject.dt,timeZone)

        val icon=currentObject.weather?.get(0)?.icon
        val img=holder.binding.image
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
        holder.binding.tempCon.text=currentObject.weather?.get(0)?.main

        holder.binding.lowTemp.text=(currentObject.temp?.day.toString().substringBefore("."))
        holder.binding.highTemp.text=(currentObject.temp?.night.toString().substringBefore("."))



    }


//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun convertDate(dt: Long?, timeZone: String): String {
//        val dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(dt as Long), ZoneId.of(timeZone))
//        return dateTime.format(DateTimeFormatter.ofPattern("h a"))
//
//    }
@RequiresApi(Build.VERSION_CODES.O)
fun convertDate(dt: Long?, timeZone: String?): String {
    val dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(dt as Long), ZoneId.of(timeZone))
    return dateTime.format(DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH))
}
}

class DailyViewHolder(var binding: WeekItemBinding):RecyclerView.ViewHolder(binding.root)




class DailyDiffUtil:DiffUtil.ItemCallback<DailyItem>(){
    override fun areItemsTheSame(oldItem: DailyItem, newItem: DailyItem): Boolean {
        return oldItem.dt==newItem.dt
    }

    override fun areContentsTheSame(oldItem: DailyItem, newItem: DailyItem): Boolean {
        return oldItem==newItem
    }

}
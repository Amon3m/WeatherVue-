package com.m3.weathervue.home.view

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.m3.weathervue.databinding.HoursItemBinding
import com.m3.weathervue.model.HourlyItem
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class HoursAdapter (val context: Context)
    :ListAdapter<HourlyItem,HoursViewHolder>(HoursDiffUtil()) {
        lateinit var binding: HoursItemBinding
          var  timeZone:String="UTC"



        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoursViewHolder {

            val inflater:LayoutInflater=parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            binding= HoursItemBinding.inflate(inflater,parent,false)
            return HoursViewHolder(binding)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onBindViewHolder(holder: HoursViewHolder, position: Int) {
            val currentObject =getItem(position)
            holder.binding.hoursTxt.text= convertDate(currentObject.dt,timeZone)

//            Glide.with(context)
//                .load(currentObject.thumbnail)
//                .placeholder(R.drawable.ic_launcher_background)
//                .error(R.drawable.ic_launcher_foreground)
//                .into(holder.binding.imageItem)
//            holder.binding.consItem.setOnClickListener{
//                myListener(currentObject)
            holder.binding.weatherCon.text=(currentObject.temp.toString().substringBefore("."))


            }


        }

    class HoursViewHolder(var binding: HoursItemBinding):RecyclerView.ViewHolder(binding.root)



@RequiresApi(Build.VERSION_CODES.O)
fun convertDate(dt: Long?, timeZone: String): String {
    val dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(dt as Long), ZoneId.of(timeZone))
    return dateTime.format(DateTimeFormatter.ofPattern("h a"))

}
    class HoursDiffUtil:DiffUtil.ItemCallback<HourlyItem>(){
        override fun areItemsTheSame(oldItem: HourlyItem, newItem: HourlyItem): Boolean {
            return oldItem.dt==newItem.dt
        }

        override fun areContentsTheSame(oldItem: HourlyItem, newItem: HourlyItem): Boolean {
            return oldItem==newItem
        }

    }
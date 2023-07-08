package com.m3.weathervue.favorites.view

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.m3.weathervue.databinding.FavItemBinding
import com.m3.weathervue.model.FavoritesModel
import java.util.*

class FavoritesAdapter (val context: Context,private val listener: OnFavoritesClickListener)
    : ListAdapter<FavoritesModel, FavoritesViewHolder>(FavoritesDiffUtil()) {
    lateinit var binding: FavItemBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {

        val inflater: LayoutInflater =parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding= FavItemBinding.inflate(inflater,parent,false)
        return FavoritesViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        val currentObject =getItem(position)
        holder.binding.locTxt.text=currentObject.locName
        holder.binding.card.setOnClickListener {
            listener.onFavClick(currentObject)
        }
        holder.binding.deleteImg.setOnClickListener {
            listener.onDeleteClick(currentObject)
        }




    }


    //    @RequiresApi(Build.VERSION_CODES.O)
//    private fun convertDate(dt: Long?, timeZone: String): String {
//        val dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(dt as Long), ZoneId.of(timeZone))
//        return dateTime.format(DateTimeFormatter.ofPattern("h a"))
//
//    }

}

class FavoritesViewHolder(var binding: FavItemBinding): RecyclerView.ViewHolder(binding.root)




class FavoritesDiffUtil: DiffUtil.ItemCallback<FavoritesModel>(){
    override fun areItemsTheSame(oldItem: FavoritesModel, newItem: FavoritesModel): Boolean {
        return oldItem.locName==newItem.locName
    }

    override fun areContentsTheSame(oldItem: FavoritesModel, newItem: FavoritesModel): Boolean {
        return oldItem==newItem
    }

}
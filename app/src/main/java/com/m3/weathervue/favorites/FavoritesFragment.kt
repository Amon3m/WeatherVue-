package com.m3.weathervue.favorites

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import com.m3.weathervue.R
import com.m3.weathervue.databinding.FragmentFavoritesBinding
import com.m3.weathervue.map.MapsFragmentArgs


class FavoritesFragment : Fragment() {
lateinit var binding:FragmentFavoritesBinding
 var latitude:Double=0.0
 var longitude:Double=0.0
    companion object{var cont:Int=0}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentFavoritesBinding.inflate(inflater,container,false)
        return binding.root}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fab.setOnClickListener {
            val action: FavoritesFragmentDirections.ActionFavoritesFragmentToMapsFragment=
                FavoritesFragmentDirections.actionFavoritesFragmentToMapsFragment("FavoritesFragment")
            findNavController(view).navigate(action)
            cont++

        }
        if (cont!=0) {
            latitude = FavoritesFragmentArgs.fromBundle(requireArguments()).latitude.toDouble()
            longitude = FavoritesFragmentArgs.fromBundle(requireArguments()).longitude.toDouble()
        }

        Log.e("cont ","$cont ")

        Log.e("fav ","$latitude   $longitude ")

    }

}
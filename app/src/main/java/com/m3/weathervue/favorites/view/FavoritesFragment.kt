package com.m3.weathervue.favorites.view

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.productsmvvm.network.ApiClient
import com.google.android.gms.maps.model.LatLng
import com.m3.weathervue.R
import com.m3.weathervue.database.ConcreteLocalSource
import com.m3.weathervue.databinding.FragmentFavoritesBinding
import com.m3.weathervue.favorites.viewmodel.FavoritesViewModel
import com.m3.weathervue.favorites.viewmodel.FavouritesViewModelFactory
import com.m3.weathervue.home.view.HomeFragmentDirections
import com.m3.weathervue.map.viewmodel.MapsViewModel
import com.m3.weathervue.model.FavoritesModel
import com.m3.weathervue.model.Repository
import kotlinx.coroutines.launch
import java.util.*


class FavoritesFragment : Fragment(), OnFavoritesClickListener{
lateinit var binding:FragmentFavoritesBinding
lateinit var favoritesAdapter: FavoritesAdapter
lateinit var favoritesViewModel: FavoritesViewModel
lateinit var favouritesViewModelFactory: FavouritesViewModelFactory


    lateinit var mapsViewModel: MapsViewModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapsViewModel= ViewModelProvider(requireActivity()).get(MapsViewModel::class.java)

        favouritesViewModelFactory =
            FavouritesViewModelFactory(Repository.getInstance(ApiClient, ConcreteLocalSource(requireContext())))
        favoritesViewModel =
            ViewModelProvider(requireActivity(), favouritesViewModelFactory).get(FavoritesViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentFavoritesBinding.inflate(inflater,container,false)
        return binding.root}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoritesAdapter = FavoritesAdapter(requireContext(),this)
        val myLayoutManager = LinearLayoutManager(requireContext())
        myLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.locRecyclerview.apply {
            adapter = favoritesAdapter
            layoutManager = myLayoutManager
        }

        binding.fab.setOnClickListener {
            val action: FavoritesFragmentDirections.ActionFavoritesFragmentToMapsFragment =
               FavoritesFragmentDirections.actionFavoritesFragmentToMapsFragment(
                    "FavoritesFragment"
                )
            findNavController(view).navigate(action)
        }

        lifecycleScope.launch {
            mapsViewModel.favLocationFlow.collect { latLng ->
                Log.e("fav","${latLng?.latitude}")

                if (latLng != null) {
                    val lat = latLng.latitude
                    val lon = latLng.longitude
                    val locName=geocodingConvert(lat,lon)

                    favoritesViewModel.addToFavorites(FavoritesModel(locName,lon,lat))

                }
            }}

        lifecycleScope.launch {
            favoritesViewModel.favLocations.collect {
                favoritesAdapter.submitList(it)
                if (it.isNotEmpty()){
                binding.animationView.visibility=View.GONE
                binding.noDataTxt.visibility=View.GONE
                }else{
                    binding.animationView.visibility=View.VISIBLE
                    binding.noDataTxt.visibility=View.VISIBLE
                }
            }
        }

    }

    override fun onFavClick(favoritesModel: FavoritesModel) {
        Log.e("isFromFavFlow","favoritesModel ${(favoritesModel.latitude)}")
    if(isNetworkAvailable(requireContext())) {
        val action: FavoritesFragmentDirections.ActionFavoritesFragmentToHomeFragment =
            FavoritesFragmentDirections.actionFavoritesFragmentToHomeFragment(
                favoritesModel.latitude.toLong(),
                favoritesModel.longitudeval.toLong(), true

            )
        findNavController(requireView()).navigate(action)

        favoritesViewModel.updateFavToHome(
            LatLng(
                favoritesModel.latitude,
                favoritesModel.longitudeval
            )
        )
        favoritesViewModel.updateIsFromFav(true)

    }else{
        showNoInternetDialogue(requireContext(), view as View)
    }
    }

    override fun onDeleteClick(favoritesModel: FavoritesModel) {
        favoritesViewModel.removeFromFav(favoritesModel)
    }


    fun geocodingConvert(lat: Double, lon: Double): String {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addressList: List<Address>? = geocoder.getFromLocation(lat, lon, 1)
        var addressLine: String = ""
        if (addressList != null && addressList.isNotEmpty()) {
            val address: Address = addressList[0]
            addressLine = address.getAddressLine(0)

            Log.e("loc", "$addressLine ")
        }
        val lastString = addressLine.substringAfterLast(",").trim()

        return lastString
    }
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
    fun showNoInternetDialogue(context: Context, view:View) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.no_Internet))
            .setPositiveButton("retry") { dialog, _ ->


                val action: FavoritesFragmentDirections.ActionFavoritesFragmentToHomeFragment =
                    FavoritesFragmentDirections.actionFavoritesFragmentToHomeFragment(0,0,false)

                findNavController(view).navigate(action)
                dialog.dismiss()

            }

        builder.setCancelable(false)
        builder.create().show()


    }


}
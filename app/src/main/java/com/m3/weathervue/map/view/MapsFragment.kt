package com.m3.weathervue.map.view

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.m3.weathervue.R
import com.m3.weathervue.map.viewmodel.MapsViewModel
import java.io.IOException
import java.util.*


class MapsFragment : Fragment() {

    var marker: Marker? = null
    lateinit var comeFrm: String
    lateinit var mapsViewModel: MapsViewModel
    lateinit var button: Button

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
//        val sydney = LatLng(-34.0, 151.0)
//        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        googleMap.setOnMapClickListener { latLng ->

            marker?.remove()

            marker = googleMap.addMarker(MarkerOptions().position(latLng))

//            "Access to the current location by"

            var loc = geocodingConvert(latLng.latitude, latLng.longitude)
            val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            builder.setTitle(getString(R.string.map_dialog))
                .setMessage("$loc")
                .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                    // Handle negative button click
                    dialog.dismiss()
                }.setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                    when (comeFrm) {
                        "FavoritesFragment" -> {
                            mapsViewModel.updateFavLocation(latLng)
                            findNavController(requireView()).navigateUp()
                        }
                        "HomeFragment" -> {
                            mapsViewModel.updateLocation(latLng)
                            findNavController(requireView()).navigateUp()
                        }
                    }

                    dialog.dismiss()
                }


            builder.create().show()

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapsViewModel=ViewModelProvider(requireActivity()).get(MapsViewModel::class.java)
        comeFrm = MapsFragmentArgs.fromBundle(requireArguments()).fragmentName as String

        Log.e("comeFrm map", "$comeFrm ")

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        button = view.findViewById(R.id.back)
        button.setOnClickListener {
            findNavController(view).navigateUp()

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



}
package com.m3.weathervue.map

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.m3.weathervue.R
import com.m3.weathervue.favorites.FavoritesFragmentDirections
import java.util.*


class MapsFragment : Fragment() {
    var marker:Marker?=null
    lateinit var comeFrm:String

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

            var loc =geocodingConvert(latLng.latitude,latLng.longitude)
            val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            builder.setTitle("Are you sure you want to select this location?")
                .setMessage("$loc")
                .setNegativeButton("No") { dialog, _ ->
                    // Handle negative button click
                    dialog.dismiss()
                }.setPositiveButton("Yes") { dialog, _ ->
//                    val action: FavoritesFragmentDirections.ActionFavoritesFragmentToMapsFragment=
//                        FavoritesFragmentDirections.actionFavoritesFragmentToMapsFragment("FavoritesFragment")
//
                    val action: MapsFragmentDirections.ActionMapsFragmentToFavoritesFragment =
                        MapsFragmentDirections.actionMapsFragmentToFavoritesFragment(
                            latLng.latitude.toLong(),
                            latLng.longitude.toLong()
                        )

                    findNavController(requireView()).navigate(action)


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
        comeFrm = MapsFragmentArgs.fromBundle(requireArguments()).fragmentName as String

        Log.e("name ","$comeFrm ")

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        button=view.findViewById(R.id.back)
        button.setOnClickListener {
            findNavController(view).navigateUp()

        }
    }

    fun geocodingConvert(lat:Double,lon: Double):String{
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addressList: List<Address>? = geocoder.getFromLocation(lat, lon, 1)
        var addressLine:String=""
        if (addressList != null && addressList.isNotEmpty()) {
            val address: Address = addressList[0]
            addressLine= address.getAddressLine(0)

            Log.e("loc","$addressLine ")
        }
       // val lastString = addressLine.substringAfterLast(",").trim()

        return  addressLine
    }
}
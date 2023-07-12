package com.m3.weathervue

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.productsmvvm.network.ApiClient
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.navigation.NavigationView
import com.m3.weathervue.database.ConcreteLocalSource
import com.m3.weathervue.databinding.ActivityMainBinding
import com.m3.weathervue.favorites.viewmodel.FavoritesViewModel
import com.m3.weathervue.favorites.viewmodel.FavouritesViewModelFactory
import com.m3.weathervue.model.PreferenceManager
import com.m3.weathervue.model.Repository
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    lateinit var favoritesViewModel: FavoritesViewModel
    lateinit var favouritesViewModelFactory: FavouritesViewModelFactory
    lateinit var preferenceManager:PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager = PreferenceManager.getInstance(this)

        if (!preferenceManager.language.isNullOrEmpty()){
            when(preferenceManager.language){
                "en" -> {
                    setAppLocale("en")


                }
                "ar" -> {
                    setAppLocale("ar")

                }
            }
        }

        favouritesViewModelFactory =
            FavouritesViewModelFactory(Repository.getInstance(ApiClient, ConcreteLocalSource(this)))
        favoritesViewModel =
            ViewModelProvider(this, favouritesViewModelFactory).get(FavoritesViewModel::class.java)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navigationView = binding.navView
        drawerLayout = binding.drawerLayout

        val actionBar = supportActionBar
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)
        actionBar?.setDisplayShowHomeEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        lifecycleScope.launch {
            favoritesViewModel.isFromFavFlow.collect {
                Log.e("main","$it")
                if(it){
                    Log.e("if main","$it")
                    supportActionBar?.hide()
                }else{
                    Log.e("else main","$it")
                    supportActionBar?.show()
                }
            }
        }

        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(navigationView, navController)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun setAppLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val configuration = Configuration()
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

}

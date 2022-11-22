package com.example.myapplication


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.myapplication.Util.*
import com.example.myapplication.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlin.random.Random

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {


    //google map objects
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    //Objects for bottom menu
    private lateinit var bottomNav: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this@MapsActivity)

        //Testing for menu
        val settingsFragment = SettingsFragment()
        val historyFragment = HistoryFragment()
        //val test = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        setFragmentMenu(settingsFragment)
        bottomNav = findViewById(R.id.bottomNav)
        bottomNav.setOnNavigationItemSelectedListener {
            when(it.itemId){
                //R.id.map->setFragmentMenu(test)
                R.id.settings->setFragmentMenu(settingsFragment)
                R.id.history->setFragmentMenu(historyFragment)
            }
            true
        }
    }

    //Test function for menu
    private fun setFragmentMenu(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.testFragment,fragment)
            commit()
        }

    override fun onMapReady(googleMap: GoogleMap) {

            mMap = googleMap
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
            mMap.setMinZoomPreference(15f)
            enableMyLocation()

            if (mMap.isMyLocationEnabled){
                drawMyLocation()
            }

    }
    fun drawMyLocation(){
        mMap.isMyLocationEnabled
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation(){

        // 1. Check if permissions are granted, if so, enable the my location layer
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            return
        }

        // 2. If if a permission rationale dialog should be shown
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            PermissionUtils.RationaleDialog.newInstance(
                LOCATION_PERMISSION_REQUEST_CODE, true
            ).show(supportFragmentManager, "dialog")
            return
        }

        // 3. Otherwise, request permission
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }


    fun populateObjects(googleMap: GoogleMap){
        mMap = googleMap
        Log.d("POPULATE OBJECTS BEING CALLED","!!!!!!!!!!!")

    }
    companion object {
        /**
         * Request code for location permission request.
         *
         * @see .onRequestPermissionsResult
         */
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
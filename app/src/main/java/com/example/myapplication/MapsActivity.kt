package com.example.myapplication


import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.myapplication.Util.*
import com.example.myapplication.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import kotlin.random.Random

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {


    //google map objects
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding


    //Nick test comment
    //David test comment


    //Location service objects
    private var locationPermissionEnabled:Boolean = false
    //Boolean to keep track of whether activity is currently requesting location Updates
    private var locationRequestsEnabled:Boolean = false
    //Member object for the FusedLocationProvider
    private lateinit var locationProviderClient: FusedLocationProviderClient
    //Member object for the last known location
    private lateinit var mCurrentLocation: Location
    //Member object to hold onto locationCallback object
    //Needed to remove requests for location updates
    private lateinit var mLocationCallback: LocationCallback
    private lateinit var currentLocation: LatLng



    private var objects = ArrayList<LatLng>()


    private var counter : Int = 0



    val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("MapsActivity","Permission Granted")
            } else {
                Toast.makeText(this,"Location Permissions not granted. Location disabled on map",Toast.LENGTH_LONG).show()
            }
        }



    //setting up location callbacks and permmissions
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            //If successful, startLocationRequests
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                locationPermissionEnabled = true
                startLocationRequests()
            }
            //If successful at coarse detail, we still want those
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                locationPermissionEnabled = true
                startLocationRequests()
            } else -> {
            //Otherwise, send toast saying location is not enabled
            locationPermissionEnabled = false
            Toast.makeText(this,"Location Not Enabled", Toast.LENGTH_LONG)
        }
        }
    }

    //LocationUtilCallback object
    //Dynamically defining two results from locationUtils
    //Namely requestPermissions and locationUpdated
    private val locationUtilCallback = object: LocationUtilCallback {
        //If locationUtil request fails because of permission issues
        //Ask for permissions
        override fun requestPermissionCallback() {
            locationPermissionRequest.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION))
        }
        //If locationUtil returns a Location object
        //Populate the current location and log
        override fun locationUpdatedCallback(location: Location) {
            mCurrentLocation = location
            currentLocation = LatLng(location.latitude,location.longitude)

            if (counter == 0)
            {
                val mapFragment = supportFragmentManager
                    .findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(this@MapsActivity)
            }

//            Log.d("MainActivity","Location is [Lat: ${location.latitude}, Long: ${location.longitude}]")
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        checkForLocationPermission()





    }
    override fun onStart() {
        super.onStart()
        //Start location updates
        startLocationRequests()


    }

    override fun onStop(){
        super.onStop()
        //if we are currently getting updates
        if(locationRequestsEnabled){
            //stop getting updates
            locationRequestsEnabled = false
            stopLocationUpdates(locationProviderClient,mLocationCallback)
        }


    }

    private fun startLocationRequests(){
        //If we aren't currently getting location updates
        if(!locationRequestsEnabled){
            //create a location callback
            mLocationCallback = createLocationCallback(locationUtilCallback)
            //and request location updates, setting the boolean equal to whether this was successful
            locationRequestsEnabled = createLocationRequest(this,locationProviderClient,mLocationCallback)
        }
    }


    private fun checkForLocationPermission(){
        when {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getLastLocation(this,locationProviderClient,locationUtilCallback)
                //registerLocationUpdateCallbacks()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {


            mMap = googleMap


            val sydney = LatLng(-34.0, 151.0)


            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));

            mMap.setMinZoomPreference(15f)
            mMap.addMarker(MarkerOptions().position(currentLocation).title("Marker in Sydney"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))



            counter++

    }

    fun populateObjects(googleMap: GoogleMap){
        mMap = googleMap
        Log.d("POPULATE OBJECTS BEING CALLED","!!!!!!!!!!!")






        for (i in 1..9){
            val angle: Double = Random.nextDouble() * Math.PI * 2
            val randomRadius:Double = angle * Random.nextDouble()

            val relativeX = Math.cos(angle) * randomRadius
            val relativeY = Math.sin(angle) * randomRadius


            Log.d("FOR LOOOOOOOOP",relativeX.toString())
            val currentObject = LatLng(currentLocation.latitude+10,currentLocation.longitude+relativeY)
            objects.add(currentObject)
            mMap.addMarker(MarkerOptions().position(currentObject).title("Marker in Sydney"))
        }


    }
}
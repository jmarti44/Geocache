package com.example.myapplication


import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.Model.GeoCacheDataSource
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Util.*
import com.example.myapplication.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    //google map objects
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    lateinit var geoCacheSource : GeoCacheDataSource

    //Objects for bottom menu
    private lateinit var bottomNav: BottomNavigationView


    private var currentLongitude : Double = 0.0
    private var currentLatitude: Double = 0.0


    private  lateinit var geoCaches: List<String>
    private lateinit var latlngObjects : MutableList<LatLng>




    //consumer key



//    Path: /v1/geocaches
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        geoCacheSource = GeoCacheDataSource(applicationContext)

//        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
//
//
//        Log.d("NEARBY FRAGEMENT ON CREATE","!!!!!!")
//
//
//        val tempDataArray = ArrayList<NearbyItemViewModel>()
//
//        for (i in 1..3){
//            tempDataArray.add(NearbyItemViewModel("test", "test", i.toString()))
//            Log.d("TEMP DATA ARRAY","!!!!!!")
//        }
//
//        val nearAdapter = NearbyAdapter(tempDataArray)
//        recyclerview?.adapter = nearAdapter
//        recyclerview?.layoutManager = LinearLayoutManager(this)



    val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
            } else -> {
            // No location access granted.
        }
        }
    }



    val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this@MapsActivity)

        //Creates fragment's to reference on bottom menu bar
        val settingsFragment = SettingsFragment()
        val historyFragment = HistoryFragment()
        val nearbyFragment = NearbyFragment()

        //Sets a default fragment
        setFragmentMenu(nearbyFragment)

        //Finds bottom nav value
        bottomNav = findViewById(R.id.bottomNav)

        //For each button
        bottomNav.setOnNavigationItemSelectedListener {
            when(it.itemId){
                //Sets and changes to map activity
                R.id.map-> {
                    val mapIntent = Intent(this, MapsActivity::class.java)
                    startActivity(mapIntent)
                }

                //Sets and changes to history fragment
                R.id.history->{
                    //setFragmentMenu(historyFragment)
                    if (savedInstanceState == null){
                        val histFrag = HistoryFragment()
                        val changeFrag: FragmentTransaction = supportFragmentManager.beginTransaction()
                        changeFrag.replace(R.id.map, histFrag)
                        changeFrag.commit()
                    }
                }

                //Sets and changes to nearby fragment
                R.id.nearby->{
                    //setFragmentMenu(nearbyFragment)
                    if (savedInstanceState == null){
                        val nearFrag = NearbyFragment()
                        val changeFrag: FragmentTransaction = supportFragmentManager.beginTransaction()
                        changeFrag.replace(R.id.map, nearFrag)
                        changeFrag.commit()
//                        val nearIntent = Intent(this, NearbyActivity::class.java)
//                        startActivity(nearIntent)
                    }
                }

                //Sets and changes to settings fragment
//                R.id.settings->{
//                    //setFragmentMenu(settingsFragment)
//                    if (savedInstanceState == null){
//                        val setFrag = SettingsFragment()
//                        val changeFrag: FragmentTransaction = supportFragmentManager.beginTransaction()
//                        changeFrag.replace(R.id.map, setFrag)
//                        changeFrag.commit()
//                    }
//                }
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

//    override fun onResume() {
//
//        super.onResume()
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//        fusedLocationClient.lastLocation
//            .addOnSuccessListener { location : Location? ->
//                if (location != null) {
//                    currentLatitude = location.latitude
//                    currentLongitude = location.longitude
//
//                    CoroutineScope(Dispatchers.IO).launch {
//                        val geoCaches : List<String> = geoCacheSource.getGeoCacheCodes(currentLongitude,currentLatitude)
//                        withContext(Dispatchers.Main){
//                            try{
//                                toast("Got ${geoCaches.size} Geocaches!")
//                            }catch (e: HttpException) {
//                                toast("Exception ${e.message}")
//                            } catch (e: Throwable) {
//                                toast("Ooops: Something else went wrong")
//                            }
//                        }
//                        populateObjects(geoCaches)
//                    }
//
//                }
//
//                // Got last known location. In some rare situations this can be null.
//            }
//
//    }

    override fun onMapReady(googleMap: GoogleMap) {
            mMap = googleMap
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
//            mMap.setMinZoomPreference(15f)
            enableMyLocation()
            drawMyLocation()
            //getting currentLocation and getting geo cache codes
            if (mMap.isMyLocationEnabled){
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location : Location? ->
                        if (location != null) {
                            currentLatitude = location.latitude
                            currentLongitude = location.longitude

                            CoroutineScope(Dispatchers.IO).launch {
                                geoCaches = geoCacheSource.getGeoCacheCodes(currentLongitude,currentLatitude)
                                withContext(Dispatchers.Main){
                                    try{
                                        toast("Got ${geoCaches.size} Geocaches!")
                                    }catch (e: HttpException) {
                                        toast("Exception ${e.message}")
                                    } catch (e: Throwable) {
                                        toast("Ooops: Something else went wrong")
                                    }
                                }
                                val geoCacheLocations : MutableList<String?> = geoCacheSource.getGeoCaches(geoCaches)

//
                                withContext(Dispatchers.Main){
                                    try{
                                        toast("Got ${geoCacheLocations.size} Geocache Points!")
                                        var locationData: MutableList<List<String>> = arrayListOf()
                                        for (location in geoCacheLocations){
                                            val delimeter = "|"
                                            if (location != null) {
                                                var latitude = location.split(delimeter)[0].toDouble()
                                                var longitude = location.split(delimeter)[1].toDouble()
                                                val geoCacheMarker = LatLng(latitude, longitude)
                                                val myLocation  = LatLng(currentLatitude,currentLongitude)


                                                mMap.addMarker(
                                                    MarkerOptions()
                                                        .position(geoCacheMarker)
                                                        .title("Geo Cache Marker")
                                                )
                                            }
                                        }
                                    }catch (e: HttpException) {
                                        toast("Exception ${e.message}")
                                    } catch (e: Throwable) {
                                        toast("Ooops: Something else went wrong")
                                    }
                                }


                            }
//                            if (geoCaches.size!=0){
//                                Log.d("POPULATED OBJECTS BEING CALLED","!!!!!!!!")
//                                populateObjects(geoCaches)
//                                drawMyLocation()
//                            }
                        }
                    // Got last known location. In some rare situations this can be null.
                    }


            }
    }
    fun drawMyLocation(){
        mMap.isMyLocationEnabled = true


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
    fun populateObjects(geoCaches: List<String>) {
        var locationData: MutableList<List<String>> = arrayListOf()

        //getting locations of geocaches based on retreived geocachecodes
        CoroutineScope(Dispatchers.IO).launch {
            val geoCacheLocations : MutableList<String?> = geoCacheSource.getGeoCaches(geoCaches)
            //cleaning location data (lat|long)
            for (location in geoCacheLocations){
                var delimeter = "|"
                if (location != null) {
                    locationData.add(location.split(delimeter))
                }
            }

        }
        if (locationData.size!=0){
            Log.d("POPULATED LOCATION DATA BEING CALLED","!!!!!!!!")

            for (latlng in locationData){
                val latitude :Double? = latlng.get(0).toDouble()
                val longitude :Double? = latlng.get(1).toDouble()


//                val geoCacheMarker = LatLng(latitude!!, longitude!!)
                Log.d("RETURNED GEOCACHE Latitude",latitude.toString())
                Log.d("RETURNED GEOCACHE LONGITUDE",longitude.toString())

                val geoCacheMarker = LatLng(latitude!!, longitude!!)
                latlngObjects.add(geoCacheMarker)
            }
        }

//        for (code in geoCaches){
//            val geoCaches : List<String> = geoCacheSource.getGeoCaches(code)
//        }
    }
    companion object {
        /**
         * Request code for location permission request.
         *
         * @see .onRequestPermissionsResult
         */
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }


    private fun toast(text:String){
        Toast.makeText(this,text, Toast.LENGTH_LONG).show()
    }
}
package com.example.myapplication


import android.Manifest
import android.annotation.SuppressLint
//import android.app.FragmentTransaction
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.Model.GeoCacheDataSource
import com.example.myapplication.Util.*
import com.example.myapplication.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    //google map objects
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    //geoCacheData source for rertofit api call
    lateinit var geoCacheSource : GeoCacheDataSource

    //Objects for bottom menu
    private lateinit var bottomNav: BottomNavigationView


    //variables for tracking location
    private var currentLongitude : Double = 0.0
    private var currentLatitude: Double = 0.0


    //for geoCacheCodes
    private  lateinit var geoCaches: List<String>



    //liveData for newly added
    var  liveCacheData= MutableLiveData<MutableList<String>>().apply {
        value  = mutableListOf()
    }

    //hash map for referencing collected caches
    private var geoMarkerMap : HashMap<String, Marker> = HashMap<String,Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        geoCacheSource = GeoCacheDataSource(applicationContext)


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

                        val nearFrag = NearbyFragment(liveCacheData)
                        val changeFrag: FragmentTransaction = supportFragmentManager.beginTransaction()
                        changeFrag.replace(R.id.map, nearFrag)
                        changeFrag.commit()
                    }
                }
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
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))

             //enabling and drawing current lcation
            enableMyLocation()
            drawMyLocation()
            if (mMap.isMyLocationEnabled){
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location : Location? ->
                        if (location != null) {
                            currentLatitude = location.latitude
                            currentLongitude = location.longitude

                            //coroutine for making geocache api call - getting caches based on cache codes
                            CoroutineScope(Dispatchers.IO).launch {
                                geoCaches = geoCacheSource.getGeoCacheCodes(currentLongitude,currentLatitude)
                                val geoCacheLocations : HashMap<String, String>? = geoCacheSource.getGeoCaches(geoCaches)
                                var geoCacheNames : MutableList<String> = mutableListOf<String>()


                                //second coroutine for getting caches based on fetched geoCaches
                                withContext(Dispatchers.Main){
                                    try{
                                        for (name in geoCacheLocations?.keys!!){
                                            val delimeter = "|"
                                            var location = geoCacheLocations.get(name)
                                            var geoCacheName = name
                                            geoCacheNames.add(geoCacheName)


                                            //setting marker objects based on geocache name and location
                                            if (location != null) {
                                                var latitude = location.split(delimeter)[0].toDouble()
                                                var longitude = location.split(delimeter)[1].toDouble()
                                                val geoCacheMarker = LatLng(latitude, longitude)

                                                val currentMarker = mMap.addMarker(
                                                    MarkerOptions()
                                                        .position(geoCacheMarker)
                                                        .title(geoCacheName)
                                                        .snippet("Collect Me")
                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_cache))

                                                )
                                                //saving marker objects
                                                if (currentMarker != null) {
                                                    geoMarkerMap.put(geoCacheName,currentMarker)
                                                }
                                            }
                                        }

                                        //retreving user caches and updating markers
                                        getCaches()

                                        //adding test marker!
                                        var currentMarker = mMap.addMarker(
                                            MarkerOptions()
                                                .position(LatLng(currentLatitude,currentLongitude))
                                                .title("My GeoCache")
                                        )
                                        setInfoWindow(mMap,currentMarker)


                                    }catch (e: HttpException) {
                                        toast("Exception ${e.message}")
                                    } catch (e: Throwable) {
                                        toast("Ooops: Something else went wrong")
                                    }
                                }
                            }

                        }

                    }

            }

    }



    private fun getCaches() {

        //firestore
        val db = Firebase.firestore
        var tests :MutableList<HashMap<String, String>> = mutableListOf()

        //retreving user caches
        db.collection("caches")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    var newCache = hashMapOf(
                        "title" to document.data["title"].toString(),
                        "latitude" to document.data["latitude"].toString(),
                        "longitude" to document.data["longitude"].toString()
                    )
                    //persisting recycler view
                    NearbyItemViewModel.tests.add(newCache)
                    liveCacheData.value?.add(document.data["title"].toString())

                }

                liveCacheData.observe(this@MapsActivity){userCaches->

                    if (userCaches.size!=0){


                        //updating markers based on changes to liveCacheData
                        val items = ArrayList<NearbyItemViewModel>()
                        for (cache in userCaches){
                            var currentMarker : Marker? = geoMarkerMap.get(cache)
                            currentMarker?.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_cache_found))
                            currentMarker?.snippet = "FOUND"
                        }

                    }
                }
            }
            .addOnFailureListener { exception ->
            }

    }



    //s
    private fun setInfoWindow(mMap: GoogleMap, currentMarker: Marker?) {
        mMap.setOnInfoWindowClickListener(this)

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
    companion object {
        /**
         * Request code for location permission request.
         *
         * @see .onRequestPermissionsResult
         */
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

//    Toast.makeText(this,text, Toast.LENGTH_LONG).show()

    private fun toast(text:String){
        Toast.makeText(this,text, Toast.LENGTH_LONG).show()

    }

    //adding to database when marker info screen is pressed
    override fun onInfoWindowClick(marker: Marker) {


        //getting
        val db = Firebase.firestore
        var cacheLocation: Location = Location(LocationManager.GPS_PROVIDER)
        cacheLocation.longitude = marker.position.longitude
        cacheLocation.latitude = marker.position.latitude



        //getting distnance between user and caches
        var myLocation : Location = Location(LocationManager.GPS_PROVIDER)
        myLocation.latitude = currentLatitude
        myLocation.longitude = currentLongitude
        var distnaceInBetween: Float = myLocation.distanceTo(cacheLocation)


        //checking if user is within range of the cache
       if (distnaceInBetween <= 3){
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_cache_found))
            marker.snippet = "FOUND"

           //creating newCache hashmap
            var newCache = hashMapOf(
                "title" to marker.title.toString(),
                "latitude" to marker.position.latitude.toString(),
                "longitude" to marker.position.longitude.toString(),
            )

           //adding persisted cache into recyclervView
            NearbyItemViewModel.tests.add(newCache)


           //adding cache into database
             db.collection("caches")
                .add(newCache)
                 .addOnSuccessListener { documentReference ->

                   Log.d("Record added", "DocumentSnapshot added with ID: ${documentReference.id}")
               }
                .addOnFailureListener { e ->
                    Log.w("Record Error", "Error adding document", e)
                }
        Toast.makeText(
            this, "Info window clicked",
            Toast.LENGTH_SHORT
        ).show()
        }
        else{
            toast("NOT IN RANGE OF THE CACHE")
        }

    }
}

//private fun GoogleMap.setOnInfoWindowClickListener(mapsActivity: MapsActivity) {
//
//}

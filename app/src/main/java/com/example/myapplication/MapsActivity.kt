package com.example.myapplication


import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
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


    lateinit var geoCacheSource : GeoCacheDataSource

    //Objects for bottom menu
    private lateinit var bottomNav: BottomNavigationView


    private var currentLongitude : Double = 0.0
    private var currentLatitude: Double = 0.0


    private  lateinit var geoCaches: List<String>
    var  liveCacheData= MutableLiveData<MutableList<String>>().apply {
        value  = mutableListOf()
    }
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

        //enabling and drawing current lcation
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

                            //coroutine for making geocache api call
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
                                val geoCacheLocations : HashMap<String, String>? = geoCacheSource.getGeoCaches(geoCaches)
                                var geoCacheNames : MutableList<String> = mutableListOf<String>()



                                withContext(Dispatchers.Main){
                                    try{
                                        toast("Got ${geoCacheLocations?.size} Geocache Points!")
                                        for (name in geoCacheLocations?.keys!!){
                                            val delimeter = "|"
                                            var location = geoCacheLocations.get(name)
                                            var geoCacheName = name
                                            geoCacheNames.add(geoCacheName)
                                            
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
                                                if (currentMarker != null) {
                                                    geoMarkerMap.put(geoCacheName,currentMarker)
                                                }
                                            }
                                        }
                                        //changing marker colors based on fetched data from database
                                        //checking database for previously collected caches


                                        var userCaches: MutableSet<String>


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

    private fun populateUserCaches(
        cache: String,
        geoCacheLocations: HashMap<String, String>,
        cacheMap: HashMap<String, Marker>,
        mMap: GoogleMap
    ) {
        //retreving marker object
        Log.d("POPULATING USER CACHES!!!!","!!!!!!!")
        var formerMarker: Marker? = cacheMap.get(cache)
        if (formerMarker != null) {
            formerMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_cache_found))
            formerMarker.snippet = "FOUND"
        }
    }

    private fun getCaches() {

        val db = Firebase.firestore
        Log.d("FUNCTION CALLED","!!!!!!!!!")
        var tests :MutableList<HashMap<String, String>> = mutableListOf()


        db.collection("caches")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    var newCache = hashMapOf(
                        "title" to document.data["title"].toString(),
                        "latitude" to document.data["latitude"].toString(),
                        "longitude" to document.data["longitude"].toString()
                    )
                    NearbyItemViewModel.tests.add(newCache)

                    Log.d("CACHE TITLE",document.data["title"].toString())
                    liveCacheData.value?.add(document.data["title"].toString())

                }

                liveCacheData.observe(this@MapsActivity){userCaches->
                    Log.d("OBSERVED DATA ","POPLUATED MARKERS WILL BE CALLED")

                    if (userCaches.size!=0){

                        Log.d("USER CACHE",userCaches.toString())

                        val items = ArrayList<NearbyItemViewModel>()
                        for (cache in userCaches){
                            var currentMarker : Marker? = geoMarkerMap.get(cache)
                            Log.d("CURRENT MARKER", currentMarker?.title.toString())
                            currentMarker?.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_cache_found))
                            currentMarker?.snippet = "FOUND"
                        }




//                        for (userCache in userCaches){
//                            if (userCache in geoCacheLocations.keys){
////                                                        populateUserCaches(userCache,geoCacheLocations,geoMarkerMap,mMap)
//                            }
//                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("CACHE", "Error getting documents.", exception)
            }

    }

    private fun setInfoWindow(mMap: GoogleMap, currentMarker: Marker?) {
        Log.d("SET ON INFO WINDOW LISTENER!!!!","!!!!!!!!!!")
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
//    fun populateObjects(geoCaches: List<String>) {
//        var locationData: MutableList<List<String>> = arrayListOf()
//
//        //getting locations of geocaches based on retreived geocachecodes
//        CoroutineScope(Dispatchers.IO).launch {
//            val geoCacheLocations : MutableList<String?> = geoCacheSource.getGeoCaches(geoCaches)
//            //cleaning location data (lat|long)
//            for (location in geoCacheLocations){
//                var delimeter = "|"
//                if (location != null) {
//                    locationData.add(location.split(delimeter))
//                }
//            }
//
//        }
//        if (locationData.size!=0){
//            Log.d("POPULATED LOCATION DATA BEING CALLED","!!!!!!!!")
//
//            for (latlng in locationData){
//                val latitude :Double? = latlng.get(0).toDouble()
//                val longitude :Double? = latlng.get(1).toDouble()
//
//
////                val geoCacheMarker = LatLng(latitude!!, longitude!!)
//                Log.d("RETURNED GEOCACHE Latitude",latitude.toString())
//                Log.d("RETURNED GEOCACHE LONGITUDE",longitude.toString())
//
//                val geoCacheMarker = LatLng(latitude!!, longitude!!)
//                latlngObjects.add(geoCacheMarker)
//            }
//        }
//
////        for (code in geoCaches){
////            val geoCaches : List<String> = geoCacheSource.getGeoCaches(code)
////        }
//    }
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

    override fun onInfoWindowClick(marker: Marker) {

        val db = Firebase.firestore

        var cacheLocation: Location = Location(LocationManager.GPS_PROVIDER)
        cacheLocation.longitude = marker.position.longitude
        cacheLocation.latitude = marker.position.latitude



        var myLocation : Location = Location(LocationManager.GPS_PROVIDER)
        myLocation.latitude = currentLatitude
        myLocation.longitude = currentLongitude

        var distnaceInBetween: Float = myLocation.distanceTo(cacheLocation)

        Log.d("DISTANCE_IN_BETWEEN",distnaceInBetween.toString())


//        if (distnaceInBetween <= 3){
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_cache_found))
            marker.snippet = "FOUND"

            //attaching id to

            var newCache = hashMapOf(
                "title" to marker.title.toString(),
                "latitude" to marker.position.latitude.toString(),
                "longitude" to marker.position.longitude.toString(),
            )

            NearbyItemViewModel.tests.add(newCache)

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
        //}
//        else{
//            toast("NOT IN RANGE OF THE CACHE")
//        }




//        val db = Firebase.firestore
//
//
//
//        db.collection("caches")
//            .add(newCache)
//            .addOnSuccessListener { documentReference ->
//                Log.d("Record added", "DocumentSnapshot added with ID: ${documentReference.id}")
//            }
//            .addOnFailureListener { e ->
//                Log.w("Record Error", "Error adding document", e)
//            }
//        Toast.makeText(
//            this, "Info window clicked",
//            Toast.LENGTH_SHORT
//        ).show()
    }
}

//private fun GoogleMap.setOnInfoWindowClickListener(mapsActivity: MapsActivity) {
//
//}

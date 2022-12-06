package com.example.myapplication.Model

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.myapplication.Model.GeoCaches



class GeoCacheDataSource(context:Context){

    var retrofit: Retrofit
    var geoCacheService : GeoCacheService



    init{
        retrofit = createRetrofitClient(context)
        geoCacheService = retrofit.create(GeoCacheService::class.java)

    }


    private fun createRetrofitClient(context:Context):Retrofit{
        return Retrofit.Builder()
            .baseUrl("https://www.opencaching.us/okapi/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(createClient(context))
            .build()
    }


    private fun createClient(context:Context): OkHttpClient
    {

        val okHttpClient = OkHttpClient.Builder()
            .cache(createCache(context))
            .addInterceptor { chain ->
                var request = chain.request()
                request = if (hasNetwork(context))
                    request.newBuilder().header("Cache-Control", "public, max-age=" + 5).build()
                else
                    request.newBuilder().header(
                        "Cache-Control",
                        "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7
                    ).build()
                chain.proceed(request)
            }
            .build()
        return okHttpClient
    }


    private fun createCache(context: Context): Cache {
        //Create 5 MB Cache`,
        return Cache(context.cacheDir, (5 * 1024 * 1024))
    }
    private fun hasNetwork(context:Context):Boolean{
        val cm: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetwork != null && cm.getNetworkCapabilities(cm.activeNetwork) != null
    }

    suspend fun getGeoCacheCodes(longitude:Double,latitude:Double): List<String> {

        //adjusting string location parameters
        val geoAPIKey = "uVUcXfEh6gHmQLnU96Gp"
        var center : String =latitude.toString() + "|" + longitude.toString()


//        val response = geoCacheService.getGeoCacheTest("Archived",geoAPIKey)
        val response = geoCacheService.getGeoCacheCodes(center,geoAPIKey)
        if (response.isSuccessful){

            val geoCacheCodes = response.body()
            if (geoCacheCodes!= null){
                return geoCacheCodes.results
            }
        }
        return emptyList()
    }

    suspend fun getGeoCaches(geoCacheCodes: List<String>): HashMap<String, String>? {
        val geoAPIKey = "uVUcXfEh6gHmQLnU96Gp"
        val fields = "name|location"
        val geoLocations : MutableList<String?> = arrayListOf()
        var codes =""

        //
        for (code in geoCacheCodes){
            codes+=(code.toString() +"|")
        }
        codes = codes.substring(0,codes.length-1)



        val response = geoCacheService.getGeoCaches(codes,fields,geoAPIKey)
        val caches : Map<String, GeoCaches>? = response.body()
        Log.d("RETREVIED RESPONSE", response.body().toString())
//        response.body()?.javaClass?.let { Log.d("type", it.simpleName) }
        var hashMap : HashMap<String, String>
                = HashMap<String, String> ()
        if (response.isSuccessful){
            for (code in geoCacheCodes)
            {


                hashMap.put(caches?.get(code)?.name.toString(),
                    caches?.get(code)?.location.toString()
                )
                geoLocations.add(caches?.get(code)?.location)
            }

        }
        if (hashMap.size !=0){
            return hashMap
        }
        else{
            return null
        }
//        if (geoLocations.size !=0){
//            return geoLocations
//        }
//        else{
//            return arrayListOf()
//        }

    }
//    suspend fun getPlanetsInfo():List<GeoCacheCodes>{
//        val response = geoCacheService.getGeoCaches()
//        if(response.isSuccessful) {
//            val planets = response.body()
//
//            if (planets != null) {
//                return getAllGeoCaches(planets.count)
//            }
//        }
//        return emptyList()



    ///serialize to
//    GeoCacheMap

}
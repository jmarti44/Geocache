package com.example.myapplication.Model

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

//paramters
//argument declaration
//consumer key
//center
//pass values when invoking api call


//arg = 44 | consumer_key = 3mAuArcqBF573L69RUnA | center = currentLocation
interface GeoCacheService {

    @GET("services/caches/search/nearest")
    suspend fun getGeoCacheCodes(@Query("center")center:String,@Query("consumer_key")consumer_key:String): Response<GeoCacheCodes>

    @GET("services/caches/search/all")
    suspend fun getGeoCacheTest(@Query("status")status:String,@Query("consumer_key")consumer_key:String): Response<GeoCacheCodes>


//    OP91WY, OP1A65, OP8HJ5
    @GET("services/caches/geocaches?")
    suspend fun getGeoCaches(@Query("cache_codes")code:String,@Query("fields")fields:String,@Query("consumer_key")consumer_key:String): Response<Map<String, GeoCaches>>


}




//@GET("services/caches/search/nearest?arg=44&consumer_key=3mAuArcqBF573L69RUnA&center=36.065971|-94.173782")
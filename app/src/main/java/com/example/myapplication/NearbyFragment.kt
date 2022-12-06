package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView



//This fragment shows nearby locations in
class NearbyFragment:Fragment(R.layout.fragment_nearby) {

//    private val nearbyListViewModel: NearbyItemViewModel by view?.viewModels {
//
//    }

    //When app is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val recyclerview = view?.findViewById<RecyclerView>(R.id.recyclerview)


        recyclerview?.layoutManager = LinearLayoutManager(this.context)
        Log.d("NEARBY FRAGEMENT ON CREATE","!!!!!!")


        val tempDataArray = ArrayList<NearbyItemViewModel>()

        for (i in 1..3){
            tempDataArray.add(NearbyItemViewModel("test", "test", i.toString()))
            Log.d("TEMP DATA ARRAY","!!!!!!")
        }

        val nearAdapter = NearbyAdapter(tempDataArray)
        //nearbyAdapter.
        recyclerview?.adapter = nearAdapter


    }
}
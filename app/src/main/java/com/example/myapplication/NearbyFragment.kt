package com.example.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.fragment_nearby.view.*



//This fragment shows nearby locations in
class NearbyFragment:Fragment(R.layout.fragment_nearby) {

    //When app is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val recyclerview = view?.findViewById<RecyclerView>(R.id.recyclerview)


        recyclerview?.layoutManager = LinearLayoutManager(this.context)

        val tempDataArray = ArrayList<NearbyItemViewModel>()

        for (i in 1..3){
            tempDataArray.add(NearbyItemViewModel(i.toString(), i.toString(), i.toString()))
        }

        val nearAdapter = NearbyAdapter(tempDataArray)
        recyclerview?.adapter = nearAdapter
    }
}
package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import androidx.appcompat.app.AppCompatActivity
//import kotlinx.android.synthetic.main.fragment_nearby.view.*



//This fragment shows nearby locations in
class NearbyFragment:Fragment(R.layout.fragment_nearby) {


    lateinit var items: ArrayList<NearbyItemViewModel>

    //When app is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Find the recyclerview
        val recyclerview = view?.findViewById<RecyclerView>(R.id.recyclerview)

        //Create a list of numbers
        items = NearbyItemViewModel.createList(20)

        //Set Layout
        recyclerview?.layoutManager = LinearLayoutManager(this.context)

        //Bind adapter and recycler view
        val adapter = NearbyAdapter(items)
        recyclerview?.adapter = adapter
    }
}
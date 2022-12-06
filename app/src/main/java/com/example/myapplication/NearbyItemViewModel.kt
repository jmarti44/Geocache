package com.example.myapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

public class NearbyItemViewModel(val locationTitle: String, val locationDistance: String, val locationFound: String) {
    companion object{
        private var lastID = 0
        fun createList(numItems: Int): ArrayList<NearbyItemViewModel>{
            val items = ArrayList<NearbyItemViewModel>()
            for (i in 1..numItems){
                items.add(NearbyItemViewModel(i.toString(), "test", "test"))
            }
            return  items
        }
    }

}
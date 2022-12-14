package com.example.myapplication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

public class NearbyItemViewModel(var locationTitle: String, var locationFound: String, var locationDistance : String) {


    companion object{
        private var lastID = 0
        var tests :MutableList<HashMap<String, String>> = mutableListOf()
        fun createList(): ArrayList<NearbyItemViewModel>{

            val items = ArrayList<NearbyItemViewModel>()

            for (test in tests){
                val title = test.get("title").toString()
                val location : String = test.get("latitude").toString() + ", " + test.get("longitude").toString()
                items.add(NearbyItemViewModel(title,location,"IT WORKED!"))
            }

            return  items
        }

    }


}
package com.example.myapplication.Util

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class NearbyActivity : AppCompatActivity() {
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearby)

//        //Creates fragment's to reference on bottom menu bar
//        val settingsFragment = SettingsFragment()
//        val historyFragment = HistoryFragment()
//        val nearbyFragment = NearbyFragment()
//
//        //Sets a default fragment
//        setFragmentMenu(nearbyFragment)
//
//        //Finds bottom nav value
//        bottomNav = findViewById(R.id.bottomNav)
//
//        //For each button
//        bottomNav.setOnNavigationItemSelectedListener {
//            when(it.itemId){
//                //Sets and changes to map activity
//                R.id.map-> {
//                    val mapIntent = Intent(this, MapsActivity::class.java)
//                    startActivity(mapIntent)
//                }
//
//                //Sets and changes to history fragment
//                R.id.history->{
//                    //setFragmentMenu(historyFragment)
//                    if (savedInstanceState == null){
//                        val histFrag = HistoryFragment()
//                        val changeFrag: FragmentTransaction = supportFragmentManager.beginTransaction()
//                        changeFrag.replace(R.id.map, histFrag)
//                        changeFrag.commit()
//                    }
//                }
//
//                //Sets and changes to nearby fragment
//                R.id.nearby->{
//                    //setFragmentMenu(nearbyFragment)
//                    if (savedInstanceState == null){
//                        val nearFrag = NearbyFragment()
//                        val changeFrag: FragmentTransaction = supportFragmentManager.beginTransaction()
//                        changeFrag.replace(R.id.map, nearFrag)
//                        changeFrag.commit()
//                        val nearIntent = Intent(this, NearbyActivity::class.java)
//                        startActivity(nearIntent)
//                    }
//                }
//
//                //Sets and changes to settings fragment
////                R.id.settings->{
////                    //setFragmentMenu(settingsFragment)
////                    if (savedInstanceState == null){
////                        val setFrag = SettingsFragment()
////                        val changeFrag: FragmentTransaction = supportFragmentManager.beginTransaction()
////                        changeFrag.replace(R.id.map, setFrag)
////                        changeFrag.commit()
////                    }
////                }
//            }
//            true
//        }
//
//        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
//
//        //recyclerview?.layoutManager = LinearLayoutManager(this)
//        Log.d("NEARBY FRAGEMENT ON CREATE","!!!!!!")
//
//
//
//        var tempDataArray : MutableList<String> = mutableListOf()
//        for (i in 1..3){
////            tempDataArray.add(NearbyItemViewModel("test", "test", i.toString()))
//            tempDataArray.add(i.toString())
//            Log.d("TEMP DATA ARRAY","HERE!")
//        }
//
//        val nearAdapter = NearbyAdapter(tempDataArray)
//        recyclerview?.adapter = nearAdapter
//        recyclerview.layoutManager = LinearLayoutManager(this)
//    }
//
//    //Test function for menu
//    private fun setFragmentMenu(fragment: Fragment)=
//        supportFragmentManager.beginTransaction().apply {
//            replace(R.id.testFragment,fragment)
//            commit()
        }
}
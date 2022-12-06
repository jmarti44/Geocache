package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import kotlin.coroutines.coroutineContext

class CustomWindowInfoAdapter(context : Context) : GoogleMap.InfoWindowAdapter {
    private lateinit var view: View
    private lateinit var mContext : Context

    init{
        mContext = context
        view = LayoutInflater.from(context).inflate(R.layout.layout_resource_card_view,null)
    }

    override fun getInfoContents(p0: Marker): View? {
        var title : String = p0.title.toString()
         
        TODO("Not yet implemented")
    }

    override fun getInfoWindow(p0: Marker): View? {
        TODO("Not yet implemented")
    }
}
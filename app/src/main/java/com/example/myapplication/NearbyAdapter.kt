package com.example.myapplication

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NearbyAdapter (private val nearbyList: List<NearbyItemViewModel>) : RecyclerView.Adapter<NearbyAdapter.ViewHolder>() {


    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val locationTitleText: TextView = itemView.findViewById(R.id.locationTitle)
        val locationDistanceText: TextView = itemView.findViewById(R.id.locationDistance)
        val locationFoundText: TextView = itemView.findViewById(R.id.locationFound)

        fun bind(title: String?, distance: String?, found: String?){
            Log.d("BINDING VALUES","!!!!!!")

            locationTitleText.text = title
            locationDistanceText. text = distance
            locationFoundText.text = found
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_resource_card_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nearbyItemViewModel = nearbyList[position]
        holder.bind(nearbyItemViewModel.locationTitle, nearbyItemViewModel.locationDistance, nearbyItemViewModel.locationFound)

        Log.d("ON BIND VIEW HOLDER","!!!!!!!!!!")
        holder.locationTitleText.text = nearbyItemViewModel.locationTitle
        holder.locationDistanceText.text = nearbyItemViewModel.locationDistance
        holder.locationFoundText.text = nearbyItemViewModel.locationFound
    }

    override fun getItemCount(): Int {
        return nearbyList.size
    }
}
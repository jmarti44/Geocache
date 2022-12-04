package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text

class NearbyAdapter (private val nearbyList: List<NearbyItemViewModel>) : RecyclerView.Adapter<NearbyAdapter.ViewHolder>() {


    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val locationTitleText: TextView = itemView.findViewById(R.id.locationTitle)
        val locationDistanceText: TextView = itemView.findViewById(R.id.locationDistance)
        val locationFoundText: TextView = itemView.findViewById(R.id.locationFound)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_resource_card_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nearbyItemViewModel = nearbyList[position]

        holder.locationTitleText.text = nearbyItemViewModel.locationTitle
        holder.locationDistanceText.text = nearbyItemViewModel.locationDistance
        holder.locationFoundText.text = nearbyItemViewModel.locationFound
    }

    override fun getItemCount(): Int {
        return nearbyList.size
    }
}
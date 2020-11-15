package com.example.pillreminder.adaptors


import android.content.Context
//import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pillreminder.R
import com.example.pillreminder.models.AlertItem
import kotlinx.android.synthetic.main.list_alert_item.view.*
import kotlinx.android.synthetic.main.recycler_test_holder.view.*
import java.lang.StringBuilder

class AlertItemAdapter(internal var context: Context, internal var alertList:ArrayList<AlertItem>)
    : RecyclerView.Adapter<AlertItemAdapter.AlertItemViewHolder>() {

    class AlertItemViewHolder (
        itemView: View
    ) : RecyclerView.ViewHolder(itemView){
        val icon:ImageView = itemView.alertIcon
        val title: TextView = itemView.alertTitle
        val details: TextView = itemView.alertDetails
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): AlertItemViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_alert_item,parent,false)
        return AlertItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return alertList.size
    }

    override fun onBindViewHolder(holder: AlertItemViewHolder, position: Int) {
//        holder.txt_title.text = dataList[position].title?.toString()
        if(alertList[position].type=="pill"){
            holder.icon.setImageResource(R.drawable.pill_selected)
        }
        if(alertList[position].type=="appointment"){
            holder.icon.setImageResource(R.drawable.appointment_selected)
        }
        if(alertList[position].type=="vaccine"){
            holder.icon.setImageResource(R.drawable.vaccine_selected)
        }
        holder.title.text=alertList[position].title
        holder.details.text=alertList[position].details
    }
}
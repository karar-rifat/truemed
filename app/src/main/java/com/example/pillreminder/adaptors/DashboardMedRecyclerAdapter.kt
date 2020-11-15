package com.example.pillreminder.adaptors

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pillreminder.R
import com.example.pillreminder.models.DashboardData
import com.example.pillreminder.models.Pill
import kotlinx.android.synthetic.main.list_item_row_appo_vac.view.*
import kotlinx.android.synthetic.main.pill_item_row.view.*
import kotlinx.android.synthetic.main.view_holder_dash_med.view.*

class DashboardMedRecyclerAdapter(
    internal var context: Context,
    internal var dashItemList: ArrayList<DashboardData>,
    internal var supportFragmentManager: FragmentManager) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    companion object {
        const val VIEW_TYPE_PILL:Int=1
        const val VIEW_TYPE_APPOINTMENT_VACCINE:Int=2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType== VIEW_TYPE_PILL)
            PillViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.pill_item_row,parent,false))
        else
            AppVacViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_row_appo_vac,parent,false))

    }

    override fun getItemViewType(position: Int): Int {

        return if (dashItemList[position].type=="pill")
            VIEW_TYPE_PILL
        else
            VIEW_TYPE_APPOINTMENT_VACCINE
    }

    override fun getItemCount(): Int {
        return dashItemList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (dashItemList[position].type=="pill"){
            (holder as PillViewHolder).bind(position)
        } else {
            (holder as AppVacViewHolder).bind(position)
        }
    }

    //formate time with AM,PM for button
    fun formatTime(time:String): String? {
        val timeParts = time.split(":")
        var hour=timeParts[0].toInt()
        val minute=timeParts[1].toInt()
        val format: String
        val formattedTime: String
        when {
            hour == 0 -> {
                hour += 12
                format = "AM"
            }
            hour == 12 -> {
                format = "PM"
            }
            hour > 12 -> {
                hour -= 12
                format = "PM"
            }
            else -> {
                format = "AM"
            }
        }
        val newMinute = if (minute < 10) "0$minute" else minute.toString()
        formattedTime = "$hour:$newMinute $format"
        return formattedTime
    }

    private inner class PillViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView){
        val tvTitle = itemView.tvPillName
        val tvBeforeAfter = itemView.tvBeforeOrAfter
        val tvDose = itemView.tvDose
        val tvTime = itemView.tvTime
        val tvStock = itemView.tvStock

        fun bind(position: Int){
            tvTitle.text=dashItemList[position].title
            tvBeforeAfter.text=if(dashItemList[position].beforeOrAfter=="before"){
               "Before Meal"
            }else{
                "After Meal"
            }
            tvDose.text="${dashItemList[position].dose} pill(s)"
            tvTime.text=formatTime(dashItemList[position].time!!)
            tvStock.text=dashItemList[position].stock.toString()
        }

    }

     private inner class AppVacViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val tvTitle = itemView.tvTitleAppVac
        val tvTime = itemView.tvTimeAppVac
        val icon = itemView.iconAppVac

        fun bind(position: Int){
            tvTitle.text=dashItemList[position].title
            tvTime.text=dashItemList[position].time
            if (dashItemList[position].type=="appointment")
            icon.setImageDrawable(context.resources.getDrawable(R.drawable.appointment_selected))
            else
                icon.setImageDrawable(context.resources.getDrawable(R.drawable.vaccine_selected))
        }

    }

}
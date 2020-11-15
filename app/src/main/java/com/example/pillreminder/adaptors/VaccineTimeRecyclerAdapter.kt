package com.example.pillreminder.adaptors

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pillreminder.R
import kotlinx.android.synthetic.main.vaccine_time_item.view.*

class VaccineTimeRecyclerAdapter(
    internal var context: Context,
    private var vacTimeList: MutableList<String>,
    internal var delete: (position:Int) -> Unit
) : RecyclerView.Adapter<VaccineTimeRecyclerAdapter.VacTimeViewHolder>(){

    class VacTimeViewHolder(
        vacTimeList: MutableList<String>,
        itemView: View,
        delete: (position:Int) -> Unit
    ) : RecyclerView.ViewHolder(itemView){
        val timeTitle = itemView.timeTitle
        var itemPos : Int = 0
        fun setItemPosition(itemPos : Int){
            this.itemPos = itemPos
        }
        init {
            itemView.delTimeTitle.setOnClickListener {
                delete(itemPos)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VacTimeViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.vaccine_time_item,parent,false)
        return VacTimeViewHolder(vacTimeList, itemView, delete)
    }

    override fun getItemCount(): Int {
        return vacTimeList.size
    }

    override fun onBindViewHolder(holder: VacTimeViewHolder, position: Int) {
        val timeParts=vacTimeList[position].split(" ")
        Log.e("TAG", "onBindViewHolder: ${vacTimeList[position]} ${timeParts[4]}")
        holder.timeTitle.text = "${timeParts[0]} ${timeParts[1]} ${timeParts[2]} ${timeParts[3]} ${formatTime(timeParts[4])}"
        holder.setItemPosition(position)
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


}
package com.example.pillreminder.adaptors

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pillreminder.R
import com.example.pillreminder.fragments.FragmentAppointmentDetails
import com.example.pillreminder.models.Appointment
import kotlinx.android.synthetic.main.appointment_list_parent.view.*

class AppointmentRecyclerAdapter(
    internal var context: Context,
    private var appointmentList: List<Appointment>,
    internal var supportFragmentManager: FragmentManager
) : RecyclerView.Adapter<AppointmentRecyclerAdapter.AppointmentViewHolder>(){

    class AppointmentViewHolder(
        appointmentList: List<Appointment>,
        itemView: View,
        supportFragmentManager: FragmentManager
    ) : RecyclerView.ViewHolder(itemView){
        val tvTitle = itemView.tvTitle
        val tvTime = itemView.tvTime
        val tvPlace = itemView.tvPlace
        val tvPhone = itemView.tvPhone
        val tvReminder = itemView.tvReminder

        var itemPos : Int = 0
        fun setItemPosition(itemPos : Int){
            this.itemPos = itemPos
        }
        init {
            itemView.setOnClickListener {
                handleBtnClick(it)

//                Log.e("position",itemPos.toString())
                val pop = FragmentAppointmentDetails()
//                Log.e("mbl num",appointmentList[itemPos].mobile)
                val bundle = Bundle()
                bundle.putInt("position", itemPos)
                bundle.putInt("id", appointmentList[itemPos].localId)
                bundle.putString("title", appointmentList[itemPos].title)
                bundle.putString("place", appointmentList[itemPos].place)
                bundle.putString("mobile", appointmentList[itemPos].mobile)
                bundle.putString("time", appointmentList[itemPos].time)
                bundle.putBoolean("reminder", appointmentList[itemPos].reminder!!)
                bundle.putString("note", appointmentList[itemPos].note)
                bundle.putLong("task_id", appointmentList[itemPos].taskId!!)
                bundle.putString("user_id", appointmentList[itemPos].userId!!)
                bundle.putInt("remoteId", appointmentList[itemPos].remoteId!!)
                pop.arguments = bundle
                val fm = supportFragmentManager
                pop.show(fm, "FragmentAppointmentDetails")
            }
        }

        fun handleBtnClick(view: View) {
            view.isEnabled = false
            val secondsDelayed = 2
            Handler().postDelayed(Runnable {
                view.isEnabled = true
            }, ((secondsDelayed * 1000).toLong()))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.appointment_list_parent,parent,false)
        return AppointmentViewHolder(appointmentList, itemView,supportFragmentManager)
    }

    override fun getItemCount(): Int {
        return appointmentList.size
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
//        holder.txtNumber.text = (position+1).toString()
        holder.tvTitle.text = appointmentList[position].title
        holder.tvTime.text = appointmentList[position].time
        holder.tvPlace.text = appointmentList[position].place
        holder.tvPhone.text = appointmentList[position].mobile
        if (appointmentList[position].reminder!!){
            holder.tvReminder.text = "Active"
        }else{
            holder.tvReminder.text = "Inactive"
        }
        holder.setItemPosition(position)
    }


}
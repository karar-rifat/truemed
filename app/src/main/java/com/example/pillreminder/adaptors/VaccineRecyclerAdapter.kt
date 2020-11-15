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
import com.example.pillreminder.fragments.FragmentVaccineDetails
import com.example.pillreminder.models.Vaccine
import kotlinx.android.synthetic.main.appointment_list_parent.view.*
import kotlinx.android.synthetic.main.vaccine_list_parent.view.*
import kotlinx.android.synthetic.main.vaccine_list_parent.view.tvPhone
import kotlinx.android.synthetic.main.vaccine_list_parent.view.tvPlace
import kotlinx.android.synthetic.main.vaccine_list_parent.view.tvReminder
import kotlinx.android.synthetic.main.vaccine_list_parent.view.tvTime
import kotlinx.android.synthetic.main.vaccine_list_parent.view.tvTitle

class VaccineRecyclerAdapter(internal var context: Context,
                             internal var vaccineList:List<Vaccine>,
                             internal var supportFragmentManager: FragmentManager) : RecyclerView.Adapter<VaccineRecyclerAdapter.VaccineViewHolder>(){

    class VaccineViewHolder(vaccineList: List<Vaccine>,
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

                val pop = FragmentVaccineDetails()
                val bundle = Bundle()
                bundle.putInt("position", itemPos)
                bundle.putInt("id", vaccineList[itemPos].localId)
                bundle.putString("title", vaccineList[itemPos].title)
                bundle.putString("place", vaccineList[itemPos].place)
                bundle.putString("mobile", vaccineList[itemPos].mobile)
                bundle.putString("time", vaccineList[itemPos].time)
                bundle.putBoolean("reminder", vaccineList[itemPos].reminder!!)
                bundle.putString("note", vaccineList[itemPos].note)
                bundle.putLong("task_id", vaccineList[itemPos].taskId!!)
                bundle.putString("user_id", vaccineList[itemPos].userId!!)
                bundle.putInt("remoteId", vaccineList[itemPos].remoteId!!)

                pop.arguments = bundle
                val fm = supportFragmentManager
                pop.show(fm, "FragmentVaccineDetails")
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VaccineViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.vaccine_list_parent,parent,false)
        return VaccineViewHolder(vaccineList,itemView,supportFragmentManager)
    }

    override fun getItemCount(): Int {
        return vaccineList.size
    }

    override fun onBindViewHolder(holder: VaccineViewHolder, position: Int) {
//        val txt =(position+1).toString()+"."
        holder.tvTitle.text = vaccineList[position].title
        holder.tvTime.text = vaccineList[position].time
        holder.tvPlace.text = vaccineList[position].place
        holder.tvPhone.text = vaccineList[position].mobile
        if (vaccineList[position].reminder!!){
            holder.tvReminder.text = "Active"
        }else{
            holder.tvReminder.text = "Inactive"
        }
        holder.setItemPosition(position)
    }

}
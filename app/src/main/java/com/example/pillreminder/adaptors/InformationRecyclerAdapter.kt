package com.example.pillreminder.adaptors

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pillreminder.R
import com.example.pillreminder.fragments.FragmentAppointmentDetails
import com.example.pillreminder.helper.Helper
import com.example.pillreminder.helper.ViewAnimationUtils
import com.example.pillreminder.models.Appointment
import com.example.pillreminder.models.Information
import kotlinx.android.synthetic.main.appointment_list_parent.view.*
import kotlinx.android.synthetic.main.appointment_list_parent.view.tvTitle
import kotlinx.android.synthetic.main.information_item_row.view.*

class InformationRecyclerAdapter(
    internal var context: Context,
    private var informationList: List<Information>,
    internal var supportFragmentManager: FragmentManager
) : RecyclerView.Adapter<InformationRecyclerAdapter.InformationViewHolder>(){

    class InformationViewHolder(
        informationList: List<Information>,
        itemView: View,
        supportFragmentManager: FragmentManager
    ) : RecyclerView.ViewHolder(itemView){
        val tvTitle = itemView.tvTitle
        val tvdescription = itemView.tvDescription
        val ivDownArrow = itemView.ivDownArrow
        val layoutDescription=itemView.descriptionLayout

        var itemPos : Int = 0
        fun setItemPosition(itemPos : Int){
            this.itemPos = itemPos
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InformationViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.information_item_row,parent,false)
        return InformationViewHolder(informationList, itemView,supportFragmentManager)
    }

    override fun getItemCount(): Int {
        return informationList.size
    }

    override fun onBindViewHolder(holder: InformationViewHolder, position: Int) {
//        holder.txtNumber.text = (position+1).toString()
        holder.tvTitle.text = informationList[position].title
        holder.tvdescription.text = informationList[position].description

        holder.ivDownArrow.setOnClickListener { view: View ->
            val show = Helper.toggleUpDownWithAnimation(view)
            if (show) {
                ViewAnimationUtils.expand(holder.layoutDescription)
            } else {
                ViewAnimationUtils.collapse(holder.layoutDescription)
            }
        }

    }


}
package com.example.pillreminder.adaptors

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pillreminder.R
import com.example.pillreminder.fragments.FragmentProfileDependantDetails
import com.example.pillreminder.models.Dependant
import com.example.pillreminder.models.User
import kotlinx.android.synthetic.main.appointment_list_parent.view.*
import kotlinx.android.synthetic.main.view_holder_dependant.view.*
import kotlinx.android.synthetic.main.view_holder_dependant.view.tvNumber

class DependantRecyclerAdapter(internal var context: Context,
                               internal var dependantList: List<Dependant>,
                               internal var supportFragmentManager: FragmentManager) : RecyclerView.Adapter<DependantRecyclerAdapter.DependantViewHolder>(){

    class DependantViewHolder(itemView: View,dependantList: List<Dependant>,supportFragmentManager: FragmentManager) : RecyclerView.ViewHolder(itemView) {
        val txt_title = itemView.txt_list_dependent_title
        val txtNumber = itemView.tvNumber
        var itemPos : Int = 0
        fun setItemPosition(itemPos : Int){
            this.itemPos = itemPos
        }
        init {
            itemView.setOnClickListener {
                Log.e("position",itemPos.toString())
                val pop = FragmentProfileDependantDetails()
                val bundle = Bundle()
                bundle.putInt("position", itemPos)
                bundle.putLong("id", dependantList[itemPos].id)
                bundle.putString("name", dependantList[itemPos].name)
                bundle.putString("gender", dependantList[itemPos].gender)
                bundle.putString("dob", dependantList[itemPos].dob)
                bundle.putString("relationship", dependantList[itemPos].relationship)
                pop.arguments = bundle
                val fm = supportFragmentManager
                pop.show(fm, "FragmentProfileDependantDetails")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DependantRecyclerAdapter.DependantViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_holder_dependant,parent,false)
        return DependantViewHolder(itemView,dependantList,supportFragmentManager)
    }

    override fun getItemCount(): Int {
        return dependantList.size
    }

    override fun onBindViewHolder(holder: DependantRecyclerAdapter.DependantViewHolder, position: Int) {
        holder.txtNumber.text = (position+1).toString()
        holder.txt_title.text = dependantList[position].name
        holder.setItemPosition(position)
    }

}
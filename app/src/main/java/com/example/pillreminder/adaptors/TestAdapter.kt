package com.example.pillreminder.adaptors


import android.content.Context
//import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pillreminder.R
import com.example.pillreminder.fragments.FragmentDashboard
import kotlinx.android.synthetic.main.recycler_test_holder.view.*
import java.lang.StringBuilder

class TestAdapter(internal var context: Context, internal var dataList:ArrayList<String>,internal var code:String,internal val listener: (String) -> Unit)
    : RecyclerView.Adapter<TestAdapter.TestViewHolder>() {
    class TestViewHolder (
        itemView: View
    ) : RecyclerView.ViewHolder(itemView){
        val image = itemView.imageView39
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): TestViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_test_holder,parent,false)
        return TestViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
//        holder.txt_title.text = dataList[position].title?.toString()
        if(dataList[position]=="pill"){
            holder.image.setImageResource(R.drawable.pill_selected)
        }else if(dataList[position]=="appointment"){
            holder.image.setImageResource(R.drawable.appointment_selected)
        }else if(dataList[position]=="vaccine"){
            holder.image.setImageResource(R.drawable.vaccine_selected)
        }

        holder.image.setOnClickListener {
            listener(code)
        }
    }
}
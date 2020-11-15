package com.example.pillreminder.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.pillreminder.R
import com.example.pillreminder.activities.ProfileActivity
import com.example.pillreminder.adaptors.DependantRecyclerAdapter
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.models.Dependant
import com.example.pillreminder.models.User
import com.facebook.accountkit.internal.AccountKitController
import kotlinx.android.synthetic.main.fragment_profile_dependant_list.*

class FragmentProfileDependantList : Fragment(){
    val fragmentProfileCreateDependant = FragmentProfileCreateDependant()
    lateinit var dependantRecyclerAdapter: DependantRecyclerAdapter
    var dependants : List<Dependant>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_dependant_list,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageBack.setOnClickListener {
            (activity as ProfileActivity).onBackPressed()
        }
        imageAdd.setOnClickListener {
            val fm = (activity as ProfileActivity).manager
            fragmentProfileCreateDependant.show(fm, "FragmentCreateAppointment")
        }
        recycler_dependant_list.setHasFixedSize(true)
        recycler_dependant_list.layoutManager = LinearLayoutManager(context)
        fetchData()
    }

    fun fetchData() {
        val database = Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java!!, "pill_reminder").fallbackToDestructiveMigration().build()

        Thread{
            dependants = database.dependantDao().dependants()
            if(dependants == null){
                dependants= arrayListOf()
            }
//            appointments!!.forEach {
//                                Log.i("appointment",""" ID - ${it.id}""")
//            }
            (activity as ProfileActivity).runOnUiThread {
                dependantRecyclerAdapter = DependantRecyclerAdapter(context!!, dependants!!,(activity as ProfileActivity).supportFragmentManager)
                recycler_dependant_list.adapter = dependantRecyclerAdapter
                database.close()
            }
        }.start()
    }
}
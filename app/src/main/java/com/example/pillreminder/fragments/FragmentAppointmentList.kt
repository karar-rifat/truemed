package com.example.pillreminder.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.pillreminder.R
import com.example.pillreminder.activities.DashboardActivity
import com.example.pillreminder.adaptors.AppointmentRecyclerAdapter
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.models.Appointment
import com.facebook.accountkit.internal.AccountKitController
import kotlinx.android.synthetic.main.fragment_appointment_list.*

class FragmentAppointmentList : Fragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_appointment_list,container,false)
    }
    var appointmentRecyclerAdapter: AppointmentRecyclerAdapter?=null
    var appointments : List<Appointment>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        recycler_appointment_list.setHasFixedSize(true)
//        recycler_appointment_list.layoutManager = LinearLayoutManager(context)
//        fetchData

        (activity as DashboardActivity).showAddAppointment()
        recycler_appointment_list.setHasFixedSize(true)
        recycler_appointment_list.layoutManager = LinearLayoutManager(context)
        fetchData()
    }

    override fun onResume() {
        super.onResume()

        // Register Receiver to receive messages.
        LocalBroadcastManager.getInstance(context!!).registerReceiver(broadcastReceiver, IntentFilter("push-appointment"))

    }

    // handler for received Intents for the "my-event" event
    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            // Extract data included in the Intent
            val isDataUpdated = intent.getBooleanExtra("isDataUpdated",false)
            if (isDataUpdated)
                fetchData()
            Log.d("receiver", "Got message: $isDataUpdated")
        }
    }

    fun fetchData() {
        appointments=null
        val database = Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java!!, "pill_reminder").fallbackToDestructiveMigration().build()

        Thread{
            appointments = database.appointmentDao().getUserAppointments((activity as DashboardActivity).user_id!!)
            if(appointments == null){
                appointments= arrayListOf()
            }
//            appointments!!.forEach {
//                                Log.i("appointment",""" ID - ${it.id}""")
//            }
            (activity as DashboardActivity).runOnUiThread {
                appointmentRecyclerAdapter = AppointmentRecyclerAdapter(context!!, appointments!!,(activity as DashboardActivity).supportFragmentManager)
                recycler_appointment_list.adapter = appointmentRecyclerAdapter
                database.close()
            }
        }.start()
    }
}
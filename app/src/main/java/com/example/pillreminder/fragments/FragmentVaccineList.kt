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
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.pillreminder.R
import com.example.pillreminder.activities.DashboardActivity
import com.example.pillreminder.adaptors.VaccineRecyclerAdapter
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.helper.Helper
import com.example.pillreminder.models.Vaccine
import com.facebook.accountkit.internal.AccountKitController
import kotlinx.android.synthetic.main.fragment_vaccine_list.*

class FragmentVaccineList : Fragment(){

    var vaccineRecyclerAdapter: VaccineRecyclerAdapter?=null
    var vaccines : List<Vaccine>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_vaccine_list,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as DashboardActivity).showAddVaccine()

        recycler_vaccine_list.setHasFixedSize(true)
        recycler_vaccine_list.layoutManager = LinearLayoutManager(context)
        fetchData()
    }

    override fun onResume() {
        super.onResume()

        // Register Receiver to receive messages.
        LocalBroadcastManager.getInstance(context!!).registerReceiver(broadcastReceiver, IntentFilter("push-vaccine"))

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
        vaccines=null
        val database = Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java!!, "pill_reminder").fallbackToDestructiveMigration().build()

        Thread{
            vaccines = database.vaccineDao().getUserVaccines(Helper.user_id!!)
            if(vaccines == null){
                vaccines= arrayListOf()
            }
            val times = database.timePivotDao().times(Helper.user_id!!)
            times.forEach {
                                println(it)
            }
            (activity as DashboardActivity).runOnUiThread {
                vaccineRecyclerAdapter = VaccineRecyclerAdapter(context!!, vaccines!!,(activity as DashboardActivity).supportFragmentManager)
                recycler_vaccine_list.adapter = vaccineRecyclerAdapter
                database.close()
            }
        }.start()
    }
}
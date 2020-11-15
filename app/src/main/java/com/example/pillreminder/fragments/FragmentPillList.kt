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
import com.example.pillreminder.adaptors.PillRecyclerAdapter
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.models.Pill
import com.example.pillreminder.models.Table
import com.facebook.accountkit.internal.AccountKitController
import kotlinx.android.synthetic.main.fragment_pill_list.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FragmentPillList : Fragment(){
    var pillRecyclerAdapter: PillRecyclerAdapter?=null
    var tables : List<Table>? = ArrayList()
    var pills : List<Pill>? = ArrayList()

    val database = Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java, "pill_reminder").fallbackToDestructiveMigration().build()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pill_list,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        var t = ArrayList<String>()
//        t.add("1")
//        t.add("2")
//        t.add("3")
//        t.add("4")
//        t.add("5")
        recyclerTable.setHasFixedSize(true)
        recyclerTable.layoutManager = LinearLayoutManager(context)
//        val adapter = PillRecyclerAdapter(context!!,tables!!,(activity as DashboardActivity).supportFragmentManager)
//        recyclerTable.adapter = adapter

        fetchData()

    }

    override fun onResume() {
        super.onResume()

        // Register Receiver to receive messages.
        LocalBroadcastManager.getInstance(context!!).registerReceiver(broadcastReceiver, IntentFilter("push-pill"))

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

    fun fetchData(){
        val database = Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java!!, "pill_reminder").fallbackToDestructiveMigration().build()

        Thread{
            try {
                tables =
                    database.tableDao().getUserTables((activity as DashboardActivity).user_id!!)
                pills = database.pillDao().userPills((activity as DashboardActivity).user_id!!)
            }catch (e:Exception){}
            if(tables == null){
                tables= arrayListOf()
            }
            if(pills == null){
                pills= arrayListOf()
            }
            (activity as DashboardActivity).runOnUiThread {
                try {
                    pillRecyclerAdapter = PillRecyclerAdapter(
                        context!!,
                        tables!!,
                        pills!!,
                        (activity as DashboardActivity).supportFragmentManager
                    )
                    recyclerTable.adapter = pillRecyclerAdapter
                    database.close()
                }catch (e:Exception){}
            }
        }.start()

    }
}
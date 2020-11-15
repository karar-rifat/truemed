package com.example.pillreminder.service

import android.app.*
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.room.Room
import com.example.pillreminder.R
import com.example.pillreminder.activities.DashboardActivity
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.helper.Helper
import com.example.pillreminder.network.repository.DataFetchRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DataFetchService : IntentService("DataFetchService") {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onHandleIntent(intent: Intent?) {
        Log.e("DataFetchService","onHandleIntent")
        val database = Room.databaseBuilder(applicationContext, AppDb::class.java, "pill_reminder").fallbackToDestructiveMigration().build()

        val token = intent?.getStringExtra("token")
        val userId=Helper.user_id!!

            GlobalScope.launch(Dispatchers.IO) {
                    DataFetchRepo().fetchUserCharacteristics(token!!, userId)
                    DataFetchRepo().fetchTable(token, userId,applicationContext)
                    DataFetchRepo().fetchPill(token, userId)
                    DataFetchRepo().fetchAppointment(token!!, userId)
                    DataFetchRepo().fetchVaccine(token, userId)
                    DataFetchRepo().fetchTimePivot(token, userId)
                    DataFetchRepo().fetchReport(token, userId)
                    DataFetchRepo().fetchDocument(token, userId)
                    Helper.isDataFetched=true


                val intentFetch=Intent("fetch-data")
                intentFetch.putExtra("isDataFetched",true)
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intentFetch)

//                DashboardActivity().runOnUiThread {
//                    val transaction = DashboardActivity().manager.beginTransaction()
//                    transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
//                    transaction.replace(
//                        R.id.fragment_container,
//                        DashboardActivity().fragmentDashboard,
//                        "FragmentDashboard"
//                    )
////        transaction.addToBackStack(null)
//                    transaction.commit()
//                    DashboardActivity().activeFragment = DashboardActivity().fragmentDashboard
//                }
//                    BaseInterFaceImp().initData()


            }

    }

    override fun onDestroy() {
        super.onDestroy()
        //r.stop()
    }
}
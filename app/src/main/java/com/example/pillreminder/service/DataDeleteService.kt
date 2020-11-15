package com.example.pillreminder.service

import android.app.*
import android.content.Intent
import android.util.Log
import androidx.room.Room
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.helper.Helper
import com.example.pillreminder.models.*
import com.example.pillreminder.network.repository.DataDeleteRepo
import com.example.pillreminder.network.repository.DataFetchRepo
import com.example.pillreminder.network.repository.DataPushRepo
import com.facebook.accountkit.internal.AccountKitController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DataDeleteService : IntentService("DataDeleteService") {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onHandleIntent(intent: Intent?) {
        Log.e("DataDeleteService","onHandleIntent")
        val database = Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java, "pill_reminder").fallbackToDestructiveMigration().build()

        val token = Helper.token
        val type = intent?.getStringExtra("type")
        val title = intent?.getStringExtra("title")
        val remoteId = intent?.getIntExtra("remoteId",0)
        val userId=Helper.user_id!!

        if (type=="pill") {
//            val pill = database.pillDao().getPillDetails(title!!,Helper.user_id!!)
            val timePivotList = database.timePivotDao().getTimePivotDetails(title!!,Helper.user_id!!)
            Log.e("DataDeleteService","pill  ")
            GlobalScope.launch(Dispatchers.IO) {
                DataDeleteRepo().deletePill(token!!, remoteId!!)
                timePivotList.forEach {
                    DataDeleteRepo().deleteTimePivot(token, it)
                }
            }
        }

        if (type=="appointment") {
//            val appointment = database.appointmentDao().getAppointmentDetails(title!!,Helper.user_id!!)
            val timePivotList = database.timePivotDao().getTimePivotDetails(title!!,Helper.user_id!!)
            Log.e("DataDeleteService","appointment  ")
            GlobalScope.launch(Dispatchers.IO) {
                DataDeleteRepo().deleteAppointment(token!!, remoteId!!)
                timePivotList.forEach {
                    DataDeleteRepo().deleteTimePivot(token, it)
                }
            }
        }

        if (type=="vaccine") {
//           val vaccine = database.vaccineDao().getVaccineDetails(title!!,Helper.user_id!!)
            val timePivotList = database.timePivotDao().getTimePivotDetails(title!!,Helper.user_id!!)
            Log.e("DataDeleteService","vaccine  ")
            GlobalScope.launch(Dispatchers.IO) {
                DataDeleteRepo().deleteVaccine(token!!, remoteId!!)
                timePivotList.forEach {
                    DataDeleteRepo().deleteTimePivot(token, it)
                }
            }
        }

        if (type=="timePivot") {
            val timePivotList = database.timePivotDao().getTimePivotDetails(title!!,Helper.user_id!!)
            Log.e("DataDeleteService","time pivot ${timePivotList.size}")
            GlobalScope.launch(Dispatchers.IO) {
                timePivotList.forEach {
                    DataDeleteRepo().deleteTimePivot(token!!, it)
                }
            }
        }


        if (type=="table") {
            val table = database.tableDao().getTableDetails(title!!,Helper.user_id!!)
            Log.e("DataDeleteService","table ")

            GlobalScope.launch(Dispatchers.IO) {
                DataDeleteRepo().deleteTable(token!!, table)
            }
        }

        if (type=="document") {
//            val document = database.documentDao().getDocumentDetails(title!!,Helper.user_id!!)
            Log.e("DataDeleteService","document  ")

            GlobalScope.launch(Dispatchers.IO) {
                DataDeleteRepo().deleteDocument(token!!, remoteId!!)
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        //r.stop()
    }
}
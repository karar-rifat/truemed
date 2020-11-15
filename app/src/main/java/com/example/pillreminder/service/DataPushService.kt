package com.example.pillreminder.service

import android.app.*
import android.content.Intent
import android.util.Log
import androidx.room.Room
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.helper.Helper
import com.example.pillreminder.helper.SessionManager
import com.example.pillreminder.helper.SharedPrefHelper
import com.example.pillreminder.models.*
import com.example.pillreminder.network.repository.DataPushRepo
import com.facebook.accountkit.internal.AccountKitController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DataPushService : IntentService("DataPushService") {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onHandleIntent(intent: Intent?) {
        Log.e("DataPushService","onHandleIntent")
        val database = Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java, "pill_reminder").fallbackToDestructiveMigration().build()

        val type = intent?.getStringExtra("type")
//        val token = intent?.getStringExtra("token")
        val token = SessionManager(AccountKitController.getApplicationContext()).fetchAuthToken()

        val userId= SharedPrefHelper(AccountKitController.getApplicationContext()).getInstance().getUserId()

        if (type=="user") {
//            val user: User? = Helper.user
            val user = database.userDao().unSyncedUser(userId!!)
            Log.e("DataPushService"," size ${user.size}")

            GlobalScope.launch(Dispatchers.IO) {
                    DataPushRepo().pushUser(token!!, user)
            }
        }

        if (type=="pill"||type=="report") {
//            val pillList: ArrayList<Pill>? = Helper.pillList
//            val timePivotList: ArrayList<TimePivot>? = Helper.timePivotList
            val pillList: List<Pill>? = database.pillDao().unSyncedUserPills(userId!!)
            val timePivotList: List<TimePivot>? = database.timePivotDao().unSyncedTimes(userId)
            Log.e("DataPushService","pill size ${pillList!!.size}")
            GlobalScope.launch(Dispatchers.IO) {
                DataPushRepo().pushPill(token!!, pillList,applicationContext)
                DataPushRepo().pushTimePivot(token, timePivotList!!)
            }
        }

        if (type=="appointment"||type=="report") {
//            val appointmentList: ArrayList<Appointment>? = Helper.appointmentList
//            val timePivotList: ArrayList<TimePivot>? = Helper.timePivotList
            val appointmentList: List<Appointment>? = database.appointmentDao().unSyncedAppointments(userId!!)
            val timePivotList: List<TimePivot>? = database.timePivotDao().unSyncedTimes(userId)
            Log.e("DataPushService","appointment size ${appointmentList!!.size}")

            GlobalScope.launch(Dispatchers.IO) {
                DataPushRepo().pushAppointment(token!!, appointmentList,applicationContext)
                DataPushRepo().pushTimePivot(token, timePivotList!!)
            }
        }

        if (type=="vaccine"||type=="report") {
//            val vaccineList: ArrayList<Vaccine>? = Helper.vaccineList
//            val timePivotList: ArrayList<TimePivot>? = Helper.timePivotList
            val vaccineList: List<Vaccine>? = database.vaccineDao().unSyncedVaccines(userId!!)
            val timePivotList: List<TimePivot>? = database.timePivotDao().unSyncedTimes(userId)
            Log.e("DataPushService","vaccine size ${vaccineList!!.size}")

            GlobalScope.launch(Dispatchers.IO) {
                DataPushRepo().pushVaccine(token!!, vaccineList,applicationContext)
                DataPushRepo().pushTimePivot(token, timePivotList!!)
            }
        }

        if (type=="timePivot") {
//            val timePivotList: ArrayList<TimePivot>? = Helper.timePivotList
            val timePivotList: List<TimePivot>? = database.timePivotDao().unSyncedTimes(userId!!)
            Log.e("DataPushService","timePivot size ${timePivotList!!.size}")

            GlobalScope.launch(Dispatchers.IO) {
                DataPushRepo().pushTimePivot(token!!, timePivotList)
            }
        }

        if (type=="table") {
            val tableList: List<Table>? = database.tableDao().unSyncedTable(userId!!)
            Log.e("DataPushService","table size ${tableList!!.size}")

            GlobalScope.launch(Dispatchers.IO) {
                DataPushRepo().pushTable(token!!, tableList)
            }
        }

        if (type=="report") {
            val reportList: List<Report>? = database.reportDao().unSyncedReport(userId!!)
            Log.e("DataPushService","table size ${reportList!!.size}")

            GlobalScope.launch(Dispatchers.IO) {
                DataPushRepo().pushReport(token!!, reportList)
            }
        }

        if (type=="document") {
            val documentList: List<Document>? = database.documentDao().unSyncedDocuments(userId!!)
            Log.e("DataPushService","table size ${documentList!!.size}")

            GlobalScope.launch(Dispatchers.IO) {
                DataPushRepo().pushDocument(token!!, documentList,applicationContext)
            }
        }

        if (type=="all") {
            Log.e("DataPushService","type $type")

            GlobalScope.launch(Dispatchers.IO) {
                val user= database.userDao().unSyncedUser(userId!!)
                Log.e("DataPushService"," size ${user.size}")

                GlobalScope.launch(Dispatchers.IO) {
                    DataPushRepo().pushUser(token!!, user)
                }

                val pillList: List<Pill>? = database.pillDao().unSyncedUserPills(userId)
                Log.e("DataPushService","pill ${pillList?.size}")
                if (pillList!=null&& pillList.isNotEmpty())
                DataPushRepo().pushPill(token!!, pillList,applicationContext)

                val appointmentList: List<Appointment>? = database.appointmentDao().unSyncedAppointments(userId)
                Log.e("DataPushService","appointmentList ${appointmentList?.size}")
                if (appointmentList!=null&& appointmentList.isNotEmpty())
                DataPushRepo().pushAppointment(token!!, appointmentList,applicationContext)

                val vaccineList: List<Vaccine>? = database.vaccineDao().unSyncedVaccines(userId)
                Log.e("DataPushService","vaccineList ${vaccineList?.size}")
                if (vaccineList!=null&& vaccineList.isNotEmpty())
                DataPushRepo().pushVaccine(token!!, vaccineList,applicationContext)

                val timePivotList: List<TimePivot>? = database.timePivotDao().unSyncedTimes(userId)
                Log.e("DataPushService","timePivot ${timePivotList?.size}")
                if (timePivotList!=null&& timePivotList.isNotEmpty())
                    DataPushRepo().pushTimePivot(token!!, timePivotList)

                val tableList: List<Table>? = database.tableDao().unSyncedTable(userId)
                Log.e("DataPushService","table size ${tableList?.size}")
                if (tableList!=null&& tableList.isNotEmpty())
                    DataPushRepo().pushTable(token!!, tableList)

                val reportList: List<Report>? = database.reportDao().unSyncedReport(userId)
                Log.e("DataPushService","table size ${reportList?.size}")
                if (reportList!=null&& reportList.isNotEmpty())
                    DataPushRepo().pushReport(token!!, reportList)

                val documentList: List<Document>? = database.documentDao().unSyncedDocuments(userId)
                Log.e("DataPushService","Document size ${documentList?.size}")
                if (documentList!=null&& documentList.isNotEmpty())
                    DataPushRepo().pushDocument(token!!, documentList,applicationContext)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //r.stop()
    }
}
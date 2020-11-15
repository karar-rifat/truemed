package com.example.pillreminder.network.repository

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.room.Room
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.helper.AlarmHelper
import com.example.pillreminder.helper.Helper
import com.example.pillreminder.models.*
import com.example.pillreminder.network.RetrofitClient
import com.example.pillreminder.network.apiservice.DataFetchService
import com.example.pillreminder.service.DataPushService
import com.facebook.accountkit.internal.AccountKitController
import retrofit2.HttpException
import retrofit2.awaitResponse
import java.text.SimpleDateFormat
import java.util.*

class DataFetchRepo {

    private val retrofit= RetrofitClient.getRetrofitInstance()
    private val dataFetchService: DataFetchService =retrofit!!.create(DataFetchService::class.java)
    val database = Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java, "pill_reminder").fallbackToDestructiveMigration().build()
    var simpleDateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.ENGLISH)

    suspend fun fetchUserCharacteristics(token:String,uid:String) {
        Log.e("fetch repo","entered fetchUser")
        try {
            val fetchResponse = dataFetchService.fetchUserCharacteristics("Bearer $token", uid).awaitResponse()
            val user = fetchResponse.body()

            if (fetchResponse.code() == 200 && user != null) {
                Log.e("fetch repo size", user.size.toString())

                if (user.isNotEmpty())
                updateLocalUser(user[0])
            } else {
                Log.e("fetch repo", fetchResponse.message())
            }
        }catch (httpException: HttpException){
            Log.e("sync repo", "failed $httpException")
        }
    }

    suspend fun fetchPill(token:String,uid:String) {
        Log.e("fetch repo","entered fetchPill")

        try {
            val fetchResponse = dataFetchService.fetchPill("Bearer $token", uid).awaitResponse()
            val pillList = fetchResponse.body()

            if (fetchResponse.code() == 200 && !pillList.isNullOrEmpty()) {
                Log.e("fetch repo", pillList?.size.toString())
                pillList?.forEach {
                    updateLocalPill(it)
                }
            } else {
                Log.e("fetch repo", fetchResponse.message())
            }
        }catch (httpException:HttpException){
            Log.e("sync repo", "failed $httpException")
        }
    }

    suspend fun fetchAppointment(token:String,uid:String) {
        Log.e("fetch repo","entered fetchAppointment")

        try {
            val fetchResponse = dataFetchService.fetchAppointment("Bearer $token", uid).awaitResponse()
            val appointmentList = fetchResponse.body()

            if (fetchResponse.code() == 200 && !appointmentList.isNullOrEmpty()) {
                Log.e("fetch repo", appointmentList?.size.toString())
                appointmentList?.forEach {
                    updateLocalAppointment(it)
                }
            } else {
                Log.e("fetch repo", fetchResponse.message())
            }
        }catch (httpException:HttpException){
            Log.e("sync repo", "failed $httpException")
        }
    }

    suspend fun fetchVaccine(token:String,uid:String) {
        Log.e("fetch repo","entered fetchVaccine")

        try {
            val fetchResponse = dataFetchService.fetchVaccine("Bearer $token", uid).awaitResponse()
            val vaccineList = fetchResponse.body()

            if (fetchResponse.code() == 200 && !vaccineList.isNullOrEmpty()) {
                Log.e("fetch repo", vaccineList.size.toString())
                vaccineList.forEach {
                    updateLocalVaccine(it)
                }
            } else {
                Log.e("fetch repo", fetchResponse.message())
            }
        }catch (httpException:HttpException){
            Log.e("sync repo", "failed $httpException")
        }
    }

    suspend fun fetchTimePivot(token:String,uid:String) {
        Log.e("fetch repo", "entered fetchTimePivot")

        try {
            val fetchResponse = dataFetchService.fetchTimePivot("Bearer $token", uid).awaitResponse()
            val timePivotList = fetchResponse.body()

            if (fetchResponse.code() == 200 && !timePivotList.isNullOrEmpty()) {
                Log.e("fetch repo", timePivotList?.size.toString())
                timePivotList?.forEach {
                    updateLocalTimePivot(it)
                }
            } else {
                Log.e("fetch repo", fetchResponse.message())
            }
        }catch (httpException:HttpException){
            Log.e("sync repo", "failed $httpException")
        }
    }

    suspend fun fetchTable(token:String,uid:String,context: Context) {
        Log.e("fetch repo", "entered fetchTable")

        try {
            val fetchResponse = dataFetchService.fetchPillTable("Bearer $token", uid).awaitResponse()
            val tableList = fetchResponse.body()

            if (fetchResponse.code() == 200 ) {
                Log.e("fetch repo", tableList?.size.toString())
                tableList?.forEach {
                    updateLocalPillTable(it)
                }
                if (tableList!!.isEmpty()){
                    createLocalTable(context)
                }
            } else {
                Log.e("fetch repo", fetchResponse.message())
            }
        }catch (httpException:HttpException){
            Log.e("sync repo", "failed $httpException")
        }
    }

    suspend fun fetchReport(token:String,uid:String) {
        Log.e("fetch repo", "entered fetchReport")

        try {
            val fetchResponse = dataFetchService.fetchReport("Bearer $token", uid).awaitResponse()
            val reportList = fetchResponse.body()

            if (fetchResponse.code() == 200 && !reportList.isNullOrEmpty()) {
                Log.e("fetch repo", reportList?.size.toString())
                reportList?.forEach {
                    updateLocalReport(it)
                }
            } else {
                Log.e("fetch repo", fetchResponse.message())
            }
        }catch (httpException:HttpException){
            Log.e("sync repo", "failed $httpException")
        }
    }

    suspend fun fetchDocument(token:String,uid:String) {
        Log.e("fetch repo", "entered fetchDocument")

        try {
            val fetchResponse = dataFetchService.fetchDocument("Bearer $token", uid).awaitResponse()
            val documentList = fetchResponse.body()

            if (fetchResponse.code() == 200 && !documentList.isNullOrEmpty()) {
                Log.e("fetch repo", documentList?.size.toString())
                documentList?.forEach {
                    updateLocalDocument(it)
                }
            } else {
                Log.e("fetch repo", fetchResponse.message())
            }
        }catch (httpException:HttpException){
            Log.e("sync repo", "failed $httpException")
        }
    }

     fun updateLocalUser(user:User){
        Log.e("fetch repo", "entered update user")
//         database.userDao().saveUser(user)
        val localUser:User?=database.userDao().getUserByEmail(user.email!!)
         if (localUser!=null) {
//             val updatedLocal: Date = simpleDateFormat.parse(localUser.updatedAt)
//             val updatedServer: Date = simpleDateFormat.parse(user.updatedAt)
//             if (updatedLocal.compareTo(updatedServer) < 0)
//             user.synced=true
             localUser.dateOfBirth=user.dateOfBirth
             localUser.gender=user.gender
             localUser.weight=user.weight
             localUser.height=user.height
             localUser.bloodGroup=user.bloodGroup
             localUser.bloodPressure=user.bloodPressure
             localUser.photo=user.photo
             localUser.synced=true
             database.userDao().update(localUser)
         }
    }

     fun updateLocalPill(pill: Pill){
        Log.e("fetch repo", "entered update pill")

//        val localPill=database.pillDao().getPillDetails(pill.title!!)
//        val updatedLocal: Date = simpleDateFormat.parse(localPill.updatedAt)
//        val updatedServer: Date = simpleDateFormat.parse(pill.updatedAt)
//
//        Log.e("fetch repo", "local $updatedLocal server $updatedServer")

         pill.synced=true
         pill.lowestStock=5
         pill.stockReminder=true
         database.pillDao().savePill(pill)
//        if (updatedLocal.compareTo(updatedServer) < 0){
//            database.pillDao().update(pill)
//        Log.e("fetch repo", "updated user")
//    }
    }

     fun updateLocalAppointment(appointment: Appointment){
        Log.e("fetch repo", "entered update appointment")

//        val localAppointment=database.appointmentDao().getAppointment(appointment.local_id)
//        val updatedLocal: Date = simpleDateFormat.parse(localAppointment.updatedAt)
//        val updatedServer: Date = simpleDateFormat.parse(appointment.updatedAt)
//
//        Log.e("fetch repo", "local $updatedLocal server $updatedServer")

         appointment.synced=true
         database.appointmentDao().saveAppointment(appointment)
//        if (updatedLocal.compareTo(updatedServer) < 0){
//            database.appointmentDao().update(appointment)
//        Log.e("fetch repo", "updated appointment")
//    }
    }

     fun updateLocalVaccine(vaccine: Vaccine){
        Log.e("fetch repo", "entered update vaccine")

//        val localVaccine=database.vaccineDao().getVaccine(vaccine.local_id)
//        val updatedLocal: Date = simpleDateFormat.parse(localVaccine.updatedAt)
//        val updatedServer: Date = simpleDateFormat.parse(vaccine.updatedAt)
//
//        Log.e("fetch repo", "local $updatedLocal server $updatedServer")

         vaccine.synced=true
         val local_id=database.vaccineDao().saveVaccine(vaccine)
//        if (updatedLocal.compareTo(updatedServer) < 0){
//            database.vaccineDao().update(vaccine)
//        Log.e("fetch repo", "updated vaccine id $local_id")
//    }
         Log.e("fetch repo", "updated vaccine id $local_id")
    }

     fun updateLocalTimePivot(timePivot: TimePivot){
        Log.e("fetch repo", "entered update time")

//        val localTimePivot=database.timePivotDao().getTimeById(timePivot.local_id)
//        val updatedLocal: Date = simpleDateFormat.parse(localTimePivot.updatedAt)
//        val updatedServer: Date = simpleDateFormat.parse(timePivot.updatedAt)
//
//        Log.e("fetch repo", "local $updatedLocal server $updatedServer")

         timePivot.synced=true
         val local_id=database.timePivotDao().saveTime(timePivot)
//        if (updatedLocal.compareTo(updatedServer) < 0) {
//            database.timePivotDao().update(timePivot)
//            Log.e("fetch repo", "updated time")
//        }
         Log.e("fetch repo", "updated time id $local_id")
    }

     fun updateLocalPillTable(table: Table){
        Log.e("fetch repo", "entered update table")

//        val localTable=database.tableDao().getTable(table.local_id)
//        val updatedLocal: Date = simpleDateFormat.parse(localTable.updatedAt)
//        val updatedServer: Date = simpleDateFormat.parse(localTable.updatedAt)
//
//        Log.e("fetch repo", "local $updatedLocal server $updatedServer")
         table.synced=true
         database.tableDao().saveTable(table)
//        if (updatedLocal.compareTo(updatedServer) < 0) {
//            database.tableDao().update(localTable)
//            Log.e("fetch repo", "updated time")
//        }
    }

     fun updateLocalReport(report: Report){
        Log.e("fetch repo", "entered update report")

//        val localTimePivot=database.timePivotDao().getTimeById(report.local_id)
//        val updatedLocal: Date = simpleDateFormat.parse(localTimePivot.updatedAt)
//        val updatedServer: Date = simpleDateFormat.parse(report.updatedAt)
//
//        Log.e("fetch repo", "local $updatedLocal server $updatedServer")
         report.synced=true
         database.reportDao().saveReport(report)
//        if (updatedLocal.compareTo(updatedServer) < 0) {
//            database.reportDao().update(report)
//            Log.e("fetch repo", "updated time")
//        }
    }

    fun updateLocalDocument(document: Document){
        Log.e("fetch repo", "entered update document")

//        val localTimePivot=database.timePivotDao().getTimeById(report.local_id)
//        val updatedLocal: Date = simpleDateFormat.parse(localTimePivot.updatedAt)
//        val updatedServer: Date = simpleDateFormat.parse(report.updatedAt)
//
//        Log.e("fetch repo", "local $updatedLocal server $updatedServer")
        document.synced=true
        database.documentDao().saveDocument(document)
//        if (updatedLocal.compareTo(updatedServer) < 0) {
//            database.reportDao().update(report)
//            Log.e("fetch repo", "updated time")
//        }
    }

    fun createLocalTable(context: Context){
        Log.e("sync repo table", "create table entered")
        Thread {
            val database = Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java!!, "pill_reminder").fallbackToDestructiveMigration().build()

            //get current datetime
            val simpleDateFormat= SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.ENGLISH)
            val currentDateTime=simpleDateFormat.format(Date()).toString()

            val tables = database.tableDao().tables()
            Log.e("dashboard", "onCreate: table size ${tables.size}" )
            if (tables.isEmpty()) {
//                (activity as DashboardActivity).showAddPill()

                val newTable = Table("My Pill Table",Helper.user_id!!,currentDateTime,false)
                val id  = database.tableDao().saveTable(newTable)
                newTable.localId = id.toInt()
                Log.e("new table details",newTable.localId.toString()+" "+newTable.title)

                //update data to server with background service
                if (Helper.isConnected(context)) {
                    val token= Helper.token
                    Log.e("add table", "starting push service table")
                    val service = Intent(context, DataPushService::class.java)
                    service.putExtra("type", "table")
                    service.putExtra("token", token)
                    context.startService(service)
                }else{
                    AlarmHelper().startAlarmForSync(context,"table")
                    Log.e("add table ", "alarm for sync table")
                }


            }
//            database.close()
        }.start()
    }

}
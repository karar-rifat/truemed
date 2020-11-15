package com.example.pillreminder.network.repository

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.room.Room
import com.example.pillreminder.activities.DashboardActivity
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.models.*
import com.example.pillreminder.network.RetrofitClient
import com.example.pillreminder.network.apiservice.DataPushService
import com.example.pillreminder.network.request.TableRequest
import com.example.pillreminder.network.response.PushDataResponse
import com.example.pillreminder.network.response.PushReportResponse
import com.facebook.accountkit.internal.AccountKitController
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.awaitResponse
import java.util.ArrayList

class DataPushRepo {

    private val retrofit= RetrofitClient.getRetrofitInstance()
    private val dataPushService: DataPushService =retrofit!!.create(DataPushService::class.java)
    val database = Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java, "pill_reminder").fallbackToDestructiveMigration().build()

    suspend fun pushUser(token:String,userList: List<User>): String? {
        Log.e("sync repo","entered pushUser")
        val pushResponse = dataPushService.pushUser("Bearer $token",userList).awaitResponse()

        if (pushResponse.code() == 200 && !pushResponse.body().isNullOrEmpty()) {
            Log.e("sync repo", pushResponse.body()!!)
           userList.forEach {
               it.synced=true
               database.userDao().update(it)
           }
        }
        else{
            Log.e("sync repo", pushResponse.message())
        }
        return pushResponse.body()
    }

    suspend fun pushPill(token:String,pill: List<Pill>,context: Context): List<PushDataResponse>? {
        Log.e("sync repo","entered pushPill")
        var pushResponseList: List<PushDataResponse>?=null
        try {
            val response = dataPushService.pushPill("Bearer $token", pill).awaitResponse()
            pushResponseList=response.body()!!
            if (response.code() == 200 && !response.body().isNullOrEmpty()) {
                Log.e("sync repo", pushResponseList.size.toString())
                for (it in pill.indices){
                    pill[it].synced = true
                    pill[it].remoteId = pushResponseList[it].id!!
                    database.pillDao().update(pill[it])

                    val intentPush= Intent("push-pill")
                    intentPush.putExtra("isDataUpdated",true)
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intentPush)
                }
            } else {
                Log.e("sync repo", response.message())
            }
        }catch (httpException:HttpException){
            Log.e("sync repo", "failed $httpException")
        }
        return pushResponseList
    }

    suspend fun pushAppointment(token:String,appointment: List<Appointment>,context: Context): List<PushDataResponse>? {
        Log.e("sync repo","entered pushAppointment")
        var pushResponse: List<PushDataResponse>?=null
        try {
            val response = dataPushService.pushAppointment("Bearer $token", appointment).awaitResponse()
            pushResponse=response.body()
            if (response.code() == 200 && !response.body().isNullOrEmpty()) {
                Log.e("sync repo", pushResponse!!.size.toString())
                for (it in appointment.indices){
                    appointment[it].synced = true
                    appointment[it].remoteId = pushResponse[it].id!!
                    database.appointmentDao().update(appointment[it])

                    val intentPush= Intent("push-appointment")
                    intentPush.putExtra("isDataUpdated",true)
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intentPush)
                }
            } else {
                Log.e("sync repo", response.message())
            }
        }catch (httpException:HttpException){
            Log.e("sync repo", "failed $httpException")
        }
        return pushResponse
    }

    suspend fun pushVaccine(token:String,vaccine: List<Vaccine>,context: Context): List<PushDataResponse>? {
        Log.e("sync repo","entered pushVaccine")
        var pushResponse: List<PushDataResponse>?=null
        try {
            val response = dataPushService.pushVaccine("Bearer $token", vaccine).awaitResponse()
            pushResponse=response.body()
            if (response.code() == 200 && !response.body().isNullOrEmpty()) {
                Log.e("sync repo", pushResponse!!.size.toString())
                for (it in vaccine.indices){
                    vaccine[it].synced = true
                    vaccine[it].remoteId = pushResponse[it].id!!
                    database.vaccineDao().update(vaccine[it])

                    val intentPush= Intent("push-vaccine")
                    intentPush.putExtra("isDataUpdated",true)
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intentPush)
                }
            } else {
                Log.e("sync repo", response.message())
            }
        }catch (httpException:HttpException){
            Log.e("sync repo", "failed $httpException")
        }
            return pushResponse
    }

    suspend fun pushTimePivot(token:String,timePivot: List<TimePivot>): List<PushDataResponse>? {
        Log.e("sync repo", "entered pushTimePivot")
        var pushResponse: List<PushDataResponse>?=null
        try {
            val response = dataPushService.pushTimePivot("Bearer $token", timePivot).awaitResponse()
            pushResponse = response.body()
            if (response.code() == 200 && !response.body().isNullOrEmpty()) {
                Log.e("sync repo", pushResponse!!.size.toString())
                for (it in timePivot.indices){
                    timePivot[it].synced = true
                    timePivot[it].remoteId = pushResponse[it].id!!
                    database.timePivotDao().update(timePivot[it])
                }
            }else {
                Log.e("sync repo", response.message())
            }
        }catch (httpException:HttpException){
            Log.e("sync repo", "failed $httpException")
        }
        return pushResponse
    }

    suspend fun pushTable(token:String,table: List<Table>): List<PushDataResponse>? {
        Log.e("sync repo", "entered pushTable")
        var pushResponse: List<PushDataResponse>?=null
        try {
            val response = dataPushService.pushPillTable("Bearer $token", table).awaitResponse()
            pushResponse = response.body()
            if (response.code() == 200 && !response.body().isNullOrEmpty()) {
                Log.e("sync repo",pushResponse!!.size.toString())
                for (it in table.indices){
                    table[it].synced = true
                    table[it].remoteId = pushResponse[it].id!!
                    database.tableDao().update(table[it])
                }
            }else {
                Log.e("sync repo", response.message())
            }
        }catch (httpException:HttpException){
            Log.e("sync repo", "failed $httpException")
        }
        return pushResponse
    }

    suspend fun pushReport(token:String,report: List<Report>): List<PushReportResponse>? {
        Log.e("sync repo", "entered pushReport")
        var pushResponse: List<PushReportResponse>?=null
        try {
            val response = dataPushService.pushReport("Bearer $token", report).awaitResponse()
            pushResponse = response.body()
            if (response.code() == 200 && !response.body().isNullOrEmpty()) {
                Log.e("sync repo", pushResponse!!.size.toString())
                for (it in report.indices){
                    report[it].synced = true
                    report[it].remoteId = pushResponse[it].id!!
                    database.reportDao().update(report[it])
                }
            }else {
                Log.e("sync repo", response.message())
            }
        }catch (httpException:HttpException){
            Log.e("sync repo", "failed $httpException")
        }
        return pushResponse
    }

    suspend fun pushDocument(token:String,document: List<Document>,context: Context):  List<PushDataResponse>? {
        Log.e("sync repo", "entered pushDocument")
        var pushResponse: List<PushDataResponse>?=null
        try {
            val response = dataPushService.pushDocument("Bearer $token", document).awaitResponse()
            pushResponse = response.body()
            if (response.code() == 200 && !response.body().isNullOrEmpty()) {
                Log.e("sync repo", pushResponse!!.size.toString())
                for (it in document.indices){
                    document[it].synced = true
                    document[it].remoteId = pushResponse[it].id!!
                    database.documentDao().update(document[it])
                    Log.e("sync repo document", "remote id ${pushResponse[it].id}")
                }
            }else {
                Log.e("sync repo", response.message())
            }
        }catch (httpException:HttpException){
            Log.e("sync repo", "failed $httpException")
        }
        return pushResponse
    }


}
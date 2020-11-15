package com.example.pillreminder.network.repository

import android.util.Log
import androidx.room.Room
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.models.*
import com.example.pillreminder.network.RetrofitClient
import com.example.pillreminder.network.apiservice.DataDeleteService
import com.example.pillreminder.network.apiservice.DataFetchService
import com.facebook.accountkit.internal.AccountKitController
import retrofit2.HttpException
import retrofit2.awaitResponse
import java.text.SimpleDateFormat
import java.util.*

class DataDeleteRepo {

    private val retrofit= RetrofitClient.getRetrofitInstance()
    private val dataDeleteService: DataDeleteService =retrofit!!.create(DataDeleteService::class.java)
    val database = Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java, "pill_reminder").fallbackToDestructiveMigration().build()
    var simpleDateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.ENGLISH)


    suspend fun deletePill(token:String,remoteId:Int) {
        Log.e("delete repo","entered deletePill")

        try {
            val response = dataDeleteService.deletePill("Bearer $token", remoteId.toLong()).awaitResponse()
            val responseMsg = response.body()

            if (response.code() == 200 ) {
                Log.e("delete repo", response.body()!!)
//                deletePillFromLocalDB(remoteId)

            } else {
                Log.e("delete repo", response.message())
            }
        }catch (httpException:HttpException){
            Log.e("delete repo", "failed $httpException")
        }
    }

    suspend fun deleteAppointment(token:String,remoteId:Int) {
        Log.e("delete repo","entered deleteAppointment")

        try {
            val response = dataDeleteService.deleteAppointment("Bearer $token", remoteId.toLong()).awaitResponse()
            val responseMsg = response.body()

            if (response.code() == 200 ) {
                Log.e("delete repo", response.body()!!)


            } else {
                Log.e("delete repo", response.message())
            }
        }catch (httpException:HttpException){
            Log.e("delete repo", "failed $httpException")
        }
    }

    suspend fun deleteVaccine(token:String,remoteId:Int) {
        Log.e("delete repo","entered deleteVaccine")

        try {
            val response = dataDeleteService.deleteVaccine("Bearer $token",remoteId.toLong()).awaitResponse()
            val responseMsg = response.body()

            if (response.code() == 200 ) {
                Log.e("delete repo", response.body()!!)

            } else {
                Log.e("delete repo", response.message())
            }
        }catch (httpException:HttpException){
            Log.e("delete repo", "failed $httpException")
        }
    }

    suspend fun deleteTimePivot(token:String,timePivot: TimePivot) {
        Log.e("delete repo", "entered deleteTimePivot ${timePivot.title} ${timePivot.remoteId}")

        try {
            val response = dataDeleteService.deleteTimePivot("Bearer $token", timePivot.remoteId!!.toLong()).awaitResponse()
            val responseMsg = response.body()

            if (response.code() == 200 ) {
                Log.e("delete repo", response.body()!!)
                deleteTimePivotFromLocalDB(timePivot)

            } else {
                Log.e("delete repo", response.message())
            }
        }catch (httpException:HttpException){
            Log.e("delete repo", "failed $httpException")
        }
    }

    suspend fun deleteTable(token:String,table: Table) {
        Log.e("delete repo", "entered deleteTable")

        try {
            val response = dataDeleteService.deletePillTable("Bearer $token", table.remoteId!!.toLong()).awaitResponse()
            val responseMsg = response.body()

            if (response.code() == 200 ) {
                Log.e("delete repo", response.body()!!)

            } else {
                Log.e("delete repo", response.message())
            }
        }catch (httpException:HttpException){
            Log.e("delete repo", "failed $httpException")
        }
    }

    suspend fun deleteDocument(token:String,remoteId:Int) {
        Log.e("delete repo", "entered deleteReport")

        try {
            val response = dataDeleteService.deleteDocument("Bearer $token", remoteId.toLong()).awaitResponse()
            val responseMsg = response.body()

            if (response.code() == 200 ) {
                Log.e("delete repo", response.body()!!)

            } else {
                Log.e("delete repo", response.message())
            }
        }catch (httpException:HttpException){
            Log.e("delete repo", "failed $httpException")
        }
    }

    fun deletePillFromLocalDB(pill:Pill){
        database.pillDao().delete(pill)
    }
    fun deleteAppointmentFromLocalDB(appointment: Appointment){
        database.appointmentDao().delete(appointment)
    }
    fun deleteVaccineFromLocalDB(vaccine: Vaccine){
        database.vaccineDao().delete(vaccine)
    }

    fun deleteTimePivotFromLocalDB(timePivot: TimePivot){
        database.timePivotDao().delete(timePivot)
        Log.e("delete local timePivot", "deleted")
    }

    fun deleteDocumentFromLocalDB(document: Document){
        database.documentDao().delete(document)
    }

}
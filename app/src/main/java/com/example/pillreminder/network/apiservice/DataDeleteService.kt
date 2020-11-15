package com.example.pillreminder.network.apiservice

import com.example.pillreminder.models.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface DataDeleteService {

     @DELETE("pill")
     fun deletePill(@Header("Authorization") token: String,@Query("id") remoteId:Long): Call<String>

    @DELETE("appointment")
     fun deleteAppointment(@Header("Authorization") token: String,@Query("id") remoteId:Long): Call<String>

    @DELETE("vaccine")
     fun deleteVaccine(@Header("Authorization") token: String,@Query("id") remoteId:Long): Call<String>

    @DELETE("time-pivot")
     fun deleteTimePivot(@Header("Authorization") token: String,@Query("id") remoteId:Long): Call<String>

    @DELETE("pill-table")
     fun deletePillTable(@Header("Authorization") token: String,@Query("id") remoteId:Long): Call<String>

    @DELETE("document")
     fun deleteDocument(@Header("Authorization") token: String,@Query("id") remoteId:Long): Call<String>
}
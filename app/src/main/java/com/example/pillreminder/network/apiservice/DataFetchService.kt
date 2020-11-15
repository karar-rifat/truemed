package com.example.pillreminder.network.apiservice

import com.example.pillreminder.models.*
import com.example.pillreminder.network.response.UserResponse
import retrofit2.Call
import retrofit2.http.*

interface DataFetchService {

    @GET("profile")
     fun fetchUser(@Header("Authorization") token: String,@Query("profileId") email: String): Call<UserResponse>

    @GET("user-characteristics")
    fun fetchUserCharacteristics(@Header("Authorization") token: String,@Query("uid") profileId:String): Call<List<User>>

    @GET("pill")
     fun fetchPill(@Header("Authorization") token: String,@Query("userId") uid: String): Call<List<Pill>>

    @GET("appointment")
     fun fetchAppointment(@Header("Authorization") token: String,@Query("userId") uid: String): Call<List<Appointment>>

    @GET("vaccine")
     fun fetchVaccine(@Header("Authorization") token: String,@Query("userId") uid: String): Call<List<Vaccine>>

    @GET("time-pivot")
     fun fetchTimePivot(@Header("Authorization") token: String,@Query("userId") uid: String): Call<List<TimePivot>>

    @GET("pill-table")
     fun fetchPillTable(@Header("Authorization") token: String,@Query("userId") uid: String): Call<List<Table>>

    @GET("report")
     fun fetchReport(@Header("Authorization") token: String,@Query("userId") uid: String): Call<List<Report>>

    @GET("document")
    fun fetchDocument(@Header("Authorization") token: String,@Query("userId") uid: String): Call<List<Document>>
}
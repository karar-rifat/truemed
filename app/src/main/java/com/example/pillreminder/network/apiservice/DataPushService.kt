package com.example.pillreminder.network.apiservice

import com.example.pillreminder.models.*
import com.example.pillreminder.network.request.TableRequest
import com.example.pillreminder.network.response.PushDataResponse
import com.example.pillreminder.network.response.PushReportResponse
import retrofit2.Call
import retrofit2.http.*

interface DataPushService {

    @POST("user-characteristics")
    fun pushUser(@Header("Authorization") token: String,@Body user: List<User>): Call<String>

    @POST("pill")
    fun pushPill(@Header("Authorization") token: String,@Body pill: List<Pill>): Call<List<PushDataResponse>>

    @POST("appointment")
    fun pushAppointment(@Header("Authorization") token: String,@Body appointment: List<Appointment>): Call<List<PushDataResponse>>

    @POST("vaccine")
    fun pushVaccine(@Header("Authorization") token: String,@Body vaccine: List<Vaccine>): Call<List<PushDataResponse>>

    @POST("time-pivot")
    fun pushTimePivot(@Header("Authorization") token: String,@Body pivot: List<TimePivot>): Call<List<PushDataResponse>>

    @POST("pill-table")
    fun pushPillTable(@Header("Authorization") token: String,@Body table: List<Table>): Call<List<PushDataResponse>>
//    fun pushPillTable(@Header("Authorization") token: String,@Body table: List<TableRequest>): Call<String>

    @POST("report")
    fun pushReport(@Header("Authorization") token: String,@Body report: List<Report>): Call<List<PushReportResponse>>

    @POST("document")
    fun pushDocument(@Header("Authorization") token: String,@Body document: List<Document>): Call<List<PushDataResponse>>
}
package com.example.pillreminder.network.apiservice

import com.example.pillreminder.models.Information
import com.example.pillreminder.network.response.MedicineResponse
import retrofit2.Call
import retrofit2.http.*

interface InformationService {

    @GET("information")
    fun getInformation(@Header("Authorization") token: String): Call<List<Information>>
}
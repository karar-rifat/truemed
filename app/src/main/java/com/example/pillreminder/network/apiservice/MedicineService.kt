package com.example.pillreminder.network.apiservice

import com.example.pillreminder.network.response.MedicineResponse
import retrofit2.Call
import retrofit2.http.*

interface MedicineService {

    @GET("medicines")
    fun getMedicine(@Header("Authorization") token: String): Call<List<MedicineResponse>>
}
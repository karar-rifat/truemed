package com.example.pillreminder.network.repository

import android.util.Log
import com.example.pillreminder.helper.Helper
import com.example.pillreminder.network.RetrofitClient
import com.example.pillreminder.network.apiservice.MedicineService
import com.example.pillreminder.network.response.MedicineResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MedicineRepo {
    private val retrofit= RetrofitClient.getRetrofitInstance()
    private val medicineService:MedicineService =retrofit!!.create(MedicineService::class.java)
    var medicineList: List<MedicineResponse> =ArrayList<MedicineResponse>()

    fun getMedicine(token:String): List<MedicineResponse>? {
        Log.e("medicine repo", "entered medicine")

        val call: Call<List<MedicineResponse>> = medicineService.getMedicine("Bearer $token")

        call.enqueue(object :
            Callback<List<MedicineResponse>> {
            override fun onResponse(
                call: Call<List<MedicineResponse>>,
                response: Response<List<MedicineResponse>>
            ) {
                Log.e("medicine repo", "response ${response.code()} ${response.body()}")
                val medicineResponse = response.body()
                if (response.code() == 200 && !medicineResponse.isNullOrEmpty()) {
                    //login success
                        Helper.medicinesList = medicineResponse as ArrayList<MedicineResponse>
                    Log.e("medicine repo", Helper.medicinesList.size.toString() + " " + medicineResponse.size.toString() + " " + medicineResponse[0].productname).toString()

                }
            }

            override fun onFailure(call: Call<List<MedicineResponse>>, t: Throwable) {
                //login failed
                Log.e("medicine repo", "failed $t")
            }
        })
        Log.e("medicine repo", "size " + medicineList.size.toString())

        return medicineList
    }

}
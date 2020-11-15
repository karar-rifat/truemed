package com.example.pillreminder.network.repository

import android.util.Log
import androidx.room.Room
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.helper.Helper
import com.example.pillreminder.models.Appointment
import com.example.pillreminder.models.Information
import com.example.pillreminder.network.RetrofitClient
import com.example.pillreminder.network.apiservice.InformationService
import com.example.pillreminder.network.apiservice.MedicineService
import com.example.pillreminder.network.response.MedicineResponse
import com.facebook.accountkit.internal.AccountKitController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InformationRepo {
    private val retrofit = RetrofitClient.getRetrofitInstance()
    private val informationService: InformationService = retrofit!!.create(InformationService::class.java)
    var informationList: List<Information> = ArrayList<Information>()
    val database =
        Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java, "pill_reminder")
            .fallbackToDestructiveMigration().build()

    fun getInformation(token: String, informationHandler: (List<Information>) -> Unit): List<Information>? {
        Log.e("Information repo", "entered Information")

        val call: Call<List<Information>> = informationService.getInformation("Bearer $token")

        call.enqueue(object :
            Callback<List<Information>> {
            override fun onResponse(
                call: Call<List<Information>>,
                response: Response<List<Information>>
            ) {
                Log.e("Information repo", "response ${response.code()} ${response.body()}")
                val informationResponse = response.body()
                if (response.code() == 200 && !informationResponse.isNullOrEmpty()) {
                    //login success
                    informationList = informationResponse as ArrayList<Information>
                    Helper.informationList = informationResponse as ArrayList<Information>
                    informationHandler(informationList)

//                    informationList.forEach {
//                        database.informationDao().saveInformation(it)
//                        Log.e("Information repo", "title ${it.title}")
//                    }

                    Log.e(
                        "Information repo",
                        Helper.informationList.size.toString() + " " + informationResponse.size.toString() + " " + informationResponse[0].title
                    ).toString()
                }
            }

            override fun onFailure(call: Call<List<Information>>, t: Throwable) {
                //login failed
                Log.e("Information repo", "failed $t")
            }
        })
        Log.e("medicine repo", "size " + informationList.size.toString())

        return informationList
    }

}
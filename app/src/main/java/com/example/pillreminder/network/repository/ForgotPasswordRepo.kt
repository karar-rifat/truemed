package com.example.pillreminder.network.repository

import android.util.Log
import com.example.pillreminder.network.RetrofitClient
import com.example.pillreminder.network.apiservice.ForgotPasswordService
import com.example.pillreminder.network.request.ForgotPasswordRequest
import com.example.pillreminder.network.request.ResetPasswordRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordRepo {
    private val retrofit= RetrofitClient.getRetrofitInstance()
    private val forgotPasswordService =retrofit!!.create(ForgotPasswordService::class.java)

    suspend fun forgotPassword(forgotPasswordRequest: ForgotPasswordRequest,responseHandler:(url:String)->Unit,messageHandler:(url:String)->Unit): String? {
        Log.e("forgot password repo","entered ")
        var serverResponse:String?=null
        val call: Call<String> = forgotPasswordService.forgotPassword(forgotPasswordRequest)

        call.enqueue(object :
            Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.e("forgot pass repo","response ${response.code()} ${response.body()}")
                val forgotPasswordResponse = response.body()
                if (response.code() == 200&&forgotPasswordResponse!!.isNotEmpty()) {
                    // success
                    if(forgotPasswordResponse!="Invalid Email") {
                        serverResponse = forgotPasswordResponse
                        responseHandler(serverResponse!!)
                        Log.e("forgot pass repo", forgotPasswordResponse)
                    }else{
                        messageHandler("Email doesn't match")
                        Log.e("forgot pass repo", "Invalid Email")
                    }
                }
           }
            override fun onFailure(call: Call<String>, t: Throwable) {
                //login failed
                Log.e("forgot pass repo","failed $t")
            }

        })

        return serverResponse
    }

    suspend fun resetPassword(resetPasswordRequest: ResetPasswordRequest,messageHandler:(url:String)->Unit): String? {
        Log.e("reset pass repo","entered ")
        val call: Call<String> = forgotPasswordService.resetPassword(resetPasswordRequest)
        var serverResponse:String?=null
        call.enqueue(object :
            Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.e("reset pass repo","response ${response.code()} ${response.body()}")
               serverResponse = response.body()
                if (response.code() == 200&&serverResponse!!.isNotEmpty()) {
                    //login success
                    messageHandler(serverResponse!!)
                    Log.e("reset pass repo",serverResponse!!)
                }
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                //login failed
//                FragmentForgotPassword().messageHandler("Error")
                Log.e("reset pass repo","failed $t")
            }

        })

        return serverResponse
    }


}
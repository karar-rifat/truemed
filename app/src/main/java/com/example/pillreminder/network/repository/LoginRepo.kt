package com.example.pillreminder.network.repository

import android.util.Log
import com.example.pillreminder.helper.Helper
import com.example.pillreminder.network.RetrofitClient
import com.example.pillreminder.network.apiservice.LoginService
import com.example.pillreminder.network.request.LoginRequest
import com.example.pillreminder.network.response.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginRepo {
    private val retrofit= RetrofitClient.getRetrofitInstance()
    private val loginService: LoginService =retrofit!!.create(LoginService::class.java)

    fun login(loginRequest: LoginRequest,responseHandler:(message:String,error:Boolean)->Unit): String? {
        Log.e("login repo","entered login")
        Log.e("login repo",loginRequest.uid+" "+loginRequest.password!!)
        Helper.token=null
        var token:String?=null
        val call: Call<LoginResponse> = loginService.login(loginRequest)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                Log.e("login repo","response ${response.code()} ${response.body()}")
                val loginResponse = response.body()
                if (response.code() == 200&&!loginResponse?.token.isNullOrEmpty()) {
                    //login success
                    token=loginResponse!!.token
                    Helper.token=token
                    responseHandler(token!!,false)
                    Log.e("login repo",loginResponse.token!!)
                    Log.e("login Session",loginResponse.token!!)
                }else{
                    responseHandler(response.message(),true)
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                //login failed
                Log.e("login repo","failed $t")
            }
        })

        return token
    }


}
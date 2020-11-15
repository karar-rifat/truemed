package com.example.pillreminder.network.repository

import android.util.Log
import com.example.pillreminder.network.RetrofitClient
import com.example.pillreminder.network.apiservice.SignUpService
import com.example.pillreminder.network.request.RegisterRequest
import retrofit2.*
import java.io.EOFException


class SignUpRepo {
    private val retrofit = RetrofitClient.getRetrofitInstance()
    private val signUpService: SignUpService = retrofit!!.create(SignUpService::class.java)

     suspend fun signUp(signUpRequest: RegisterRequest, signUpHandler:(isSuccess:Boolean)->Unit): Boolean {
        Log.e("signUp repo", "entered signUp ${signUpRequest.email} " +
                "${signUpRequest.name} ${signUpRequest.password} ${signUpRequest.type} ${signUpRequest.uid} ${signUpRequest.phoneNumber}")
        var issuccess: Boolean = false
        try {
//            val call = signUpService.signUp(signUpRequest)

            val response = signUpService.signUp(signUpRequest).awaitResponse()
            Log.e("signUp repo", "response ${response.code()} ${response.body()}")
            if (response.code() == 200) {
                //login success
                issuccess = true
                signUpHandler(issuccess)
                Log.e("signUp repo", "successful")
            } else {
                signUpHandler(issuccess)
                Log.e("sync repo", response.message())
            }

//            call.enqueue(object : Callback<String> {
//
//                override fun onResponse(call: Call<String>, response: Response<String>) {
//                    Log.e("signUp repo", "response ${response.code()} ${response.body()}")
//                    //login success
//                issuccess = true
//                signUpHandler(issuccess)
//                Log.e("signUp repo", "successful")
//                }
//
//                override fun onFailure(call: Call<String>, t: Throwable) {
//                    Log.e("sync repo", "failed $t")
//                }
//            })

        } catch (httpException: HttpException) {
            Log.e("sync repo", "failed $httpException")
        }catch (eofException: EOFException) {
            Log.e("sync repo", "failed $eofException")
        }

        return issuccess
    }

//    suspend fun signUp(user: User): Boolean {
//        Log.e("signUp repo", "entered signUp")
//        var issuccess: Boolean = false
//        try {
//            val response = signUpService.signUp(user).awaitResponse()
//            Log.e("signUp repo", "response ${response.code()} ${response.body()}")
//            if (response.code() == 200) {
//                //login success
//                issuccess = true
//                Log.e("signUp repo", "successful")
//            } else {
//                Log.e("sync repo", response.message())
//            }
//        } catch (httpException: HttpException) {
//            Log.e("sync repo", "failed $httpException")
//        }
//
//        return issuccess
//    }

}
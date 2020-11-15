package com.example.pillreminder.network.apiservice

import com.example.pillreminder.models.User
import com.example.pillreminder.network.request.RegisterRequest
import com.example.pillreminder.network.response.RegisterResponse
import retrofit2.Call
import retrofit2.http.*

interface SignUpService {


    @POST("register")
    fun signUp(@Body signUpRequest: RegisterRequest):Call<String>

//    @POST("register")
//    suspend fun signUp(@Body user: User):Call<Void>

}
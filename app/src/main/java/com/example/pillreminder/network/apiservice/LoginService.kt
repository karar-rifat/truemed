package com.example.pillreminder.network.apiservice

import com.example.pillreminder.network.request.LoginRequest
import com.example.pillreminder.network.response.LoginResponse
import retrofit2.Call
import retrofit2.http.*

interface LoginService {

    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>
}
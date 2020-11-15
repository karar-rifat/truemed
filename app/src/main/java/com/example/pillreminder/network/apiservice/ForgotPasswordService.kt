package com.example.pillreminder.network.apiservice

import com.example.pillreminder.network.request.ForgotPasswordRequest
import com.example.pillreminder.network.request.ResetPasswordRequest
import retrofit2.Call
import retrofit2.http.*

interface ForgotPasswordService {

    @POST("forgot-password")
    fun forgotPassword(@Body forgotPasswordRequest: ForgotPasswordRequest): Call<String>

    @POST("reset-password")
    fun resetPassword(@Body resetPasswordRequest: ResetPasswordRequest): Call<String>
}
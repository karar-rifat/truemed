package com.example.pillreminder.network.apiservice

import com.example.pillreminder.network.request.FeedBackRequest
import com.example.pillreminder.network.request.RegisterRequest
import com.example.pillreminder.network.response.UserResponse
import retrofit2.Call
import retrofit2.http.*

interface UserService {

    @GET("profile")
     fun getUserDetails(@Header("Authorization") token: String,@Query("profileId") profileId:String): Call<UserResponse>

    @PUT("profile")
    fun updateUser(@Header("Authorization") token: String,@Body registerRequest: RegisterRequest): Call<String>

    @POST("send-feedback")
     fun sendFeedBack(@Header("Authorization") token: String,@Body feedBackRequest: FeedBackRequest): Call<String>

}
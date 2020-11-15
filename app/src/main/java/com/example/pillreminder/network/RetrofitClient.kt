package com.example.pillreminder.network

import com.google.gson.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitClient {

    private var retrofit: Retrofit? = null

    //the base URL for our API
    //url for localhost
    //find the ip using ipconfig command
//    val BASE_URL = "http://172.18.0.1:8081/"
    //url for live server
    val BASE_URL = "http://13.212.61.110:8081/"

    fun getRetrofitInstance(): Retrofit? {
        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

        val gson=GsonBuilder()
            .setLenient()
            .setDateFormat("MMM dd, yyyy")
            .create()

        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL).client(client)
                .addConverterFactory(ScalarsConverterFactory.create()) //for receiving string response
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
        return retrofit
    }
}
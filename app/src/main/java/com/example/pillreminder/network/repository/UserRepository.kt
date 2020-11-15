package com.example.pillreminder.network.repository

import android.util.Log
import androidx.room.Room
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.network.RetrofitClient
import com.example.pillreminder.network.apiservice.UserService
import com.example.pillreminder.network.request.FeedBackRequest
import com.example.pillreminder.network.request.RegisterRequest
import com.example.pillreminder.network.response.UserResponse
import com.facebook.accountkit.internal.AccountKitController
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.awaitResponse
import java.io.EOFException

class UserRepository {

    private val retrofit= RetrofitClient.getRetrofitInstance()
    private val userService: UserService =retrofit!!.create(UserService::class.java)
    val database = Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java, "pill_reminder").fallbackToDestructiveMigration().build()

    suspend fun sendFeedBack(token:String, feedBackRequest: FeedBackRequest): String? {
        Log.e("sync repo","entered send feedback: ${feedBackRequest.subject}")
        val pushResponse = userService.sendFeedBack("Bearer $token",feedBackRequest).awaitResponse()

        Log.e("feedback repo", "code: ${pushResponse.code()} \nmessage: ${pushResponse.message()}")
        if (pushResponse.code() == 200 && !pushResponse.body().isNullOrEmpty()) {
            Log.e("feedback repo", pushResponse.body()!!)
        }
        else{
            Log.e("feedback repo", pushResponse.message())
        }
        return pushResponse.body()

    }

    suspend fun getUserDetails(token:String,email: String,saveUserInDb:(user:UserResponse)->Unit): UserResponse? {
        Log.e("sync repo","entered getUserDetails $token $email")
        var user:UserResponse?=null
        try{
        val useresponse = userService.getUserDetails("Bearer $token",email).awaitResponse()
        if (useresponse.code() == 200 && useresponse.body()!=null) {
            user=useresponse.body()
//            Helper.user=useresponse.body()
            saveUserInDb(user!!)
            Log.e("sync repo", useresponse.body().toString())
        }
        else{
            Log.e("sync repo", useresponse.message())
        }
        } catch (httpException: HttpException) {
            Log.e("sync repo", "failed $httpException")
        }catch (runtimeException: RuntimeException) {
            Log.e("sync repo", "failed $runtimeException")
        }

        return user

    }

    suspend fun updateUser(token:String,request: RegisterRequest): String? {
        Log.e("sync repo", "entered updateUser")
        var updateResponse:Response<String>?=null
        var result:String="Something wrong"
        try {
            updateResponse = userService.updateUser("Bearer $token", request).awaitResponse()

            if (updateResponse.code() == 200 && !updateResponse.body().isNullOrEmpty()) {
                Log.e("sync repo", updateResponse.body()!!)
                result="Password changed!"
            } else {
                Log.e("sync repo", updateResponse.message())
            }

        } catch (httpException: HttpException) {
            Log.e("sync repo", "failed $httpException")
        } catch (exception: EOFException) {
        Log.e("sync repo", "failed $exception")
    }
        catch (runtimeException: RuntimeException) {
            Log.e("sync repo", "failed $runtimeException")
        }
        return result
    }
}
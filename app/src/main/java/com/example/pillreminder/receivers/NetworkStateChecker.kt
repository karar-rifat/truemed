package com.example.pillreminder.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log
import com.example.pillreminder.helper.AlarmHelper
import com.example.pillreminder.helper.Helper
import com.example.pillreminder.helper.SessionManager
import com.example.pillreminder.service.DataPushService


class NetworkStateChecker : BroadcastReceiver() {
    //context and database helper object
    private var context: Context? = null

    override fun onReceive(context: Context, intent: Intent?) {
        Log.e("network state change","onReceive entered")

        var type = intent?.getStringExtra("type")
        var from = intent?.getStringExtra("from")
        if (type==null)
            type="all"

        this.context = context
        if (Helper.isConnected(context)) {
            Log.e("network state change","connected")
            //if connected to wifi or mobile data plan
            val token=SessionManager(context).fetchAuthToken()
            if (!token.isNullOrEmpty()){
                val service = Intent(context, DataPushService::class.java)
                service.putExtra("type", type)
                service.putExtra("token", token)
                context.startService(service)
            }

        }

        if (!Helper.isConnected(context)&&from=="alarm") {
            Log.e("network state change","not connected")
            val token=SessionManager(context).fetchAuthToken()
            if (!token.isNullOrEmpty()){
                AlarmHelper().startAlarmForSync(context,type)
            }

        }


    }

    /*
    * method taking two arguments
    * name that is to be saved and id of the name from SQLite
    * if the name is successfully sent
    * we will update the status as synced in SQLite
    * */
    private fun saveName(id: Int, name: String) {

    }
}
package com.example.pillreminder.helper

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.util.Log
import android.view.View
import com.example.pillreminder.models.*
import com.example.pillreminder.network.response.MedicineResponse
import com.example.pillreminder.service.ForegroundService
import com.example.pillreminder.service.ServiceState
import com.example.pillreminder.service.getServiceState


object Helper {
    var user_id:String?=null

    var isDataFetched:Boolean=false

    var pillAlarms:List<PillAlarms>?=null
    var appointmentVaccineAlarms:List<AppointmentVaccineAlarms>?=null
    var appointment: Appointment? =null
    var vaccine:Vaccine? = null
    var isAppAlive:Boolean=false
    var token:String?=null

    var medicinesList=ArrayList<MedicineResponse>()
    var informationList=ArrayList<Information>()

    var user: User? =null
    var pillList=ArrayList<Pill>()
    var appointmentList=ArrayList<Appointment>()
    var vaccineList=ArrayList<Vaccine>()
    var timePivotList=ArrayList<TimePivot>()

    var isAlertShowing:Boolean=false

    fun isAppRunning(context: Context, packageName: String): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val procInfos = activityManager.runningAppProcesses
        if (procInfos != null) {
            for (processInfo in procInfos) {
                if (processInfo.processName == packageName) {
                    return true
                }
            }
        }
        return false
    }

    fun isConnected(context: Context):Boolean{
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        val isConnected=activeNetwork?.isConnected
        Log.e("network state","is connected $isConnected")
        //if there is a network
        if (activeNetwork != null&&isConnected!!) {
            return true
        }

        return false
    }

    fun toggleUpDownWithAnimation(view: View): Boolean {
        if (view.rotation == 0f) {
            view.animate().setDuration(150).rotation(180f)
            return true
        } else {
            view.animate().setDuration(150).rotation(0f)
            return false
        }
    }

     fun actionOnService(context: Context ,action: Helper.Actions) {
        if (getServiceState(context) == ServiceState.STOPPED && action == Helper.Actions.STOP) return
        Intent(context, ForegroundService::class.java).also {
            it.action = action.name
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.e("Alarm receiver","Starting the service in >=26 Mode")
                context.startForegroundService(it)
                return
            }
            Log.e("Alarm receiver","Starting the service in < 26 Mode")
            context.startService(it)
        }
    }

    enum class Actions {
        START,
        STOP
    }
}
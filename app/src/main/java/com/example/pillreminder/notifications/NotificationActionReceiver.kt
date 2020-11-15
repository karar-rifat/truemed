package com.example.pillreminder.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.room.Room
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.helper.AlarmHelper
import com.example.pillreminder.helper.Helper
import com.example.pillreminder.helper.SharedPrefHelper
import com.example.pillreminder.models.*
import com.example.pillreminder.receivers.AlarmReceiver
import com.example.pillreminder.service.DataPushService
import com.facebook.accountkit.internal.AccountKitController
import java.text.SimpleDateFormat
import java.util.*

class NotificationActionReceiver: BroadcastReceiver() {
    var actionTaken: Boolean = false
    var user: User?=null
    var user_id:String?=null
    var type: String? = null

    var pillAlarms: List<PillAlarms>? = null
    var appointmentVaccineAlarms: List<AppointmentVaccineAlarms>? = null
    var appointment: Appointment? = null
    var vaccine: Vaccine? = null
    var snoozeAlarm: Boolean? = null
    var date: String? = null
    var currentTime: String? = null
    var snoozeSettings: Settings? = null

    //get current datetime
    val simpleDateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.ENGLISH)
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
    val currentDateTime = simpleDateFormat.format(Date()).toString()

    override fun onReceive(p0: Context?, p1: Intent?) {
        TODO("Not yet implemented")
    }


    fun actionSkip(){
            actionTaken = true

        val sdf = SimpleDateFormat("hh:mm a")
        currentTime = sdf.format(Date())
        date = dateFormat.format(Date())
        Log.e("IsolatedActivity", "date: $date")



            val database = Room.databaseBuilder(
                AccountKitController.getApplicationContext(),
                AppDb::class.java,
                "pill_reminder"
            ).fallbackToDestructiveMigration().build()
            Log.e("IsolatedActivity", "action skipped")
            Thread {
                user_id = SharedPrefHelper(AccountKitController.getApplicationContext()).getInstance().getUserId()



                if (type == "pill") {
                    pillAlarms?.forEach {

                        if (snoozeAlarm!!) {
                            cancelAlarm((it.task_id!! + 1000).toLong())
                        }

                        val report = Report(
                            user_id!!,
                            it.title!!,
                            it.type!!,
                            dateFormat.parse(date),
                            currentTime!!,
                            it.time!!,
                            "Skipped",
                            currentDateTime,
                            false
                        )
                        database.reportDao().saveReport(report)
                        database.pillDao().updateLastPillStatus(it.title!!, "Missed")
                        Log.e("IsolatedActivity", "action skipped for ${it.title}")

                        //set reminder for low stock
                        val pill = database.pillDao().getPillDetails(it.title!!)
                        if (it.stock_reminder!! && (pill.stock!! <= it.lowest_stock!!)) {
                            startStockReminder(AccountKitController.getApplicationContext(), it.title!!)
                        }
                    }
                } else {

                    appointmentVaccineAlarms?.forEach {

                        if (snoozeAlarm!!) {
                            cancelAlarm((it.task_id!! + 1000).toLong())
                        }

                        val report = Report(
                            it.user_id!!,
                            it.title!!,
                            it.type!!,
                            dateFormat.parse(date),
                            currentTime!!,
                            it.time!!,
                            "Skipped",
                            currentDateTime, false
                        )
                        database.reportDao().saveReport(report)
                        Log.e("IsolatedActivity", "action skipped for ${it.title}")
                    }
                }

                Helper.isAlertShowing = false

                updateReportInService()

                database.close()
            }.start()
//                Toast.makeText(this, "Table Created Successfully", Toast.LENGTH_LONG).show();
    }


    fun cancelAlarm(task_id: Long) {
        val alarmManager: AlarmManager = AccountKitController.getApplicationContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(AccountKitController.getApplicationContext(), AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(AccountKitController.getApplicationContext(), task_id.toInt() + 1000, intent, 0)
        alarmManager.cancel(pendingIntent)
        Log.e("isolated cancel alarm", "cancelAlarm: task_id $task_id")
    }

    fun startAlarm(
        context: Context,
        id: Int,
        reminderType: String,
        title: String,
        time: String,
        task_id: Long
    ) {

        Thread {
            user_id = SharedPrefHelper(AccountKitController.getApplicationContext()).getInstance().getUserId()
            val database = Room.databaseBuilder(
                AccountKitController.getApplicationContext(),
                AppDb::class.java,
                "pill_reminder"
            ).fallbackToDestructiveMigration().build()
            snoozeSettings = database.settingsDao().getSpecificSettings("snooze_time")
            user=database.userDao().getUserByEmail(user_id!!)
            Log.e("Isolated", "init: $user_id ${user!!.name}")

            database.close()
        }.start()

        Log.e("isolated startAlarm", "snooze status= ${snoozeSettings?.status}")

        val c = Calendar.getInstance()
        Log.e("isolated startAlarm", c.time.toString())
        Log.e("isolated startAlarm", "$type $time snoozeAlarm $snoozeAlarm")
        val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("id", id)
        intent.putExtra("type", reminderType)
        intent.putExtra("title", title)
        intent.putExtra("time", time)
        intent.putExtra("snoozeAlarm", true)
        val pendingIntent = PendingIntent.getBroadcast(context, task_id.toInt() + 1000, intent, 0)

//            alarmManager.setRepeating(
//                AlarmManager.RTC_WAKEUP, c.timeInMillis,
//                snoozeSettings!!.description!!.toLong()*60*1000, pendingIntent
//            )

        if (Build.VERSION.SDK_INT >= 23) {
            Log.e("isolated setAlarm", "set for sdk >=23")
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                c.timeInMillis + (2 * 60 * 1000),
                pendingIntent
            );
        } else if (Build.VERSION.SDK_INT >= 19) {
            Log.e("isolated setAlarm", "set for sdk >=19")
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                c.timeInMillis + (snoozeSettings!!.description!!.toLong() * 60 * 1000),
                pendingIntent
            );
        } else {
            Log.e("isolated setAlarm", "set for sdk <=23")
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                c.timeInMillis + (snoozeSettings!!.description!!.toLong() * 60 * 1000),
                pendingIntent
            );
        }
    }

    fun updateReportInService(){
        //update data to server with background service
        if (Helper.isConnected(AccountKitController.getApplicationContext())) {
            val token=Helper.token

            Log.e("report add ", "starting push service report")
            val service = Intent(AccountKitController.getApplicationContext(), DataPushService::class.java)
            service.putExtra("type", "report")
            service.putExtra("token", token)
            AccountKitController.getApplicationContext().startService(service)
        }else{
            AlarmHelper().startAlarmForSync(AccountKitController.getApplicationContext(),"report")
            Log.e("report add ", "alarm for sync report")
        }
    }

    fun startStockReminder(
        context: Context,
        title: String
    ) {

        val c = Calendar.getInstance()
        Log.e("isolated StockReminder", title.toString())
        val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("title", title)
        intent.putExtra("type", "stock")
        val pendingIntent = PendingIntent.getBroadcast(context, Math.random().toInt(), intent, 0)

        if (Build.VERSION.SDK_INT >= 23) {
            Log.e("isolated setAlarm", "set for sdk >=23")
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                c.timeInMillis + (2 * 60 * 1000),
                pendingIntent
            );
        } else if (Build.VERSION.SDK_INT >= 19) {
            Log.e("isolated setAlarm", "set for sdk >=19")
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                c.timeInMillis + (2 * 60 * 1000),
                pendingIntent
            );
        } else {
            Log.e("isolated setAlarm", "set for sdk <=23")
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                c.timeInMillis + (2 * 60 * 1000),
                pendingIntent
            );
        }
    }

}
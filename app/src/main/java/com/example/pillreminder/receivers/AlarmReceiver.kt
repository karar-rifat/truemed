package com.example.pillreminder.receivers


import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.legacy.content.WakefulBroadcastReceiver
import androidx.room.Room
import com.example.pillreminder.activities.IsolatedActivity
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.helper.AlarmHelper
import com.example.pillreminder.helper.Helper
import com.example.pillreminder.helper.Helper.actionOnService
import com.example.pillreminder.helper.Ringtone
import com.example.pillreminder.models.AppointmentVaccineAlarms
import com.example.pillreminder.models.PillAlarms
import com.example.pillreminder.notifications.AlarmNotificationHelper
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AlarmReceiver : WakefulBroadcastReceiver() {
    //    lateinit var r: Ringtone
//    lateinit var alarm : Settings
    var pillAlarms: List<PillAlarms>? = null
    var appointmentVaccineAlarms: ArrayList<AppointmentVaccineAlarms>? = ArrayList<AppointmentVaccineAlarms>()
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
    var context:Context?=null
    private var screenWakeLock: PowerManager.WakeLock? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        System.out.println("============= context:entered alarm =>")
        System.out.println(context)
        this.context=context
        var alarmFlag = true
        val id = intent?.getIntExtra("id", 0)
        val type = intent?.getStringExtra("type")
        val title = intent?.getStringExtra("title")
        val time = intent?.getStringExtra("time")
        val snoozeAlarm = intent?.getBooleanExtra("snoozeAlarm", false)
        Log.e(TAG, "onReceive: alarm data: $id $type $title $time snoozeAlarm $snoozeAlarm")
        Log.e(TAG, "onReceive: alarm isAppAlive: ${Helper.isAppAlive}")

        //start foreground service
//        actionOnService(context!!,Helper.Actions.START)

        if (screenWakeLock == null)
        {
            val pm:PowerManager = context!!.getSystemService(Context.POWER_SERVICE) as PowerManager
            screenWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, "ScreenLock:AlarmListener")
            screenWakeLock!!.acquire();
        }

        if (screenWakeLock != null)
            screenWakeLock!!.release();


        if (intent?.action == "android.intent.action.BOOT_COMPLETED") {
            alarmFlag = false
            Log.e(TAG, "onReceive: reset alarm on restart")
            ////// reset your alarms here
            AlarmHelper().setExistingAlarms(context!!)
        }

        val database = Room.databaseBuilder(
            context!!,
            AppDb::class.java,
            "pill_reminder"
        ).fallbackToDestructiveMigration().build()

//        check all pill alarms at this time
//        if (type=="pill") {
//            AlarmHelper().mergeAlarm(time!!)
//            pillAlarms = Helper.pillAlarms
//            Log.e(TAG, "mergeAlarm: ${pillAlarms!!.size} $time")
//        }

//        var alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
//        r = RingtoneManager.getRingtone(context, alert)
//
//        if (r == null) {
//
//            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//            r = RingtoneManager.getRingtone(context, alert)
//
//            if (r == null) {
//                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
//                r = RingtoneManager.getRingtone(context, alert)
//            }
//        }
//
//        r?.play()
        val service = Intent(context, AlarmNotificationHelper::class.java)
//        val service = Intent(context, ForegroundService::class.java)
        service.putExtra("id", id)
        service.putExtra("type", type)
        service.putExtra("title", title)
        service.putExtra("time", time)
        service.data = Uri.parse("custom://" + System.currentTimeMillis())
//        Log.i("AlarmReceiver",intent?.extras?.getString("title"))
//        Log.i("AlarmReceiver2",intent?.getStringExtra("title"))
        System.out.println(context)
        val flag = false
        val thread1 = Thread {

            val alarm = database.settingsDao().getSpecificSettings("alarm")

            //        check all pill alarms at this time
            val today=dateFormat.parse(dateFormat.format(Date()))
            if (type == "pill") {
                pillAlarms = database.timePivotDao().getPillDetails(title!!,time!!, "pill", true)
                Helper.pillAlarms = pillAlarms
                Log.e(TAG, "mergeAlarm: ${pillAlarms!!.size} $time")

                //check end date and cancel alarm if expired
                pillAlarms?.forEach {
                    val startDate=dateFormat.parse(it.start_from)
                    val calendar = Calendar.getInstance()
                    calendar.time = startDate
                    calendar.add(Calendar.DATE,it.no_of_day!!)
                    val endDate=dateFormat.parse(dateFormat.format(calendar.time))

                    if (endDate < today){
                        AlarmHelper().cancelAlarm(it.task_id!!)
                        alarmFlag=false
                    }
                }
            }

            if (type == "pill" && flag) {
//                Thread.sleep(2000)
                val pill = database.pillDao().getPill(id!!)
                if (pill.stock!! > 0) {
                    System.out.println("============= context2: =>")
                    System.out.println(context)
                    System.out.println(ALARM_SERVICE.javaClass)

                    val alarmManager =
                        context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val alarmIntent = Intent(context, AlarmReceiver::class.java)
                    val parts = time!!.split(":")
                    val c = Calendar.getInstance()
                    val task_id = System.currentTimeMillis().toInt()

                    c.set(Calendar.HOUR_OF_DAY, parts[0].toInt())
                    c.set(Calendar.MINUTE, parts[1].toInt())
                    c.set(Calendar.SECOND, 0)

                    Log.e("hrs&mins", parts[0] + "  " + parts[1])

                    alarmIntent.putExtra("id", id)
                    alarmIntent.putExtra("type", type)
                    alarmIntent.putExtra("title", title)
                    alarmIntent.putExtra("time", time)
                    val pendingIntent = PendingIntent.getBroadcast(context, task_id, alarmIntent, 0)
                    if (Build.VERSION.SDK_INT >= 23) {
//                        alarmManager?.setExactAndAllowWhileIdle(AlarmManager.RTC,System.currentTimeMillis()+60000, pendingIntent);
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            c.timeInMillis, pendingIntent
                        );
                    } else if (Build.VERSION.SDK_INT >= 19) {
                        alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            c.timeInMillis,
                            pendingIntent
                        );
                    } else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent);
                    }
                } else {
                    pill.reminder = true
                    database.pillDao().update(pill)
                }

            }

            //check if reminder is on or off for specific alarm
            Log.e("alarm", "onReceive: checking reminder for alarm")
            //check if there is any alarm for pill at this time
            if (type?.toLowerCase(Locale.ROOT) == "pill") {
                Log.e("alarm pill", "onReceive: pill size ${pillAlarms?.size} type $type")
                if (pillAlarms.isNullOrEmpty()) {
                    alarmFlag = false
                }else{
                    AlarmHelper().prepareNextPillAlarm(context,pillAlarms)
                }

                Log.e("alarm", "onReceive: checking reminder for pill $alarmFlag")
            }

            if (type?.toLowerCase(Locale.ROOT) == "appointment") {
//                val appointment = database.appointmentDao().getAppointment(id!!)
                val appointments = database.appointmentDao().getActiveAppointment(time!!, "appointment", true)
                if (appointments.isNotEmpty())
                    appointmentVaccineAlarms!!.addAll(appointments)

                val vaccines = database.vaccineDao().getActiveVaccine(time, "vaccine", true)
                if (vaccines.isNotEmpty())
                    appointmentVaccineAlarms!!.addAll(vaccines)

                Helper.appointmentVaccineAlarms = appointmentVaccineAlarms

                if (appointmentVaccineAlarms == null || appointmentVaccineAlarms!!.isEmpty())
                    alarmFlag = false

                Log.e("alarm", "onReceive: checking reminder for appointment $alarmFlag")
            }
            if (type?.toLowerCase(Locale.ROOT) == "vaccine") {
                val appointments = database.appointmentDao().getActiveAppointment(time!!, "appointment", true)
                if (appointments.isNotEmpty())
                    appointmentVaccineAlarms!!.addAll(appointments)

                val vaccines = database.vaccineDao().getActiveVaccine(time, "vaccine", true)
                Log.e("alarm", "onReceive: checking reminder for vaccine ${vaccines.size}")
                if (vaccines.isNotEmpty())
                    appointmentVaccineAlarms!!.addAll(vaccines)

                Helper.appointmentVaccineAlarms = appointmentVaccineAlarms

                if (appointmentVaccineAlarms == null || appointmentVaccineAlarms!!.isEmpty())
                    alarmFlag = false

                Log.e("alarm", "onReceive: checking reminder for vaccine $alarmFlag")
            }
            database.close()

//            System.out.println("============>")
//            System.out.println(alarm)
            if (alarm.status == true && alarmFlag && !Helper.isAlertShowing) {

                Ringtone.runRingtone(context!!)
                if (!Ringtone.isPlaying())
                    Ringtone.play()

//                //start foreground service
//                actionOnService(Helper.Actions.START)

                Log.e("alarm", "onReceive: checking is app running")
//                if (Helper.isAppRunning(context, "com.example.pillreminder")) {
                if (Helper.isAppAlive || type != "stock") {
                    Log.e("alarm", "onReceive: app is running ${Helper.isAppAlive}")
                    val notifyIntent = Intent(context, IsolatedActivity::class.java)
//                        val notifyIntent = Intent(context, AlarmActivity::class.java)
                    notifyIntent.putExtra("id", id)
                    notifyIntent.putExtra("type", type)
                    notifyIntent.putExtra("title", title)
                    notifyIntent.putExtra("time", time)
                    notifyIntent.putExtra("direct", true)
                    notifyIntent.putExtra("snoozeAlarm", snoozeAlarm)
                    notifyIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                    Log.e(TAG, "onReceive: IsolatedActivity data: $id $type $title $time snoozeAlarm $snoozeAlarm")

//                        context.startActivity(notifyIntent)

                    context.startService(service)

                } else {
                    Log.e("alarm", "onReceive: app is not running ${Helper.isAppAlive}")
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//                            context.startForegroundService(service)
//                        else
//                            context.startService(service)

                    context.startService(service)
                }
            }
            System.out.println("============== context 3 =>")
            System.out.println(context)
        }
        thread1.start()
        thread1.join()
//        val t = Thread(Runnable {
//            AlarmReceiver = database.settingsDao().getSpecificSettings("alarm")
//        })
//
//        t.start() // spawn thread
//
//        t.join()  // wait for thread to finish
//        System.out.println(alarm)
//        if(alarm!=null && alarm!!.status==true){
//            context?.startService(service)
//        }
//        val notificationHelper = AlarmNotificationHelper(context!!)
//        val nb = notificationHelper.channelNotification
//        notificationHelper.manager.notify(1, nb.build())
    }

}
//import android.app.Activity;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.media.Ringtone;
//import android.media.RingtoneManager;
//import android.net.Uri;
//import android.support.v4.content.WakefulBroadcastReceiver;
//import com.example.pillreminder.activities.DashboardActivity
//import com.example.pillreminder.notifications.AlarmService
//
//
//class AlarmReceiver : WakefulBroadcastReceiver() {
//
//    override fun onReceive(context: Context, intent: Intent) {
//        //this will update the UI with message
//        val inst = DashboardActivity.instance()
//        inst.setAlarmText("Alarm! Wake up! Wake up!")
//
//        //this will sound the alarm tone
//        //this will sound the alarm once, if you wish to
//        //raise alarm in loop continuously then use MediaPlayer and setLooping(true)
//        var alarmUri: Uri? = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
//        if (alarmUri == null) {
//            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//        }
//        val ringtone = RingtoneManager.getRingtone(context, alarmUri)
//        ringtone.play()
//
//        //this will send a notification message
//        val comp = ComponentName(
//            context.packageName,
//            AlarmService::class.java.name
//        )
//        WakefulBroadcastReceiver.startWakefulService(context, intent.setComponent(comp))
//        resultCode = Activity.RESULT_OK
//    }
//}


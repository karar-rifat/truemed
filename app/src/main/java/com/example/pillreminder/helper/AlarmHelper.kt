package com.example.pillreminder.helper

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.room.Room
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.models.AlertItem
import com.example.pillreminder.models.PillAlarms
import com.example.pillreminder.models.TimePivot
import com.example.pillreminder.receivers.AlarmReceiver
import com.example.pillreminder.receivers.NetworkStateChecker
import com.facebook.accountkit.internal.AccountKitController
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AlarmHelper :Application(){

    var timePivot:List<TimePivot>?=null
    val database = Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java, "pill_reminder").fallbackToDestructiveMigration().build()
    lateinit var context:Context
    fun prepareNextPillAlarm(context:Context,pillAlarms: List<PillAlarms>?) {
        this.context=context
        pillAlarms?.forEach {
            Log.e("Alarm helper:", "next pill alarm ${it.title!!}")

            val cal = Calendar.getInstance()

            Log.e("next alarm:", "condition 1")
            cal.add(Calendar.DATE, 1)
            Log.e("next alarm:", "increased date ${cal.time}")

            setNextPillAlarm(
                cal,
                it.local_id!!,
                it.frequency!!,
                it.title!!,
                it.time!!,
                it.task_id!!.toLong()
            )
        }
    }

    fun setNextPillAlarm(
        c: Calendar,
        id: Int,
        frequency: Int,
        title: String,
        time: String,
        task_id: Long
    ) {

        Log.e("start next pill Alarm", c.time.toString())
        val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("id", id)
        intent.putExtra("type", "pill")
        intent.putExtra("title", title)
        intent.putExtra("time", time)
        val pendingIntent = PendingIntent.getBroadcast(context, task_id.toInt(), intent, 0)

//        if (c.before(Calendar.getInstance())) {
//            c.add(Calendar.DATE, 1)
//        }

//            if (isDaily) {
//                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.timeInMillis,AlarmManager.INTERVAL_DAY, pendingIntent);
//            }
//            else{
//                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.timeInMillis,AlarmManager.INTERVAL_DAY*(frequency+1), pendingIntent);
//            }

        if (frequency == 1) {
            Log.e("start next pill Alarm", "frequency=1")
            if (Build.VERSION.SDK_INT >= 23) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    c.timeInMillis, pendingIntent
                )
            } else if (Build.VERSION.SDK_INT >= 19) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent);
            }
        } else {
            Log.e("start next pill Alarm", "frequency>1")
            if (c.before(Calendar.getInstance())) {
                c.add(Calendar.DATE, frequency)
            }
            Log.e("next Alarm +frequency:", c.time.toString())
            if (Build.VERSION.SDK_INT >= 23) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    c.timeInMillis, pendingIntent
                );
            } else if (Build.VERSION.SDK_INT >= 19) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent);
            }
            c.add(Calendar.DATE, -(frequency))
        }
    }

    fun setExistingAlarms(context: Context) {
        val cal = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("MMM dd,yyyy  HH:mm")
        val currentDateTime: Date = simpleDateFormat.parse(simpleDateFormat.format(Date()))

        val userId=SharedPrefHelper(AccountKitController.getApplicationContext()).getInstance().getUserId()

        Thread {
            Log.e(TAG, "rest alarms: entered setExistingAlarms")
           /* //working with only pill table
            val pillAlarms=database.pillDao().pills(true)
            pillAlarms.forEach {
                if (it.morning!!.isNotEmpty()&&it.morning!=null) {
                    Log.e("AlarmHelper", "type pill time ${it.morning} task ${it.task_id_morning}")
                    val temp = it.morning!!.split(":")
                    cal.set(Calendar.HOUR_OF_DAY, temp[0].toInt())
                    cal.set(Calendar.MINUTE, temp[1].toInt())
                    cal.set(Calendar.SECOND, 0)

                    //compare selected date with current date
                    val selectedDateTime: Date =
                        simpleDateFormat.parse(simpleDateFormat.format(cal.time))
                    if (selectedDateTime.compareTo(currentDateTime) < 0) {
                        cal.add(Calendar.DATE, 1)
                        startAlarm(
                            context,
                            cal,
                            "repeating",
                            it.id,
                            "Pill",
                            it.title!!,
                            it.morning!!,
                            it.task_id_morning!!
                        )
                        val alarmTime: Date =
                            simpleDateFormat.parse(simpleDateFormat.format(cal.time))
                        Log.e("${it.morning} alarm set", "$alarmTime $currentDateTime")
                        cal.add(Calendar.DATE, -1)
                    } else {
                        startAlarm(
                            context,
                            cal,
                            "repeating",
                            it.id,
                            "Pill",
                            it.title!!,
                            it.morning!!,
                            it.task_id_morning!!
                        )
                        val alarmTime: Date =
                            simpleDateFormat.parse(simpleDateFormat.format(cal.time))
                        Log.e("${it.morning} alarm set", "$alarmTime $currentDateTime")
                    }
                }
            }
*/
            //working pivot table
            timePivot = database.timePivotDao().times(userId!!)
            Log.e("entered AlarmHelper", "${timePivot!!.size} $currentDateTime")
            timePivot!!.forEach {
                cal.setTime(currentDateTime)
                if (it.type == "pill") {
                    val pill=database.pillDao().getPillDetails(it.title!!,userId)
                    Log.e("AlarmHelper", "type ${it.type} time ${it.time} task ${it.taskId}")
                    val temp = it.time!!.split(":")
                    cal.set(Calendar.HOUR_OF_DAY, temp[0].toInt())
                    cal.set(Calendar.MINUTE, temp[1].toInt())
                    cal.set(Calendar.SECOND, 0)

                    if (pill.reminder!!) {
                        //compare selected date with current date
                        val selectedDateTime: Date = simpleDateFormat.parse(simpleDateFormat.format(cal.time))
                        if (selectedDateTime.compareTo(currentDateTime) < 0) {
                            cal.add(Calendar.DATE, 1)
                            startAlarm(
                                context,
                                cal,
                                "repeating",
                                it.idToMatch,
                                "pill",
                                pill.everyday!!,
                                pill.frequency!!,
                                it.title!!,
                                it.time!!,
                                it.taskId!!
                            )
                            val alarmTime: Date =
                                simpleDateFormat.parse(simpleDateFormat.format(cal.time))
                            Log.e("${it.time} alarm set", "$alarmTime $currentDateTime")
                            cal.add(Calendar.DATE, -1)
                        } else {
                            startAlarm(
                                context,
                                cal,
                                "repeating",
                                it.idToMatch,
                                "pill",
                                pill.everyday!!,
                                pill.frequency!!,
                                it.title!!,
                                it.time!!,
                                it.taskId!!
                            )
                            val alarmTime: Date =
                                simpleDateFormat.parse(simpleDateFormat.format(cal.time))
                            Log.e("${it.time} alarm set", "$alarmTime $currentDateTime")
                        }
                    }
                } else {
                    Log.e("AlarmHelper", "type ${it.type} time ${it.time} task ${it.taskId}")
                    val date: Date = simpleDateFormat.parse(it.time)
                    cal.setTime(date)

                    //check if the date is passed before starting alarm
                    if (date.compareTo(currentDateTime) > 0) {
                        startAlarm(
                            context,
                            cal!!,
                            "once",
                            it.idToMatch,
                            it.type!!,
                            true,
                            0,
                            it.title!!,
                            it.time!!,
                            it.taskId!!
                        )
                        val alarmTime: Date =
                            simpleDateFormat.parse(simpleDateFormat.format(cal.time))
                        Log.e("${it.time} alarm set V/A", "$alarmTime $currentDateTime")
                    }
                }

            }

            database.close()
        }.start()
    }

    //merge multiple alarm at same time
    fun mergeAlarm(time:String){
        var pillAlarms: List<PillAlarms>? = null
        Thread {
            pillAlarms = database.timePivotDao().getPillDetails("",time, "pill", true)
//            PillAlarmsImp().pillAlarms(pillAlarms!!)
            Helper.pillAlarms=pillAlarms
            Log.e(TAG, "mergeAlarm: ${pillAlarms!!.size} $time")
        }.start()
        database.close()
    }

    fun startAlarm(context: Context,c: Calendar, type: String, id: Int, reminderType: String,isDaily:Boolean,frequency:Int, title: String, time: String, task_id:Long) {

        Log.e("startAlam",c.time.toString())
        Log.e("startAlam", "$type $time")
        val alarmManager : AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("id", id)
        intent.putExtra("type", reminderType)
        intent.putExtra("title", title)
        intent.putExtra("time", time)
        val pendingIntent = PendingIntent.getBroadcast(context, task_id.toInt(), intent, 0)

        if(type == "repeating"){

//            if (isDaily) {
//                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.timeInMillis,AlarmManager.INTERVAL_DAY, pendingIntent);
//            }
//            else{
//                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.timeInMillis,AlarmManager.INTERVAL_DAY*(frequency+1), pendingIntent);
//            }

            if (Build.VERSION.SDK_INT >= 23) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    c.timeInMillis, pendingIntent);
            } else if (Build.VERSION.SDK_INT >= 19) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent);
            }
        }else{
            if (Build.VERSION.SDK_INT >= 23) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    c.timeInMillis, pendingIntent);
            } else if (Build.VERSION.SDK_INT >= 19) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent);
            }
        }
    }

    fun cancelAlarm(task_id: Int){
        val alarmManager : AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, task_id, intent, 0)
        alarmManager.cancel(pendingIntent)
        Log.e("cancel alarm", "cancelAlarm: task_id $task_id")
    }

    fun startAlarmForSync(context: Context,type:String){
        val cal = Calendar.getInstance()
        val task_id = System.currentTimeMillis().toInt()
        val alarmManager : AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NetworkStateChecker::class.java)
        intent.putExtra("type", type)
        intent.putExtra("from", "alarm")
        val pendingIntent = PendingIntent.getBroadcast(context, task_id, intent, 0)
        Log.e("startAlarmForSync", type)

        if (Build.VERSION.SDK_INT >= 23) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                cal.timeInMillis+(10*60*1000), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,cal.timeInMillis+(10*60*1000), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP,cal.timeInMillis+(10*60*1000), pendingIntent);
        }
    }
}
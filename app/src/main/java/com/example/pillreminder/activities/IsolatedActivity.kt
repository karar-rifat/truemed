package com.example.pillreminder.activities

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.pillreminder.helper.Ringtone
import com.example.pillreminder.R
import com.example.pillreminder.adaptors.AlertItemAdapter
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.helper.AlarmHelper
import com.example.pillreminder.helper.Helper
import com.example.pillreminder.helper.SharedPrefHelper
import com.example.pillreminder.models.*
import com.example.pillreminder.receivers.AlarmReceiver
import com.example.pillreminder.service.DataPushService
import com.facebook.accountkit.internal.AccountKitController
//import kotlinx.android.synthetic.main.activity_isolated.alertRecycler
//import kotlinx.android.synthetic.main.activity_isolated.btnSkip
//import kotlinx.android.synthetic.main.activity_isolated.btnTake
import kotlinx.android.synthetic.main.fragment_alert.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class IsolatedActivity : AppCompatActivity() {
    var actionTaken: Boolean = false
    var pillAlarms: List<PillAlarms>? = null
    var appointmentVaccineAlarms: ArrayList<AppointmentVaccineAlarms>? = ArrayList()
    var appointment: Appointment? = null
    var vaccine: Vaccine? = null
    lateinit var alertItemAdapter: AlertItemAdapter
    var alertItems: ArrayList<AlertItem> = ArrayList()
    var id: Int = 0
    var type: String? = null
    var date: String? = null
    var time: String? = null
    var currentTime: String? = null
    var title: String? = null
    var snoozeAlarm: Boolean? = null
    var snoozeSettings: Settings? = null

    var user: User? = null
    var user_id: String? = null

    //get current datetime
    val simpleDateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.ENGLISH)
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
    val currentDateTime = simpleDateFormat.format(Date()).toString()

    val database = Room.databaseBuilder(
        AccountKitController.getApplicationContext(),
        AppDb::class.java,
        "pill_reminder"
    ).fallbackToDestructiveMigration().build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_isolated2)

        Helper.isAlertShowing = true

        //      var receiver : AlarmReceiver? = AlarmReceiver()
//        receiver?.r?.stop()
//        AlarmNotificationHelper.r.stop()
        Log.e("IsolatedActivity", "entered")

        alertRecycler.setHasFixedSize(false)
        alertRecycler.layoutManager = LinearLayoutManager(this)

        val sdf = SimpleDateFormat("hh:mm a")
        currentTime = sdf.format(Date())
        date = dateFormat.format(Date())
        Log.e("IsolatedActivity", "date: $date")

        if (intent.extras != null) {
            title = intent.getStringExtra("title")
            time = intent.getStringExtra("time")
            type = intent.getStringExtra("type")
            id = intent.getIntExtra("id", 0)
            snoozeAlarm = intent.getBooleanExtra("snoozeAlarm", false)
            //time = "34:00"
//            textView92.text = date
            Log.e("Isolated", "onCreate: intent extra $id $title $time $date snoozeAlarm $snoozeAlarm")
        }

        Thread {
            user_id = SharedPrefHelper(this).getInstance().getUserId()

            snoozeSettings = database.settingsDao().getSpecificSettings("snooze_time")
            user = database.userDao().getUserByEmail(user_id!!)
            Log.e("Isolated", "init: $user_id ${user!!.name}")
            runOnUiThread {
                tvUser.text = user!!.name
            }
//            database.close()
        }.start()

        if (intent.getBooleanExtra("direct", false)) {
//            turnOffAlarm()
//            timer()
        }

        if (intent.getBooleanExtra("direct", false) || intent.getBooleanExtra(
                "notification",
                false
            )
        ) { //Just for confirmation

            if (type == "pill") {

//                Thread {
//                pillAlarms = Helper.pillAlarms
//                    pillAlarms = database.timePivotDao().getPillDetails(time!!, "pill", true)
//                }.start()

                pillAlarms = Helper.pillAlarms
                if (pillAlarms == null || pillAlarms!!.isEmpty()) {

                    Thread {
                        pillAlarms = database.timePivotDao().getPillDetails(title!!,date!!, "pill", true)
                        Helper.pillAlarms = pillAlarms
                        Log.e(ContentValues.TAG, "mergeAlarm isolated: ${pillAlarms!!.size} $date")
                    }.start()
                }
//                Log.e("isolated pill size", pillAlarms!!.size.toString())

                val cal = Calendar.getInstance()
                val simpleDateFormat = SimpleDateFormat("MMM dd,yyyy  HH:mm")
                val currentDateTime: Date = simpleDateFormat.parse(simpleDateFormat.format(Date()))

                //check if multiple alarm is set at this time
                if (pillAlarms != null) {
                    //multiple alarm is set at this time
                    var details: String? = null
                    pillAlarms!!.forEach {
                        Log.e("isolated pill", it.title!!)
                        val beforeOrAfter = if (it.before_or_after_meal == "before")
                            "Before Meal"
                        else
                            "After Meal"
                        details = "${it.dose} pill(s) $beforeOrAfter"

                        val alert = AlertItem(it.title!!, details!!, "pill")
                        alertItems.add(alert)

                        Log.e("isolated pill", alertItems.size.toString())

//                        Log.e("next alarm:", "condition 1")
//                        cal.add(Calendar.DATE, 1)
//                        Log.e("next alarm:", "increased date ${cal.time}
//                        setNextPillAlarm(
//                            cal,
//                            it.local_id!!,
//                            it.frequency!!,
//                            it.title!!,
//                            it.time!!,
//                            it.task_id!!.toLong()
//                        )
//                            cal.add(Calendar.DATE, -1)

                    }

                    Log.e("isolated pill", "entered if statement ${alertItems.size}")
//                    textView91.text = "It's time to take your medicines!"
                } else {
                    Log.e("isolated pill", "pill list empty entered")
//                    textView91.text = "It's time to take your medicine!"
                    val details = ""
                    val alert = AlertItem(title!!, details, "pill")
                    alertItems.add(alert)
                }

            } else if (type == "appointment" || type == "vaccine") {
                Log.e("isolated App || Vac", "entered")
//                textView91.text = "It's time for your appointment!"
//                appointment = Helper.appointment
                appointmentVaccineAlarms = Helper.appointmentVaccineAlarms as ArrayList<AppointmentVaccineAlarms>

//                val details = "${appointment?.place}, ${appointment?.mobile}"
//                val alert = AlertItem(appointment?.title!!, details, "appointment")
//                alertItems.add(alert)

//                Thread {
//                    appointmentVaccineAlarms?.clear()
//                    val appointments =
//                        database.appointmentDao().getActiveAppointment(time!!, "appointment", true)
//                    if (appointments.isNotEmpty())
//                        appointmentVaccineAlarms!!.addAll(appointments)
//
//                    val vaccines = database.vaccineDao().getActiveVaccine(time!!, "vaccine", true)
//                    if (vaccines.isNotEmpty())
//                        appointmentVaccineAlarms!!.addAll(vaccines)
//                }.start()

                //check if multiple alarm is set at this time
                if (appointmentVaccineAlarms != null) {
                    //multiple alarm is set at this time
                    var details: String? = null
                    appointmentVaccineAlarms!!.forEach {
                        Log.e("isolated App || Vac", it.title!!)
                        details = "${it.place}, ${it.mobile}"
                        val alert = AlertItem(it.title!!, details!!, it.type!!)
                        alertItems.add(alert)

                        Log.e("isolated App || Vac", alertItems.size.toString())
                    }

                    Log.e("isolated App || Vac", "entered if statement ${alertItems.size}")
//                    textView91.text = "It's time to take your medicines!"
                } else {
                    Log.e("isolated App || Vac", "App || Vac list empty entered")
//                    textView91.text = "It's time to take your medicine!"
                    val details = ""
                    val alert = AlertItem(title!!, details, type!!)
                    alertItems.add(alert)
                }
                btnTake.setText("Attend")

            }

            alertItemAdapter = AlertItemAdapter(this, alertItems)
            alertRecycler.adapter = alertItemAdapter

            Log.e("isolated activity", id.toString())
//            finish()
            btnSkip.setOnClickListener {
                actionTaken = true

                turnOffAlarm()
                val database = Room.databaseBuilder(
                    AccountKitController.getApplicationContext(),
                    AppDb::class.java,
                    "pill_reminder"
                ).fallbackToDestructiveMigration().build()
                Log.e("IsolatedActivity", "action skipped")
                Thread {
                    user_id = SharedPrefHelper(this).getInstance().getUserId()

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
                                startStockReminder(this, it.title!!)
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
                            Log.e("IsolatedActivity", "action skipped for $title")
                        }
                    }

                    Helper.isAlertShowing = false

                    updateReportInService()

                    runOnUiThread {
                        finish()
                    }
                    database.close()
                }.start()
//                Toast.makeText(this, "Table Created Successfully", Toast.LENGTH_LONG).show();
            }
            Log.e("Current Date and Time", currentTime!!)
            btnTake.setOnClickListener {
                actionTaken = true

                turnOffAlarm()
                val database = Room.databaseBuilder(
                    AccountKitController.getApplicationContext(),
                    AppDb::class.java,
                    "pill_reminder"
                ).fallbackToDestructiveMigration().build()

                Log.e("IsolatedActivity", "action taken")
                Thread {
                    user_id = SharedPrefHelper(this).getInstance().getUserId()

                    if (type == "pill") {
                        pillAlarms?.forEach {

                            if (snoozeAlarm!!) {
                                cancelAlarm((taskId + 1000).toLong())
                            }

                            val pill = database.pillDao().getPillDetails(it.title!!)
                            if (pill.stock!! > 0) {
                                pill.stock = pill.stock!! - 1
                                pill.synced = false
                                database.pillDao().update(pill)
                            }
                            val report = Report(
                                user_id!!,
                                it.title!!,
                                it.type!!,
                                dateFormat.parse(date),
                                currentTime!!,
                                it.time!!,
                                "Taken",
                                currentDateTime,
                                false
                            )
                            database.reportDao().saveReport(report)
                            database.pillDao().updateLastPillStatus(it.title!!, "Taken")
                            Log.e("IsolatedActivity", "action taken for ${it.title}")
                            Log.e(
                                "isolated stock",
                                it.stock.toString() + " " + it.lowest_stock + " " + it.stock_reminder
                            )

                            //set reminder for low stock
                            if (it.stock_reminder!! && (pill.stock!! <= it.lowest_stock!!)) {
                                startStockReminder(this, it.title!!)
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
                                "Taken",
                                currentDateTime, false
                            )
                            database.reportDao().saveReport(report)
                            Log.e("IsolatedActivity", "action taken for $title")
                        }

                    }

                    updateReportInService()

                    Helper.isAlertShowing = false

                    runOnUiThread {
                        finish()
                    }
                    database.close()
                }.start()
//                Toast.makeText(this, "Table Created Successfully", Toast.LENGTH_LONG).show();
            }
//            txtTitleView.text = intent.getStringExtra("title")
//            txtMsgView.text = intent.getStringExtra("message")
        }
        database.close()
    }

    fun timer() {
        Log.e("isolated timer", "entered $type")
        val secondsDelayed = 60
        Handler().postDelayed(Runnable {
            if (!actionTaken && snoozeSettings!!.status!! && snoozeSettings!!.description!!.toInt() > 0) {
                if (type == "pill") {
                    pillAlarms!!.forEach {
                        startAlarm(
                            this,
                            it.local_id!!,
                            it.type!!,
                            it.title!!,
                            it.time!!,
                            it.task_id!!.toLong()
                        )
                        Log.e("isolated timer pill", alertItems.size.toString())
                    }
                }
                if (type == "appointment" || type == "vaccine") {
                    appointmentVaccineAlarms?.forEach {
                        startAlarm(
                            this,
                            it.local_id!!,
                            it.type!!,
                            it.title!!,
                            it.time!!,
                            it.task_id!!
                        )
                        Log.e("isolated timer appoint.", alertItems.size.toString())
                    }
                }
            }
            Helper.isAlertShowing = false
            turnOffAlarm()

            if (!actionTaken)
                handleMissedAlarm()

            finish()
        }, ((secondsDelayed * 1000).toLong()))
    }

    override fun onPause() {
        super.onPause()
        Log.e("Isolated Activity", "onPause: actionTaken? $actionTaken")

        turnOffAlarm()

        if (intent.getBooleanExtra("notification", false) && !actionTaken) {
            handleMissedAlarm()
        }
    }

    fun handleMissedAlarm() {
        Log.e("Isolated Activity", "handleMissedAlarm: actionTaken? $actionTaken")
        val database = Room.databaseBuilder(
            AccountKitController.getApplicationContext(),
            AppDb::class.java,
            "pill_reminder"
        ).fallbackToDestructiveMigration().build()
        if (!actionTaken && !snoozeSettings!!.status!!) {

            Thread {
                val user_id = SharedPrefHelper(this).getInstance().getUserId()
                if (type == "pill") {
                    pillAlarms?.forEach {
                        val report = Report(
                            user_id!!,
                            it.title!!,
                            it.type!!,
                            dateFormat.parse(date),
                            currentTime!!,
                            it.time!!,
                            "Missed",
                            currentDateTime,
                            false
                        )
                        database.reportDao().saveReport(report)
                        database.pillDao().updateLastPillStatus(it.title!!, "Missed")
                        Log.e("IsolatedActivity", "action Missed for ${it.title}")

                        val pill = database.pillDao().getPillDetails(it.title!!)
                        //set reminder for low stock
                        if (it.stock_reminder!! && (pill.stock!! <= it.lowest_stock!!)) {
                            startStockReminder(this, it.title!!)
                        }
                    }
                } else {
                    appointmentVaccineAlarms?.forEach {
                        val report = Report(
                            user_id!!,
                            it.title!!,
                            it.type!!,
                            dateFormat.parse(date),
                            currentTime!!,
                            it.time!!,
                            "Missed",
                            currentDateTime,
                            false
                        )
                        database.reportDao().saveReport(report)
                        Log.e("IsolatedActivity", "action Missed for $title")
                    }
                }
                database.close()
                Helper.isAlertShowing = false
                actionTaken = true
                updateReportInService()
            }.start()
            Log.e("IsolatedActivity", "action Missed")
        }
    }

    private fun turnOffAlarm() {
        Ringtone.stopRingtone()
    }

    fun cancelAlarm(task_id: Long) {
        val alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, task_id.toInt() + 1000, intent, 0)
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

        Log.e("isolated startAlarm", "snooze status= ${snoozeSettings?.status}")

        val c = Calendar.getInstance()
        Log.e("isolated startAlarm", c.time.toString())
        Log.e("isolated startAlarm", "$type $time snoozeAlarm $snoozeAlarm")
        val alarmManager: AlarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
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

    fun setNextPillAlarm(
        c: Calendar,
        id: Int,
        frequency: Int,
        title: String,
        time: String,
        task_id: Long
    ) {

        Log.e("start next pill Alarm", c.time.toString())
        Log.e("start next pill Alarm", "$type $time")
        val alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        intent.putExtra("id", id)
        intent.putExtra("type", "pill")
        intent.putExtra("title", title)
        intent.putExtra("time", time)
        val pendingIntent = PendingIntent.getBroadcast(this, task_id.toInt(), intent, 0)

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
            if (c.before(Calendar.getInstance())) {
                c.add(Calendar.DATE, frequency)
            }
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

    fun updateReportInService() {
        //update data to server with background service
        if (Helper.isConnected(this)) {
            val token = Helper.token

            Log.e("report add ", "starting push service report")
            val service = Intent(this, DataPushService::class.java)
            service.putExtra("type", "report")
            service.putExtra("token", token)
            startService(service)
        } else {
            AlarmHelper().startAlarmForSync(this, "report")
            Log.e("report add ", "alarm for sync report")
        }
    }

    fun startStockReminder(
        context: Context,
        title: String
    ) {

        val c = Calendar.getInstance()
        Log.e("isolated StockReminder", title.toString())
        val alarmManager: AlarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
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
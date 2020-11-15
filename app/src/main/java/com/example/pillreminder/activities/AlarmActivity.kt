package com.example.pillreminder.activities

import android.app.ActivityManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.pillreminder.R
import com.example.pillreminder.adaptors.AlertItemAdapter
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.helper.Helper
import com.example.pillreminder.helper.Ringtone
import com.example.pillreminder.helper.SharedPrefHelper
import com.example.pillreminder.models.*
import com.example.pillreminder.receivers.AlarmReceiver
import com.facebook.accountkit.internal.AccountKitController
import kotlinx.android.synthetic.main.fragment_alert.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AlarmActivity : AppCompatActivity() {
    lateinit var dialog:AlertDialog
    lateinit var dialogBuilder:AlertDialog.Builder

    var actionTaken: Boolean = false
    var pillAlarms: List<PillAlarms>? = null
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

    var user:User?=null
    var user_id:String?=null

    //get current datetime
    val simpleDateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.ENGLISH)
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
    val currentDateTime = simpleDateFormat.format(Date()).toString()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_isolated2)

//        if (!isMyServiceRunning(applicationContext, ForegroundService::class.java)) {
//            val permissionIntent = Intent(this, ForegroundService::class.java)
//            ContextCompat.startForegroundService(this, permissionIntent)
//        }
//
//        checkDrawOverlayPermission()

        Helper.isAlertShowing = true

        //      var receiver : AlarmReceiver? = AlarmReceiver()
//        receiver?.r?.stop()
//        AlarmNotificationHelper.r.stop()
        Log.e("IsolatedActivity", "entered")

//        alertRecycler.setHasFixedSize(true)
//        alertRecycler.layoutManager = LinearLayoutManager(this)

        val sdf = SimpleDateFormat("hh:mm a")
        currentTime = sdf.format(Date())
        date = dateFormat.format(Date())

        if (intent.extras != null) {
            title = intent.getStringExtra("title")
            time = intent.getStringExtra("time")
            type = intent.getStringExtra("type")
            id = intent.getIntExtra("id", 0)
            snoozeAlarm = intent.getBooleanExtra("snoozeAlarm", false)
            //time = "34:00"
//            textView92.text = date
            Log.e("Isolated", "onCreate: intent extra $id $title $date snoozeAlarm $snoozeAlarm")
        }

        Thread {
            user_id = SharedPrefHelper(this).getInstance().getUserId()
            val database = Room.databaseBuilder(
                AccountKitController.getApplicationContext(),
                AppDb::class.java,
                "pill_reminder"
            ).fallbackToDestructiveMigration().build()
            snoozeSettings = database.settingsDao().getSpecificSettings("snooze_time")
            user=database.userDao().getUserByEmail(user_id!!)
            Log.e("Isolated", "init: $user_id ${user!!.name}")
//            runOnUiThread {
//                tvUser.text=user!!.name
//            }
            database.close()
        }.start()

        if (intent.getBooleanExtra("direct", false)) {
//            turnOffAlarm()
            timer()
        }

        if (intent.getBooleanExtra("direct", false) || intent.getBooleanExtra("notification", false)) { //Just for confirmation
//            showDialog()
            initiateDialog()
        }
    }

    fun checkDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            /** check if we already  have permission to draw over other apps  */
            if (!android.provider.Settings.canDrawOverlays(this)) {
                /** if not construct intent to request permission  */
                val intent = Intent(
                    android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                /** request permission via start activity for result  */
                startActivityForResult(intent, 999)
            }
        }
    }

    fun isMyServiceRunning(
        context: Context,
        serviceClass: Class<*>
    ): Boolean {
        val manager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    fun setView(view:View){

        view.alertRecycler.setHasFixedSize(true)
        view.alertRecycler.layoutManager = LinearLayoutManager(this)

        if (type == "pill") {
            pillAlarms = Helper.pillAlarms
            if (pillAlarms == null || pillAlarms!!.isEmpty()) {
                val database = Room.databaseBuilder(
                    AccountKitController.getApplicationContext(),
                    AppDb::class.java,
                    "pill_reminder"
                ).fallbackToDestructiveMigration().build()
                pillAlarms = database.timePivotDao().getPillDetails(title!!,date!!, "pill", true)
                Helper.pillAlarms = pillAlarms
                Log.e(ContentValues.TAG, "mergeAlarm isolated: ${pillAlarms!!.size} $date")
            }
//                Log.e("isolated pill size", pillAlarms!!.size.toString())

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

        } else if (type == "appointment") {
            Log.e("isolated Appointment", "entered")
//                textView91.text = "It's time for your appointment!"
            appointment = Helper.appointment
            val details = "${appointment?.place}, ${appointment?.mobile}"
            val alert = AlertItem(appointment?.title!!, details, "appointment")
            alertItems.add(alert)
            view.btnTake.setText("Attend")
        } else if (type == "vaccine") {
            Log.e("isolated Vaccine", "entered")
//                textView91.text = "It's time to take your vaccine!"
            vaccine = Helper.vaccine
            val details = "${vaccine?.place}, ${vaccine?.mobile}"
            val alert = AlertItem(vaccine?.title!!, details, "vaccine")
            alertItems.add(alert)
            view.btnTake.setText("Attend")
        }

        alertItemAdapter = AlertItemAdapter(this, alertItems)
        view.alertRecycler.adapter = alertItemAdapter
        Log.e("isolated activity", id.toString())
//            finish()
        view.btnSkip.setOnClickListener {
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
                    }
                } else {

                    if (snoozeAlarm!!) {
                        cancelAlarm((taskId + 1000).toLong())
                    }

                    val report = Report(
                        user_id!!,
                        title!!,
                        type!!,
                        dateFormat.parse(date),
                        currentTime!!,
                        time!!,
                        "Skipped",
                        currentDateTime, false
                    )
                    database.reportDao().saveReport(report)
                    Log.e("IsolatedActivity", "action skipped for $title")
                }

                Helper.isAlertShowing = false

                runOnUiThread {
                    finish()
                }
                database.close()
            }.start()
//                Toast.makeText(this, "Table Created Successfully", Toast.LENGTH_LONG).show();
        }
        Log.e("Current Date and Time", currentTime!!)
        view.btnTake.setOnClickListener {
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
                        pill.stock = pill.stock!! - 1
                        database.pillDao().update(pill)
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
                    }
                } else {

                    if (snoozeAlarm!!) {
                        cancelAlarm((taskId + 1000).toLong())
                    }

                    val report = Report(
                        user_id!!,
                        title!!,
                        type!!,
                        dateFormat.parse(date),
                        currentTime!!,
                        time!!,
                        "Attended",
                        currentDateTime, false
                    )
                    database.reportDao().saveReport(report)
                    Log.e("IsolatedActivity", "action taken for $title")
                }

                Helper.isAlertShowing = false

                runOnUiThread {
                    finish()
                }
                database.close()
            }.start()
        }
    }

    fun showDialog() {
        try {
            if (!dialog.isShowing) {
                dialog.show()
                if (dialog.window != null) {
                    dialog.window?.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initiateDialog() {
//        val builder = AlertDialog.Builder(applicationContext)
        val builder = AlertDialog.Builder(
            ContextThemeWrapper(
                this,
                R.style.AppFullScreenTheme
            )
        )
        val inflater = LayoutInflater.from(applicationContext)
        val dialogView: View = inflater.inflate(R.layout.fragment_alert, null)

//        val button: ImageView = dialogView.findViewById(R.id.close_btn)

        builder.setView(dialogView)
        val alert = builder.create()
        alert.window?.requestFeature(Window.FEATURE_NO_TITLE)
        if (alert.getWindow() != null) {
            alert.getWindow()?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        alert.setCanceledOnTouchOutside(true)

        alert.show()
        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        val window = alert.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window?.setGravity(Gravity.TOP)
        lp.copyFrom(window?.attributes)
        //This makes the dialog take up the full width
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.attributes = lp
//        button.setOnClickListener(object : OnClickListener() {
//            fun onClick(view: View?) {
//                //close the service and remove the from from the window
//                alert.dismiss()
//            }
//        })
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
                if (type == "appointment") {
                    startAlarm(
                        this,
                        appointment!!.localId,
                        "appointment",
                        appointment!!.title!!,
                        appointment!!.time!!,
                        appointment!!.taskId!!
                    )
                    Log.e("isolated timer appoint.", alertItems.size.toString())
                }
                if (type == "vaccine") {
                    startAlarm(
                        this,
                        vaccine!!.localId,
                        "vaccine",
                        vaccine!!.title!!,
                        vaccine!!.time!!,
                        vaccine!!.taskId!!
                    )
                    Log.e("isolated timer vaccine", alertItems.size.toString())
                }
            }
            Helper.isAlertShowing = false
            turnOffAlarm()
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
                    }
                } else {
                    val report = Report(
                        user_id!!,
                        intent?.getStringExtra("title")!!,
                        intent?.getStringExtra("type")!!,
                        dateFormat.parse(date),
                        currentTime!!,
                        time!!,
                        "Missed",
                        currentDateTime,
                        false
                    )
                    database.reportDao().saveReport(report)
                    Log.e("IsolatedActivity", "action Missed for $title")
                }
                database.close()
                Helper.isAlertShowing = false
                actionTaken = true
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
}
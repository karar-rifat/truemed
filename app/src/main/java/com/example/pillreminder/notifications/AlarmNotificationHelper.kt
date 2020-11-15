package com.example.pillreminder.notifications

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import java.util.*
import android.app.NotificationChannel
import android.media.Ringtone
import android.util.Log
import androidx.room.Room
import com.example.pillreminder.MainActivity
import com.example.pillreminder.R
import com.example.pillreminder.activities.IsolatedActivity
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.helper.Helper
import com.example.pillreminder.helper.SharedPrefHelper
import com.example.pillreminder.models.Report
import com.example.pillreminder.receivers.AlarmReceiver
import com.facebook.accountkit.internal.AccountKitController
import java.text.SimpleDateFormat


class AlarmNotificationHelper : IntentService("NotificationService") {
    private lateinit var mNotification: Notification
    private val mNotificationId: Int = 1000
    private var type:String?=null
//    lateinit var r: Ringtone

    @SuppressLint("NewApi")
    private fun createChannel() {

        Log.e("Notification Helper","createChannel")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            Log.e("Notification Helper",">O in createChannel type $type")

            val context = this.applicationContext
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = when (type?.toLowerCase(Locale.ENGLISH)) {
                "pill" -> {
                    NotificationChannel(CHANNEL_ID_PILL, CHANNEL_NAME_PILL, importance)
                }
                "vaccine" -> {
                    NotificationChannel(CHANNEL_ID_VACCINE, CHANNEL_NAME_VACCINE, importance)
                }
                else -> {
                    NotificationChannel(CHANNEL_ID_APPOINTMENT, CHANNEL_NAME_APPOINTMENT, importance)
                }
            }
//            val notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)

            notificationChannel.enableVibration(true)
            notificationChannel.setShowBadge(true)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.parseColor("#e8334a")
            notificationChannel.description = getString(R.string.notification_channel_description)
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }

    companion object {

        const val CHANNEL_ID_PILL = "TrueMed.Pill"
        const val CHANNEL_NAME_PILL = "TrueMed Pill Notification"

        const val CHANNEL_ID_VACCINE = "TrueMed.Vaccine"
        const val CHANNEL_NAME_VACCINE = "TrueMed Vaccine Notification"

        const val CHANNEL_ID_APPOINTMENT = "TrueMed.Appointment"
        const val CHANNEL_NAME_APPOINTMENT = "TrueMed Appointment Notification"

        const val CHANNEL_ID_STOCK = "TrueMed.Stock"
//        lateinit var r: Ringtone
    }


    override fun onHandleIntent(intent: Intent?) {
//        var alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
//        r = RingtoneManager.getRingtone(baseContext, alert)
//
//        if (r == null) {
//
//            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//            r = RingtoneManager.getRingtone(baseContext, alert)
//
//            if (r == null) {
//                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
//                r = RingtoneManager.getRingtone(baseContext, alert)
//            }
//        }
//
//        r?.play()



        val id = intent?.getIntExtra("id", 0)
        type = intent?.getStringExtra("type")
        val title = intent?.getStringExtra("title")
        val time = intent?.getStringExtra("time")
        Log.e("Notification Helper", "timestamp>0 type $type")
        Log.e("Notification Helper", "onHandleIntent $title $time")

        val database = Room.databaseBuilder(
            AccountKitController.getApplicationContext(),
            AppDb::class.java!!,
            "pill_reminder"
        )
            .fallbackToDestructiveMigration().build()

        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
        val currentDate = dateFormat.format(Date())
        val sdf = SimpleDateFormat("hh:mm a")
        val currentTime = sdf.format(Date())

        val userId= SharedPrefHelper(AccountKitController.getApplicationContext()).getInstance().getUserId()
        val notification = com.example.pillreminder.models.Notification(userId!!, title!!, type!!,currentDate, currentTime)
        database.NotificationDao().saveNotification(notification)
        database.close()

        //Create Channel
        createChannel()

        var timestamp: Long = 0
        if (intent != null && intent.extras != null) {
            timestamp = intent.extras!!.getLong("timestamp")
        }

        timestamp = 1000
        Log.e("NotiHelperTimestamp", timestamp.toString())


        if (timestamp > 0) {

            if (type != "stock") {
                val nTitle = "$type Reminder!!!"
                val nMessage = "You have a reminder. Click to check details."

                val context = this.applicationContext
                var notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                val notifyIntent = Intent(this, IsolatedActivity::class.java)
                notifyIntent.putExtra("id", id)
                notifyIntent.putExtra("type", type)
                notifyIntent.putExtra("title", title)
                notifyIntent.putExtra("time", time)
                notifyIntent.putExtra("notification", true)
                notifyIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                val requestCode = when(type?.toLowerCase(Locale.ENGLISH)){
                    "pill"->{
                       0
                    }
                    "vaccine"->{
                       1
                    }
                    else->{
                       2
                    }
                }

                val pendingIntent = PendingIntent.getActivity(
                    context,
                    requestCode,
                    notifyIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

                val takeIntent = Intent(this, NotificationActionReceiver::class.java)
                takeIntent.putExtra("id", id)
                takeIntent.putExtra("type", type)
                takeIntent.putExtra("title", title)
                takeIntent.putExtra("time", time)
                takeIntent.putExtra("notification", true)
                takeIntent.putExtra("action", "take")

                takeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                val pendingIntentTake = PendingIntent.getActivity(
                    context,
                    0,
                    takeIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

                val skipIntent = Intent(this, NotificationActionReceiver::class.java)
                skipIntent.putExtra("id", id)
                skipIntent.putExtra("type", type)
                skipIntent.putExtra("title", title)
                skipIntent.putExtra("time", time)
                skipIntent.putExtra("notification", true)
                skipIntent.putExtra("action", "skip")

                skipIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                val pendingIntentSkip = PendingIntent.getActivity(
                    context,
                    0,
                    skipIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

                Log.i(
                    "AlarmNotificationHelper",
                    intent?.getIntExtra("id", 0)
                        .toString() + " " + intent?.getStringExtra("type") + intent?.getStringExtra(
                        "title"
                    )
                )

                val res = this.resources
                val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

                val calendar = Calendar.getInstance()
                calendar.timeInMillis = timestamp

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    Log.e("Notification Helper", "type $type")
                    mNotification = when(type?.toLowerCase(Locale.ENGLISH)){
                        "pill"->{
                            Notification.Builder(this, CHANNEL_ID_PILL)
                        }
                        "vaccine"->{
                            Notification.Builder(this, CHANNEL_ID_VACCINE)
                        }
                        else->{
                            Notification.Builder(this, CHANNEL_ID_APPOINTMENT)
                        }
                    }

                        // Set the intent that will fire when the user taps the notification
                        .setContentIntent(pendingIntent)
                        .setDeleteIntent(pendingIntent)
//                        .addAction(R.drawable.ic_check,"Take",pendingIntentTake)
//                        .addAction(R.drawable.ic_close,"Skip",pendingIntentSkip)
                        .setSmallIcon(R.drawable.ic_user_icon)
                        .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                        .setAutoCancel(true)
                        .setContentTitle(nTitle)
                        .setStyle(
                            Notification.BigTextStyle()
                                .bigText(nMessage)
                        )
                        .setContentText(nMessage).build()
                } else {

                    mNotification = Notification.Builder(this)
                        // Set the intent that will fire when the user taps the notification
//                    .addAction(mNotification,r.stop())
                        .setContentIntent(pendingIntent)
                        .setDeleteIntent(pendingIntent)
//                        .addAction(R.drawable.ic_check,"Take",pendingIntentTake)
//                        .addAction(R.drawable.ic_close,"Skip",pendingIntentSkip)
                        .setSmallIcon(R.drawable.ic_user_icon)
                        .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                        .setAutoCancel(true)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setContentTitle(nTitle)
                        .setStyle(
                            Notification.BigTextStyle()
                                .bigText(nMessage)
                        )
                        .setSound(uri)
                        .setContentText(nMessage).build()

                }

                notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                // mNotificationId is a unique int for each notification that you must define
                notificationManager.notify(mNotificationId, mNotification)

            } else{
                val nTitle = "$title stock Reminder!!!"
                val nMessage = "You have a stock reminder. refill."

                val context = this.applicationContext
                var notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

//                val notifyIntent = Intent(this, IsolatedActivity::class.java)
//                notifyIntent.putExtra("id", id)
//                notifyIntent.putExtra("type", type)
//                notifyIntent.putExtra("title", title)
////                notifyIntent.putExtra("time", time)
//                notifyIntent.putExtra("notification", true)
//                notifyIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK


                val requestCode = when(type?.toLowerCase(Locale.ENGLISH)){
                    "pill"->{
                        0
                    }
                    "vaccine"->{
                        1
                    }
                    else->{
                        2
                    }
                }

//                val pendingIntent = PendingIntent.getActivity(
//                    context,
//                    requestCode,
//                    notifyIntent,
//                    PendingIntent.FLAG_UPDATE_CURRENT
//                )

                val takeIntent = Intent(this, NotificationActionReceiver::class.java)
                takeIntent.putExtra("id", id)
                takeIntent.putExtra("type", type)
                takeIntent.putExtra("title", title)
                takeIntent.putExtra("time", time)
                takeIntent.putExtra("notification", true)
                takeIntent.putExtra("action", "take")

                takeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                val pendingIntentTake = PendingIntent.getActivity(
                    context,
                    0,
                    takeIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

                val skipIntent = Intent(this, NotificationActionReceiver::class.java)
                skipIntent.putExtra("id", id)
                skipIntent.putExtra("type", type)
                skipIntent.putExtra("title", title)
                skipIntent.putExtra("time", time)
                skipIntent.putExtra("notification", true)
                skipIntent.putExtra("action", "skip")

                skipIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                val pendingIntentSkip = PendingIntent.getActivity(
                    context,
                    0,
                    skipIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

                Log.i(
                    "AlarmNotificationHelper",
                    intent?.getIntExtra("id", 0)
                        .toString() + " " + intent?.getStringExtra("type") + intent?.getStringExtra(
                        "title"
                    )
                )

                val res = this.resources
                val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

                val calendar = Calendar.getInstance()
                calendar.timeInMillis = timestamp

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    Log.e("Notification Helper", "type $type")
                    mNotification = when(type?.toLowerCase(Locale.ENGLISH)){
                        "pill"->{
                            Notification.Builder(this, CHANNEL_ID_PILL)
                        }
                        "vaccine"->{
                            Notification.Builder(this, CHANNEL_ID_VACCINE)
                        }
                        else->{
                            Notification.Builder(this, CHANNEL_ID_APPOINTMENT)
                        }
                    }

                        // Set the intent that will fire when the user taps the notification
//                        .setContentIntent(pendingIntent)
//                        .setDeleteIntent(pendingIntent)
//                        .addAction(R.drawable.ic_check,"Take",pendingIntentTake)
//                        .addAction(R.drawable.ic_close,"Skip",pendingIntentSkip)

                        .setSmallIcon(R.drawable.ic_user_icon)
                        .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                        .setAutoCancel(true)
                        .setContentTitle(nTitle)
                        .setStyle(
                            Notification.BigTextStyle()
                                .bigText(nMessage)
                        )
                        .setContentText(nMessage).build()
                } else {

                    mNotification = Notification.Builder(this)
                        // Set the intent that will fire when the user taps the notification
//                    .addAction(mNotification,r.stop())
//                        .setContentIntent(pendingIntent)
//                        .setDeleteIntent(pendingIntent)
//                        .addAction(R.drawable.ic_check,"Take",pendingIntentTake)
//                        .addAction(R.drawable.ic_close,"Skip",pendingIntentSkip)
                        .setSmallIcon(R.drawable.ic_user_icon)
                        .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                        .setAutoCancel(true)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setContentTitle(nTitle)
                        .setStyle(
                            Notification.BigTextStyle()
                                .bigText(nMessage)
                        )
                        .setSound(uri)
                        .setContentText(nMessage).build()

                }

                notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                // mNotificationId is a unique int for each notification that you must define
                notificationManager.notify(mNotificationId, mNotification)
//                val nTitle = "$title is low on stock!!!"
//                val nMessage = "$title is low on stock. Please refill."
//
//                val context = this.applicationContext
//                var notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//                Log.i(
//                    "AlarmNotificationHelper",
//                    intent?.getIntExtra("id", 0)
//                        .toString() + " " + intent?.getStringExtra("type") + intent?.getStringExtra(
//                        "title"
//                    )
//                )
//
//
//                val res = this.resources
//                val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    mNotification = Notification.Builder(this, CHANNEL_ID_STOCK)
//                        // Set the intent that will fire when the user taps the notification
//                        .setSmallIcon(R.drawable.ic_user_icon)
//                        .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
//                        .setAutoCancel(true)
//                        .setContentTitle(nTitle)
//                        .setStyle(
//                            Notification.BigTextStyle()
//                                .bigText(nMessage)
//                        )
//                        .setContentText(nMessage).build()
//                } else {
//
//                    mNotification = Notification.Builder(this)
//                        // Set the intent that will fire when the user taps the notification
////                    .addAction(mNotification,r.stop())
//                        .setSmallIcon(R.drawable.ic_user_icon)
//                        .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
//                        .setAutoCancel(true)
//                        .setPriority(Notification.PRIORITY_MAX)
//                        .setContentTitle(nTitle)
//                        .setStyle(
//                            Notification.BigTextStyle()
//                                .bigText(nMessage)
//                        )
//                        .setSound(uri)
//                        .setContentText(nMessage).build()
//                }
//
//                notificationManager =
//                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//                // mNotificationId is a unique int for each notification that you must define
//                notificationManager.notify(mNotificationId, mNotification)



            }
        }
    }




    override fun onDestroy() {
        super.onDestroy()
        //r.stop()
    }

}
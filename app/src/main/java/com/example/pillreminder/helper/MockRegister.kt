package com.example.pillreminder.helper

import android.util.Log
import androidx.room.Room
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.models.Settings
import com.example.pillreminder.models.User
import com.facebook.accountkit.internal.AccountKitController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MockRegister {

    var userId: String? = "john@email.com"
//    var userId: String? = null

    fun saveInDb(): String? {
        val database = Room.databaseBuilder(
            AccountKitController.getApplicationContext(),
            AppDb::class.java,
            "pill_reminder"
        ).fallbackToDestructiveMigration().build()

        Thread {
            //get current datetime
            val simpleDateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.ENGLISH)
            val currentDateTime = simpleDateFormat.format(Date()).toString()

            val users = database.userDao().checkIfExist("john@email.com")

            if (users.isEmpty()) {

                val newUser =
                    User(
                        "John Doe",
                        "john@email.com",
                        "123456",
                        "1234567890",
                        "1 Jul 2000",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        currentDateTime,
                        false
                    )
                database.settingsDao().settings().forEach {
                    Log.i("settings=====>", """ Title - ${it.title}""")
                }
                database.userDao().saveUser(newUser).toInt()

                GlobalScope.launch(Dispatchers.IO) {
//                    DataPushRepo().pushUser(Helper.token!!,newUser)
                }

                Log.e("mock register", "profile saved in db")
                val alarm = database.settingsDao().getSpecificSettings("alarm")
                val notification_message =
                    database.settingsDao().getSpecificSettings("notification_message")
                val snooze_time = database.settingsDao().getSpecificSettings("snooze_time")
                if (alarm == null) {
                    val forAlarm = Settings("alarm", "", true, "a@a.com")
                    database.settingsDao().saveSettings(forAlarm)
                    Log.e("Checking alarm tune", "null")
                } else if (alarm != null) {
                    Log.e("Checking alarm tune", "not null")
                }
                if (notification_message == null) {
                    val forNotification = Settings("notification_message", "", true, "a@a.com")
                    database.settingsDao().saveSettings(forNotification)
                    Log.e("Checking notification", "null")
                } else if (notification_message != null) {
                    Log.e("Checking notification", "not null")
                }
                if (snooze_time == null) {
                    val forSnooze = Settings("snooze_time", "0", false, "a@a.com")
                    database.settingsDao().saveSettings(forSnooze)
                    Log.e("Checking snooze", "null")
                } else if (snooze_time != null) {
                    Log.e("Checking snooze", "not null")
                }
            } else {
                this.userId = users[0].email
                Log.e("mock register", "userId= ${userId}, existing user found!")
            }

        }.start()

        Log.e("mock register", "userId= ${userId}, done successfully!")
        return userId
    }
}
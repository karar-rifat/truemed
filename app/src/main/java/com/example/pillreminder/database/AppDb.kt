package com.example.pillreminder.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.pillreminder.helper.DateConverter
import com.example.pillreminder.interfaces.*
import com.example.pillreminder.models.*


@Database(entities = [Appointment::class, Table::class, Pill::class, Vaccine::class, Document::class, User::class, Report::class, Settings::class, Dependant::class, TimePivot::class, Information::class,Notification::class],version = 1, exportSchema = false)

@TypeConverters(DateConverter::class)
abstract class AppDb :  RoomDatabase() {
    abstract fun appointmentDao() : AppointmentDao
    abstract fun tableDao() : TableDao
    abstract fun pillDao() : PillDao
    abstract fun vaccineDao() : VaccineDao
    abstract fun documentDao() : DocumentDao
    abstract fun userDao() : UserDao
    abstract fun reportDao() : ReportDao
    abstract fun settingsDao() : SettingsDao
    abstract fun dependantDao() : DependantDao
    abstract fun timePivotDao() : TimePivotDao
    abstract fun informationDao() : InformationDao
    abstract fun NotificationDao() : NotificationDao

    var instance: AppDb? = null

    @Synchronized
    open fun getInstance(context: Context): AppDb? {
        if (instance == null) {
            instance = Room.databaseBuilder(context.applicationContext, AppDb::class.java, "pill_reminder")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()
        }
        return instance
    }
}
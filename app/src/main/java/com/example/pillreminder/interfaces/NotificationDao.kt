package com.example.pillreminder.interfaces

import androidx.room.*
import com.example.pillreminder.models.Appointment
import com.example.pillreminder.models.Notification
import com.example.pillreminder.models.Report
import java.util.*

@Dao
interface NotificationDao {
    @Insert
    fun saveNotification(notification: Notification) : Long

    @Query("select * from `notification` ORDER BY id DESC")
    fun notifications() : List<Notification>

    @Query("DELETE  from `notification`" )
    fun clearNotification()

    @Query("SELECT * FROM `notification` WHERE id =:id")
    fun getNotification(id: Int): Notification

    @Update
    fun update(u: Notification)

    @Delete
    fun delete(u: Notification)
}
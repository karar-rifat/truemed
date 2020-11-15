package com.example.pillreminder.interfaces

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.pillreminder.models.Appointment
import androidx.room.Delete
import androidx.room.Update
import com.example.pillreminder.models.AppointmentVaccineAlarms
import com.example.pillreminder.models.Pill
import com.example.pillreminder.models.PillAlarms
import io.reactivex.Flowable


@Dao
interface AppointmentDao {
    @Insert
    fun saveAppointment(appointment: Appointment): Long

    @Query("select * from appointment")
    fun appointments(): List<Appointment>

    @Query("DELETE  from `appointment`" )
    fun clearAppointment()

    @Query("select * from appointment WHERE user_id =:user_id AND synced=:isSynced")
    fun unSyncedAppointments(user_id: String,isSynced:Boolean=false): List<Appointment>

    @Query("SELECT * FROM `appointment` WHERE local_id =:id")
    fun getAppointment(id: Int): Appointment

//    @Query("SELECT * FROM `appointment` WHERE `time` =:time AND reminder=:reminder")
//    fun getActiveAppointment(time: String,reminder:Boolean): List<AppointmentVaccineAlarms>

    @Query("select * from `appointment` WHERE user_id =:user_id AND title=:title")
    fun getAppointmentDetails(title:String,user_id: String) : Appointment

    @Query("SELECT time_pivot.*,appointment.reminder AS reminder,appointment.place AS place,appointment.mobile AS mobile  FROM `time_pivot` JOIN appointment ON time_pivot.title=appointment.title  WHERE time_pivot.time =:time AND time_pivot.type =:type AND appointment.reminder=:reminder")
    fun getActiveAppointment(time: String, type: String,reminder:Boolean): List<AppointmentVaccineAlarms>

    @Query("SELECT * FROM `appointment` WHERE user_id =:user_id")
    fun getUserAppointments(user_id: String): List<Appointment>

    @Update
    fun update(u: Appointment)

    @Delete
    fun delete(u: Appointment)

    //    @Query("UPDATE appointment SET available_rooms = +1 WHERE available_rooms != 2")
//    fun updateRemains(id: Int): Appointment
//    @Query("SELECT * FROM appointment WHERE time LIKE :search")
    @Query("SELECT appointment.*,time_pivot.time AS time FROM appointment JOIN time_pivot ON appointment.title=time_pivot.title WHERE time_pivot.type = :type AND time_pivot.time LIKE :search")
    fun dateWiseAppointments(type: String?, search: String?): List<Appointment>
//    search = "%fido%";
}


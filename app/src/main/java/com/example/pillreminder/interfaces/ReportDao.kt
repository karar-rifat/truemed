package com.example.pillreminder.interfaces

import androidx.room.*
import com.example.pillreminder.models.Appointment
import com.example.pillreminder.models.Report
import java.util.*

@Dao
interface ReportDao {
    @Insert
    fun saveReport(doc : Report) : Long

    @Query("select * from `report` ORDER BY local_id DESC")
    fun reports() : List<Report>

    @Query("select * from `report` WHERE `date` BETWEEN :dateThen AND :dateNow")
    fun reportsByDays(dateThen:Date,dateNow: Date) : List<Report>

    @Query("DELETE  from `report`" )
    fun clearReport()

    @Query("SELECT * FROM `report` WHERE local_id =:id")
    fun getReport(id: Int): Report

    @Query("select * from report WHERE user_id =:user_id AND synced=:isSynced")
    fun unSyncedReport(user_id:String,isSynced:Boolean=false): List<Report>


    @Update
    fun update(u: Report)

    @Delete
    fun delete(u: Report)
}
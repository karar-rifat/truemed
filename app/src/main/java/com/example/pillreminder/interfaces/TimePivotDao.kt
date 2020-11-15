package com.example.pillreminder.interfaces

import androidx.room.*
import com.example.pillreminder.models.*

@Dao
interface TimePivotDao {
    @Insert
    fun saveTime(table: TimePivot): Long

    @Query("select * from `time_pivot` WHERE user_id=:user_id")
    fun times(user_id: String): List<TimePivot>

    @Query("DELETE  from `time_pivot`")
    fun clearTimePivot()

    @Query("SELECT * FROM `time_pivot` WHERE local_id =:id")
    fun getTimeById(id: Int): TimePivot

    @Query("select * from `time_pivot` WHERE user_id =:user_id AND synced=:isSynced")
    fun unSyncedTimes(user_id: String,isSynced: Boolean = false): List<TimePivot>

    @Query("select * from `time_pivot` WHERE user_id =:user_id AND title=:title AND remote_id NOT NULL")
    fun getTimePivotDetails(title: String, user_id: String): List<TimePivot>

//    @Query("SELECT * FROM `time_pivot` WHERE id_to_match =:id and `type` =:type")
//fun getData(id: Int, type: String): List<TimePivot>

    @Query("SELECT * FROM `time_pivot` WHERE title =:title and `type` =:type")
    fun getData(title: String, type: String): List<TimePivot>

    @Query("SELECT * FROM `time_pivot` WHERE title =:title AND time=:time AND `type` =:type")
    fun getData(title: String, time: String, type: String): List<TimePivot>

    @Query("SELECT * FROM `time_pivot` WHERE `time` =:time and `type` =:type")
    fun getDataByTime(time: String, type: String): List<TimePivot>

    @Query("SELECT time_pivot.*,pill.start_from AS start_from,pill.no_of_day AS no_of_day,pill.stock AS stock,pill.reminder AS reminder,pill.lowest_stock AS lowest_stock,pill.stock_reminder AS stock_reminder, pill.no_of_dose AS dose, pill.frequency AS frequency,pill.before_or_after_meal AS before_or_after_meal FROM `time_pivot` JOIN pill ON time_pivot.title=pill.title  WHERE time_pivot.title=:title AND time_pivot.time =:time AND time_pivot.type =:type AND pill.reminder=:reminder")
    fun getPillDetails(title:String,time: String, type: String, reminder: Boolean): List<PillAlarms>

    @Query("SELECT * FROM `time_pivot` WHERE user_id =:user_id and `type` =:type and `time` LIKE :search GROUP BY id_to_match")
    fun getUserData(user_id: String, type: String, search: String?): List<TimePivot>

    @Query("DELETE FROM `time_pivot` WHERE id_to_match=:id and `type` =:type")
    fun deleteRows(id: Int, type: String)

    @Update
    fun update(u: TimePivot)

    @Delete
    fun delete(u: TimePivot)
}
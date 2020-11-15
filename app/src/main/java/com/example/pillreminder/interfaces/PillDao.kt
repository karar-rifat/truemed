package com.example.pillreminder.interfaces

import androidx.room.*
import com.example.pillreminder.models.Pill

@Dao
interface PillDao {
    @Insert
    fun savePill(pill : Pill) : Long

    @Query("select * from `pill`")
    fun pills() : List<Pill>

    @Query("DELETE  from `pill`" )
    fun clearPill()

    @Query("select * from `pill` WHERE user_id =:user_id")
    fun userPills(user_id: String) : List<Pill>

    @Query("select * from `pill` WHERE user_id =:user_id AND synced=:isSynced")
    fun unSyncedUserPills(user_id: String,isSynced:Boolean=false) : List<Pill>

    @Query("select * from `pill` WHERE user_id =:user_id AND title=:title")
    fun getPillDetails(title:String,user_id: String) : Pill

    @Query("SELECT * FROM `pill` WHERE local_id =:id")
    fun getPill(id: Int): Pill

    @Query("SELECT * FROM `pill` WHERE `title` =:title")
    fun getPillDetails(title: String): Pill

    @Query("SELECT * FROM `pill` WHERE table_id =:id")
    fun getTableSpecificPills(id: Int): List<Pill>

    @Query("DELETE FROM `pill` WHERE local_id =:id")
    fun deleteTableSpecificPills(id: Int)

    @Update
    fun update(u: Pill)

    @Delete
    fun delete(u: Pill)

    @Query("SELECT * FROM `pill` WHERE `morning` IS NOT NULL AND `morning`!=\"\"")
    fun morningPills(): List<Pill>

    @Query("SELECT * FROM `pill` WHERE `noon` IS NOT NULL AND `noon`!=\"\"")
    fun noonPills(): List<Pill>

    @Query("SELECT * FROM `pill` WHERE `evening` IS NOT NULL AND `evening`!=\"\"")
    fun eveningPills(): List<Pill>

    @Query("SELECT * FROM `pill` WHERE `night` IS NOT NULL AND `night`!=\"\"")
    fun nightPills(): List<Pill>

    @Query("UPDATE pill SET stock=stock-1 WHERE local_id =:id")
    fun updateRemains(id: Int)

    @Query("UPDATE pill SET last_pill_status=:status,synced=:synced WHERE `title` =:title")
    fun updateLastPillStatus(title: String,status:String,synced:Boolean=false)

//    @Query("UPDATE pill SET last_pill_status=:status WHERE `id` =:id")
//    fun updateLastPillStatus(id: Int,status:String)
}
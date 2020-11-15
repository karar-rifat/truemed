package com.example.pillreminder.interfaces

import androidx.room.*
import com.example.pillreminder.models.Appointment
import com.example.pillreminder.models.Pill
import com.example.pillreminder.models.Table

@Dao
interface TableDao {
    @Insert
    fun saveTable(table : Table) : Long

    @Query("select * from `table`")
    fun tables() : List<Table>

    @Query("DELETE  from `table`" )
    fun clearTable()

    @Query("SELECT * FROM `table` WHERE local_id =:id")
    fun getTable(id: Int): Table

    @Query("select * from `table` WHERE user_id =:user_id AND synced=:isSynced")
    fun unSyncedTable(user_id: String,isSynced:Boolean=false): List<Table>

    @Query("SELECT * FROM `table` WHERE user_id =:user_id")
    fun getUserTables(user_id: String): List<Table>

    @Query("select * from `table` WHERE user_id =:user_id AND title=:title")
    fun getTableDetails(title:String,user_id: String) : Table

    @Update
    fun update(u: Table)

    @Delete
    fun delete(u: Table)
}
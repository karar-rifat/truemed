package com.example.pillreminder.interfaces

import androidx.room.*
import com.example.pillreminder.models.Appointment
import com.example.pillreminder.models.Information
import com.example.pillreminder.models.Pill
import com.example.pillreminder.models.Table

@Dao
interface InformationDao {
    @Insert
    fun saveInformation(information : Information) : Long

    @Query("select * from `information`")
    fun informations() : List<Information>

    @Query("DELETE  from `information`" )
    fun clearInformations()

    @Query("SELECT * FROM `information` WHERE local_id =:id")
    fun getInformation(id: Int): Information

    @Update
    fun update(u: Information)

    @Delete
    fun delete(u: Information)
}
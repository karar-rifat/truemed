package com.example.pillreminder.interfaces

import androidx.room.*
import com.example.pillreminder.models.Settings

@Dao
interface SettingsDao {
    @Insert
    fun saveSettings(settings : Settings) : Long

    @Query("select * from `settings`")
    fun settings() : List<Settings>

    @Query("SELECT * FROM `settings` WHERE `title` =:string LIMIT 1")
    fun getSpecificSettings(string: String): Settings

    @Update
    fun update(u: Settings)

    @Delete
    fun delete(u: Settings)
}
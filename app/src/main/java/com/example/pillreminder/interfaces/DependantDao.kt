package com.example.pillreminder.interfaces

import androidx.room.*
import com.example.pillreminder.models.Dependant

@Dao
interface DependantDao {
    @Insert
    fun saveDependant(dependant : Dependant) : Long

    @Query("select * from `dependant`")
    fun dependants() : List<Dependant>

    @Query("SELECT * FROM `dependant` WHERE `id` =:id")
    fun getDependant(id: Int): Dependant

    @Update
    fun update(u: Dependant)

    @Delete
    fun delete(u: Dependant)
}